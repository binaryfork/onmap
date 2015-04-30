package com.binaryfork.onmap.mvp;

import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.binaryfork.onmap.network.Media;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;

public interface MapMediaView {

    void showTime(String time);
    void onMenuClick();
    void openPhoto(Media media);
    void openPhotoFromMap(MediaClusterItem clusterTargetItem);
    void clickPhotoCluster(Cluster<MediaClusterItem> cluster);
    void showCenterMarker(int distance);
    void goToLocation(LatLng latLng);
    LatLng getLocation();
    void allMarkesLoaded();
    void showSearchSuggestions();
    void setDistance(int distance);
}
