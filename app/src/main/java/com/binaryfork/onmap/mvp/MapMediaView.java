package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;

public interface MapMediaView {

    void openPhoto(MediaClusterItem clusterTargetItem);
    void clickPhotoCluster(Cluster<MediaClusterItem> cluster);
    void goToLocation(LatLng latLng);
    void onMenuClick();
    void showTime(String time);
}
