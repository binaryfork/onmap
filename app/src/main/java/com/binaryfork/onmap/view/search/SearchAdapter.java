package com.binaryfork.onmap.view.search;

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
import com.binaryfork.onmap.presenter.SearchPresenterImplementation.SearchItem;
import com.binaryfork.onmap.util.Theme;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {

    private ArrayList<SearchItem> data = new ArrayList<>();
    private final Activity activity;

    public SearchAdapter(Activity activity) {
        this.activity = activity;
    }

    public void clear() {
        data = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setData(ArrayList<SearchItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override public int getCount() {
        if (data == null)
            return 0;
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
        SearchItem searchItem = data.get(position);
        viewHolder.text.setText(searchItem.text);

        Drawable icon = Theme.getDrawable(searchItem.resId == 0 ? Theme.getPhotoPlaceholderResId() : searchItem.resId);
        viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
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
        if (searchItem.photoUrl != null)
            Picasso.with(activity.getApplicationContext()).load(searchItem.photoUrl)
                    .placeholder(Theme.getPhotoPlaceholderResId())
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
