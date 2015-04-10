package com.binaryfork.onmap.network;

import android.text.Spannable;

public interface Media {

    String getPhotoUrl();

    String getThumbnail();

    String getVideoUrl();

    String getUsername();

    String getUserpic();

    String getSiteUrl();

    float getLatitude();

    float getLongitude();

    boolean isVideo();

    long getCreatedDate();

    Spannable getComments();
}
