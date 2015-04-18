package com.binaryfork.onmap.mvp;

import android.view.View;

import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.binaryfork.onmap.network.Media;

public interface MediaView {
    void openFromMap(MediaClusterItem clusterTargetItem);
    void openFromGrid(Media media, View thumbView);
    void hide();
}
