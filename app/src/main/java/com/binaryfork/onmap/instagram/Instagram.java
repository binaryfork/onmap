package com.binaryfork.onmap.instagram;

import android.content.Context;

import com.binaryfork.onmap.Constants;
import com.binaryfork.onmap.instagram.services.MediaService;
import com.binaryfork.onmap.util.Utils;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;
import retrofit.client.OkClient;

public class Instagram {
    private static final String API_URL = "https://api.instagram.com/v1/";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String CLIENT_ID = "e116cf2defd74561bef595c78bf23697";
    private static Instagram instance;
    private Context context;

    private RestAdapter restAdapter;
    private OkHttpClient okHttpClient;

    public Instagram() {
        this.context = context;
    }

    public static Instagram getInstance() {
        if (instance == null) {
            instance = new Instagram();
        }
        return instance;
    }

    private RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder.setEndpoint(API_URL);
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addQueryParam(PARAM_CLIENT_ID, CLIENT_ID);
                }
            });

            if (Constants.DEBUG) {
                builder.setLogLevel(RestAdapter.LogLevel.FULL);
            }

            if (okHttpClient != null)
                builder.setClient(new OkClient(okHttpClient));

            restAdapter = builder.build();
        }

        return restAdapter;
    }

    public Instagram cache(Context context) {
        this.context = context;
        File cacheDirectory = new File(context.getCacheDir(), "cache");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        try {
            Cache cache = new Cache(cacheDirectory, cacheSize);
            okHttpClient = new OkHttpClient();
            okHttpClient.setCache(cache);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public MediaService mediaService() {
        return getRestAdapter().create(MediaService.class);
    }
}