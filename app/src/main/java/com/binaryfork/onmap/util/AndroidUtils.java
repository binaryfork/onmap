package com.binaryfork.onmap.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.binaryfork.onmap.BaseApplication;

public class AndroidUtils {

    public static float density = 1;

    static {
        density = BaseApplication.get().getResources().getDisplayMetrics().density;
    }

    public static int dp(float value) {
        return (int) Math.ceil(density * value);
    }

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
