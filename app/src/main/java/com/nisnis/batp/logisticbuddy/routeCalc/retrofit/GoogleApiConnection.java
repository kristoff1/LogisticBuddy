package com.nisnis.batp.logisticbuddy.routeCalc.retrofit;

import com.nisnis.batp.logisticbuddy.routeCalc.constant.GoogleApiUrl;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sebastianuskh on 9/4/16.
 */

public class GoogleApiConnection {
    private static GoogleApiConnection instance;

    public static GoogleApiConnection getInstance(){
        if(instance == null){
            instance = new GoogleApiConnection();
        }
        return instance;
    }

    private GoogleApiService service;

    public GoogleApiService createService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GoogleApiUrl.MAPS_API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            service = retrofit.create(GoogleApiService.class);
        }
        return service;
    }

}
