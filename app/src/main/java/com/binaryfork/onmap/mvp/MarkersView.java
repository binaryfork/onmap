package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.network.model.MediaResponse;

public interface MarkersView {

    public final static String PICASSO_MAP_MARKER_TAG = "marker";

    void showMarkers(MediaResponse mediaResponse);
}
