package com.binaryfork.onmap.network.instagram;

import android.content.Context;

import com.binaryfork.onmap.Constants;
import com.binaryfork.onmap.network.OkHttpInstance;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class Instagram {
    private static final String API_URL = "https://api.instagram.com/v1/";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String CLIENT_ID = "e116cf2defd74561bef595c78bf23697";
    private static Instagram instance;
    private Context context;

    private RestAdapter restAdapter;

    public Instagram(Context context) {
        this.context = context;
    }

    public static Instagram getInstance(Context context) {
        if (instance == null) {
            instance = new Instagram(context);
        }
        return instance;
    }

    private RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder
                    .setClient(new OkClient(OkHttpInstance.getOkHttpClient(context)))
                    .setEndpoint(API_URL)
                    .setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addQueryParam(PARAM_CLIENT_ID, CLIENT_ID);
                }
            });

            if (Constants.DEBUG) {
                builder.setLogLevel(RestAdapter.LogLevel.BASIC);
            }
            restAdapter = builder.build();
        }
        return restAdapter;
    }

    public InstagramMediaService mediaService() {
        return getRestAdapter().create(InstagramMediaService.class);
    }
}