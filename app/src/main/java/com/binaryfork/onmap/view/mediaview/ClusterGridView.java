package com.binaryfork.onmap.view.mediaview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.binaryfork.onmap.components.clustering.MediaClusterItem;
import com.binaryfork.onmap.util.Animations;
import com.binaryfork.onmap.components.widget.RecyclerItemClickListener;
import com.google.maps.android.clustering.Cluster;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import timber.log.Timber;

public class ClusterGridView extends RecyclerView implements RecyclerItemClickListener.OnItemClickListener {

    public MediaView mediaView;
    private ClusterAdapter adapter;
    private int cx;
    private int cy;

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
        adapter = new ClusterAdapter();
        setAdapter(adapter);
        setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                hide();
            }
        });
    }

    public void setupData(Cluster<MediaClusterItem> cluster, int x, int y) {
        cx = x;
        cy = y;
        setVisibility(View.VISIBLE);
        adapter.setItems((ArrayList<MediaClusterItem>) cluster.getItems());
        animateOpenGrid();
    }

    public void animateOpenGrid() {
        Timber.i("ani " + getWidth());
        Timber.i("ani " + getHeight());
        int finalRadius = Math.max(getWidth(), getHeight());
        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(this, cx, cy, 0, finalRadius);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300);
        animator.start();
    }

    public void hide() {
        int finalRadius = Math.max(getWidth(), getHeight());
        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(this, cx, cy - 48, finalRadius, 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(Animations.hideListenerSupAnimator(this));
        animator.setDuration(400);
        animator.start();
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(this, "scaleX", 0.7f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", 0.7f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(300);
        set.play(scaleDownX).with(scaleDownY);
    }

    @Override
    public void onItemClick(View childView, int position, MotionEvent event) {
        Timber.i("click " + position);
        mediaView.openFromGrid(adapter.getItem(position), childView);
      //  bringChildToFront(childView);
       // childView.requestFocus();
      //  childView.invalidate();
     //   adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }
}
