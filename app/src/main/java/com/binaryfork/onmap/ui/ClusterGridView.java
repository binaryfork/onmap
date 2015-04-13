package com.binaryfork.onmap.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.google.maps.android.clustering.Cluster;

import java.util.ArrayList;

public class ClusterGridView extends RecyclerView {

    private ClusterAdapter adapter;

    public ClusterGridView(Context context) {
        super(context);
        init();
    }

    public ClusterGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClusterGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        setLayoutManager(manager);
    }

    public void setupData(Cluster<MediaClusterItem> cluster) {
        adapter = new ClusterAdapter((ArrayList<MediaClusterItem>) cluster.getItems());
        setAdapter(adapter);
    }
}
