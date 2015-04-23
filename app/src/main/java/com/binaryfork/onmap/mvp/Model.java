package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.network.MediaList;
import com.google.android.gms.maps.model.LatLng;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Search;

import rx.Observable;

public interface Model {

    Observable<? extends MediaList> flickr(LatLng location);
    Observable<? extends MediaList> loadMediaByLocation(LatLng location);
    Observable<? extends MediaList> loadMediaByLocationAndDate(LatLng location, long from, long to);
    void twitter(LatLng location, Callback<Search> callback);
}
