package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.network.model.GeocodeResults;
import com.binaryfork.onmap.network.model.MediaResponse;

public interface MarkersView {

    void showMarkers(MediaResponse mediaResponse);
}
