package com.binaryfork.onmap.view.search;

import com.binaryfork.onmap.model.ApiSource;
import com.binaryfork.onmap.presenter.SearchItem;
import com.binaryfork.onmap.view.map.MediaMapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface SearchView {

    //
    // MediaMapView callbacks.
    //
    void setMediaMapView(MediaMapView mediaMapView);

    void showProgress(boolean isLoading);

    void setHint(ApiSource source, LatLng location);

    boolean isShown();

    void show();

    void hide();

    //
    // Presenter callbacks.
    //

    void showSuggestions(ArrayList<SearchItem> items);
    void showPopularPlaces(ArrayList<SearchItem> items);

}
