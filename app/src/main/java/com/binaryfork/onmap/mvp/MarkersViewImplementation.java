package com.binaryfork.onmap.mvp;

import android.app.Activity;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.clustering.Clusterer;
import com.binaryfork.onmap.network.Media;
import com.binaryfork.onmap.network.MediaList;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class MarkersViewImplementation implements MarkersView {

    private final static String PICASSO_MAP_MARKER_TAG = "marker";

    private final Activity activity;
    private final GoogleMap map;
    private Circle mapCircle;
    private Clusterer clusterer;
    private LatLng location;

    public MarkersViewImplementation(GoogleMap map, Activity activity, MapMediaView mapMediaView) {
        this.map = map;
        this.activity = activity;
        clusterer = new Clusterer(activity, map, mapMediaView);
    }

    @Override public void setLocation(LatLng location) {
        this.location = location;
    }

    @Override public void showCenterMarker() {
        if (mapCircle != null)
            mapCircle.remove();
        mapCircle = map.addCircle(new CircleOptions()
                .center(location)
                .radius(1000)
                .strokeWidth(activity.getResources().getDimension(R.dimen.map_circle_stroke))
                .strokeColor(0x663333ff)
                .fillColor(0x113333ff));
        map.addMarker(new MarkerOptions()
                .position(location));
    }

    @Override public void showMarkers(final MediaList mediaResponse) {
        // Cancel all loading map photos because all markers will be cleared.
        Picasso.with(activity).cancelTag(PICASSO_MAP_MARKER_TAG);
        clusterer.clearItems();
        map.clear();
        showCenterMarker();

        for (final Media media : mediaResponse.getList()) {
            // Add a cluster item and run cluster manager each time when photo bitmap is loaded.
            Picasso.with(activity)
                    .load(media.getThumbnail())
                    .tag(PICASSO_MAP_MARKER_TAG)
                    .into(clusterer.getClusterItemTarget(media));
        }

    }
}
