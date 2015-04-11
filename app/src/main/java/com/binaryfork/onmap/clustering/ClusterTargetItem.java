package com.binaryfork.onmap.clustering;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.network.Media;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ClusterTargetItem implements ClusterItem, Target {

    public Media media;
    public Bitmap thumbBitmap;
    public Context context;
    private int markerPhotoSize;

    public ClusterTargetItem(Media media, Context context) {
        this.media = media;
        this.context = context;
        markerPhotoSize = (int) context.getResources().getDimension(R.dimen.map_marker_photo);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (bitmap != null) {
            bitmap = Bitmap.createScaledBitmap(bitmap, markerPhotoSize, markerPhotoSize, true);
            if (media.isVideo()) {
                bitmap = drawVideoIcon(bitmap);
            }
            thumbBitmap = bitmap;
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }

    private Bitmap drawVideoIcon(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Drawable d = context.getResources().getDrawable(android.R.drawable.ic_media_play);
        if (d == null)
            return null;
        d.setBounds(canvas.getClipBounds());
        d.draw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return bitmap;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(media.getLatitude(), media.getLongitude());
    }
}
