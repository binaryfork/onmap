package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.network.instagram.model.MediaResponse;

public interface MarkersView {

    void showCenterMarker();
    void showMarkers(MediaResponse mediaResponse);
}
