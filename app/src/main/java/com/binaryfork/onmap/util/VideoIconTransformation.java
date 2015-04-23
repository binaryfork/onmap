package com.binaryfork.onmap.util;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.binaryfork.onmap.BaseApplication;
import com.squareup.picasso.Transformation;

public class VideoIconTransformation implements Transformation {

    private final Drawable videoIcon;

    public VideoIconTransformation() {
        videoIcon = BaseApplication.get().getResources().getDrawable(android.R.drawable.ic_media_play);
    }

    @Override
    public String key() {
        return "video";
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        synchronized (VideoIconTransformation.class) {
            if (bitmap == null) {
                return null;
            }
            Bitmap resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(resultBitmap);
            if (videoIcon == null)
                return null;
            videoIcon.setBounds(canvas.getClipBounds());
            videoIcon.draw(canvas);
            canvas.drawBitmap(resultBitmap, 0, 0, null);
            bitmap.recycle();
            return resultBitmap;
        }
    }
}
