package com.binaryfork.onmap.network;

import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

public class OkHttpInstance {

    private static OkHttpClient okHttpClient;

    public static OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient == null) {
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(context.getCacheDir(), cacheSize);
            okHttpClient = new OkHttpClient();
            okHttpClient.setCache(cache);
        }
        return okHttpClient;
    }
}
