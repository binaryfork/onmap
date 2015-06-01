package com.binaryfork.onmap.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

public class BitmapList {

    private final List<Bitmap> bitmaps;
    public int height;
    public int width;

    public BitmapList(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public Bitmap draw() {
        Bitmap canvasBitmap = Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.save();
        canvas.clipRect(0, 0, width / 2, height);
        canvas.translate(-width / 4, 0);
        canvas.drawBitmap(bitmaps.get(0), 0, 0, null);
        canvas.restore();

        // Paint right half
        canvas.save();
        canvas.clipRect(width / 2, 0, height, height);
        canvas.translate(width / 4, 0);
        canvas.drawBitmap(bitmaps.get(1), 0, 0, null);
        canvas.restore();

        return canvasBitmap;
    }
}
