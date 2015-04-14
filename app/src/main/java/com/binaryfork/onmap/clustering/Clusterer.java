package com.binaryfork.onmap.clustering;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.mvp.MapMediaView;
import com.binaryfork.onmap.network.Media;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

public class Clusterer {

    private final Activity activity;
    private final GoogleMap map;
    private ClusterManager<MediaClusterItem> clusterManager;
    private Drawable videoIcon;
    private int markerDimension;
    private HashSet<Target> targets = new HashSet<>();

    public Clusterer(Activity activity, GoogleMap googleMap, final MapMediaView mapMediaView) {
        this.activity = activity;
        this.map = googleMap;

        markerDimension = (int) activity.getResources().getDimension(R.dimen.map_marker_photo);
        videoIcon = activity.getResources().getDrawable(android.R.drawable.ic_media_play);

        clusterManager = new ClusterManager<>(activity, map);
        clusterManager.setRenderer(new MediaRenderer());
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MediaClusterItem>() {
            @Override
            public boolean onClusterItemClick(MediaClusterItem mediaClusterItem) {
                mapMediaView.openPhoto(mediaClusterItem);
                return true;
            }
        });
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MediaClusterItem>() {
            @Override
            public boolean onClusterClick(Cluster<MediaClusterItem> cluster) {
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (ClusterItem item : cluster.getItems()) {
                    builder.include(item.getPosition());
                }
                final LatLngBounds bounds = builder.build();
                Timber.i("Bounds %s", bounds.northeast);
                Timber.i("Bounds %s", bounds.southwest);
                if (bounds.southwest.equals(bounds.northeast)) {
                    mapMediaView.clickPhotoCluster(cluster);
                } else {
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 400));
                }
                return true;
            }
        });
        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
    }

    public void clearItems() {
        clusterManager.clearItems();
        targets.clear();
    }

    public Target getClusterItemTarget(final Media media) {
        final MediaClusterItem cluster = new MediaClusterItem(media, activity);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (bitmap != null) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, markerDimension, markerDimension, true);
                    if (media.isVideo()) {
                        bitmap = drawVideoIcon(bitmap);
                    }
                    cluster.thumbBitmap = bitmap;
                    clusterManager.addItem(cluster);
                    clusterManager.cluster();
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
                if (videoIcon == null)
                    return null;
                videoIcon.setBounds(canvas.getClipBounds());
                videoIcon.draw(canvas);
                canvas.drawBitmap(bitmap, 0, 0, null);
                return bitmap;
            }
        };
        targets.add(target);
        return target;
    }


    private class MediaRenderer extends DefaultClusterRenderer<MediaClusterItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(activity);
        private final IconGenerator mClusterIconGenerator = new IconGenerator(activity);
        private final ImageView mImageView;
        private final ImageView mClusterImageView;

        public MediaRenderer() {
            super(activity, map, clusterManager);

            View multiProfile = activity.getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(activity);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(markerDimension, markerDimension));
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(MediaClusterItem item, MarkerOptions markerOptions) {
            mImageView.setImageBitmap(item.thumbBitmap);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MediaClusterItem> cluster, MarkerOptions markerOptions) {
            List<Drawable> drawables = new ArrayList<>(Math.min(4, cluster.getSize()));
            int width = markerDimension;
            int height = markerDimension;

            for (MediaClusterItem item : cluster.getItems()) {
                // Draw 4 at most.
                if (drawables.size() == 4) break;
                BitmapDrawable drawable = new BitmapDrawable(item.thumbBitmap);
                drawable.setBounds(0, 0, width, height);
                drawables.add(drawable);
            }

            MultiDrawable multiDrawable = new MultiDrawable(drawables);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);

            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(com.google.maps.android.clustering.Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }
}
