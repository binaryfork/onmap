package com.binaryfork.onmap.network.flickr;

import android.content.Context;

import com.binaryfork.onmap.Constants;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.network.OkHttpInstance;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class Flickr {

    private static final String BASE_URL = "https://api.flickr.com/services";

    /**
     * Available image sizes are described here: https://www.flickr.com/services/api/misc.urls.html
     */
    private final static String EXTRAS = "url_m,url_t,geo,date_taken,owner_name,date_upload";

    private static Flickr instance;
    private Context context;

    private RestAdapter restAdapter;

    public Flickr(Context context) {
        this.context = context;
    }

    public static Flickr getInstance(Context context) {
        if (instance == null) {
            instance = new Flickr(context);
        }
        return instance;
    }

    private RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder
                    .setClient(new OkClient(OkHttpInstance.getOkHttpClient(context)))
                    .setEndpoint(BASE_URL)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addQueryParam("api_key", context.getString(R.string.fl));
                            request.addQueryParam("format", "json");
                            request.addQueryParam("nojsoncallback", "1");
                            request.addQueryParam("per_page", "50");
                            request.addQueryParam("extras", EXTRAS);
                        }
                    });

            if (Constants.DEBUG) {
                builder.setLogLevel(RestAdapter.LogLevel.BASIC);
            }
            restAdapter = builder.build();
        }
        return restAdapter;
    }

    public FlickrService photos() {
        return getRestAdapter().create(FlickrService.class);
    }
}
