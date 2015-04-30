package com.binaryfork.onmap.model;

import com.binaryfork.onmap.BaseApplication;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

public class OkHttpInstance {

    private static OkHttpClient okHttpClient;

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(BaseApplication.get().getCacheDir(), cacheSize);
            okHttpClient = new OkHttpClient();
            okHttpClient.setCache(cache);
        }
        return okHttpClient;
    }
}
