package com.binaryfork.onmap.network.flickr.model;

import android.text.Spannable;

import com.binaryfork.onmap.network.Media;

public class FlickrPhoto implements Media {

    public String url_m;
    public String url_q;
    public String ownername;
    public long dateupload;
    public float latitude;
    public float longitude;

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
    public float getLatitude() {
        return latitude;
    }

    @Override
    public float getLongitude() {
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
}
