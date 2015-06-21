package com.binaryfork.onmap.view.map.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;


public class CustomAlignmentSpan extends ReplacementSpan {

    // color span bug http://stackoverflow.com/questions/28323901/foregroundcolorspan-is-not-applied-to-replacementspan
    private int color;

    public CustomAlignmentSpan() {
        this.color = color;
    }

    @Override public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return 0;
    }

    @Override public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
      //  paint.setColor(color);
        canvas.drawText(text, start, end, canvas.getWidth() - paint.measureText(text, start, end), y, paint);
    }
}