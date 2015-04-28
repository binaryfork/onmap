package com.binaryfork.onmap.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.mvp.MapMediaView;
import com.binaryfork.onmap.mvp.Presenter;
import com.binaryfork.onmap.mvp.PresenterImplementation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMediaFragment extends SupportMapFragment implements GoogleMap.OnMapClickListener {

    private Presenter presenter;
    private MapMediaView mapMediaView;
    private Marker searchMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

    public void setMapMediaView(MapMediaView mapMediaView) {
        this.mapMediaView = mapMediaView;
        if (presenter == null)
            presenter = new PresenterImplementation();
        presenter.setMapMediaView(mapMediaView);
        presenter.setupClusterer(getActivity().getApplicationContext(), getMap());
    }

    public Presenter getPresenter() {
        return presenter;
    }

    @Override public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
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
                mapMediaView.goToLocation(latLng);
            }
        });
    }
}