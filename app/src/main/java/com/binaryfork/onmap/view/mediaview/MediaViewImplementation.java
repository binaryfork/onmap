package com.binaryfork.onmap.view.mediaview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.components.widget.SquareVideoView;
import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.rx.Events;
import com.binaryfork.onmap.util.Animations;
import com.binaryfork.onmap.util.CircleTransform;
import com.binaryfork.onmap.util.DateUtils;
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
/*        videoView.setup(uri, Color.RED);
        videoView.setFrameVideoViewListener(new FrameVideoViewListener() {
            @Override public void mediaPlayerPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
                Timber.i("video prepared " + mp.isPlaying());
            }
        });*/
        videoView.setVideoURI(uri);
     //   videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                Timber.i("video prepared " + mp.isPlaying());
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override public void onCompletion(MediaPlayer mp) {
                        Timber.i("video prepared44 " + mp.isPlaying());

                    }
                });
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        expandedImage.setVisibility(View.INVISIBLE);
                        Timber.i("video prepared44 " + mp.isPlaying());
                        return false;
                    }
                });
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        Timber.i("video prepared22 " + mp.isPlaying());
                    }
                });
                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        Timber.i("video prepared333 " + mp.isPlaying());
                    }
                });
            }
        });
        videoView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override public void onSystemUiVisibilityChange(int visibility) {
                Timber.i("video prepared55");
            }
        });
        videoView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                Timber.i("video prepared66");
            }
        });
        videoView.post(new Runnable() {
            @Override public void run() {

                Timber.i("video prepared77");
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
}
