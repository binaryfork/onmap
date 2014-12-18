package com.binaryfork.onmap.ui;

import android.view.View;
import android.widget.RelativeLayout;

public class ViewMover {

    public static void moveToXy(View view, int x, int y) {
        view.setTranslationX(x);
        view.setTranslationY(y);
    }
}
