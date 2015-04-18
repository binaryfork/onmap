package com.binaryfork.onmap.ui;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.binaryfork.onmap.mvp.MediaView;
import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.binaryfork.onmap.widget.RecyclerItemClickListener;
import com.google.maps.android.clustering.Cluster;

import java.util.ArrayList;

public class ClusterGridView extends RecyclerView implements RecyclerItemClickListener.OnItemClickListener {

    public MediaView mediaView;
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
        addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
    }

    public void setupData(Cluster<MediaClusterItem> cluster) {
        adapter = new ClusterAdapter((ArrayList<MediaClusterItem>) cluster.getItems());
        setAdapter(adapter);
    }

    @Override
    public void onItemClick(View childView, int position, MotionEvent event) {
        mediaView.openFromGrid(adapter.getItem(position), childView);
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }
}
