package com.binaryfork.onmap.util;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.binaryfork.onmap.BaseApplication;
import com.squareup.picasso.Transformation;

public class VideoIconSmallTransformation implements Transformation {

    private final Drawable videoIcon;

    public VideoIconSmallTransformation() {
        videoIcon = BaseApplication.get().getResources().getDrawable(android.R.drawable.ic_media_play);
        if (videoIcon == null)
            return;
    }

    @Override
    public String key() {
        return "video";
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        synchronized (VideoIconSmallTransformation.class) {
            if (bitmap == null) {
                return null;
            }
            Bitmap resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(resultBitmap);
            if (videoIcon == null)
                return null;
            int bounds = AndroidUtils.dp(24);
            videoIcon.setBounds(canvas.getWidth() - bounds, 0, canvas.getWidth(), bounds);
            videoIcon.draw(canvas);
          //  canvas.drawBitmap(resultBitmap, canvas.getWidth() - videoIcon.getIntrinsicWidth(), 0, null);
            bitmap.recycle();
            return resultBitmap;
        }
    }
}
