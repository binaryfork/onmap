package com.binaryfork.onmap.model.flickr.model;

import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.model.MediaList;

import java.util.ArrayList;

public class FlickrPhotos implements MediaList {

    public Photos photos;

    @Override
    public ArrayList<? extends Media> getList() {
        return photos.photo;
    }

    public class Photos {
        public ArrayList<FlickrPhoto> photo;
    }
}