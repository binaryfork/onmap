package com.binaryfork.onmap.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class Animations {

    public static void fillScreenWithView(boolean fill, final View view) {
        view.setVisibility(View.VISIBLE);
        final float to = fill ? 60 : 1;
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", to),
                PropertyValuesHolder.ofFloat("scaleY", to));
        scaleDown.setDuration(300);
        scaleDown.setInterpolator(fill ? new AccelerateInterpolator() : new DecelerateInterpolator());
        if (!fill) {
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        scaleDown.start();
    }
}
