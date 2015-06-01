package com.binaryfork.onmap.model;

import com.binaryfork.onmap.model.foursquare.Foursquare;
import com.binaryfork.onmap.model.foursquare.model.FoursquareResponse;
import com.binaryfork.onmap.model.twitter.TwitterInstance;
import com.binaryfork.onmap.model.flickr.Flickr;
import com.binaryfork.onmap.model.flickr.model.FlickrPhotos;
import com.binaryfork.onmap.model.instagram.Instagram;
import com.binaryfork.onmap.model.instagram.model.InstagramItems;
import com.google.android.gms.maps.model.LatLng;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.services.params.Geocode;

import rx.Observable;

public class ModelImplementation implements Model {

    private final int RESULTS_COUNT = 100;
    public int distance = 1000; // meters
    public long from; // minimum timestamp in seconds
    public long to; // maximum timestamp in seconds

    private static final String POPULAR_TAG = "adventure";

    @Override public Observable<InstagramItems> instagram(LatLng location) {
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

    @Override public Observable<InstagramItems> instagramPopular(int count) {
        return Instagram.getInstance()
                .mediaService()
                .tagged(POPULAR_TAG, count);
    }

    @Override public Observable<FlickrPhotos> flickr(LatLng location) {
        return Flickr.getInstance()
                .photos()
                .searchByLocation(1, from * 1000, to * 1000, kilometers(), location.latitude, location.longitude);
    }

    @Override public Observable<FlickrPhotos> flickrPopular() {
        return Flickr.getInstance()
                .photos()
                .searchRecent(1);
    }

    /**
     * https://dev.twitter.com/rest/reference/get/search/tweets
     */
    @Override public void twitter(LatLng location, Callback<Search> callback) {
        Geocode geocode = new Geocode(location.latitude, location.longitude, kilometers(), Geocode.Distance.KILOMETERS);
        TwitterInstance.getInstance().getSearchService()
                .tweets("filter:images", geocode, "", "", "", RESULTS_COUNT, "", 0l, 0l, true, callback);
    }

    @Override public Observable<FoursquareResponse> foursquare(LatLng location) {
        String latLng = String.valueOf(location.latitude) + "," + String.valueOf(location.longitude);
        return Foursquare.getInstance().venues()
                .explore(latLng, distance, 50, 1);
    }

    private int kilometers() {
        return distance < 1000 ? 1 : distance / 1000;
    }
}