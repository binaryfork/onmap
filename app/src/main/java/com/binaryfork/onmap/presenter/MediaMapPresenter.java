package com.binaryfork.onmap.presenter;

import android.content.Context;

import com.binaryfork.onmap.model.ApiSource;
import com.binaryfork.onmap.view.map.MediaMapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public interface MediaMapPresenter {

    void setMediaMapView(MediaMapView view);
    void setupClusterer(Context context, GoogleMap map);
    void changeSource(ApiSource apiSource);
    ApiSource getSource();
    void setTime(long min, long max);
    void setDistance(int distance);
    void loadMedia(LatLng location);
    LatLng getLocation();
    void onDestroy();
}
