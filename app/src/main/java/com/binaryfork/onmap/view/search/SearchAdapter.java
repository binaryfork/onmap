package com.binaryfork.onmap.view.search;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.presenter.SearchItem;
import com.binaryfork.onmap.util.Theme;
import com.squareup.picasso.Picasso;

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

        if (searchItem.isSection) {
            viewHolder.image.setVisibility(View.GONE);
            return convertView;
        }
        if (searchItem.isList) { // TODO horizontal list of photos ?
            return convertView;
        }
        viewHolder.image.setVisibility(View.VISIBLE);
        int size = (int) activity.getResources().getDimension(R.dimen.search_item_size);
        int icon = searchItem.resId == 0 ? Theme.getPhotoPlaceholderResId() : searchItem.resId;
        viewHolder.image.setImageResource(icon);
        if (searchItem.photoUrl != null) {
            Picasso.with(activity.getApplicationContext())
                    .load(searchItem.photoUrl)
                    .placeholder(Theme.getPhotoPlaceholderResId())
                    .resize(size, size)
                    .into(viewHolder.image);
        }
        return convertView;
    }

    private class ViewHolder {
        public ImageView image;
        public TextView text;

        public ViewHolder(View v) {
            image = (ImageView) v.findViewById(R.id.image);
            text = (TextView) v.findViewById(R.id.title);
        }
    }
}
