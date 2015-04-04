package com.binaryfork.onmap.mvp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.EditText;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.network.model.GeocodeResults;
import com.binaryfork.onmap.network.model.Media;
import com.binaryfork.onmap.network.model.MediaResponse;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;

public class MarkersViewImplementation implements MarkersView {

    private final Context context;
    private final GoogleMap map;
    public EditText searchBox;
    public HashMap<String, MarkerTarget> targets;

    public MarkersViewImplementation(GoogleMap map, Context context) {
        this.map = map;
        this.context = context;
    }

    @Override
    public void showMarkers(MediaResponse mediaResponse) {
        targets = new HashMap<>();
        for (final Media media : mediaResponse.data) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .draggable(true)
                    .anchor(.5f, 1.25f)
                    .position(new LatLng(media.location.latitude, media.location.longitude)));
            MarkerTarget markerTarget = new MarkerTarget(media, marker, context);
            targets.put(marker.getId(), markerTarget);
            Picasso.with(context)
                    .load(media.images.thumbnail.url)
                            //    .transform(new CircleTransform())
                    .into(markerTarget);
        }
    }

    @Override
    public void showSearchSuggestions(GeocodeResults results) {
        if (results != null) {
            if (results.results != null && results.results.size() > 0)
                Log.i("", "geo " + results.results.get(0).formatted_address);
            else
                Log.i("", "geo NOOO");

        }
    }

    public static class MarkerTarget implements Target {

        public Media media;
        public Marker marker;
        public Bitmap thumbBitmap;
        private int markerPhotoSize;

        public MarkerTarget(Media media, Marker marker, Context context) {
            this.media = media;
            this.marker = marker;
            markerPhotoSize = (int) context.getResources().getDimension(R.dimen.map_marker_photo);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, markerPhotoSize, markerPhotoSize, true);
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                thumbBitmap = bitmap;
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }
}
