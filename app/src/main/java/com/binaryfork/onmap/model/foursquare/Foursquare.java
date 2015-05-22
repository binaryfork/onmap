package com.binaryfork.onmap.model.foursquare;

import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.Constants;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.model.OkHttpInstance;
import com.binaryfork.onmap.util.AndroidUtils;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class Foursquare {
    private static final String API_URL = "https://api.foursquare.com/v2/";
    private static Foursquare instance;

    private RestAdapter restAdapter;

    public static Foursquare getInstance() {
        if (instance == null) {
            instance = new Foursquare();
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
                            request.addQueryParam("v", "20150426");
                            request.addQueryParam("client_id", BaseApplication.get().getString(R.string.fsq_id));
                            request.addQueryParam("client_secret", BaseApplication.get().getString(R.string.fsq_sec));
                            request.addHeader("Accept", "application/json;versions=1");
                            if (AndroidUtils.isDeviceOnline(BaseApplication.get())) {
                                int maxAge = 60; // read from cache for 1 minute
                                request.addHeader("Cache-Control", "public, max-age=" + maxAge);
                            } else {
                                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                                request.addHeader("Cache-Control",
                                        "public, only-if-cached, max-stale=" + maxStale);
                            }
                        }
                    });

            if (Constants.DEBUG) {
                builder.setLogLevel(RestAdapter.LogLevel.BASIC);
            }
            restAdapter = builder.build();
        }
        return restAdapter;
    }

    public FoursquareService venues() {
        return getRestAdapter().create(FoursquareService.class);
    }
}