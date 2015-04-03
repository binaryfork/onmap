package com.binaryfork.onmap.mvp;

import android.content.Context;
import android.location.Location;

import com.binaryfork.onmap.instagram.Instagram;
import com.binaryfork.onmap.instagram.model.MediaResponse;

import rx.Observable;

public class ModelImplementation implements Model {

    @Override
    public Observable<MediaResponse> loadMedia(Context context, Location location) {
        return Instagram.getInstance(context)
                .mediaService()
                .mediaSearchRx(location.getLatitude(), location.getLongitude());
    }
}
