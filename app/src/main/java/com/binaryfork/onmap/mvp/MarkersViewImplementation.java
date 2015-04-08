package com.binaryfork.onmap.mvp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.network.MediaTypes;
import com.binaryfork.onmap.network.model.MediaItem;
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
    public HashMap<String, MarkerTarget> targets;

    public MarkersViewImplementation(GoogleMap map, Context context) {
        this.map = map;
        this.context = context;
    }

    @Override
    public void showMarkers(MediaResponse mediaResponse) {
        targets = new HashMap<>();
        for (final MediaItem media : mediaResponse.data) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .draggable(true)
                    .anchor(.5f, 1.25f)
                    .position(new LatLng(media.location.latitude, media.location.longitude)));
            MarkerTarget markerTarget = new MarkerTarget(media, marker, context);
            targets.put(marker.getId(), markerTarget);
            Picasso.with(context)
                    .load(media.images.thumbnail.url)
                    .tag(PICASSO_MAP_MARKER_TAG)
                            //    .transform(new CircleTransform())
                    .into(markerTarget);
        }
    }

    public static class MarkerTarget implements Target {

        public MediaItem media;
        public Marker marker;
        public Bitmap thumbBitmap;
        public Context context;
        private int markerPhotoSize;

        public MarkerTarget(MediaItem media, Marker marker, Context context) {
            this.media = media;
            this.marker = marker;
            this.context = context;
            markerPhotoSize = (int) context.getResources().getDimension(R.dimen.map_marker_photo);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, markerPhotoSize, markerPhotoSize, true);
                if (media.type.equals(MediaTypes.VIDEO)) {
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
            d.setBounds(canvas.getClipBounds());
            d.draw(canvas);
            canvas.drawBitmap(bitmap, 0, 0, null);
            return bitmap;
        }
    }
}
