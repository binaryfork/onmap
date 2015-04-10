package com.binaryfork.onmap.mvp;

import android.content.Context;

import com.binaryfork.onmap.network.instagram.model.InstagramItems;
import com.google.android.gms.maps.model.LatLng;

import rx.Observable;

public interface Model {

    Observable<InstagramItems> loadMediaByLocation(Context context, LatLng location);
    Observable<InstagramItems> loadMediaByLocationAndDate(Context context, LatLng location, long from, long to);
}
