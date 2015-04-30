package com.binaryfork.onmap.view.map;

import com.google.android.gms.maps.model.LatLng;

public interface LocationState {
    LatLng getLastLocation();
    void saveLastLocation(LatLng latLng);
}