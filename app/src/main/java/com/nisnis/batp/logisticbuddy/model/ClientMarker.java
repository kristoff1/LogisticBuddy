package com.nisnis.batp.logisticbuddy.model;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by sebastianuskh on 9/4/16.
 */
public class ClientMarker {

    private Marker marker;

    public ClientMarker setMarker(Marker marker) {
        this.marker = marker;
        return this;
    }

    public Marker getMarker() {
        return marker;
    }
}
