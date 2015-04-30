package com.binaryfork.onmap.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.MainPreferenceActivity;
import com.google.android.gms.maps.model.LatLng;

public class Prefs {

    private static SharedPreferences prefs() {
        return PreferenceManager.getDefaultSharedPreferences(BaseApplication.get());
    }

    public static void putLastLocation(LatLng latLng) {
        SharedPreferences.Editor editor = prefs().edit();
        editor.putFloat("lat", (float) latLng.latitude);
        editor.putFloat("lng", (float) latLng.longitude);
        editor.commit();
    }

    @Nullable public static LatLng getLastLocation() {
        if (prefs().getFloat("lat", 0) == 0 && prefs().getFloat("lng", 0) == 0)
            return null;
        else
            return new LatLng(prefs().getFloat("lat", 0), prefs().getFloat("lng", 0));
    }

    public static boolean isSaveLocation() {
        return prefs().getBoolean(MainPreferenceActivity.SAVE_LOCATION, true);
    }
}