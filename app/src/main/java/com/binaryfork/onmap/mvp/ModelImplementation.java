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

    private final int RESULTS_COUNT = 100;

    @Override public Observable<InstagramItems> instagram(LatLng location, int distance, long from, long to) {
        if (from == 0 || to == 0)
            // Load most recent media.
            return Instagram.getInstance()
                    .mediaService()
                    .mediaSearch(location.latitude, location.longitude, distance, RESULTS_COUNT);
        else
            // Load media between two timestamps in seconds.
            return Instagram.getInstance()
                    .mediaService()
                    .mediaSearch(location.latitude, location.longitude, from, to, distance, RESULTS_COUNT);
    }

    @Override public Observable<FlickrPhotos> flickr(LatLng location) {
        return Flickr.getInstance()
                .photos()
                .searchByLocation(location.latitude, location.longitude);
    }

    @Override public void twitter(LatLng location, int distance, Callback<Search> callback) {
        Geocode geocode = new Geocode(location.latitude, location.longitude, distance / 1000, Geocode.Distance.KILOMETERS);
        TwitterInstance.getInstance().getSearchService()
                .tweets("filter:images", geocode, "", "", "", RESULTS_COUNT, "", 0l, 0l, true, callback);
    }
}
