package com.binaryfork.onmap.network.flickr.model;

import android.graphics.Bitmap;
import android.text.Spannable;

import com.binaryfork.onmap.network.Media;

public class FlickrPhoto implements Media {

    private String url_m;
    private String url_q;
    private String ownername;
    private long dateupload;
    private double latitude;
    private double longitude;

    transient private Bitmap thumbBitmap;

    @Override
    public String getPhotoUrl() {
        return url_m;
    }

    @Override
    public String getThumbnail() {
        return url_q;
    }

    @Override
    public String getVideoUrl() {
        return null;
    }

    @Override
    public String getUsername() {
        return ownername;
    }

    @Override
    public String getUserpic() {
        return null;
    }

    @Override
    public String getSiteUrl() {
        return null;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean isVideo() {
        return false;
    }

    @Override
    public long getCreatedDate() {
        return dateupload;
    }

    @Override
    public Spannable getComments() {
        return null;
    }

    @Override public void setThumbBitmap(Bitmap bitmap) {
        thumbBitmap = bitmap;
    }

    @Override public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

}
