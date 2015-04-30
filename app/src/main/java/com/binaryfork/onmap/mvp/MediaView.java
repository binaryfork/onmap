package com.binaryfork.onmap.mvp;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.View;

import com.binaryfork.onmap.network.Media;

public interface MediaView {
    Media getMedia();
    void openFromMap(Media media, Bitmap bitmap, Point markerPoint);
    void openFromGrid(Media media, View thumbView);
    void hide();
}
