package com.binaryfork.onmap.network;

import android.graphics.Bitmap;
import android.text.Spannable;

public interface Media {

    String getPhotoUrl();

    String getThumbnail();

    String getVideoUrl();

    String getUsername();

    String getUserpic();

    String getSiteUrl();

    double getLatitude();

    double getLongitude();

    boolean isVideo();

    long getCreatedDate();

    Spannable getComments();

    void setThumbBitmap(Bitmap bitmap);

    Bitmap getThumbBitmap();
}
