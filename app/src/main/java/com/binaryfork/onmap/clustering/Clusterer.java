package com.binaryfork.onmap.clustering;

import android.content.Context;
import android.graphics.Bitmap;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.mvp.MapMediaView;
import com.binaryfork.onmap.network.Media;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

public class Clusterer {

    private Context context;
    public GoogleMap map;
    private ClusterManager<MediaClusterItem> clusterManager;
    private int markerDimension;

    public Clusterer(Context context, GoogleMap googleMap) {
        this.context = context;
        this.map = googleMap;
        clusterManager = new ClusterManager<>(context, map);
    }

    public void init(final MapMediaView mapMediaView) {
        markerDimension = (int) context.getResources().getDimension(R.dimen.map_marker_photo);
        clusterManager.setRenderer(new MediaRenderer(context, map, clusterManager, markerDimension));
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MediaClusterItem>() {
            @Override
            public boolean onClusterItemClick(MediaClusterItem mediaClusterItem) {
                mapMediaView.openPhotoFromMap(mediaClusterItem);
                return true;
            }
        });
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MediaClusterItem>() {
            @Override
            public boolean onClusterClick(Cluster<MediaClusterItem> cluster) {
                mapMediaView.clickPhotoCluster(cluster);
                return true;
            }
        });
        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
    }

    public int getMarkerDimensions() {
        return markerDimension;
    }

    public void clearItems() {
        clusterManager.clearItems();
        map.clear();
    }

    public void addItem(Media media, Bitmap bitmap) {
        MediaClusterItem cluster = new MediaClusterItem(media, bitmap);
        clusterManager.addItem(cluster);
    }

    public void cluster() {
        clusterManager.cluster();
    }
}
