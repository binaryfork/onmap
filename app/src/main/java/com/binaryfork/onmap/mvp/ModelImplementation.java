package com.binaryfork.onmap.mvp;

import android.content.Context;

import com.binaryfork.onmap.network.Instagram;
import com.binaryfork.onmap.network.model.MediaResponse;
import com.google.android.gms.maps.model.LatLng;

import rx.Observable;

public class ModelImplementation implements Model {

    @Override
    public Observable<MediaResponse> loadMediaByLocation(Context context, LatLng location) {
        return Instagram.getInstance(context)
                .mediaService()
                .mediaSearch(location.latitude, location.longitude);
    }

    @Override
    public Observable<MediaResponse> loadMediaByLocationAndDate(Context context, LatLng location, long from, long to) {
        return Instagram.getInstance(context)
                .mediaService()
                .mediaSearch(location.latitude, location.longitude, from, to);
    }
}
