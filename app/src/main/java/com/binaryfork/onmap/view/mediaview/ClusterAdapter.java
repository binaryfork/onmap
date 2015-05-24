package com.binaryfork.onmap.view.mediaview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.clustering.MediaClusterItem;
import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.util.VideoIconSmallTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

public class ClusterAdapter extends RecyclerView.Adapter<ClusterAdapter.ViewHolder> {
    private ArrayList<MediaClusterItem> data;
    private Context context;

    public void setItems(ArrayList<MediaClusterItem> data) {
        this.data = data;
        notifyDataSetChanged();
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
        RequestCreator builder = Picasso.with(context).load(data.get(position).media.getPhotoUrl())
                .placeholder(R.drawable.empty_drwable);
        if (data.get(position).media.isVideo())
            builder = builder.transform(new VideoIconSmallTransformation());
        builder.into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;
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