package com.binaryfork.onmap.presenter;

import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public interface SearchPresenter {

    void suggestGeoLocations(EditText editText);
    SearchItem getFirstSuggestion();
    void loadPopularPlaces(LatLng location);
    void addToHistory(SearchItem searchItem);
    void loadPopularPhotos();
}
