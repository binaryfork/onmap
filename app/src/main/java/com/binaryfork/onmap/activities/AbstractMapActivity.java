package com.binaryfork.onmap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.mvp.MarkersViewImplementation;
import com.binaryfork.onmap.mvp.ModelImplementation;
import com.binaryfork.onmap.mvp.PresenterImplementation;
import com.binaryfork.onmap.util.DateUtils;
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
import timber.log.Timber;

public abstract class AbstractMapActivity extends AbstractLocationActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    protected GoogleMap map;

    private Circle mapCircle;

    @InjectView(R.id.date)
    TextView dateTxt;

    @InjectView(R.id.searchbox)
    SearchBox searchBox;

    protected PresenterImplementation presenter;
    protected MarkersViewImplementation view;

    private long maxTimestampSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
        ButterKnife.inject(this);

        ModelImplementation model = new ModelImplementation();
        view = new MarkersViewImplementation(map, getApplicationContext());
        presenter = new PresenterImplementation(model, view, getApplicationContext());

        searchBox.setOnSuggestionClickListener(onSearchSuggestionClick());

        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxTimestampSeconds = DateUtils.weekAgoTime(maxTimestampSeconds);
                dateTxt.setText(DateUtils.getWeekInterval(maxTimestampSeconds));
                setupPhotosOnMap();
            }
        });

        setInstagramIntervalToCurrentTime();

        if (location != null) {
            goToLocation(location);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private void setInstagramIntervalToCurrentTime() {
        maxTimestampSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
        dateTxt.setText(DateUtils.getWeekInterval(maxTimestampSeconds));
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
        Timber.i("location %s", location);
        goToLocation(location);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        goToLocation(latLng);
    }

    private void goToLocation(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        location = latLng;
        setupPhotosOnMap();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 14);
        map.animateCamera(cameraUpdate);
        setInstagramIntervalToCurrentTime();
    }

    private void setupPhotosOnMap() {
        map.clear();
        showCenterMarker();
        presenter.getMediaByLocationAndDate(location, DateUtils.weekAgoTime(maxTimestampSeconds), maxTimestampSeconds);
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

}
