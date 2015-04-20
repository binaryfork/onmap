package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.network.flickr.Flickr;
import com.binaryfork.onmap.network.flickr.model.FlickrPhotos;
import com.binaryfork.onmap.network.instagram.Instagram;
import com.binaryfork.onmap.network.instagram.model.InstagramItems;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import rx.Observable;

public class ModelImplementation implements Model, Serializable {

    private final int RESULTS_COUNT = 50;
    private final int DISTANCE = 1000; // meters

    @Override
    public Observable<InstagramItems> loadMediaByLocation(LatLng location) {
        return Instagram.getInstance()
                .mediaService()
                .mediaSearch(location.latitude, location.longitude, RESULTS_COUNT);
    }

    @Override
    public Observable<InstagramItems> loadMediaByLocationAndDate(LatLng location, long from, long to) {
        if (from == 0 || to == 0)
            // Load most recent media.
            return Instagram.getInstance()
                    .mediaService()
                    .mediaSearch(location.latitude, location.longitude, RESULTS_COUNT);
        else
            // Load media between two timestamps in seconds.
            return Instagram.getInstance()
                    .mediaService()
                    .mediaSearch(location.latitude, location.longitude, from, to, DISTANCE, RESULTS_COUNT);
    }

    public Observable<FlickrPhotos> flickr(LatLng location) {
        return Flickr.getInstance()
                .photos()
                .searchByLocation(location.latitude, location.longitude);
    }
}
