package com.nisnis.batp.logisticbuddy.routeCalc;


import com.nisnis.batp.logisticbuddy.model.ClientMarker;

import org.ejml.simple.SimpleMatrix;

import java.util.List;

import rx.Subscription;

/**
 * Created by sebastianuskh on 9/4/16.
 */
public interface RouteCalc {

    Subscription getDistanceMatrix(List<ClientMarker> markers, int vehicleNumber);
    RouteCalc setListener(RouteCalcListener listener);
    RouteCalc setGOOGLE_API_KEY(String GOOGLE_API_KEY);

    interface RouteCalcListener {
        void onSuccess(SimpleMatrix s);
        void onFailure();
    }

}
