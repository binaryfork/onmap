package com.binaryfork.onmap.mvp;

import android.content.Context;
import android.location.Location;

import com.binaryfork.onmap.network.model.GeocodeResults;
import com.binaryfork.onmap.network.model.MediaResponse;

import rx.Observable;

public interface Model {

    Observable<MediaResponse> loadMediaByLocation(Context context, Location location);
    Observable<MediaResponse> loadMediaByLocationAndDate(Context context, Location location, long from, long to);
    Observable<String> onSearchTextChanged();
    Observable<GeocodeResults> suggestLocations(Context context, String query);
}
