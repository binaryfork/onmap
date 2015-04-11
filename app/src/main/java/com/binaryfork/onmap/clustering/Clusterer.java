package com.binaryfork.onmap.clustering;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.binaryfork.onmap.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class Clusterer {

    private final Activity activity;
    private final GoogleMap map;
    private ClusterManager<ClusterTargetItem> clusterManager;

    public Clusterer(Activity activity, GoogleMap map,
                     ClusterManager.OnClusterItemClickListener<ClusterTargetItem> listener) {
        this.activity = activity;
        this.map = map;
        init(listener);
    }

    private void init(ClusterManager.OnClusterItemClickListener<ClusterTargetItem> listener) {
        clusterManager = new ClusterManager<ClusterTargetItem>(activity, map);
        clusterManager.setRenderer(new MediaRenderer());
        clusterManager.setOnClusterItemClickListener(listener);
        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
    }

    public void addItem(ClusterTargetItem item) {
        clusterManager.addItem(item);
    }

    public void clearItems() {
        clusterManager.clearItems();
    }

    private class MediaRenderer extends DefaultClusterRenderer<ClusterTargetItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(activity);
        private final IconGenerator mClusterIconGenerator = new IconGenerator(activity);
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public MediaRenderer() {
            super(activity, map, clusterManager);

            View multiProfile = activity.getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(activity);
            mDimension = (int) activity.getResources().getDimension(R.dimen.map_marker_photo);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(ClusterTargetItem item, MarkerOptions markerOptions) {
            mImageView.setImageBitmap(item.thumbBitmap);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<ClusterTargetItem> cluster, MarkerOptions markerOptions) {
            List<Drawable> drawables = new ArrayList<>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (ClusterTargetItem item : cluster.getItems()) {
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
