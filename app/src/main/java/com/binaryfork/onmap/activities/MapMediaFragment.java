package com.binaryfork.onmap.activities;

import android.os.Bundle;
import android.view.View;

import com.binaryfork.onmap.clustering.Clusterer;
import com.binaryfork.onmap.mvp.MapMediaView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapMediaFragment extends SupportMapFragment {

    private Clusterer clusterer;
    private MapMediaView mapMediaView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        getMapAsync(new OnMapReadyCallback() {
            @Override public void onMapReady(GoogleMap googleMap) {
                clusterer = new Clusterer(getActivity().getApplicationContext(), getMap());
                clusterer.init(mapMediaView);
            }
        });
    }


    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setMapMediaView(final MapMediaView mapMediaView) {
        this.mapMediaView = mapMediaView;
        getMapAsync(new OnMapReadyCallback() {
            @Override public void onMapReady(GoogleMap googleMap) {
                clusterer.init(mapMediaView);
            }
        });
    }

    public Clusterer getClusterer() {
        return clusterer;
    }
}
