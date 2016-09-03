package com.nisnis.batp.logisticbuddy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.util.ArrayList;

public class DriverActivity extends FragmentActivity implements
        OnMapReadyCallback {

    //max zoom 21.0f, min zoom 2.0f
    private static final float DEFAULT_ZOOM = 17.0f;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1001;

    private GoogleMap mMap;
    private ArrayList<MapData> listMarker;
    private LatLng currentLocation;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listMarker = new ArrayList<>();
        initializeFirebase();
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

        if (listMarker.size() != 0 && listMarker.get(0).getPosition() != null)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(listMarker.get(0).getPosition(), DEFAULT_ZOOM));

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

}
