package com.binaryfork.onmap.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.instagram.model.Media;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

public abstract class MapActivity extends LocationActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    protected GoogleMap map;
    protected HashMap<String, MarkerTarget> targets = new HashMap<>();
    private int markerPhotoSize;

    private Circle mapCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.inject(this);
        setUpMapIfNeeded();
        if (location != null) {
            loadInstagramMedia(location.getLatitude(), location.getLongitude());
        }
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            map.setOnMarkerClickListener(this);
            map.setOnMapClickListener(this);
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        super.onLocationChanged(location);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 12);
        map.animateCamera(cameraUpdate);
        loadInstagramMedia(location.getLatitude(), location.getLongitude());
    }

    private void showCenterMarker() {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mapCircle != null)
            mapCircle.remove();
        mapCircle = map.addCircle(new CircleOptions()
                .center(latLng)
                .radius(2500)
                .strokeWidth(getResources().getDimension(R.dimen.map_circle_stroke))
                .strokeColor(0x663333ff)
                .fillColor(0x113333ff));
        map.addMarker(new MarkerOptions()
                .position(latLng));
    }

    protected void instagramMediaLoaded(List<Media> list) {
        map.clear();
        targets = new HashMap<>();
        for (final Media media : list) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .anchor(.5f, 1.25f)
                    .position(new LatLng(media.location.latitude, media.location.longitude)));
            MarkerTarget markerTarget = new MarkerTarget(media, marker);
            targets.put(marker.getId(), markerTarget);
            Picasso.with(getApplicationContext())
                    .load(media.images.thumbnail.url)
                //    .transform(new CircleTransform())
                    .into(markerTarget);
        }
        showCenterMarker();
    }

    protected int getMarkerPhotoSize() {
        if (markerPhotoSize == 0)
            markerPhotoSize = (int) getResources().getDimension(R.dimen.map_marker_photo);
        return markerPhotoSize;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        map.addMarker(new MarkerOptions()
                .position(latLng));
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        loadInstagramMedia(latLng.latitude, latLng.longitude);
    }

    protected class MarkerTarget implements Target {

        public Media media;
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

}
