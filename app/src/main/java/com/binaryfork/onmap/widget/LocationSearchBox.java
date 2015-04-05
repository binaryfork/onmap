package com.binaryfork.onmap.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.binaryfork.onmap.network.GoogleGeo;
import com.binaryfork.onmap.network.model.GeocodeItem;
import com.binaryfork.onmap.network.model.GeocodeResults;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;

public class LocationSearchBox extends SearchBox {

    public LocationSearchBox(Context context) {
        super(context);
        init();
    }

    public LocationSearchBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LocationSearchBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        setLogoText("Search location");
        onSearchTextChanged()
                .debounce(200, TimeUnit.MILLISECONDS)
                .switchMap(new Func1<String, Observable<GeocodeResults>>() {
                    @Override
                    public Observable<GeocodeResults> call(String query) {
                        if (query == null || query.length() < 3) {
                            return Observable.<GeocodeResults>empty();
                        }
                        return suggestLocations(getContext(), query);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GeocodeResults>() {
                    @Override
                    public void call(GeocodeResults results) {
                        showSearchSuggestions(results);
                    }
                });
    }

    public Observable<GeocodeResults> suggestLocations(Context context, String query) {
        Log.i("", (query == null || query.length() < 3) + " type " + query);
        return GoogleGeo.getInstance(context)
                .geo()
                .mediaSearch(query);
    }

    public void showSearchSuggestions(GeocodeResults results) {
        if (results != null) {
            if (results.results != null && results.results.size() > 0) {
                clearSearchable();
                for(GeocodeItem item : results.results) {
                    SearchResult option = new SearchResult(
                            item.formatted_address,
                            getResources().getDrawable(android.R.drawable.ic_menu_recent_history),
                            item.geometry.location.lat,
                            item.geometry.location.lng);
                    addSearchable(option);
                }
                updateResults();
            }

        }
    }

    public Observable<String> onSearchTextChanged() {
        return WidgetObservable
                .text(getEditText())
                .map(new Func1<OnTextChangeEvent, String>() {
                    @Override
                    public String call(OnTextChangeEvent event) {
                        Log.i("", "OnTextChangeEvent " + event.text().toString().trim());
                        return event.text().toString().trim();
                    }
                });
    }
}
