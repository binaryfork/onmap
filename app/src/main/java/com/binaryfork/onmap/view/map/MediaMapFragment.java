package com.binaryfork.onmap.view.map;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.presenter.MediaMapPresenter;
import com.binaryfork.onmap.presenter.MediaMapPresenterImplementation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MediaMapFragment extends SupportMapFragment implements GoogleMap.OnMapClickListener {

    private MediaMapPresenter mediaMapPresenter;
    private MediaMapView mediaMapView;
    private Marker searchMarker;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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