package com.binaryfork.onmap.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.binaryfork.onmap.mvp.MapMediaView;
import com.google.android.gms.maps.model.Circle;

public class RangeSeekBar extends SeekBar implements SeekBar.OnSeekBarChangeListener {

    private final static int MIN = 200;

    private MapMediaView mapMediaView;
    private Circle circle;

    public RangeSeekBar(Context context) {
        super(context);
    }

    public RangeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RangeSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMapMediaView(MapMediaView mapMediaView) {
        this.mapMediaView = mapMediaView;
        setOnSeekBarChangeListener(this);
    }

    public void setMapCircle(Circle circle) {
        this.circle = circle;
    }

    @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            progress = progress >= MIN ? progress : MIN;
            setProgress(progress);
            if (circle.getRadius() == progress)
                return;
            circle.setRadius(progress);
        }
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override public void onStopTrackingTouch(SeekBar seekBar) {
        if (circle.getRadius() == getProgress())
            return;
        mapMediaView.setDistance(getProgress());
    }

}
