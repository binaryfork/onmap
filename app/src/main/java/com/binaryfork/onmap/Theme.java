package com.binaryfork.onmap;

import android.app.Activity;
import android.util.TypedValue;

public class Theme {

    public static int getPhotoPlaceholder(Activity activity) {
        return resolveAttribute(activity, R.attr.photoPlaceholder);
    }

    public static int resolveAttribute(Activity activity, int attr) {
        final TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId;
    }
}