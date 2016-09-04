package com.nisnis.batp.logisticbuddy.model;

import android.provider.SyncStateContract;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nisie on 9/3/16.
 */
@IgnoreExtraProperties
public class MapData {

    LatLng position;
    String address;
    String recipient;
    String phone;
    ArrayList<ItemData> item;
    String verifyCode;

    public LatLng getPosition() {
        return this.position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
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

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Exclude
    public MarkerOptions getMarker() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(this.getPosition());
        if (this.getRecipient() != null)
            markerOptions.title(this.getRecipient());
        if (this.getAddress() != null)
            markerOptions.snippet(this.getAddress());
        return markerOptions;

    }

    @Exclude
    public static MapData convertFromFirebase(Map<String, Object> mapObj) {
        MapData marker = new MapData();
        marker.setAddress((String) mapObj.get("address"));
        marker.setPhone((String) mapObj.get("phone"));
        marker.setRecipient((String) mapObj.get("recipient"));
        marker.setPosition(converPositionFromFirebase(mapObj));
        marker.setItem(convertItemsFromFirebase(mapObj));
        return marker;
    }

    private static ArrayList<ItemData> convertItemsFromFirebase(Map<String, Object> mapObj) {
        ArrayList<Object> objectMap = (ArrayList<Object>) mapObj.get("item");
        ArrayList<ItemData> list = new ArrayList<>();
        for (Object obj : objectMap) {
            if (obj instanceof Map) {
                Map<String, Object> item = (Map<String, Object>) obj;
                ItemData itemData = new ItemData();
                itemData.setId((String) item.get("id"));
                list.add(itemData);
            }
        }
        return list;
    }

    private static LatLng converPositionFromFirebase(Map<String, Object> mapObj) {
        return new LatLng(
                (Double) ((HashMap<String, Double>) mapObj.get("position")).get("latitude")
                , (Double) ((HashMap<String, Double>) mapObj.get("position")).get("longitude")
        );
    }
}
