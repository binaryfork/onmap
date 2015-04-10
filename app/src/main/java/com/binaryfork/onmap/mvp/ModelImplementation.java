package com.binaryfork.onmap.mvp;

import android.content.Context;

import com.binaryfork.onmap.network.instagram.Instagram;
import com.binaryfork.onmap.network.instagram.model.MediaResponse;
import com.google.android.gms.maps.model.LatLng;

import rx.Observable;

public class ModelImplementation implements Model {

    private final int RESULTS_COUNT = 50;

    @Override
    public Observable<MediaResponse> loadMediaByLocation(Context context, LatLng location) {
        return Instagram.getInstance(context)
                .mediaService()
                .mediaSearch(location.latitude, location.longitude, RESULTS_COUNT);
    }

    @Override
    public Observable<MediaResponse> loadMediaByLocationAndDate(Context context, LatLng location, long from, long to) {
        return Instagram.getInstance(context)
                .mediaService()
                .mediaSearch(location.latitude, location.longitude, from, to, RESULTS_COUNT);
    }
}
