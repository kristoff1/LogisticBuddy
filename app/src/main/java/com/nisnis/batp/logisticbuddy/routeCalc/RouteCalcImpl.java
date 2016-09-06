package com.nisnis.batp.logisticbuddy.routeCalc;

import android.util.Log;


import com.nisnis.batp.logisticbuddy.model.ClientMarker;
import com.nisnis.batp.logisticbuddy.model.DistanceMatrixGoogle.DistanceMatrix;
import com.nisnis.batp.logisticbuddy.routeCalc.retrofit.GoogleApiConnection;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import routing.VehicleRoutingImpl;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by sebastianuskh on 9/4/16.
 */

public class RouteCalcImpl implements RouteCalc {

    private static final String TAG = RouteCalcImpl.class.getSimpleName();
    private RouteCalcListener listener;
    private GoogleApiConnection googleApiConnection;
    private String GOOGLE_API_KEY;

    @Override
    public RouteCalc setListener(RouteCalcListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public RouteCalc setGOOGLE_API_KEY(String GOOGLE_API_KEY) {
        this.GOOGLE_API_KEY = GOOGLE_API_KEY;
        return this;
    }

    @Override
    public Subscription getDistanceMatrix(List<ClientMarker> markers, final int vehicleNumber) {
        if(listener != null){
            googleApiConnection = GoogleApiConnection.getInstance();

            String nodes = "";
            for (ClientMarker marker: markers){
                nodes += marker.getMarker().getPosition().latitude
                        + ","
                        + marker.getMarker().getPosition().longitude
                        + "|";
            }

            // delete the last "|"
            nodes = nodes.substring(0, nodes.length() - 1);

            Log.i(TAG, nodes);

            Map<String, String> params = new HashMap<>();
            params.put("origins", nodes);
            params.put("destinations", nodes);
            params.put("key", GOOGLE_API_KEY);

            return googleApiConnection.createService().getDistanceMatrix(params)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(Schedulers.immediate())
                    .flatMap(new Func1<DistanceMatrix, Observable<SimpleMatrix>>() {
                        @Override
                        public Observable<SimpleMatrix> call(DistanceMatrix distanceMatrix) {
                            Log.i(TAG, distanceMatrix.getRows().toString());
                            return calculateRouteMatrix(distanceMatrix, vehicleNumber);
                        }
                    })
                    .subscribe(new Subscriber<SimpleMatrix>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, e.toString());
                        }

                        @Override
                        public void onNext(SimpleMatrix s) {
                            Log.i(TAG, s.toString());
                            listener.onSuccess(s);
                        }
                    });

        }

        return null;
    }

    private Observable<SimpleMatrix> calculateRouteMatrix(DistanceMatrix distanceMatrix, int vehicleNumber) {
        int distanceMatrixSize = distanceMatrix.getDestinationAddresses().size();
        SimpleMatrix distanceSimpleMatrix = new SimpleMatrix(distanceMatrixSize, distanceMatrixSize);

        Log.i(TAG, distanceMatrix.toString());

        for(int i = 0; i < distanceMatrixSize; i ++){
            for (int j = 0; j < distanceMatrixSize; j ++){
                distanceSimpleMatrix.set(i, j, distanceMatrix.getRows().get(i).getElements().get(j).getDistance().getValue());
            }
        }

        Log.i(TAG, distanceSimpleMatrix.toString());

        List<Double> clientDemand = new ArrayList<>();
        for(int i = 0; i < distanceMatrixSize; i++){
            clientDemand.add(5.0);
        }


        return Observable.just(
                new VehicleRoutingImpl(vehicleNumber, 15, 5)
                        .setClient(distanceSimpleMatrix, clientDemand)
                        .computeBestRoute());
    }
}
