package com.binaryfork.onmap.activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.TextView;

import com.binaryfork.onmap.Intents;
import com.binaryfork.onmap.Prefs;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.binaryfork.onmap.mvp.GeoSearchView;
import com.binaryfork.onmap.mvp.LocationState;
import com.binaryfork.onmap.mvp.MapMediaView;
import com.binaryfork.onmap.mvp.MediaView;
import com.binaryfork.onmap.mvp.MediaViewImplementation;
import com.binaryfork.onmap.mvp.Presenter;
import com.binaryfork.onmap.network.Media;
import com.binaryfork.onmap.ui.CalendarDialog;
import com.binaryfork.onmap.ui.ClusterGridView;
import com.binaryfork.onmap.ui.DrawerList;
import com.binaryfork.onmap.ui.RangeSeekBar;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class MapMediaActivity extends AbstractLocationActivity implements
        MapMediaView, LocationState {

    @InjectView(R.id.date) TextView dateTxt;
    @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @InjectView(R.id.left_drawer) DrawerList drawerList;
    @InjectView(R.id.info_layout) View mediaContainerLayout;
    @InjectView(R.id.gridView) ClusterGridView gridView;
    @InjectView(R.id.rangeSeekBar) RangeSeekBar rangeSeekBar;

    private GoogleMap map;
    private Circle mapCircle;
    private Presenter presenter;
    private MediaView mediaView;
    private GeoSearchView geoSearchView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        drawerList.setCallback(new DrawerList.OnDrawerItemClickListener() {
            @Override public void onClick(DrawerList.DrawerItem drawerItem) {
                if (drawerItem.apiSource != null) {
                    presenter.changeSource(drawerItem.apiSource);
                    loadMarkers();
                } else if (drawerItem.mapType != 0) {
                    map.setMapType(drawerItem.mapType);
                } else {
                    startActivity(new Intent(MapMediaActivity.this, MainPreferenceActivity.class));
                }
                drawerLayout.closeDrawer(drawerList);
            }
        });
        rangeSeekBar.setMapMediaView(this);
        mediaView = new MediaViewImplementation(mediaContainerLayout, getApplicationContext());
        gridView.mediaView = mediaView;
        setupRetainedFragment();
        if (savedInstanceState == null) {
            if (getLastLocation() == null)
                // If no previous location was saved get current location of the user.
                setupLocation();
            else
                // Go to last saved location.
                goToLocation(getLastLocation());
        }
    }

    private void setupRetainedFragment() {
        FragmentManager fm = getSupportFragmentManager();
        MapMediaFragment mapMediaFragment = (MapMediaFragment) fm.findFragmentById(R.id.map);
        mapMediaFragment.setMapMediaView(this);
        presenter = mapMediaFragment.getPresenter();
        if (map == null) {
            map = mapMediaFragment.getMap();
            map.setMyLocationEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
        }
        SearchFragment searchFragment = (SearchFragment) fm.findFragmentById(R.id.searchFragment);
        geoSearchView = searchFragment;
        geoSearchView.setMapMediaView(this);
    }

    @Override public void openPhotoFromMap(MediaClusterItem clusterTargetItem) {
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
        saveLastLocation(location);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 14);
        map.animateCamera(cameraUpdate);
        loadMarkers();
    }

    @Override public LatLng getLocation() {
        return location;
    }

    private void loadMarkers() {
        geoSearchView.showProgress(true);
        presenter.getMedia(location);
    }

    @Override public void allMarkesLoaded() {
        geoSearchView.showProgress(false);
    }

    @Override public void showSearchSuggestions() {
    }

    @Override public void setDistance(int distance) {
        presenter.setDistance(distance);
    }

    @Override public void showCenterMarker(int distance) {
        if (mapCircle != null)
            mapCircle.remove();
        mapCircle = map.addCircle(new CircleOptions()
                .center(location)
                .radius(distance)
                .strokeWidth(getResources().getDimension(R.dimen.map_circle_stroke))
                .strokeColor(0x663333ff)
                .fillColor(0x113333ff));
        rangeSeekBar.setMapCircle(mapCircle);
        map.addMarker(new MarkerOptions()
                .position(location));
    }

    @Override protected void onLocationReceived(LatLng location) {
        Timber.i("location %s", location);
        goToLocation(location);
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

    @Override public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerList)) {
            drawerLayout.closeDrawer(drawerList);
        } else if (mediaContainerLayout.isShown()) {
            mediaView.hide();
        } else if (gridView.isShown()) {
            hidePhotoGrid();
        } else if (geoSearchView.isShown()) {
            geoSearchView.hide();
        } else {
            super.onBackPressed();
        }
    }

    @Override public LatLng getLastLocation() {
        return Prefs.isSaveLocation() ? Prefs.getLastLocation() : null;
    }

    @Override public void saveLastLocation(LatLng latLng) {
        Prefs.putLastLocation(latLng);
    }
}
