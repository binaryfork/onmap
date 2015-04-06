package com.binaryfork.onmap.mvp;

import com.google.android.gms.maps.model.LatLng;

public interface Presenter {

    void getMediaByLocation(LatLng location);
    void getMediaByLocationAndDate(LatLng location, long from, long to);
    void onDestroy();

}
