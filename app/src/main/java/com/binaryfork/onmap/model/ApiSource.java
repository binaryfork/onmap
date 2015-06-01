package com.binaryfork.onmap.model;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;

import com.binaryfork.onmap.R;

public enum ApiSource {
    INSTAGRAM(R.string.instagram, R.color.instagram),
    FLICKR(R.string.flickr, R.color.flickr),
    TWITTER(R.string.twitter, R.color.twitter),
    FOURSQUARE(R.string.foursquare, R.color.foursquare);

    @StringRes private int string;
    @ColorRes private int color;

    ApiSource(int string, int color) {
        this.string = string;
        this.color = color;
    }

    public String getString(Context context) {
        return context.getString(string);
    }
    public int getColor(Context context) {
        return context.getResources().getColor(color);
    }
}
