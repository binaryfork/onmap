package com.binaryfork.onmap.model.instagram;

import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.Constants;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.model.OkHttpInstance;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class Instagram {
    private static final String API_URL = "https://api.instagram.com/v1/";
    private static Instagram instance;

    private RestAdapter restAdapter;

    public static Instagram getInstance() {
        if (instance == null) {
            instance = new Instagram();
        }
        return instance;
    }

    private RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder
                    .setClient(new OkClient(OkHttpInstance.getOkHttpClient()))
                    .setEndpoint(API_URL)
                    .setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addQueryParam("client_id", BaseApplication.get().getString(R.string.instagram_api_key));
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