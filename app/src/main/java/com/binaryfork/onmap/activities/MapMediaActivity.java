package com.binaryfork.onmap.activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.binaryfork.onmap.Intents;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.binaryfork.onmap.mvp.MapMediaView;
import com.binaryfork.onmap.mvp.MediaView;
import com.binaryfork.onmap.mvp.MediaViewImplementation;
import com.binaryfork.onmap.mvp.Presenter;
import com.binaryfork.onmap.network.ApiSource;
import com.binaryfork.onmap.ui.CalendarDialog;
import com.binaryfork.onmap.ui.ClusterGridView;
import com.binaryfork.onmap.ui.LocationSearchBox;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class MapMediaActivity extends AbstractLocationActivity implements
        GoogleMap.OnMapClickListener, MapMediaView {

    @InjectView(R.id.date) TextView dateTxt;
    @InjectView(R.id.searchbox) LocationSearchBox searchBox;
    @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @InjectView(R.id.left_drawer) ListView drawerList;
    @InjectView(R.id.info_layout) View mediaContainerLayout;
    @InjectView(R.id.gridView) ClusterGridView gridView;

    private GoogleMap map;
    private Circle mapCircle;
    private Presenter presenter;
    private MediaView mediaView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        drawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.api_sources)));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApiSource apiSource;
                switch (position) {
                    default:
                    case 0:
                        apiSource = ApiSource.INSTAGRAM;
                        break;
                    case 1:
                        apiSource = ApiSource.FLICKR;
                        break;
                    case 2:
                        apiSource = ApiSource.TWITTER;
                        break;
                }
                presenter.changeSource(apiSource);
                loadMarkers();
                drawerLayout.closeDrawer(drawerList);
            }
        });

        searchBox.setup(this);
        mediaView = new MediaViewImplementation(mediaContainerLayout, getApplicationContext());
        gridView.mediaView = mediaView;

        setupRetainedFragment();

        if (savedInstanceState == null)
            getLocation();
    }

    private void setupRetainedFragment() {
        FragmentManager fm = getSupportFragmentManager();
        MapMediaFragment mapMediaFragment = (MapMediaFragment) fm.findFragmentById(R.id.map);
        mapMediaFragment.setMapMediaView(this);
        presenter = mapMediaFragment.getPresenter();
        if (map == null) {
            map = mapMediaFragment.getMap();
            map.setOnMapClickListener(this);
            map.setMyLocationEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    @Override public void openPhoto(MediaClusterItem clusterTargetItem) {
        Projection projection = map.getProjection();
        LatLng markerLocation = clusterTargetItem.getPosition();
        Point markerPosition = projection.toScreenLocation(markerLocation);
        mediaView.openFromMap(clusterTargetItem, markerPosition);
    }

    @Override public void clickPhotoCluster(Cluster<MediaClusterItem> cluster) {
        showPhotoGrid(cluster);
    }

    private void showPhotoGrid(Cluster<MediaClusterItem> cluster) {
        gridView.setVisibility(View.VISIBLE);
        gridView.setupData(cluster);
    }

    private void hidePhotoGrid() {
        gridView.setVisibility(View.GONE);
    }

    @Override public void onMenuClick() {
        if (drawerLayout.isDrawerOpen(drawerList))
            drawerLayout.closeDrawer(drawerList);
        else
            drawerLayout.openDrawer(drawerList);
    }

    @Override public void showTime(String time) {
        dateTxt.setText(time);
    }

    @Override public void goToLocation(LatLng latLng) {
        if (latLng == null)
            return;
        location = latLng;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 14);
        map.animateCamera(cameraUpdate);
        loadMarkers();
    }

    private void loadMarkers() {
        searchBox.showLoading(true);
        presenter.getMedia(location);
    }

    @Override public void allMarkesLoaded() {
        searchBox.showLoading(false);
    }

    @Override public void showCenterMarker() {
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

    @Override protected void onLocationReceived(LatLng location) {
        Timber.i("location %s", location);
        goToLocation(location);
    }

    @Override public void onMapClick(LatLng latLng) {
        goToLocation(latLng);
    }

    @OnClick(R.id.date) void datePicker() {
        CalendarDialog calendarDialog = new CalendarDialog();
        calendarDialog.onDateChangeListener = new CalendarDialog.OnDateChangeListener() {
            @Override public void onDateChanged(long min, long max) {
                presenter.setTime(min, max);
            }
        };
        calendarDialog.show(getSupportFragmentManager(), "date");
    }

    @OnClick(R.id.username) public void onClickUsername() {
        Intents.openLink(this, mediaView.getMedia().getSiteUrl());
    }

    // Required by SearchBox.
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    @Override public void onBackPressed() {
        if (mediaContainerLayout.isShown()) {
            mediaView.hide();
        } else if (gridView.isShown()) {
            hidePhotoGrid();
        } else {
            super.onBackPressed();
        }
    }
}
