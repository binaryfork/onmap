package com.binaryfork.onmap.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.binaryfork.onmap.R;

public class ExpandingImage extends SquareImageView {
    
    private AnimatorSet mCurrentAnimator;
    private int markerPhotoSize;

    public ExpandingImage(Context context) {
        super(context);
    }

    public ExpandingImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandingImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int getMarkerPhotoSize() {
        if (markerPhotoSize == 0)
            markerPhotoSize = (int) getResources().getDimension(R.dimen.markerPhotoSize);
        return markerPhotoSize;
    }
    
    public void zoomImageFromThumb(Point startPoint) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        startBounds.left = startPoint.x - getMarkerPhotoSize() /2;
        startBounds.right = startPoint.x + getMarkerPhotoSize() /2;
        startBounds.top = startPoint.y - getMarkerPhotoSize()/ 2;
        startBounds.bottom = startPoint.y + getMarkerPhotoSize()/2;

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        //     thumbView.getGlobalVisibleRect(startBounds);

        getRootView().findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);


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

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        //thumbView.setAlpha(0f);
        setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        setPivotX(0f);
        setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(this, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(this, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(this, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(this,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(300);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(ExpandingImage.this, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(ExpandingImage.this,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(ExpandingImage.this,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(ExpandingImage.this,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(300);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //          thumbView.setAlpha(1f);
                        setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        //           thumbView.setAlpha(1f);
                        setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
               // infoLayout.setVisibility(View.GONE);
                //  Animations.fillScreenWithView(false, whiteCircle);
            }
        });
    }
    
    
}
