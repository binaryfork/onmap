package com.binaryfork.onmap.view.map;

import com.binaryfork.onmap.components.clustering.MediaClusterItem;
import com.binaryfork.onmap.model.Media;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;

public interface MediaMapView {

    void showTime(String time);
    void onMenuClick();
    void openPhoto(Media media);
    void openPhotoFromMap(MediaClusterItem clusterTargetItem);
    void clickPhotoCluster(Cluster<MediaClusterItem> cluster);
    void showCenterMarker(int distance);
    void goToLocation(LatLng latLng);
    LatLng getLocation();
    void allMarkesLoaded();
    void setDistance(int distance);
}
