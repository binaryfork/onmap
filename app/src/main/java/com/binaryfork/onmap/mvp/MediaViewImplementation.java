package com.binaryfork.onmap.mvp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.network.Media;
import com.binaryfork.onmap.util.Animations;
import com.binaryfork.onmap.util.CircleTransform;
import com.binaryfork.onmap.util.DateUtils;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class MediaViewImplementation implements MediaView {

    private final Context context;
    private Media media;

    private Rect startBounds;
    private Point globalOffset;
    private Rect finalBounds;
    private float startScaleFinal;
    private int markerPhotoSize;
    private AnimatorSet mCurrentAnimator;

    public View container;
    @InjectView(R.id.expanded_image) ImageView expandedImage;
    @InjectView(R.id.username) TextView usernameTxt;
    @InjectView(R.id.comments) TextView commentsTxt;
    @InjectView(R.id.user_photo) ImageView userPhoto;
    @InjectView(R.id.videoView) VideoView videoView;
    private View clickedGridViewItemHolder;

    public MediaViewImplementation(View container, Context context) {
        ButterKnife.inject(this, container);
        this.container = container;
        this.context = context;
        expandedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });
    }

    @Override public Media getMedia() {
        return media;
    }

    @Override public void openFromMap(Media media, Bitmap thumbBitmap, Point markerPoint) {
        this.media = media;
        if (media != null) {
            container.setVisibility(View.VISIBLE);
            Drawable d = new BitmapDrawable(context.getResources(), thumbBitmap);
            Picasso.with(context)
                    .load(media.getPhotoUrl())
                    .placeholder(d)
                    .into(expandedImage);
            setupDimensions();
            Rect thumbBounds = new Rect();
            thumbBounds.left = markerPoint.x - getMarkerPhotoSize() / 2;
            thumbBounds.right = markerPoint.x + getMarkerPhotoSize() / 2;
            thumbBounds.top = markerPoint.y - getMarkerPhotoSize() / 2;
            thumbBounds.bottom = markerPoint.y + getMarkerPhotoSize() / 2;
            thumbBounds.offset(-globalOffset.x, -globalOffset.y);
            zoomImageFromThumb(thumbBounds);
        }
    }

    @Override public void openFromGrid(Media media, View thumbView) {
        this.media = media;
        if (media != null) {
            container.setVisibility(View.VISIBLE);
            clickedGridViewItemHolder = thumbView;
            clickedGridViewItemHolder.setVisibility(View.INVISIBLE);
            Picasso.with(context)
                    .load(media.getPhotoUrl())
                    .into(expandedImage);
            setupDimensions();
            Rect thumbBounds = new Rect();
            thumbView.getGlobalVisibleRect(thumbBounds);
            thumbBounds.offset(-globalOffset.x, -globalOffset.y + usernameTxt.getHeight());
            zoomImageFromThumb(thumbBounds);
        }
    }

    private void setupDimensions() {
        finalBounds = new Rect();
        globalOffset = new Point();
        container.getGlobalVisibleRect(finalBounds, globalOffset);
        finalBounds.offset(-globalOffset.x, -globalOffset.y + usernameTxt.getHeight());
    }

    private void showMediaInfo() {
        Animations.moveFromTop(commentsTxt);
        Animations.moveFromBottom(usernameTxt);
        Animations.moveFromBottom(userPhoto);
        commentsTxt.setVisibility(View.VISIBLE);
        usernameTxt.setVisibility(View.VISIBLE);
        usernameTxt.setText(media.getTitle() + " " + DateUtils.formatDate(media.getCreatedDate()));
        commentsTxt.setText(media.getComments());
        if (userPhoto != null)
            Picasso.with(context).load(media.getUserpic())
                    .transform(new CircleTransform())
                    .into(userPhoto);
        if (media.isVideo()) {
            loadVideo();
        }
    }

    private void loadVideo() {
        Uri uri = Uri.parse(media.getVideoUrl());
        Timber.i("video url %s", uri);
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                expandedImage.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override public void hide() {
        Animations.moveTo(commentsTxt, true);
        Animations.moveTo(userPhoto, false);
        Animations.moveTo(usernameTxt, false, new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {
            }

            @Override public void onAnimationEnd(Animation animation) {
                userPhoto.setImageDrawable(null);
                usernameTxt.setVisibility(View.GONE);
                commentsTxt.setVisibility(View.GONE);
                zoomOutImage();
            }

            @Override public void onAnimationRepeat(Animation animation) {
            }
        });
        expandedImage.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        videoView.stopPlayback();
    }

    private void hideMediaInfo() {
        container.setVisibility(View.INVISIBLE);
        expandedImage.setVisibility(View.INVISIBLE);
        if (clickedGridViewItemHolder != null)
            clickedGridViewItemHolder.setVisibility(View.VISIBLE);
        mCurrentAnimator = null;
    }

    protected int getMarkerPhotoSize() {
        if (markerPhotoSize == 0)
            markerPhotoSize = (int) context.getResources().getDimension(R.dimen.map_marker_photo);
        return markerPhotoSize;
    }

    private void zoomImageFromThumb(Rect thumbBounds) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // markersView. Also set the container markersView's offset as the origin for the
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

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImage, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImage, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImage,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(200);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
                showMediaInfo();
            }

            @Override public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        startScaleFinal = startScale;
    }

    private void zoomOutImage() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        if (startBounds == null) {
            hideMediaInfo();
            return;
        }

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
                .ofFloat(expandedImage, View.X, startBounds.left))
                .with(ObjectAnimator
                        .ofFloat(expandedImage,
                                View.Y, startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(expandedImage,
                                View.SCALE_X, startScaleFinal))
                .with(ObjectAnimator
                        .ofFloat(expandedImage,
                                View.SCALE_Y, startScaleFinal));
        set.setDuration(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                hideMediaInfo();
            }

            @Override public void onAnimationCancel(Animator animation) {
                hideMediaInfo();
            }
        });
        set.start();
        mCurrentAnimator = set;
    }
}
