package com.binaryfork.onmap.view.map;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.model.GeoSearchModel;
import com.binaryfork.onmap.presenter.MediaMapPresenter;
import com.binaryfork.onmap.presenter.MediaMapPresenterImplementation;
import com.binaryfork.onmap.util.AndroidUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
        getMap().setOnMapClickListener(this);
    }

    public void setMediaMapView(MediaMapView mediaMapView) {
        this.mediaMapView = mediaMapView;
        if (mediaMapPresenter == null)
            mediaMapPresenter = new MediaMapPresenterImplementation();
        mediaMapPresenter.setMediaMapView(mediaMapView);
        mediaMapPresenter.setupClusterer(getActivity().getApplicationContext(), getMap());
        BaseApplication.setMediaMapPresenter(mediaMapPresenter);
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
        String markerTitle = "Tap to load photos";
        if (getMap().getCameraPosition().zoom < 7) {
            String address = GeoSearchModel.addressByLocation(latLng);
            if (!TextUtils.isEmpty(address))
                markerTitle += " in\n" + address;
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .title(markerTitle)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.empty))
                .position(latLng)
                .draggable(true);
        if (searchMarker != null)
            searchMarker.remove();
        searchMarker = getMap().addMarker(markerOptions);
        animateMarker();

        getMap().setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override public void onInfoWindowClick(Marker marker) {
                mediaMapView.goToLocation(latLng);
            }
        });
    }

    private void animateMarker() {
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {
                final float value = (Float) valueAnimator.getAnimatedValue();
                getMap().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override public View getInfoWindow(Marker marker) {
                        View infoWindow = getLayoutInflater(null).inflate(R.layout.info_window_layout, null);
                        View w = infoWindow.findViewById(R.id.window);
                        TextView title = (TextView) infoWindow.findViewById(R.id.title);
                        title.setText(marker.getTitle());
                        w.setY(w.getY() - (AndroidUtils.dp(48) - AndroidUtils.dp(48) * value));
                        w.setAlpha(value);
                        return infoWindow;
                    }

                    @Override public View getInfoContents(Marker marker) {
                        return null;
                    }
                });
                searchMarker.showInfoWindow();
            }
        });
        anim.setInterpolator(new OvershootInterpolator());
        anim.setDuration(400);
        anim.start();
    }

    private void animateMarkerHide() {
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {
                final float value = (Float) valueAnimator.getAnimatedValue();
                getMap().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override public View getInfoWindow(Marker marker) {
                        View infoWindow = getLayoutInflater(null).inflate(R.layout.info_window_layout, null);
                        View w = infoWindow.findViewById(R.id.window);
                        TextView title = (TextView) infoWindow.findViewById(R.id.title);
                        title.setText(marker.getTitle());
                        w.setY(w.getY() - (AndroidUtils.dp(48) - AndroidUtils.dp(48) * value));
                        w.setAlpha(value);
                        return infoWindow;
                    }

                    @Override public View getInfoContents(Marker marker) {
                        return null;
                    }
                });
                searchMarker.showInfoWindow();
            }
        });
        anim.setInterpolator(new OvershootInterpolator());
        anim.setDuration(400);
        anim.start();
    }
}