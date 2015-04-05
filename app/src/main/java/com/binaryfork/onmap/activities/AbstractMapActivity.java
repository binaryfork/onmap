package com.binaryfork.onmap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.mvp.MarkersViewImplementation;
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
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.ArrayList;
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

        ModelImplementation model = new ModelImplementation();
        view = new MarkersViewImplementation(map, getApplicationContext());
        presenter = new PresenterImplementation(model, view, getApplicationContext());

        searchBox.setOnSuggestionClickListener(onSearchSuggestionClick());

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

    private SearchBox.OnSuggestionClick onSearchSuggestionClick() {
        return new SearchBox.OnSuggestionClick() {
            @Override
            public void onSuggestionClick(SearchResult searchResult) {
                goToLocation(new LatLng(searchResult.lat, searchResult.lng));
            }
        };
    }

    // Required by SearchBox.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchBox.populateEditText(matches);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Required by SearchBox.
    public void mic(View v) {
        searchBox.micClick(this);
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
    protected void onLocationReceived(LatLng location) {
        Log.i("location", "location " + location);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 14);
        map.animateCamera(cameraUpdate);
        setupPhotosOnMap();
    }

    private void showCenterMarker() {
        if (mapCircle != null)
            mapCircle.remove();
        mapCircle = map.addCircle(new CircleOptions()
                .center(location)
                .radius(1000)
                .strokeWidth(getResources().getDimension(R.dimen.map_circle_stroke))
                .strokeColor(0x663333ff)
                .fillColor(0x113333ff));
        map.addMarker(new MarkerOptions()
                .position(location));
    }

    protected int getMarkerPhotoSize() {
        if (markerPhotoSize == 0)
            markerPhotoSize = (int) getResources().getDimension(R.dimen.map_marker_photo);
        return markerPhotoSize;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        goToLocation(latLng);
    }

    private void goToLocation(LatLng latLng) {
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(latLng));
        Log.i("", "GoogleApiClient " + location);
        if (location == null) {
            return;
        }
        location = latLng;
        setupPhotosOnMap();
        showCenterMarker();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 14);
        map.animateCamera(cameraUpdate);
    }

}
