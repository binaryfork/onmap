package com.binaryfork.onmap.model;

import android.content.Context;
import android.support.annotation.StringRes;

import com.binaryfork.onmap.R;

public enum ApiSource {
    INSTAGRAM(R.string.instagram),
    FLICKR(R.string.flickr),
    TWITTER(R.string.twitter),
    FOURSQUARE(R.string.foursquare);

    @StringRes private int resource;

    ApiSource(int resource) {
        this.resource = resource;
    }

    public String getString(Context context) {
        return context.getString(resource);
    }
}
