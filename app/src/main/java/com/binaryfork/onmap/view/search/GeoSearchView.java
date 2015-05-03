package com.binaryfork.onmap.view.search;

import com.binaryfork.onmap.presenter.SearchItem;
import com.binaryfork.onmap.view.map.MediaMapView;

import java.util.ArrayList;

public interface GeoSearchView {

    //
    // MediaMapView callbacks.
    //
    void setMediaMapView(MediaMapView mediaMapView);

    void showProgress(boolean isLoading);

    boolean isShown();

    void hide();

    //
    // Presenter callbacks.
    //

    void showSuggestions(ArrayList<SearchItem> items);
    void showPopularPlaces(ArrayList<SearchItem> items);
}
