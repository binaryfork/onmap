package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.network.instagram.model.InstagramItems;

public interface MarkersView {

    void showCenterMarker();
    void showMarkers(InstagramItems mediaResponse);
}
