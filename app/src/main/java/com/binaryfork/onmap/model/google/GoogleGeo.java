package com.binaryfork.onmap.model.google;

import com.binaryfork.onmap.Constants;
import com.binaryfork.onmap.model.OkHttpInstance;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class GoogleGeo {
    private static final String API_URL = "http://maps.google.com/maps/api/geocode/";
    private static GoogleGeo instance;

    private RestAdapter restAdapter;

    public static GoogleGeo getInstance() {
        if (instance == null) {
            instance = new GoogleGeo();
        }
        return instance;
    }

    private RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder
                    .setClient(new OkClient(OkHttpInstance.getOkHttpClient()))
                    .setEndpoint(API_URL);

            if (Constants.DEBUG) {
                builder.setLogLevel(RestAdapter.LogLevel.FULL);
            }
            restAdapter = builder.build();
        }
        return restAdapter;
    }

    public GoogleGeoService geo() {
        return getRestAdapter().create(GoogleGeoService.class);
    }
}
