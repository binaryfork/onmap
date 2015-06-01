package com.binaryfork.onmap.view.mediaview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.components.clustering.MediaClusterItem;
import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.util.VideoIconSmallTransformation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

public class ClusterAdapter extends RecyclerView.Adapter<ClusterAdapter.ViewHolder> {
    private ArrayList<MediaClusterItem> data = new ArrayList<>();
    private Context context;

    public ClusterAdapter(ArrayList<MediaClusterItem> items) {
        data.addAll(items);
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
        RequestCreator builder = Picasso.with(context).load(data.get(position).media.getPhotoUrl())
                .placeholder(R.drawable.empty_drwable);
        if (data.get(position).media.isVideo())
            builder = builder.transform(new VideoIconSmallTransformation());
        builder.into(holder.imageView, new Callback() {
            @Override public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override public void onError() {
                holder.progressBar.setVisibility(View.GONE);
            }
        });
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
        public ProgressBar progressBar;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            progressBar = (ProgressBar) v.findViewById(R.id.progress);
        }
    }
}