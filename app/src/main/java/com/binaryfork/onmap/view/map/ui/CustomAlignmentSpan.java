package com.binaryfork.onmap.view.map.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;
import android.util.Log;


public class CustomAlignmentSpan extends ReplacementSpan {

    // color span bug http://stackoverflow.com/questions/28323901/foregroundcolorspan-is-not-applied-to-replacementspan
    private int color;
    private int viewWidth;
    private int viewHeight;
    private int padding;

    public CustomAlignmentSpan(int color, int viewWidth, int viewHeight, int padding) {
        this.color = color;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.padding = padding;
    }

    @Override public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return 0;
    }

    @Override public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Log.e("w", "start " + start);
        Log.e("w", "end " + end);
        Log.e("w", "x " + x);
        Log.e("w", "y " + y);
        Log.e("w", "width " + viewWidth);
        Log.e("w", "viewHeight " + viewHeight);
        paint.setColor(color);
        canvas.drawText(text, start, end, viewWidth - paint.measureText(text.toString()) - padding, viewHeight/4, paint);
    }
}
