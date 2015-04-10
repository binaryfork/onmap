package com.binaryfork.onmap.mvp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.network.Media;
import com.binaryfork.onmap.network.MediaList;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;

import timber.log.Timber;

public class MarkersViewImplementation implements MarkersView {

    private final static String PICASSO_MAP_MARKER_TAG = "marker";

    private final Context context;
    private final GoogleMap map;
    private Circle mapCircle;
    private HashMap<String, MarkerTarget> targets;
    public LatLng location;

    public MarkersViewImplementation(GoogleMap map, Context context) {
        this.map = map;
        this.context = context;
    }

    public Media getMedia(String markerId) {
        return targets.get(markerId).media;
    }

    public Bitmap getMarkerPhoto(String markerId) {
        return targets.get(markerId).thumbBitmap;
    }

    @Override
    public void showCenterMarker() {
        if (mapCircle != null)
            mapCircle.remove();
        mapCircle = map.addCircle(new CircleOptions()
                .center(location)
                .radius(1000)
                .strokeWidth(context.getResources().getDimension(R.dimen.map_circle_stroke))
                .strokeColor(0x663333ff)
                .fillColor(0x113333ff));
        map.addMarker(new MarkerOptions()
                .position(location));
    }

    @Override
    public void showMarkers(MediaList mediaResponse) {
        // Cancel all loading map photos because all markers will be cleared.
        Picasso.with(context).cancelTag(PICASSO_MAP_MARKER_TAG);
        map.clear();
        showCenterMarker();
        targets = new HashMap<>();
        Timber.i("map %s", targets.size());
        for (final Media media : mediaResponse.getList()) {
         //   if (media.type.equals(MediaTypes.IMAGE))
         //       continue;
            Marker marker = map.addMarker(new MarkerOptions()
                    .draggable(true)
                    .anchor(.5f, 1.25f)
                    .position(new LatLng(media.getLatitude(), media.getLongitude())));
            MarkerTarget markerTarget = new MarkerTarget(media, marker, context);
            targets.put(marker.getId(), markerTarget);
            Timber.i("map th %s", media.getThumbnail());
            Picasso.with(context)
                    .load(media.getThumbnail())
                    .tag(PICASSO_MAP_MARKER_TAG)
                            //    .transform(new CircleTransform())
                    .into(markerTarget);
        }
    }

    private static class MarkerTarget implements Target {

        public Media media;
        public Marker marker;
        public Bitmap thumbBitmap;
        public Context context;
        private int markerPhotoSize;

        public MarkerTarget(Media media, Marker marker, Context context) {
            this.media = media;
            this.marker = marker;
            this.context = context;
            markerPhotoSize = (int) context.getResources().getDimension(R.dimen.map_marker_photo);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, markerPhotoSize, markerPhotoSize, true);
                if (media.isVideo()) {
                    bitmap = drawVideoIcon(bitmap);
                }
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

        private Bitmap drawVideoIcon(Bitmap bitmap) {
            Canvas canvas = new Canvas(bitmap);
            Drawable d = context.getResources().getDrawable(android.R.drawable.ic_media_play);
            if (d == null)
                return null;
            d.setBounds(canvas.getClipBounds());
            d.draw(canvas);
            canvas.drawBitmap(bitmap, 0, 0, null);
            return bitmap;
        }
    }
}
