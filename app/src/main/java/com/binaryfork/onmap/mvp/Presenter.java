package com.binaryfork.onmap.mvp;

import com.google.android.gms.maps.model.LatLng;

public interface Presenter {

    void backInTime();
    void forwardInTime();
    void toCurrentTime();
    void setTime(long time);
    void getMedia(LatLng location);
    void onDestroy();

}
