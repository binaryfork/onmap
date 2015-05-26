package com.binaryfork.onmap.view.search;

import com.binaryfork.onmap.presenter.SearchItem;
import com.binaryfork.onmap.view.map.MediaMapView;

import java.util.ArrayList;

public interface SearchView {

    //
    // MediaMapView callbacks.
    //
    void setMediaMapView(MediaMapView mediaMapView);

    void showProgress(boolean isLoading);

    boolean isShown();

    void show();

    void hide();

    //
    // Presenter callbacks.
    //

    void showSuggestions(ArrayList<SearchItem> items);
    void showPopularPlaces(ArrayList<SearchItem> items);
}
