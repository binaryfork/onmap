package com.binaryfork.onmap.view.search;

import com.binaryfork.onmap.model.Media;
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

    void showSuggestions(ArrayList<Media> items);
    void showPopularPlaces(ArrayList<Media> items);
}
