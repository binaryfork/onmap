package com.binaryfork.onmap.clustering;


import android.graphics.Bitmap;

import com.binaryfork.onmap.model.Media;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MediaClusterItem implements ClusterItem {

    public Media media;
    public Bitmap thumbBitmap;

    public MediaClusterItem(Media media, Bitmap bitmap) {
        this.media = media;
        this.thumbBitmap = bitmap;
    }

    @Override public LatLng getPosition() {
        return new LatLng(media.getLatitude(), media.getLongitude());
    }

}
