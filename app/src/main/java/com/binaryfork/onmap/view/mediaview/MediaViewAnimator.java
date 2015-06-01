package com.binaryfork.onmap.view.mediaview;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.util.AndroidUtils;
import com.binaryfork.onmap.util.Animations;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.animation.arcanimator.ArcAnimator;
import io.codetail.animation.arcanimator.Side;
import timber.log.Timber;

public class MediaViewAnimator {

    private View expandedImage;
    private View backgroundView;
    private int wx;
    private int wy;

    private Rect startBounds;
    private Rect finalBounds;
    private float startScaleFinal;
    private AnimatorSet mCurrentAnimator;
    private AnimatorListener animatorListener;

    public void setBgView(View backgroundView) {
        this.backgroundView = backgroundView;
    }

    public static MediaViewAnimator get() {
        MediaViewAnimator instance = BaseApplication.animator;
        if (instance == null) {
            instance = new MediaViewAnimator();
            BaseApplication.animator = instance;
        }
        return instance;
    }

    public void whiteBgReveal(float x, float y, int startRadius) {
        wx = (int) x;
        wy = (int) y;
        backgroundView.setVisibility(View.VISIBLE);
        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(backgroundView, wx, wy, startRadius, AndroidUtils.screenSize());
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(500);
        animator.start();
    }

    public void whiteBgHide() {
        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(backgroundView, wx, wy, AndroidUtils.screenSize(), 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(200);
        animator.addListener(Animations.hideListenerSupAnimator(backgroundView));
        animator.start();
    }

    public void setup(AnimatorListener animatorListener, final View expandedImage) {
        this.expandedImage = expandedImage;
        this.animatorListener = animatorListener;
        if (finalBounds == null) {
            expandedImage.post(new Runnable() {
                @Override public void run() {
                    finalBounds = new Rect();
                    expandedImage.getLocalVisibleRect(finalBounds);
                }
            });
        }
    }

    public void zoomImageFromThumb(Rect thumbBounds) {
        Timber.i("startBounds " + thumbBounds.top + "x" + thumbBounds.left);
        Timber.i("finalBounds " + finalBounds.top + "x" + finalBounds.left);
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // markersView. Also setBgView the container markersView's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        startBounds = thumbBounds;

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in markersView. When the animation
        // begins, it will position the zoomed-in markersView in the place of the
        // thumbnail.
        //thumbView.setAlpha(0f);
        expandedImage.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in markersView (the default
        // is the center of the markersView).
        expandedImage.setPivotX(0f);
        expandedImage.setPivotY(0f);

        expandedImage.setX(startBounds.left);
        expandedImage.setY(startBounds.top);

        Side side = startBounds.left < finalBounds.right / 2 ? Side.RIGHT : Side.LEFT;
        ArcAnimator arcAnimator = ArcAnimator.createArcAnimator(expandedImage,
                finalBounds.right / 2, finalBounds.bottom / 2 + AndroidUtils.dp(56), 60, side);
        arcAnimator.setDuration(400);
        //arcAnimator.start();

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImage, "scaleX", startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImage, "scaleY", startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImage, "x", startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImage, "y", startBounds.top, finalBounds.top + AndroidUtils.dp(56)));
        set.setDuration(200);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                animatorListener.onFinishOpen();
                mCurrentAnimator = null;
            }

            @Override public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        startScaleFinal = startScale;
    }

    public void zoomOutImage() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        if (startBounds == null) {
            animatorListener.onFinishClose();
            mCurrentAnimator = null;
            return;
        }
        MediaViewAnimator.get().whiteBgHide();
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expandedImage, "x", startBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImage, "y", startBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImage, "scaleX", startScaleFinal))
                .with(ObjectAnimator.ofFloat(expandedImage, "scaleY", startScaleFinal));
        set.setDuration(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                animatorListener.onFinishClose();
                mCurrentAnimator = null;
            }

            @Override public void onAnimationCancel(Animator animation) {
                animatorListener.onFinishClose();
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }

    public void zoomImageFromPoint(Point markerPoint) {
        Rect thumbBounds = new Rect();
        int markerRadius = (int) BaseApplication.get().getResources().getDimension(R.dimen.map_marker_photo) / 2;
        thumbBounds.left = markerPoint.x - markerRadius;
        thumbBounds.right = markerPoint.x + markerRadius;
        thumbBounds.top = markerPoint.y - markerRadius;
        thumbBounds.bottom = markerPoint.y + markerRadius;
        thumbBounds.offset(0, -AndroidUtils.dp(28));
        zoomImageFromThumb(thumbBounds);
        whiteBgReveal(markerPoint.x, markerPoint.y - AndroidUtils.dp(28), AndroidUtils.dp(28));
    }

    public void zoomImageFromView(View view) {
        Rect thumbBounds = new Rect();
        view.getGlobalVisibleRect(thumbBounds);
        thumbBounds.offset(0, -AndroidUtils.dp(24));
        zoomImageFromThumb(thumbBounds);
        int radius = view.getWidth() / 2;
        MediaViewAnimator.get().whiteBgReveal(thumbBounds.right-radius, thumbBounds.bottom-radius, radius);
    }

    public interface AnimatorListener {
        void onFinishOpen();
        void onFinishClose();
    }
}
