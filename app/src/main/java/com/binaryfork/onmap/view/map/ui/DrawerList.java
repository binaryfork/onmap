package com.binaryfork.onmap.view.map.ui;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.model.ApiSource;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class DrawerList extends ListView implements AdapterView.OnItemClickListener {

    private OnDrawerItemClickListener onDrawerItemClickListener;
    private DrawerAdapter drawerAdapter;

    public DrawerList(Context context) {
        super(context);
        init();
    }

    public DrawerList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawerList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ArrayList<DrawerItem> items = new ArrayList<>();
        items.add(new DrawerItem(ApiSource.INSTAGRAM));
        items.add(new DrawerItem(ApiSource.FOURSQUARE));
        items.add(new DrawerItem(ApiSource.TWITTER));
        items.add(new DrawerItem(ApiSource.FLICKR));
        items.add(new DrawerItem(getContext().getString(R.string.satellite),
                GoogleMap.MAP_TYPE_HYBRID, android.R.drawable.ic_dialog_map));
        items.add(new DrawerItem(getContext().getString(R.string.terrain),
                GoogleMap.MAP_TYPE_TERRAIN, android.R.drawable.ic_dialog_map));
        items.add(new DrawerItem(getContext().getString(R.string.prefs)));
        drawerAdapter = new DrawerAdapter(items);
        setAdapter(drawerAdapter);
        setOnItemClickListener(this);
    }

    public void setCallback(OnDrawerItemClickListener onDrawerItemClickListener) {
        this.onDrawerItemClickListener = onDrawerItemClickListener;
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onDrawerItemClickListener.onClick((DrawerItem) drawerAdapter.getItem(position));
    }

    public class DrawerItem {
        @DrawableRes public int resource;
        public String title;
        public ApiSource apiSource;
        public int mapType;

        public DrawerItem(String title) {
            this.title = title;
        }

        public DrawerItem(String title, @DrawableRes int drawableResource) {
            this.title = title;
            this.resource = drawableResource;
        }
        public DrawerItem(String title, int mapType, @DrawableRes int drawableResource) {
            this.title = title;
            this.mapType = mapType;
            this.resource = drawableResource;
        }

        public DrawerItem(ApiSource apiSource) {
            this.title = apiSource.getString(getContext());
            this.apiSource = apiSource;
        }
    }

    private class DrawerAdapter extends BaseAdapter {

        private ArrayList<DrawerItem> data;

        public DrawerAdapter(ArrayList<DrawerItem> data) {
            this.data = data;
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
            TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.drawer_item, null);
            view.setText(data.get(position).title);
            view.setCompoundDrawablesWithIntrinsicBounds(data.get(position).resource, 0, 0, 0);
            return view;
        }
    }

    public interface OnDrawerItemClickListener {
        void onClick(DrawerItem drawerItem);
    }
}
