package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.network.flickr.model.FlickrPhotos;
import com.binaryfork.onmap.network.instagram.model.InstagramItems;
import com.google.android.gms.maps.model.LatLng;

import rx.Observable;

public interface Model {

    Observable<FlickrPhotos> flickr(LatLng location);
    Observable<InstagramItems> loadMediaByLocation(LatLng location);
    Observable<InstagramItems> loadMediaByLocationAndDate(LatLng location, long from, long to);
}
