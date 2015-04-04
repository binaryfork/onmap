package com.binaryfork.onmap.activities;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.mvp.MarkersViewImplementation;
import com.binaryfork.onmap.mvp.Model;
import com.binaryfork.onmap.mvp.ModelImplementation;
import com.binaryfork.onmap.mvp.PresenterImplementation;
import com.binaryfork.onmap.ui.DateUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quinny898.library.persistentsearch.SearchBox;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class AbstractMapActivity extends AbstractLocationActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    protected GoogleMap map;
    private int markerPhotoSize;

    private Circle mapCircle;

    DateUtils dateUtils;

    @InjectView(R.id.date)
    TextView dateTxt;

    @InjectView(R.id.searchbox)
    SearchBox searchBox;

    protected PresenterImplementation presenter;
    protected MarkersViewImplementation view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
        if (location != null && presenter != null) {
            setupPhotosOnMap();
        }
        ButterKnife.inject(this);

        Model model = new ModelImplementation();
        view = new MarkersViewImplementation(map, getApplicationContext());
        presenter = new PresenterImplementation(model, view, getApplicationContext());

        Calendar calendar = Calendar.getInstance();
        dateUtils = new DateUtils(calendar);
        dateTxt.setText(dateUtils.getWeekInterval());
        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateUtils.previousWeek();
                dateTxt.setText(dateUtils.getWeekInterval());
                presenter.onDateChange(location, dateUtils.weekAgoTime(), dateUtils.currentTime());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private void setupPhotosOnMap() {
        presenter.onLocationUpdate(location);
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            map.setOnMarkerClickListener(this);
            map.setOnMapClickListener(this);
            map.setMyLocationEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    @Override
    protected void onLocationReceived(Location location) {
        Log.i("location", "location " + location);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 14);
        map.animateCamera(cameraUpdate);
        setupPhotosOnMap();
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
        setupPhotosOnMap();
        showCenterMarker();
    }

}
