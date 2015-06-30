package com.binaryfork.onmap.view.map;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.binaryfork.onmap.MainPreferenceActivity;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.components.clustering.MediaClusterItem;
import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.presenter.MediaMapPresenter;
import com.binaryfork.onmap.presenter.SearchItem;
import com.binaryfork.onmap.rx.Events;
import com.binaryfork.onmap.util.Prefs;
import com.binaryfork.onmap.util.Theme;
import com.binaryfork.onmap.view.map.ui.DrawerList;
import com.binaryfork.onmap.view.mediaview.ClusterGridView;
import com.binaryfork.onmap.view.mediaview.MediaView;
import com.binaryfork.onmap.view.mediaview.MediaViewAnimator;
import com.binaryfork.onmap.view.mediaview.MediaViewImplementation;
import com.binaryfork.onmap.view.place.PlaceActivity;
import com.binaryfork.onmap.view.search.SearchFragment;
import com.binaryfork.onmap.view.search.SearchView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;
import timber.log.Timber;

public class MediaMapActivity extends AbstractLocationActivity implements
        MediaMapView, LocationState {

    @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @InjectView(R.id.left_drawer) DrawerList drawerList;
    @InjectView(R.id.info_layout) View mediaContainerLayout;
    @InjectView(R.id.gridView) ClusterGridView gridView;
    @InjectView(R.id.backgroundView) View backgroundView;
   // @InjectView(R.id.fab) FloatingActionMenu fabMenu; // TODO

    private GoogleMap map;
    private MediaMapPresenter mediaMapPresenter;
    private MediaView mediaView;
    private SearchView searchView;
    private MediaMapFragment mediaMapFragment;
    private int zoom = 14;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Theme.updateActivity(this);
        ButterKnife.inject(this);
        mediaContainerLayout.setPadding(0, Theme.getStatusBarHeight(), 0, 0);
        gridView.setPadding(0, Theme.getStatusBarHeight(), 0, 0);
        drawerList.setCallback(new DrawerList.OnDrawerItemClickListener() {
            @Override public void onClick(DrawerList.DrawerItem drawerItem) {
                if (drawerItem.apiSource != null) {
                    mediaMapPresenter.changeSource(drawerItem.apiSource);
                    loadMarkers();
                } else if (drawerItem.mapType != 0) {
                    map.setMapType(drawerItem.mapType);
                } else {
                    startActivity(new Intent(MediaMapActivity.this, MainPreferenceActivity.class));
                }
                drawerLayout.closeDrawer(drawerList);
            }
        });
        MediaViewAnimator.get().setBgView(backgroundView);
        mediaView = new MediaViewImplementation(findViewById(R.id.container), getApplicationContext());
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

        Events.toObservable().subscribe(new Action1<Object>() {
            @Override public void call(Object o) {
                if (o instanceof Events.NavigateToMedia) {
                    zoom = 18;
                    Media media = ((Events.NavigateToMedia) o).media;
                    mediaMapPresenter.changeSource(media.getApiSource());
                    goToLocation(new LatLng(media.getLatitude(), media.getLongitude()));
                }
            }
        });
/*        fabMenu.setIconAnimated(false);
        fabMenu.setClosedOnTouchOutside(true);
        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override public void onMenuToggle(boolean b) {
                final ObjectAnimator collapseAnimator = ObjectAnimator.ofFloat(
                        fabMenu.getMenuIconView(),
                        "rotation",
                        b ? 0 : -45,
                        b ? -45 : 0
                );
                collapseAnimator.setInterpolator(new OvershootInterpolator());
                collapseAnimator.setDuration(200);
                collapseAnimator.start();
            }
        });*/
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        Theme.clear();
    }

    private void setupRetainedFragment() {
        FragmentManager fm = getSupportFragmentManager();
        mediaMapFragment = (MediaMapFragment) fm.findFragmentById(R.id.map);
        mediaMapFragment.setMediaMapView(this);
        mediaMapPresenter = mediaMapFragment.getMediaMapPresenter();
        if (map == null) {
            map = mediaMapFragment.getMap();
       //     map.setMyLocationEnabled(true);
        }
        if (location == null)
            location = mediaMapPresenter.getLocation();
        searchView = (SearchFragment) fm.findFragmentById(R.id.searchFragment);
        searchView.setMediaMapView(this);
    }

    @Override public void openPhoto(Media media, View view) {
        mediaView.openFromGrid(media, view);
    }

    @Override public void openPhotoFromMap(MediaClusterItem clusterTargetItem) {
        Projection projection = map.getProjection();
        LatLng markerLocation = clusterTargetItem.getPosition();
        Point markerPosition = projection.toScreenLocation(markerLocation);
        mediaView.openFromMap(clusterTargetItem.media, clusterTargetItem.thumbBitmap, markerPosition);
    }

    @Override public void clickPhotoCluster(Cluster<MediaClusterItem> cluster) {
        showPhotoGrid(cluster);
    }

    private void showPhotoGrid(Cluster<MediaClusterItem> cluster) {
        Projection projection = map.getProjection();
        LatLng markerLocation = cluster.getPosition();
        Point markerPosition = projection.toScreenLocation(markerLocation);
        gridView.setupData(cluster, markerPosition.x, markerPosition.y);
    }

    @Override public void onMenuClick() {
        if (drawerLayout.isDrawerOpen(drawerList))
            drawerLayout.closeDrawer(drawerList);
        else
            drawerLayout.openDrawer(drawerList);
    }

    @Override public void showTime(String time) {
        //dateTxt.setText(time);
    }

    @Override public void goToLocation(LatLng latLng) {
        Timber.i("" + latLng);
        if (latLng == null)
            return;
        location = latLng;
        saveLastLocation(location);
        Timber.i("" + latLng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, zoom);
        map.animateCamera(cameraUpdate);
        Timber.i("" + latLng);
        loadMarkers();
        searchView.hide();
        gridView.hide();
        mediaView.hide();
        zoom = 14;
        Timber.i("" + latLng);
    }

    @Override public LatLng getLocation() {
        return location;
    }

    private void loadMarkers() {
        searchView.setHint(mediaMapPresenter.getSource(), location);
        searchView.showProgress(true);
        Timber.i("" + location);
        mediaMapPresenter.loadMedia(location);
    }

    @Override public void allMarkesLoaded() {
        searchView.showProgress(false);
    }

    @Override public void setDistance(int distance) {
        mediaMapPresenter.setDistance(distance);
    }

    @Override public void provideMediaList(ArrayList<SearchItem> loadedMedia) {
        searchView.showPopularPlaces(loadedMedia);
    }

    @Override public void showCenterMarker(int distance) {
        mediaMapFragment.showMapCircle(distance, location);
    }

    @Override protected void onLocationReceived(LatLng location) {
        Timber.i("location %s", location);
        goToLocation(location);
    }


    @Override public void onRandomLocation(LatLng latLng) {
        goToLocation(latLng);
    }

    @Override public void onBackPressed() {
        /*if (fabMenu.isOpened()) {
            fabMenu.close(true);
        } else*/ if (drawerLayout.isDrawerOpen(drawerList)) {
            drawerLayout.closeDrawer(drawerList);
        } else if (mediaContainerLayout.isShown()) {
            mediaView.hide();
        } else if (gridView.isShown()) {
            gridView.hide();
        } else if (searchView.isShown()) {
            searchView.hide();
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

/*    @OnClick(R.id.menu_time) void datePicker() {
        CalendarDialog calendarDialog = new CalendarDialog();
        calendarDialog.onDateChangeListener = new CalendarDialog.OnDateChangeListener() {
            @Override public void onDateChanged(long min, long max) {
                mediaMapPresenter.setTime(min, max);
            }
        };
        calendarDialog.show(getSupportFragmentManager(), "date");
        fabMenu.close(false);
    }

    @OnClick(R.id.zoomIn) public void zoomIn() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, map.getCameraPosition().zoom + 1), 200, null);
    }

    @OnClick(R.id.zoomOut) public void zoomOut() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, map.getCameraPosition().zoom - 1), 200, null);
    }

    @OnClick(R.id.menu_location) public void loc() {
        mediaMapPresenter.randomLocation();
        fabMenu.close(true);
    }

    @OnClick(R.id.menu_random) public void ran() {
        mediaMapPresenter.randomLocation();
        fabMenu.close(true);
    }

    @OnClick(R.id.menu_foursquare) public void four() {
        mediaMapPresenter.changeSource(ApiSource.FOURSQUARE);
        loadMarkers();
        fabMenu.close(true);
    }

    @OnClick(R.id.menu_flickr) public void fli() {
        mediaMapPresenter.changeSource(ApiSource.FLICKR);
        loadMarkers();
        fabMenu.close(true);
    }*/

    @OnClick(R.id.fab) public void inst() {
        /*mediaMapPresenter.changeSource(ApiSource.INSTAGRAM);
        loadMarkers();
        fabMenu.close(true);*/
        Intent intent = new Intent(this, PlaceActivity.class);
        intent.putExtra(PlaceActivity.ARG_LOCATION, new double[]{location.latitude, location.longitude});
        startActivity(intent);
    }
}