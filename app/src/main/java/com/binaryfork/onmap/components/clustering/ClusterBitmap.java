package com.binaryfork.onmap.components.clustering;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

public class ClusterBitmap {

    private final List<Bitmap> bitmaps;
    public final int dimensions;

    public ClusterBitmap(List<Bitmap> bitmaps, int dimensions) {
        this.bitmaps = bitmaps;
        this.dimensions = dimensions;
    }

    public Bitmap draw(int number) {
        Bitmap canvasBitmap = Bitmap.createBitmap(dimensions, dimensions, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.save();
        canvas.clipRect(0, 0, dimensions, dimensions);

        if (bitmaps.size() == 2 || bitmaps.size() == 3) {
            // Paint left half
            canvas.save();
            canvas.clipRect(0, 0, dimensions / 2, dimensions);
            canvas.translate(-dimensions / 4, 0);
            canvas.drawBitmap(bitmaps.get(0), 0, 0, null);
            canvas.restore();
        }
        if (bitmaps.size() == 2) {
            // Paint right half
            canvas.save();
            canvas.clipRect(dimensions / 2, 0, dimensions, dimensions);
            canvas.translate(dimensions / 4, 0);
            canvas.drawBitmap(bitmaps.get(1), 0, 0, null);
            canvas.restore();
        } else {
            // Paint top right
            canvas.save();
            canvas.scale(.5f, .5f);
            canvas.translate(dimensions, 0);
            canvas.drawBitmap(bitmaps.get(1), 0, 0, null);

            // Paint bottom right
            canvas.translate(0, dimensions);
            canvas.drawBitmap(bitmaps.get(2), 0, 0, null);
            canvas.restore();
        }

        if (bitmaps.size() >= 4) {
            // Paint top left
            canvas.save();
            canvas.scale(.5f, .5f);
            canvas.drawBitmap(bitmaps.get(0), 0, 0, null);

            // Paint bottom left
            canvas.translate(0, dimensions);
            canvas.drawBitmap(bitmaps.get(3), 0, 0, null);
            canvas.restore();
        }

        canvas.restore();

        // Draw text background.
        Paint paintOval = new Paint();
        paintOval.setStyle(Paint.Style.FILL);
        paintOval.setColor(Color.RED);
        RectF oval1 = new RectF(dimensions / 3, dimensions / 3, dimensions * 2 / 3, dimensions * 2 / 3);
        canvas.drawOval(oval1, paintOval);

        // Draw number of photos in cluster.
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(28);
        paint.setTextAlign(Paint.Align.CENTER);
        int x = (canvas.getWidth() / 2);
        int y = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(String.valueOf(number), x, y, paint);

        return canvasBitmap;
    }
}
