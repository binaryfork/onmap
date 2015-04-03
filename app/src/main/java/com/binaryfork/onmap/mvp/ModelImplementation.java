package com.binaryfork.onmap.mvp;

import android.content.Context;
import android.location.Location;

import com.binaryfork.onmap.instagram.Instagram;
import com.binaryfork.onmap.instagram.model.MediaResponse;

import rx.Observable;

public class ModelImplementation implements Model {

    @Override
    public Observable<MediaResponse> loadMediaByLocation(Context context, Location location) {
        return Instagram.getInstance(context)
                .mediaService()
                .mediaSearch(location.getLatitude(), location.getLongitude());
    }

    @Override
    public Observable<MediaResponse> loadMediaByLocationAndDate(Context context, Location location, long from, long to) {
        return Instagram.getInstance(context)
                .mediaService()
                .mediaSearch(location.getLatitude(), location.getLongitude(), from, to);
    }
}
