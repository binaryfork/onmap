package com.binaryfork.onmap.network.flickr.model;

import com.binaryfork.onmap.network.MediaList;

import java.util.ArrayList;

public class FlickrPhotos implements MediaList {

    public Photos photos;

    @Override
    public ArrayList getList() {
        return photos.photo;
    }

    public class Photos {
        public ArrayList<FlickrPhoto> photo;
    }
}