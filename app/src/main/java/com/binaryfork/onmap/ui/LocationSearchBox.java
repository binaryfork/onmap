package com.binaryfork.onmap.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.mvp.MapMediaView;
import com.binaryfork.onmap.network.google.GoogleGeo;
import com.binaryfork.onmap.network.google.model.GeocodeItem;
import com.binaryfork.onmap.network.google.model.GeocodeResults;
import com.google.android.gms.maps.model.LatLng;
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

    public void setup(final MapMediaView mapMediaView) {
        setOnSuggestionClickListener(new OnSuggestionClick() {
            @Override
            public void onSuggestionClick(SearchResult searchResult) {
                mapMediaView.goToLocation(new LatLng(searchResult.lat, searchResult.lng));
            }
        });
        setMenuListener(new MenuListener() {
            @Override
            public void onMenuClick() {
                mapMediaView.onMenuClick();
            }
        });
    }

    private void init() {
        setLogoText(getContext().getString(R.string.search_location_hint));
        searchTextChangedObservable()
                .debounce(100, TimeUnit.MILLISECONDS)
                .switchMap(geocodeResults())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(showSearchSuggestions());
    }

    private Observable<String> searchTextChangedObservable() {
        return WidgetObservable
                .text(getEditText())
                .map(new Func1<OnTextChangeEvent, String>() {
                    @Override
                    public String call(OnTextChangeEvent event) {
                        return event.text().toString().trim();
                    }
                });
    }

    private Func1<String, Observable<GeocodeResults>> geocodeResults() {
        return new Func1<String, Observable<GeocodeResults>>() {
            @Override
            public Observable<GeocodeResults> call(String query) {
                if (query == null || query.length() < 3) {
                    return Observable.empty();
                }
                showLoading(true);
                return GoogleGeo.getInstance()
                        .geo()
                        .mediaSearch(query);
            }
        };
    }

    private Action1<GeocodeResults> showSearchSuggestions() {
        return new Action1<GeocodeResults>() {
            @Override
            public void call(GeocodeResults results) {
                if (results != null) {
                    if (results.results != null && results.results.size() > 0) {
                        clearSearchable();
                        for (GeocodeItem item : results.results) {
                            SearchResult option = new SearchResult(
                                    item.formatted_address,
                                    getResources().getDrawable(android.R.drawable.ic_menu_recent_history),
                                    item.geometry.location.lat,
                                    item.geometry.location.lng);
                            addSearchable(option);
                        }
                        updateResults();
                        showLoading(false);
                    }

                }
            }
        };
    }
}