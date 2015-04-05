package com.binaryfork.onmap.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.VideoView;

public class SquareVideoView extends VideoView {

    public SquareVideoView(Context context) {
        super(context);
    }

    public SquareVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }
}
