package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.network.twitter.TwitterInstance;
import com.binaryfork.onmap.network.flickr.Flickr;
import com.binaryfork.onmap.network.flickr.model.FlickrPhotos;
import com.binaryfork.onmap.network.instagram.Instagram;
import com.binaryfork.onmap.network.instagram.model.InstagramItems;
import com.google.android.gms.maps.model.LatLng;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.services.params.Geocode;

import rx.Observable;

public class ModelImplementation implements Model {

    private final int RESULTS_COUNT = 50;
    private final int DISTANCE = 1000; // meters

    @Override public Observable<InstagramItems> loadMediaByLocation(LatLng location) {
        return Instagram.getInstance()
                .mediaService()
                .mediaSearch(location.latitude, location.longitude, RESULTS_COUNT);
    }

    @Override public Observable<InstagramItems> loadMediaByLocationAndDate(LatLng location, long from, long to) {
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

    @Override public Observable<FlickrPhotos> flickr(LatLng location) {
        return Flickr.getInstance()
                .photos()
                .searchByLocation(location.latitude, location.longitude);
    }

    public void t(Callback<Search> callback) {
        Geocode geocode = new Geocode(55.755826, 37.6173, 5, Geocode.Distance.KILOMETERS);
        TwitterInstance.getInstance().getSearchService()
                .tweets("filter:images", geocode, "", "", "", 50, "", 0l, 0l, true, callback);
    }
}
