package com.binaryfork.onmap.presenter;

import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public interface SearchPresenter {

    void suggestGeoLocations(EditText editText);
    SearchPresenterImplementation.SearchItem getFirstSuggestion();
    void loadPopularPlaces(LatLng location);
    void addToHistory(SearchPresenterImplementation.SearchItem searchItem);
}
