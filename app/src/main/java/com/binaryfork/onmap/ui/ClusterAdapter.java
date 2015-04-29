package com.binaryfork.onmap.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.binaryfork.onmap.network.Media;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ClusterAdapter extends RecyclerView.Adapter<ClusterAdapter.ViewHolder> {
    private ArrayList<MediaClusterItem> data;
    private Context context;

    public ClusterAdapter(ArrayList<MediaClusterItem> data) {
        this.data = data;
    }

    @Override
    public ClusterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(context).load(data.get(position).media.getPhotoUrl())
                .placeholder(R.drawable.ic_launcher)
                .into(holder.imageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }

    public Media getItem(int position) {
        return data.get(position).media;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v;
        }
    }
}