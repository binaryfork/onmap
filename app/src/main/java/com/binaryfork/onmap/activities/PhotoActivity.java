package com.binaryfork.onmap.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.binaryfork.onmap.Intents;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.network.MediaTypes;
import com.binaryfork.onmap.network.model.MediaItem;
import com.binaryfork.onmap.util.Animations;
import com.binaryfork.onmap.util.CircleTransform;
import com.binaryfork.onmap.util.DateUtils;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import timber.log.Timber;

public class PhotoActivity extends AbstractMapActivity {

    private AnimatorSet mCurrentAnimator;
    private Rect startBounds;
    private float startScaleFinal;
    private MediaItem instagramMedia;
    private int markerPhotoSize;

    @InjectView(R.id.expanded_image) ImageView expandedImage;
    @InjectView(R.id.info_layout) View infoLayout;
    @InjectView(R.id.username) TextView usernameTxt;
    @InjectView(R.id.comments) TextView commentsTxt;
    @InjectView(R.id.user_photo) ImageView userPhoto;
    @InjectView(R.id.videoView) VideoView videoView;

    @Override
    public void onBackPressed() {
        if (infoLayout.isShown()) {
            hideMediaInfo();
        } else {
            super.onBackPressed();
        }
    }

    public void onClickUsername(View view) {
        Intents.openLink(this, instagramMedia.link);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        instagramMedia = view.getInstagramMediaItem(marker.getId());
        Projection projection = map.getProjection();
        LatLng markerLocation = marker.getPosition();
        Point markerPosition = projection.toScreenLocation(markerLocation);

        if (instagramMedia != null) {
            infoLayout.setVisibility(View.VISIBLE);
            Drawable d = new BitmapDrawable(getResources(), view.getMarkerPhoto(marker.getId()));
            Picasso.with(getApplicationContext())
                    .load(instagramMedia.images.standard_resolution.url)
                    .placeholder(d)
                    .into(expandedImage);
            zoomImageFromThumb(markerPosition);
        }
        return true;
    }

    private void showMediaInfo() {
        Animations.moveFromTop(commentsTxt);
        Animations.moveFromBottom(usernameTxt);
        Animations.moveFromBottom(userPhoto);
        commentsTxt.setVisibility(View.VISIBLE);
        usernameTxt.setVisibility(View.VISIBLE);
        usernameTxt.setText(instagramMedia.user.username + " " + DateUtils.formatDate(instagramMedia.created_time));

        commentsTxt.setText("");
        if (instagramMedia.caption != null)
            commentsTxt.setText(
                    spannableComment(instagramMedia.caption.from.username, instagramMedia.caption.text));
        if (instagramMedia.comments.count > 0)
            for (MediaItem.Comments.Comment comment : instagramMedia.comments.data) {
                commentsTxt.append(
                        spannableComment("\n" + comment.from.username, comment.text));
            }

        Picasso.with(getApplicationContext()).load(instagramMedia.user.profile_picture)
                .transform(new CircleTransform())
                .into(userPhoto);
        if (instagramMedia.type.equals(MediaTypes.VIDEO)) {
            loadVideo();
        }
    }

    private void loadVideo() {
        Uri uri = Uri.parse(instagramMedia.videos.standard_resolution.url);
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

    private void hideMediaInfo() {
        Animations.moveTo(commentsTxt, true);
        Animations.moveTo(userPhoto, false);
        Animations.moveTo(usernameTxt, false, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                userPhoto.setImageDrawable(null);
                usernameTxt.setVisibility(View.GONE);
                commentsTxt.setVisibility(View.GONE);
                zoomOutImage();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        expandedImage.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        videoView.stopPlayback();
    }

    private Spannable spannableComment(String username, String comment) {
        Spannable wordtoSpan =
                new SpannableString(username + " " + comment);
        wordtoSpan.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.accent)),
                0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return wordtoSpan;
    }

    protected int getMarkerPhotoSize() {
        if (markerPhotoSize == 0)
            markerPhotoSize = (int) getResources().getDimension(R.dimen.map_marker_photo);
        return markerPhotoSize;
    }

    private void zoomImageFromThumb(Point startPoint) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        //     thumbView.getGlobalVisibleRect(startBounds);
        startBounds.left = startPoint.x - getMarkerPhotoSize() / 2;
        startBounds.right = startPoint.x + getMarkerPhotoSize() / 2;
        startBounds.top = startPoint.y - getMarkerPhotoSize() / 2;
        startBounds.bottom = startPoint.y + getMarkerPhotoSize() / 2;

        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y + usernameTxt.getHeight());


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
        expandedImage.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
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
        set.setDuration(300);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
                showMediaInfo();
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
        startScaleFinal = startScale;
        expandedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideMediaInfo();
            }
        });
    }

    private void zoomOutImage() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
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
            @Override
            public void onAnimationEnd(Animator animation) {
                //          thumbView.setAlph
                infoLayout.setVisibility(View.INVISIBLE);
                expandedImage.setVisibility(View.INVISIBLE);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //           thumbView.setAlph
                infoLayout.setVisibility(View.INVISIBLE);
                expandedImage.setVisibility(View.INVISIBLE);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }
}
