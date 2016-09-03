package com.nisnis.batp.logisticbuddy;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nisnis.batp.logisticbuddy.model.Data;
import com.nisnis.batp.logisticbuddy.model.MapData;

import java.sql.Driver;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DriverActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY";
    private static final String LOCATION_KEY = "LOCATION_KEY";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "LAST_UPDATED_TIME_STRING_KEY";
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final float METERS_100 = 100f;
    private static final String TAG_DIALOG = "TAG_DIALOG";
    @BindView(R.id.last_update_time)
    TextView lastUpdateTime;

    @BindView(R.id.current_location)
    TextView currentLocation;

    @BindView(R.id.confirmButton)
    Button confirmButton;

    //max zoom 21.0f, min zoom 2.0f
    private static final float DEFAULT_ZOOM = 17.0f;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1001;

    GoogleMap mMap;
    ArrayList<MapData> listMarker;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    DatabaseReference mFirebaseDatabaseReference;
    LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false;
    private String mLastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listMarker = new ArrayList<>();
        initializeGoogleApiClient();
        createLocationRequest();
        initializeFirebase();

        updateValuesFromBundle(savedInstanceState);

        initViewListener();
    }

    private void initViewListener() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag(TAG_DIALOG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                DialogFragment dialogFragment = ConfirmDialog.createInstance();
                ((ConfirmDialog) dialogFragment).setListener(new ConfirmDialog.OnConfirmOrderListener() {
                    @Override
                    public void onConfirm() {
                        listMarker.remove(0);
                        initMarkers();
                    }
                });
                dialogFragment.show(ft, TAG_DIALOG);
            }
        });
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {


            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mLastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI(mLastLocation);
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void initializeGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void initializeFirebase() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("NISINISI", "get data " + dataSnapshot.toString());

                listMarker.clear();
                Data data = dataSnapshot.getValue(Data.class);
                listMarker.addAll(data.getMap());
                initMarkers();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("NISINISI", "failed to get data");
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setIndoorEnabled(true);

        setLocationEnabled();

    }

    private void initMarkers() {
        mMap.clear();
        for (MapData mapData : listMarker) {
            mMap.addMarker(mapData.getMarker());
        }
    }

    private void setLocationEnabled() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ACCESS_FINE_LOCATION)
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                setLocationEnabled();

            } else {
                Log.d("NISINISI", "No permission granted");
                finish();
            }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
            return;
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                updateUI(mLastLocation);
            }

            if (mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

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
                                    DriverActivity.this,
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
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateUI(location);
    }

    private void updateUI(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude()),
                DEFAULT_ZOOM));
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        lastUpdateTime.setText(mLastUpdateTime);

        checkIfNearMarker(location);
    }

    private void checkIfNearMarker(Location location) {
        if(listMarker != null && listMarker.size() > 0) {
            Location targetLocation = new Location("");//provider name is unecessary
            targetLocation.setLatitude(listMarker.get(0).getLatitude());//your coords of course
            targetLocation.setLongitude(listMarker.get(0).getLongitude());
            if (location.distanceTo(targetLocation) < METERS_100) {
                currentLocation.setText("Arrived in destination");
            }
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
            return;
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        outState.putParcelable(LOCATION_KEY, mLastLocation);
        outState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(outState);
    }

}
