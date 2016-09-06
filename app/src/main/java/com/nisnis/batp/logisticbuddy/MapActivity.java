package com.nisnis.batp.logisticbuddy;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nisnis.batp.logisticbuddy.model.ClientMarker;
import com.nisnis.batp.logisticbuddy.model.ClientSettingAdapter;
import com.nisnis.batp.logisticbuddy.model.VehicleRoutingAdapter;
import com.nisnis.batp.logisticbuddy.routeCalc.RouteCalc;
import com.nisnis.batp.logisticbuddy.routeCalc.RouteCalcImpl;

import org.ejml.simple.SimpleMatrix;

import java.util.List;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by sebastianuskh on 9/4/16.
 */
public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MapActivity.class.getSimpleName();

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 100;
    private static final float DEFAULT_ZOOM = 17.0f;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    GoogleMap googleMap;
    private ClientSettingAdapter clientAdapter;
    private RouteCalc routeCalc;
    private int numberVehicle = 0;
    private CompositeSubscription compositeSubscription;
    private Location myLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "On Create ...");
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        Log.i(TAG, "Start Map Fragment...");
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getView().setClickable(true);
        mapFragment.getMapAsync(this);

        Log.i(TAG, "Init adapter (variables)");
        clientAdapter = new ClientSettingAdapter();

        Log.i(TAG, "Init Route Calculator & Listener");
        routeCalc = new RouteCalcImpl();




    }

    private void setLocationListener() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MapActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        compositeSubscription = getNewCompositeSubIfUnsubscribed(compositeSubscription);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
            return;
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unsubscribeIfNotNull(compositeSubscription);
    }

    public static void unsubscribeIfNotNull(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed()) {
            return new CompositeSubscription();
        }

        return subscription;
    }

    @NonNull
    private RouteCalc.RouteCalcListener getRouteCalcListener() {
        return new RouteCalc.RouteCalcListener() {
            @Override
            public void onSuccess(SimpleMatrix s) {
                Log.i(TAG, "Display Routing Result");
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                RecyclerView recyclerView = new RecyclerView(MapActivity.this);
                VehicleRoutingAdapter resultAdapter = new VehicleRoutingAdapter(s);
                recyclerView.setAdapter(resultAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(MapActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                builder.setView(recyclerView);
                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "Save Result To Firebase");
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }

            @Override
            public void onFailure() {

            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "Map Ready, setting map");
        this.googleMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        googleMap.setOnMapClickListener(this);
        setLocationEnabled();
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        setLocationListener();
    }

    private void setLocationEnabled() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "Request permission result");
        if (requestCode == PERMISSION_ACCESS_FINE_LOCATION)
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setLocationEnabled();
            } else {
                finish();
            }
    }

    @Override
    public void onMapClick(final LatLng latLng) {

        Log.i(TAG, "Map clicked on : " + latLng.toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.string_setting_client));
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Save Client and set Marker");
                String clientName = editText.getText().toString();
                ClientMarker clientMarker = new ClientMarker()
                        .setMarker(googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(clientName)));
                ;
                clientAdapter.getClientMarkers().add(clientMarker);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_manage_client:
                showClientSettingDialog();
                return true;
            case R.id.menu_edit_vehicles:
                showVehicleSettingDialog();
                return true;
            case R.id.menu_compute_routes:
                calculateRoute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void calculateRoute() {
        if(!clientAdapter.getClientMarkers().isEmpty()
                && numberVehicle != 0){
            Log.i(TAG, "Start calculating best route.. ");

            List<ClientMarker> markers = clientAdapter.getClientMarkers();
//            markers.add(0, new ClientMarker()
//                            .setMarker(googleMap.addMarker(
//                                    new MarkerOptions()
//                                    .position(new LatLng(
//                                            myLocation.getLatitude(),
//                                            myLocation.getLongitude()))
//                                    .title("Main Base"))));
//            markers.get(0).getMarker().setVisible(false);

            compositeSubscription.add(routeCalc
                    .setGOOGLE_API_KEY(getString(R.string.string_google_distance_api_key))
                    .setListener(getRouteCalcListener())
                    .getDistanceMatrix(markers, numberVehicle));
        }
    }



    private void showClientSettingDialog() {
        Log.i(TAG, "Client Setting Dialog Opened");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.string_setting_client));
        RecyclerView recyclerView = new RecyclerView(this);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);
        recyclerView.setAdapter(clientAdapter);
        builder.setView(recyclerView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void showVehicleSettingDialog(){
        Log.i(TAG, "Vehicle Setting Dialog Opened");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.string_setting_vehicle));
        final EditText numberVehicle = new EditText(this);
        builder.setView(numberVehicle);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Number of vehicle saved");
                if(!numberVehicle.getText().toString().isEmpty())
                    MapActivity.this.numberVehicle = Integer.parseInt(numberVehicle.getText().toString());
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
            return;
        } else {
            myLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (myLocation != null) {
                updateUI(myLocation);
            }

            startLocationUpdates();

        }
    }



    private void updateUI(Location location) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(myLocation.getLatitude(),
                        myLocation.getLongitude()),
                DEFAULT_ZOOM));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
