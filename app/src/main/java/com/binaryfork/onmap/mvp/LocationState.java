package com.binaryfork.onmap.mvp;

import com.google.android.gms.maps.model.LatLng;

public interface LocationState {
    LatLng getLastLocation();
    void saveLastLocation(LatLng latLng);
}