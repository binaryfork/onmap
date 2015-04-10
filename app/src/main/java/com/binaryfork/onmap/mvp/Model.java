package com.binaryfork.onmap.mvp;

import android.content.Context;

import com.binaryfork.onmap.network.instagram.model.MediaResponse;
import com.google.android.gms.maps.model.LatLng;

import rx.Observable;

public interface Model {

    Observable<MediaResponse> loadMediaByLocation(Context context, LatLng location);
    Observable<MediaResponse> loadMediaByLocationAndDate(Context context, LatLng location, long from, long to);
}
