package com.binaryfork.onmap.model.flickr.model;

import android.text.Spannable;

import com.binaryfork.onmap.model.ApiSource;
import com.binaryfork.onmap.model.Media;

public class FlickrPhoto implements Media {

    private String url_m;
    private String url_q;
    private String ownername;
    private long dateupload;
    private double latitude;
    private double longitude;

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
    public String getTitle() {
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

    @Override public String getAdderss() {
        return null;
    }

    @Override public ApiSource getApiSource() {
        return ApiSource.FLICKR;
    }

}
