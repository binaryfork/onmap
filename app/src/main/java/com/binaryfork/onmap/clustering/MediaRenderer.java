package com.binaryfork.onmap.clustering;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.List;

public class MediaRenderer extends DefaultClusterRenderer<MediaClusterItem> {

    private int markerDimension;

    public MediaRenderer(Context context, GoogleMap map, ClusterManager<MediaClusterItem> clusterManager,
                         int markerDimension) {
        super(context, map, clusterManager);
        this. markerDimension = markerDimension;
    }

    @Override protected void onBeforeClusterItemRendered(MediaClusterItem item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(item.thumbBitmap));
    }

    @Override protected void onBeforeClusterRendered(Cluster<MediaClusterItem> cluster, MarkerOptions markerOptions) {
        List<Bitmap> drawables = new ArrayList<>(Math.min(4, cluster.getSize()));
        for (MediaClusterItem item : cluster.getItems()) {
            if (drawables.size() == 4) break;
            drawables.add(item.thumbBitmap);
        }
        ClusterBitmap clusterBitmap = new ClusterBitmap(drawables, markerDimension);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(clusterBitmap.draw(cluster.getSize())));
    }

    @Override protected boolean shouldRenderAsCluster(com.google.maps.android.clustering.Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }
}
