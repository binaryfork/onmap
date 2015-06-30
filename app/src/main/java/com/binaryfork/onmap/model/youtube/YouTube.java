package com.binaryfork.onmap.model.youtube;

import com.binaryfork.onmap.Constants;
import com.binaryfork.onmap.model.OkHttpInstance;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class YouTube {
    private static final String API_URL = "https://www.googleapis.com/youtube/v3/";
    private static YouTube instance;

    private RestAdapter restAdapter;

    public static YouTube getInstance() {
        if (instance == null) {
            instance = new YouTube();
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
                    request.addQueryParam("key", "AIzaSyBfjiKltU3I1eutWOZPSSZucV7C6eAbE2Y");
                }
            });

            if (Constants.DEBUG) {
                builder.setLogLevel(RestAdapter.LogLevel.BASIC);
            }
            restAdapter = builder.build();
        }
        return restAdapter;
    }

    public YouTubeService service() {
        return getRestAdapter().create(YouTubeService.class);
    }
}