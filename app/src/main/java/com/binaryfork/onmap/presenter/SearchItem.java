package com.binaryfork.onmap.presenter;


import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.model.google.model.GeocodeItem;
import com.binaryfork.onmap.util.Spanny;
import com.binaryfork.onmap.util.Theme;

public class SearchItem {

    @DrawableRes public int resId;
    public String photoUrl;
    public Spannable text;
    public double lat;
    public double lng;
    public Media media;
    public boolean isSection;

    public SearchItem(String text) {
        this.text = Spanny.spanText(text, new ForegroundColorSpan(Color.GRAY));
        isSection = true;
    }

    public SearchItem(Media media) {
        this.text = media.getComments();
        this.media = media;
        this.photoUrl = media.getThumbnail();
        this.lat = media.getLatitude();
        this.lng = media.getLongitude();
    }

    public SearchItem(GeocodeItem item) {
        this.resId = Theme.getPlaceMarkerResId();
        this.lat = item.geometry.location.lat;
        this.lng = item.geometry.location.lng;
        this.text = new SpannableString(item.formatted_address);
    }
}
