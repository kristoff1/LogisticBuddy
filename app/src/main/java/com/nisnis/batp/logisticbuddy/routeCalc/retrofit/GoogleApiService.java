package com.nisnis.batp.logisticbuddy.routeCalc.retrofit;



import com.nisnis.batp.logisticbuddy.model.DistanceMatrixGoogle.DistanceMatrix;
import com.nisnis.batp.logisticbuddy.routeCalc.constant.GoogleApiUrl;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by sebastianuskh on 9/4/16.
 */

public interface GoogleApiService {
    @GET(GoogleApiUrl.DISTANCE_MATRIX)
    Observable<DistanceMatrix> getDistanceMatrix(@QueryMap Map<String, String> params);
}
