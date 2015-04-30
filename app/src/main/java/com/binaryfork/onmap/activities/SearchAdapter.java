package com.binaryfork.onmap.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.Theme;
import com.binaryfork.onmap.network.Media;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {

    private ArrayList<Media> data;
    private final Activity activity;

    public SearchAdapter(ArrayList<Media> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    @Override public int getCount() {
        return data.size();
    }

    @Override public Object getItem(int position) {
        return data.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.search_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(R.layout.search_item, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.layout.search_item);
        }
        Media media = data.get(position);
        viewHolder.text.setText(media.getComments());
        viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(Theme.getPhotoPlaceholder(activity), 0, 0, 0);

        Target target = new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(activity.getResources(), bitmap);
                viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(bitmapDrawable, null, null, null);
            }

            @Override public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        convertView.setTag(R.id.title, target);
        if (media.getThumbnail() != null)
            Picasso.with(activity.getApplicationContext()).load(media.getThumbnail())
                    .placeholder(Theme.getPhotoPlaceholder(activity))
                    .into(target);
        return convertView;
    }

    private class ViewHolder {
        public TextView text;

        public ViewHolder(View v) {
            text = (TextView) v;
        }
    }
}
