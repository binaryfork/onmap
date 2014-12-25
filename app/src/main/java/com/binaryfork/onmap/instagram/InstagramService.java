package com.binaryfork.onmap.instagram;

import com.binaryfork.onmap.Constants;
import com.binaryfork.onmap.R;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class InstagramService extends RetrofitGsonSpiceService {

    private static final String BASE_URL = "https://api.instagram.com/v1/";
    private static final String PARAM_CLIENT_ID = "client_id";

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        RestAdapter.Builder builder = super.createRestAdapterBuilder();
        builder.setEndpoint(BASE_URL);
        builder.setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addQueryParam(PARAM_CLIENT_ID, getApplicationContext().getString(R.string.instagram_api_key));
            }
        });

        if (Constants.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        builder.setClient(new OkClient(new OkHttpClient()));
        return builder;
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

}