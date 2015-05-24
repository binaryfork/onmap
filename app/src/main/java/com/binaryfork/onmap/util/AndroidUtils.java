package com.binaryfork.onmap.util;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.WindowManager;

import com.binaryfork.onmap.BaseApplication;

public class AndroidUtils {

    public static float density = 1;
    private static int scrrenSize;

    static {
        density = BaseApplication.get().getResources().getDisplayMetrics().density;

        WindowManager wm = (WindowManager) BaseApplication.get().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        scrrenSize = Math.max(size.x, size.y);
    }

    public static int dp(float value) {
        return (int) Math.ceil(density * value);
    }

    public static int screenSize() {
        return scrrenSize;
    }

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
