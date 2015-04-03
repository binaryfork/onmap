package com.binaryfork.onmap.activities;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.binaryfork.onmap.LocationActivity;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.mvp.Model;
import com.binaryfork.onmap.mvp.ModelImplementation;
import com.binaryfork.onmap.mvp.PresenterImplementation;
import com.binaryfork.onmap.mvp.ViewImplementation;
import com.binaryfork.onmap.ui.DateUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class MapActivity extends LocationActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    protected GoogleMap map;
    private int markerPhotoSize;

    private Circle mapCircle;

    DateUtils dateUtils;

    @InjectView(R.id.date)
    TextView dateTxt;
    @InjectView(R.id.seek_bar_radius)
    SeekBar seekBarRaidus;

    protected PresenterImplementation presenter;
    protected ViewImplementation view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        if (location != null && presenter != null) {
            updateRx();
        }
        ButterKnife.inject(this);

        Model model = new ModelImplementation();
        view = new ViewImplementation(map, getApplicationContext());
        presenter = new PresenterImplementation(model, view, getApplicationContext());

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

    private void updateRx() {
        presenter.onLocationUpdate(location);
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
        updateRx();
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
        updateRx();
    }

}
