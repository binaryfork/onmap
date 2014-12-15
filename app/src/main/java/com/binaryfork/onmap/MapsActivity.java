package com.binaryfork.onmap;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.binaryfork.onmap.instagram.InstagramRequest;
import com.binaryfork.onmap.instagram.model.Media;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends LocationActivity{

    private GoogleMap map;
    private android.location.Location location;
    private ArrayList<MarkerTarget> targets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        if (location != null) {
            loadInstagramMedia();
        }
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
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
                DurationInMillis.ONE_MINUTE,
                new InstagramRequestListener());
    }

    private class InstagramRequestListener implements RequestListener<Media.MediaResponse> {

        @Override
        public void onRequestFailure(SpiceException e) {
        }

        @Override
        public void onRequestSuccess(Media.MediaResponse mediaResponse) {
            setupMarkers(mediaResponse.data);
        }
    }

    private void setupMarkers(List<Media> list) {
        targets = new ArrayList<>();
        for (final Media media : list) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(media.location.latitude, media.location.longitude)));
            MarkerTarget markerTarget = new MarkerTarget(media);
            targets.add(markerTarget);
            Picasso.with(getApplicationContext()).load(media.images.thumbnail.url).into(markerTarget);
        }
    }

    private class MarkerTarget implements Target {

        private Media media;

        public MarkerTarget(Media media) {
            this.media = media;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(media.location.latitude, media.location.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }
}
