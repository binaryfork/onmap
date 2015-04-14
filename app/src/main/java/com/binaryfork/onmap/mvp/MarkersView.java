package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.network.MediaList;
import com.google.android.gms.maps.model.LatLng;

public interface MarkersView {

    void setLocation(LatLng location);
    void showCenterMarker();
    void showMarkers(MediaList mediaResponse);
}
