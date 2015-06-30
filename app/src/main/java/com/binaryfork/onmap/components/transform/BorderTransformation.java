package com.binaryfork.onmap.components.transform;


import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.binaryfork.onmap.util.AndroidUtils;
import com.squareup.picasso.Transformation;

public class BorderTransformation implements Transformation {

    public static final int BORDER = 1;

    public static int dp() {
        return AndroidUtils.dp(BORDER);
    }

    @Override
    public String key() {
        return "border";
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        synchronized (BorderTransformation.class) {
            if (bitmap == null) {
                return null;
            }
            Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth() + AndroidUtils.dp(BORDER * 2), bitmap.getHeight() + AndroidUtils.dp(BORDER * 2), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawRGB(255, 255, 255);
            canvas.drawBitmap(bitmap, dp(), dp(), null);
            bitmap.recycle();
            return resultBitmap;
        }
    }
}
