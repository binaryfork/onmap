package com.binaryfork.onmap.ui;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

public class Animations {

    public static void moveFromTop(View view) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, view.getHeight() * -1, 0);
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator(5));
        view.startAnimation(animation);
    }

    public static void moveFromBottom(View view) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, view.getHeight(), 0);
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator(5));
        view.startAnimation(animation);
    }

    public static void moveTo(View view, boolean moveToTop) {
        moveTo(view, moveToTop, null);
    }

    public static void moveTo(View view, boolean moveToTop, Animation.AnimationListener listener) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, view.getHeight() * (moveToTop ? -1 : 1));
        animation.setDuration(100);
        if (listener != null)
            animation.setAnimationListener(listener);
        animation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(animation);
    }
}
