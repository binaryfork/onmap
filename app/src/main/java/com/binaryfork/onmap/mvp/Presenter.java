package com.binaryfork.onmap.mvp;

import com.google.android.gms.maps.model.LatLng;

public interface Presenter {

    void getMediaByLocationAndDate(LatLng location, long from, long to);
    void onDestroy();

}
