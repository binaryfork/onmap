package com.binaryfork.onmap.clustering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;

public class Clusterer {

    private Context context;
    public GoogleMap map;
    private ClusterManager<MediaClusterItem> clusterManager;
    private int markerDimension;

    public Clusterer(Context context, GoogleMap googleMap) {
        this.context = context;
        this.map = googleMap;
        clusterManager = new ClusterManager<>(context, map);
    }

    public void init(final MapMediaView mapMediaView) {
        markerDimension = (int) context.getResources().getDimension(R.dimen.map_marker_photo);
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
                if (map.getMaxZoomLevel() == map.getCameraPosition().zoom
                        || bounds.southwest.equals(bounds.northeast)) {
                    mapMediaView.clickPhotoCluster(cluster);
                } else {
                    // map.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), map
                    //         .getCameraPosition().zoom + 1));
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                }
                return true;
            }
        });
        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
    }

    public void clearItems() {
        clusterManager.clearItems();
        map.clear();
    }

    public void createCluster(Media media, Bitmap bitmap) {
        MediaClusterItem cluster = new MediaClusterItem(media, bitmap);
        clusterManager.addItem(cluster);
        clusterManager.cluster();
    }


    private class MediaRenderer extends DefaultClusterRenderer<MediaClusterItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(context);
        private final IconGenerator mClusterIconGenerator = new IconGenerator(context);
        private final ImageView mImageView;
        private final ImageView mClusterImageView;

        public MediaRenderer() {
            super(context, map, clusterManager);

            View multiProfile = LayoutInflater.from(context).inflate(R.layout.map_marker_cluster, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(context);
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
