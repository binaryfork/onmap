package com.binaryfork.onmap.mvp;

import android.content.Context;

import com.binaryfork.onmap.network.flickr.Flickr;
import com.binaryfork.onmap.network.flickr.model.FlickrPhotos;
import com.binaryfork.onmap.network.instagram.Instagram;
import com.binaryfork.onmap.network.instagram.model.InstagramItems;
import com.google.android.gms.maps.model.LatLng;

import rx.Observable;

public class ModelImplementation implements Model {

    private final int RESULTS_COUNT = 50;
    private final int DISTANCE = 1000; // meters

    @Override
    public Observable<InstagramItems> loadMediaByLocation(Context context, LatLng location) {
        return Instagram.getInstance(context)
                .mediaService()
                .mediaSearch(location.latitude, location.longitude, RESULTS_COUNT);
    }

    @Override
    public Observable<InstagramItems> loadMediaByLocationAndDate(Context context, LatLng location, long from, long to) {
        return Instagram.getInstance(context)
                .mediaService()
                .mediaSearch(location.latitude, location.longitude, from, to, DISTANCE, RESULTS_COUNT);
    }

    public Observable<FlickrPhotos> flickr(Context context, LatLng location) {
        return Flickr.getInstance(context)
                .photos()
                .searchByLocation(location.latitude, location.longitude);
    }
}
