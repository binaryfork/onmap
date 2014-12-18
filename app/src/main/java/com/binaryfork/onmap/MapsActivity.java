package com.binaryfork.onmap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.binaryfork.onmap.instagram.InstagramRequest;
import com.binaryfork.onmap.instagram.model.Media;
import com.binaryfork.onmap.widget.SquareImageView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MapsActivity extends LocationActivity implements GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private android.location.Location location;
    private HashMap<String, MarkerTarget> targets = new HashMap<>();
    private int markerPhotoSize;

    @InjectView(R.id.expanded_image) SquareImageView expandedImage;
    @InjectView(R.id.info_layout) View infoLayout;
    @InjectView(R.id.username) TextView usernameTxt;

    private AnimatorSet mCurrentAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.inject(this);
        setUpMapIfNeeded();
        if (location != null) {
            loadInstagramMedia();
        }
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            map.setOnMarkerClickListener(this);
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        super.onLocationChanged(location);
        this.location = location;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13);
        map.animateCamera(cameraUpdate);
        loadInstagramMedia();
    }

    private void loadInstagramMedia() {
        setProgressBarIndeterminateVisibility(true);
        InstagramRequest instagramRequest = new InstagramRequest(location.getLatitude(), location.getLongitude());
        getSpiceManager().execute(instagramRequest,
                instagramRequest.getRequestCacheKey(),
                DurationInMillis.ONE_HOUR,
                new InstagramRequestListener());
    }

    private class InstagramRequestListener implements RequestListener<Media.MediaResponse> {

        @Override
        public void onRequestFailure(SpiceException e) {
            e.printStackTrace();
            Log.e("Instagram", "Failure " + e.getMessage());

        }

        @Override
        public void onRequestSuccess(Media.MediaResponse mediaResponse) {
            Log.e("Instagram", "Success ");
            setupMarkers(mediaResponse.data);
        }
    }

    private void setupMarkers(List<Media> list) {
        targets = new HashMap<>();
        for (final Media media : list) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .anchor(.5f, 0f)
                    .position(new LatLng(media.location.latitude, media.location.longitude)));
            MarkerTarget markerTarget = new MarkerTarget(media, marker);
            targets.put(marker.getId(), markerTarget);
            Picasso.with(getApplicationContext())
                    .load(media.images.thumbnail.url)
                //    .transform(new CircleTransform())
                    .into(markerTarget);
        }
    }

    private int getMarkerPhotoSize() {
        if (markerPhotoSize == 0)
            markerPhotoSize = (int) getResources().getDimension(R.dimen.markerPhotoSize);
        return markerPhotoSize;
    }

    private class MarkerTarget implements Target {

        private Media media;
        public Marker marker;
        public Bitmap thumbBitmap;

        public MarkerTarget(Media media, Marker marker) {
            this.media = media;
            this.marker = marker;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, getMarkerPhotoSize(), getMarkerPhotoSize(), true);
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                thumbBitmap = bitmap;
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        MarkerTarget markerTarget = targets.get(marker.getId());

        Projection projection = map.getProjection();
        LatLng markerLocation = marker.getPosition();
        Point markerPosition = projection.toScreenLocation(markerLocation);

        if (markerTarget != null) {
            Drawable d = new BitmapDrawable(getResources(), markerTarget.thumbBitmap);
            Picasso.with(getApplicationContext())
                    .load(markerTarget.media.images.standard_resolution.url)
                    .placeholder(d)
              //      .transform(new CircleTransform())
                    .into(expandedImage);
            zoomImageFromThumb(markerPosition);

            if (!infoLayout.isShown()) {
                infoLayout.setVisibility(View.VISIBLE);
                usernameTxt.setText(markerTarget.media.user.username);
        //        Picasso.with(getApplicationContext()).load().int
            //    ViewMover.moveToXy(whiteCircle, markerPosition.x - getMarkerPhotoSize() / 2, markerPosition.y - getMarkerPhotoSize() / 2);
              //  Animations.fillScreenWithView(true, whiteCircle);
            } else {
          //      Animations.fillScreenWithView(false, whiteCircle);
            }
        }
        return true;
    }

    private void zoomImageFromThumb(Point startPoint) {
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

        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
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
        expandedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImage,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImage,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(300);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //          thumbView.setAlpha(1f);
                        expandedImage.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        //           thumbView.setAlpha(1f);
                        expandedImage.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
                infoLayout.setVisibility(View.GONE);
              //  Animations.fillScreenWithView(false, whiteCircle);
            }
        });
    }
}
