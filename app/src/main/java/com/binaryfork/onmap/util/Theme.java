package com.binaryfork.onmap.util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.TypedValue;

import com.binaryfork.onmap.R;

import java.lang.ref.WeakReference;

public class Theme {

    private static WeakReference<Activity> activityRef;

    public static void updateActivity(Activity activity) {
        if (activityRef != null)
            clear();
        activityRef = new WeakReference<>(activity);
    }

    public static void clear() {
        activityRef.clear();
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        if (resId == 0)
            return null;
        return activityRef.get().getResources().getDrawable(resId);
    }

    public static int getPhotoPlaceholderResId() {
        return resolveAttribute(activityRef.get(), R.attr.iconPhotoPlaceholder);
    }

    public static int getHistoryResId() {
        return resolveAttribute(activityRef.get(), R.attr.iconHistory);
    }

    public static int getPlaceMarkerResId() {
        return resolveAttribute(activityRef.get(), R.attr.iconPlaceMarker);
    }

    public static int resolveAttribute(Activity activity, int attr) {
        final TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId;
    }
}