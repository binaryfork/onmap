package com.binaryfork.onmap.clustering;


import android.content.Context;
import android.graphics.Bitmap;

import com.binaryfork.onmap.network.Media;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MediaClusterItem implements ClusterItem {

    public Media media;
    public Bitmap thumbBitmap;
    public Context context;

    public MediaClusterItem(Media media, Context context) {
        this.media = media;
        this.context = context;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(media.getLatitude(), media.getLongitude());
    }

}
