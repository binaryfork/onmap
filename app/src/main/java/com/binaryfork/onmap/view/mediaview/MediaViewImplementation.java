package com.binaryfork.onmap.view.mediaview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.components.widget.SquareVideoView;
import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.rx.Events;
import com.binaryfork.onmap.util.AndroidUtils;
import com.binaryfork.onmap.util.Animations;
import com.binaryfork.onmap.util.CircleTransform;
import com.binaryfork.onmap.util.DateUtils;
import com.binaryfork.onmap.util.Intents;
import com.binaryfork.onmap.util.Spanny;
import com.binaryfork.onmap.view.map.ui.CustomAlignmentSpan;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class MediaViewImplementation implements MediaView {

    private final Context context;
    private Media media;

    public View container;
    @InjectView(R.id.expanded_image) ImageView expandedImage;
    @InjectView(R.id.username) TextView usernameTxt;
    @InjectView(R.id.comments) TextView commentsTxt;
    @InjectView(R.id.user_photo) ImageView userPhoto;
    @InjectView(R.id.videoView) SquareVideoView videoView;
    @InjectView(R.id.buttons) View buttonsLayout;

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
        MediaViewAnimator.get().setup(new MediaViewAnimator.AnimatorListener() {
            @Override public void onFinishOpen() {
                showMediaInfo();
            }

            @Override public void onFinishClose() {
                hideMediaInfo();
            }
        }, expandedImage);
    }

    @Override public Media getMedia() {
        return media;
    }

    @Override public void openFromMap(Media media, Bitmap thumbBitmap, Point markerPoint) {
        this.media = media;
        if (media != null) {
            container.setVisibility(View.VISIBLE);
            expandedImage.setVisibility(View.VISIBLE);
            Drawable d = new BitmapDrawable(context.getResources(), thumbBitmap);
            Picasso.with(context)
                    .load(media.getPhotoUrl())
                    .placeholder(d)
                    .into(expandedImage);
            MediaViewAnimator.get().zoomImageFromPoint(markerPoint);
        }
    }

    @Override public void openFromGrid(final Media media, View thumbView) {
        this.media = media;
        if (media != null) {
            container.setVisibility(View.VISIBLE);
            expandedImage.setVisibility(View.VISIBLE);
            Drawable placeholder = null;
            if (thumbView instanceof ImageView) {
                placeholder = ((ImageView) thumbView).getDrawable();
            }
            Picasso.with(context)
                    .load(media.getPhotoUrl())
                    .placeholder(placeholder)
                    .into(expandedImage);
            MediaViewAnimator.get().zoomImageFromView(thumbView);
        }
    }

    private void showMediaInfo() {
        Animations.moveFromTop(buttonsLayout);
        Animations.moveFromTop(commentsTxt);
        Animations.moveFromBottom(usernameTxt);
        Animations.moveFromBottom(userPhoto);
        userPhoto.setVisibility(View.VISIBLE);
        buttonsLayout.setVisibility(View.VISIBLE);
        commentsTxt.setVisibility(View.VISIBLE);
        usernameTxt.setVisibility(View.VISIBLE);
        Spanny titleSpan = new Spanny(media.getTitle()).append(DateUtils.formatDate(media.getCreatedDate()),
                new CustomAlignmentSpan(Color.LTGRAY, usernameTxt.getWidth(), usernameTxt.getHeight(), AndroidUtils.dp(4)), new RelativeSizeSpan(.8f));
        usernameTxt.setText(titleSpan);
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
                mp.setLooping(true);
                mp.start();
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        expandedImage.setVisibility(View.INVISIBLE);
                        return false;
                    }
                });
            }
        });
    }

    @Override public void hide() {
        if (!container.isShown())
            return;
        Animations.moveTo(buttonsLayout, true);
        Animations.moveTo(commentsTxt, true);
        Animations.moveTo(userPhoto, false);
        Animations.moveTo(usernameTxt, false, new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {
            }

            @Override public void onAnimationEnd(Animation animation) {
                userPhoto.setImageDrawable(null);
                userPhoto.setVisibility(View.GONE);
                usernameTxt.setVisibility(View.GONE);
                commentsTxt.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.GONE);
                MediaViewAnimator.get().zoomOutImage();
            }

            @Override public void onAnimationRepeat(Animation animation) {
            }
        });
        expandedImage.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        //videoView.();
    }

    private void hideMediaInfo() {
        container.setVisibility(View.INVISIBLE);
        expandedImage.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.navigate) public void navigate() {
        hide();
        Events.send(new Events.NavigateToMedia(media));
    }

    @OnClick(R.id.googlemaps) public void googleMaps() {
        Intents.openGoogleMaps(context, media.getLatitude(), media.getLongitude());
    }
}
