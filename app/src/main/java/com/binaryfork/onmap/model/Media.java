package com.binaryfork.onmap.model;

import android.text.Spannable;

public interface Media {

    String getPhotoUrl();

    String getThumbnail();

    String getVideoUrl();

    String getTitle();

    String getUserpic();

    String getSiteUrl();

    double getLatitude();

    double getLongitude();

    boolean isVideo();

    long getCreatedDate();

    Spannable getComments();

    String getAdderss();
}
