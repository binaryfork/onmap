package com.binaryfork.onmap.mvp;

import android.content.Context;

import com.binaryfork.onmap.network.ApiSource;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public interface Presenter {

    void setMapMediaView(MapMediaView view);
    void setupClusterer(Context context, GoogleMap map);
    void changeSource(ApiSource apiSource);
    void setTime(long min, long max);
    void setDistance(int distance);
    void getMedia(LatLng location);
    void onDestroy();
}
