package com.binaryfork.onmap.util;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import io.codetail.animation.SupportAnimator;

public class Animations {

    public static void moveFromTop(View view) {
        moveFrom(view, true, false);
    }

    public static void moveFromBottom(View view) {
        moveFrom(view, false, false);
    }

    public static void moveFrom(final View view, boolean top, boolean hide) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, view.getHeight() * (top ? -1 : 1), 0);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        if (hide)
            animation.setAnimationListener(hideListener(view));
        view.startAnimation(animation);
    }

    public static void moveTo(View view, boolean top) {
        moveTo(view, top, null);
    }

    public static void moveToTopHide(View view) {
        moveTo(view, true, hideListener(view));
    }

    public static void moveTo(View view, boolean top, Animation.AnimationListener listener) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, view.getHeight() * (top ? -1 : 1));
        animation.setDuration(100);
        if (listener != null)
            animation.setAnimationListener(listener);
        animation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(animation);
    }

    public static void moveToXy(View view, int x, int y) {
        view.setTranslationX(x);
        view.setTranslationY(y);
    }

    public static Animation.AnimationListener hideListener(final View view) {
        return new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {
            }

            @Override public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override public void onAnimationRepeat(Animation animation) {
            }
        };
    }

    public static SupportAnimator.AnimatorListener hideListenerSupAnimator(final View view) {
        return new SupportAnimator.AnimatorListener() {
            @Override public void onAnimationStart() {
            }

            @Override public void onAnimationEnd() {
                view.setVisibility(View.GONE);
            }

            @Override public void onAnimationCancel() {
            }

            @Override public void onAnimationRepeat() {
            }
        };
    }
}
