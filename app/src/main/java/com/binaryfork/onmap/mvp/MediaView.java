package com.binaryfork.onmap.mvp;

import android.graphics.Point;
import android.view.View;

import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.binaryfork.onmap.network.Media;

public interface MediaView {
    Media getMedia();
    void openFromMap(MediaClusterItem clusterTargetItem, Point markerPoint);
    void openFromGrid(Media media, View thumbView);
    void hide();
}
