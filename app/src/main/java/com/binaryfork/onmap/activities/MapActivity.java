package com.binaryfork.onmap.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.binaryfork.onmap.LocationActivity;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.instagram.model.Media;
import com.binaryfork.onmap.instagram.model.MediaResponse;
import com.binaryfork.onmap.rx.MediaHelper;
import com.binaryfork.onmap.ui.DateUtils;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;

public abstract class MapActivity extends LocationActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    protected GoogleMap map;
    protected HashMap<String, MarkerTarget> targets = new HashMap<>();
    private int markerPhotoSize;

    private Circle mapCircle;

    DateUtils dateUtils;

    @InjectView(R.id.date)
    TextView dateTxt;
    @InjectView(R.id.seek_bar_radius)
    SeekBar seekBarRaidus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        if (location != null) {
            getMediaLocationsRx();
        }
        ButterKnife.inject(this);

        Calendar calendar = Calendar.getInstance();
        dateUtils = new DateUtils(calendar);
        dateTxt.setText(dateUtils.getWeekInterval());
/*        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateUtils.previousWeek();
                dateTxt.setText(dateUtils.getWeekInterval());
                loadInstagramMediaByTime(location.getLatitude(), location.getLongitude(),
                        dateUtils.weekAgoTime(), dateUtils.currentTime());
            }
        });*/
        seekBarRaidus.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mapCircle.setRadius(150 + (24 * progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 14);
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

    private void showCenterMarker() {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mapCircle != null)
            mapCircle.remove();
        mapCircle = map.addCircle(new CircleOptions()
                .center(latLng)
                .radius(1000)
                .strokeWidth(getResources().getDimension(R.dimen.map_circle_stroke))
                .strokeColor(0x663333ff)
                .fillColor(0x113333ff));
        map.addMarker(new MarkerOptions()
                .position(latLng));
    }

    protected void setupMarkers(List<Media> list) {
        map.clear();
        targets = new HashMap<>();
        for (final Media media : list) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .draggable(true)
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
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(latLng));
        Log.i("", "GoogleApiClient " + location);
        if (location == null) {
            return;
        }
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        getMediaLocationsRx();
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
