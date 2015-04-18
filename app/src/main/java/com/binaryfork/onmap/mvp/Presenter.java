package com.binaryfork.onmap.mvp;

import com.google.android.gms.maps.model.LatLng;

public interface Presenter {
    void setTime(long min, long max);
    void setDistance(int distance);
    void getMedia(LatLng location);
    void onDestroy();
}
