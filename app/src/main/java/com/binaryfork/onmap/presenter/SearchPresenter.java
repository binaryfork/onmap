package com.binaryfork.onmap.presenter;

import android.widget.EditText;

import com.binaryfork.onmap.model.Media;
import com.google.android.gms.maps.model.LatLng;

public interface SearchPresenter {

    void suggestGeoLocations(EditText editText);
    Media getFirstSuggestion();
    void loadPopularPlaces(LatLng location);
}
