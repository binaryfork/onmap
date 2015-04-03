package com.binaryfork.onmap;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.binaryfork.onmap.instagram.Instagram;
import com.binaryfork.onmap.instagram.model.Media;
import com.binaryfork.onmap.instagram.model.MediaResponse;
import com.binaryfork.onmap.widget.ExpandingImage;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscriber;

public class MapsActivity extends LocationActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap map;
    private android.location.Location location;
    private HashMap<String, MarkerTarget> targets;
    private int markerPhotoSize;

    @InjectView(R.id.expanded_image) ExpandingImage expandedImage;
    @InjectView(R.id.info_layout) View infoLayout;
    @InjectView(R.id.username) TextView usernameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.inject(this);
        setUpMapIfNeeded();
        if (location != null) {
            getMediaLocationsRx();
        }
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            map.setOnMarkerClickListener(this);
            map.setOnMapClickListener(this);
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onLocationReceived(Location location) {
        Log.i("location", "location " + location);
        this.location = location;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13);
        map.animateCamera(cameraUpdate);
        getMediaLocationsRx();
    }

    private void getMediaLocationsRx() {
        Log.i("mapp", "WWW start");
        Subscriber<MediaResponse> subscriber = new Subscriber<MediaResponse>() {
            @Override
            public void onNext(MediaResponse mediaResponse) {
                Log.i("mapp", "WWW " + mediaResponse.data);
                setupMarkers(mediaResponse.data);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }
        };
        MediaHelper.getInstagramMedia(getApplicationContext(), location, subscriber);
    }

    private void getMediaLocations() {
        Instagram.getInstance(getApplicationContext())
                .mediaService()
                .mediaSearch(location.getLatitude(), location.getLongitude(), new Callback<MediaResponse>() {
            @Override
            public void success(MediaResponse mediaResponse, Response response) {
                Log.i("mapp", "WWW " + response.getUrl());
                setupMarkers(mediaResponse.data);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("", "WWW " + error.toString());
                Log.e("", "WWW " + error.getUrl());
            }
        });
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

    @Override
    public void onMapClick(LatLng latLng) {
        map.addMarker(new MarkerOptions()
                .position(latLng));
        if (location == null) {
            return;
        }
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        map.clear();
        getMediaLocationsRx();
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
            expandedImage.zoomImageFromThumb(markerPosition);

            if (!infoLayout.isShown()) {
                infoLayout.setVisibility(View.GONE);
                usernameTxt.setText(markerTarget.media.user.username);
        //        Picasso.with(getApplicationContext()).load().int
            //    ViewMover.moveToXy(whiteCircle, markerPosition.x - getMarkerPhotoSize() / 2, markerPosition.y - getMarkerPhotoSize() / 2);
              //  Animations.fillScreenWithView(true, whiteCircle);
            } else {
                infoLayout.setVisibility(View.GONE);
          //      Animations.fillScreenWithView(false, whiteCircle);
            }
        }
        return true;
    }
}
