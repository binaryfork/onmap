package com.binaryfork.onmap.components.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class StatusBarBackView extends View {
    public StatusBarBackView(Context context) {
        super(context);
        init();
    }

    public StatusBarBackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StatusBarBackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int statusBarHeight = (int) Math.ceil(25 * getContext().getResources().getDisplayMetrics().density);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
    }
}
