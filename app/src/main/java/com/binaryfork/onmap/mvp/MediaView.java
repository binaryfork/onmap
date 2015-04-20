package com.binaryfork.onmap.mvp;

import android.graphics.Point;
import android.view.View;

import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.binaryfork.onmap.network.Media;

import java.io.Serializable;

public interface MediaView extends Serializable {
    Media getMedia();
    void openFromMap(MediaClusterItem clusterTargetItem, Point markerPoint);
    void openFromGrid(Media media, View thumbView);
    void hide();
}
