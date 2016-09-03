package com.nisnis.batp.logisticbuddy.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by nisie on 9/3/16.
 */
public class MapData {

    Double latitude;
    Double longitude;
    String address;
    String recipient;
    ArrayList<ItemData> item;

    public LatLng getPosition() {
        return new LatLng(
                this.getLatitude(),
                this.getLongitude()
        );
    }

    public void setPosition(LatLng position) {
        this.latitude = position.latitude;
        this.longitude = position.longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public ArrayList<ItemData> getItem() {
        return item;
    }

    public void setItem(ArrayList<ItemData> item) {
        this.item = item;
    }

    public MarkerOptions getMarker() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(this.getPosition());
        if (this.getRecipient() != null)
            markerOptions.title(this.getRecipient());
        if (this.getAddress() != null)
            markerOptions.snippet(this.getAddress());
        return markerOptions;

    }
}
