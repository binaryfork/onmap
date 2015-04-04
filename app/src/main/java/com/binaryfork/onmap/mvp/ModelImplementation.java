package com.binaryfork.onmap.mvp;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;

import com.binaryfork.onmap.network.GoogleGeo;
import com.binaryfork.onmap.network.Instagram;
import com.binaryfork.onmap.network.model.GeocodeResults;
import com.binaryfork.onmap.network.model.MediaResponse;

import rx.Observable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Func1;

public class ModelImplementation implements Model {

    public EditText searchBox;

    @Override
    public Observable<MediaResponse> loadMediaByLocation(Context context, Location location) {
        return Instagram.getInstance(context)
                .mediaService()
                .mediaSearch(location.getLatitude(), location.getLongitude());
    }

    @Override
    public Observable<MediaResponse> loadMediaByLocationAndDate(Context context, Location location, long from, long to) {
        return Instagram.getInstance(context)
                .mediaService()
                .mediaSearch(location.getLatitude(), location.getLongitude(), from, to);
    }

    @Nullable
    @Override
    public Observable<GeocodeResults> suggestLocations(Context context, String query) {
        Log.i("", (query == null || query.length() < 3) + " type " + query);
        // Do not search for geocode suggestions with a short query.
        /*if (query == null || query.length() < 3)
            return null;*/
        return GoogleGeo.getInstance(context)
                .geo()
                .mediaSearch(query);
    }

    @Override
    public Observable<String> onSearchTextChanged() {
        return WidgetObservable
                .text(searchBox)
                .map(new Func1<OnTextChangeEvent, String>() {
                    @Override
                    public String call(OnTextChangeEvent event) {
                        Log.i("", "OnTextChangeEvent " + event.text().toString().trim());
                        return event.text().toString().trim();
                    }
                });
    }
}
