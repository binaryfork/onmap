package com.binaryfork.onmap.components.clustering;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.binaryfork.onmap.util.AndroidUtils;

import java.util.List;

public class ClusterBitmap {

    private final List<Bitmap> bitmaps;
    public final int dimensions;

    public ClusterBitmap(List<Bitmap> bitmaps, int dimensions) {
        this.bitmaps = bitmaps;
        this.dimensions = dimensions;
    }

    public Bitmap draw(int number) {
        Bitmap canvasBitmap = Bitmap.createBitmap(dimensions + AndroidUtils.dp(1), dimensions + AndroidUtils.dp(1), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawRGB(255, 255, 255);
        canvas.save();
        canvas.clipRect(AndroidUtils.dp(1), AndroidUtils.dp(1), dimensions, dimensions);

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

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        // Draw triangle
/*        Point a = new Point(dimensions/2 - AndroidUtils.dp(5), dimensions);
        Point b = new Point(dimensions/2 + AndroidUtils.dp(5), dimensions);
        Point c = new Point(dimensions/2, dimensions + AndroidUtils.dp(5));
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();
        canvas.drawPath(path, paint);*/

        // Draw text background.
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xcc000000);
        RectF oval1 = new RectF(dimensions / 3, dimensions / 3, dimensions * 2 / 3, dimensions * 2 / 3);
        canvas.drawOval(oval1, paint);

        // Draw number of photos in cluster.
        paint.setColor(Color.WHITE);
        paint.setTextSize(AndroidUtils.dp(10));
        paint.setTextAlign(Paint.Align.CENTER);
        int x = (dimensions / 2);
        int y = (int) ((dimensions / 2) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(String.valueOf(number), x, y, paint);

        return canvasBitmap;
    }
}
