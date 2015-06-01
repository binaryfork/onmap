package com.binaryfork.onmap.view.map;

import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.presenter.MediaMapPresenter;
import com.binaryfork.onmap.presenter.MediaMapPresenterImplementation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import timber.log.Timber;

public class MediaMapFragment extends SupportMapFragment implements GoogleMap.OnMapClickListener {

    private MediaMapPresenter mediaMapPresenter;
    private MediaMapView mediaMapView;
    private Marker searchMarker;
    private Circle mapCircle;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    //    getMap().getUiSettings().setZoomControlsEnabled(true);
        getMap().setOnMapClickListener(this);
        getMap().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override public View getInfoWindow(Marker marker) {
                View infoWindow = getLayoutInflater(null).inflate(R.layout.info_window_layout, null);
                TextView title = (TextView) infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());
                return infoWindow;
            }

            @Override public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater(null).inflate(R.layout.info_window_layout, null);
                TextView title = (TextView) infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());
                return null;
            }
        });
    }

    public void setMediaMapView(MediaMapView mediaMapView) {
        this.mediaMapView = mediaMapView;
        if (mediaMapPresenter == null)
            mediaMapPresenter = new MediaMapPresenterImplementation();
        mediaMapPresenter.setMediaMapView(mediaMapView);
        mediaMapPresenter.setupClusterer(getActivity().getApplicationContext(), getMap());
    }

    public MediaMapPresenter getMediaMapPresenter() {
        return mediaMapPresenter;
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mediaMapPresenter.onDestroy();
    }

    public Circle getMapCircle() {
        return mapCircle;
    }

    public void showMapCircle(final int distance, LatLng location) {
        if (mapCircle != null)
            mapCircle.remove();
        mapCircle = getMap().addCircle(new CircleOptions()
                .center(location)
                .strokeWidth(getResources().getDimension(R.dimen.map_circle_stroke))
                .strokeColor(0x663333ff)
                .fillColor(0x113333ff));

        ValueAnimator vAnimator = new ValueAnimator();
        vAnimator.setIntValues((int) mapCircle.getRadius(), distance);
        vAnimator.setDuration(500);
        vAnimator.setEvaluator(new IntEvaluator());
        vAnimator.setInterpolator(new DecelerateInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction() * distance;
                Timber.i("af " + animatedFraction);
                mapCircle.setRadius(animatedFraction);
            }
        });
        vAnimator.start();
    }

    @Override public void onMapClick(final LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions()
                .title("Tap to load photos")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.empty))
                .position(latLng)
                .draggable(true);
        if (searchMarker != null)
            searchMarker.remove();
        searchMarker = getMap().addMarker(markerOptions);
        searchMarker.showInfoWindow();

        getMap().setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override public void onInfoWindowClick(Marker marker) {
                mediaMapView.goToLocation(latLng);
            }
        });
    }
}