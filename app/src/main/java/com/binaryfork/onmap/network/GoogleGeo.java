package com.binaryfork.onmap.network;

import android.content.Context;

import com.binaryfork.onmap.Constants;
import com.binaryfork.onmap.network.services.GoogleGeocoding;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class GoogleGeo {
    private static final String API_URL = "http://maps.google.com/maps/api/geocode/";
    private static GoogleGeo instance;
    private Context context;

    private RestAdapter restAdapter;

    public GoogleGeo(Context context) {
        this.context = context;
    }

    public static GoogleGeo getInstance(Context context) {
        if (instance == null) {
            instance = new GoogleGeo(context);
        }
        return instance;
    }

    private RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder
                    .setClient(new OkClient(OkHttpInstance.getOkHttpClient(context)))
                    .setEndpoint(API_URL);

            if (Constants.DEBUG) {
                builder.setLogLevel(RestAdapter.LogLevel.FULL);
            }

            restAdapter = builder.build();
        }

        return restAdapter;
    }

    public GoogleGeocoding geo() {
        return getRestAdapter().create(GoogleGeocoding.class);
    }
}
