package com.binaryfork.onmap.mvp;

import android.content.Context;
import android.location.Location;

import com.binaryfork.onmap.instagram.model.MediaResponse;

import rx.Observable;

public interface Model {

    Observable<MediaResponse> loadMedia(Context context, Location location);
}
