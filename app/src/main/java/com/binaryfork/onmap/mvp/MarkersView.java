package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.network.MediaList;

public interface MarkersView {

    void showCenterMarker();
    void showMarkers(MediaList mediaResponse);
}
