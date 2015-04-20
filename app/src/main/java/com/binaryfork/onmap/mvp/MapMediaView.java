package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.binaryfork.onmap.network.Media;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;

public interface MapMediaView {

    void showTime(String time);
    void onMenuClick();
    void openPhoto(MediaClusterItem clusterTargetItem);
    void clickPhotoCluster(Cluster<MediaClusterItem> cluster);
    void showCenterMarker();
    void goToLocation(LatLng latLng);
    void clearMap();
    void addMarker(Media media);
}
