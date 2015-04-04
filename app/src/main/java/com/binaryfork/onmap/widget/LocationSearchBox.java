package com.binaryfork.onmap.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.quinny898.library.persistentsearch.SearchBox;

public class LocationSearchBox extends SearchBox {
    public LocationSearchBox(Context context) {
        super(context);
    }

    public LocationSearchBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocationSearchBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void search() {
    }

    private void setup() {
        setLogoText("My App");
        setMenuListener(new MenuListener(){

            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
            }

        });
        setSearchListener(new SearchListener() {

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
            }

            @Override
            public void onSearchTermChanged() {
                //React to the search term changing
                //Called after it has updated results
            }

            @Override
            public void onSearch(String searchTerm) {

            }

            @Override
            public void onSearchCleared() {

            }

        });
    }
}
