package com.binaryfork.onmap.model;

import com.binaryfork.onmap.model.foursquare.model.FoursquareResponse;
import com.google.android.gms.maps.model.LatLng;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Search;

import rx.Observable;

public interface Model {

    Observable<? extends MediaList> flickr(LatLng location);
    Observable<? extends MediaList> instagram(LatLng location);
    void twitter(LatLng location, Callback<Search> callback);
    Observable<FoursquareResponse> foursquare(LatLng location);
}
