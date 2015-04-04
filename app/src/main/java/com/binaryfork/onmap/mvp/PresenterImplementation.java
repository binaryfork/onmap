package com.binaryfork.onmap.mvp;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.binaryfork.onmap.network.model.GeocodeResults;
import com.binaryfork.onmap.network.model.MediaResponse;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class PresenterImplementation implements
        Presenter {

    private final Model model;
    private final MarkersView view;
    private final Context context;
    private Subscription subscription;
    private Subscription searchSubscription;

    public PresenterImplementation(Model model, MarkersView view, Context context) {
        this.model = model;
        this.view = view;
        this.context = context;
    }

    public void onCreate() {
        searchSubscription = model
                .onSearchTextChanged()
                .debounce(300, TimeUnit.MILLISECONDS)
                .switchMap(new Func1<String, Observable<GeocodeResults>>() {
                    @Nullable
                    @Override
                    public Observable<GeocodeResults> call(String query) {
                        Log.i("", "type " + query);
                        if (query == null || query.length() < 3) {
                            return Observable.<GeocodeResults>empty();
                        }
                        return model.suggestLocations(context, query);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GeocodeResults>() {
                    @Override
                    public void call(GeocodeResults results) {
                        view.showSearchSuggestions(results);
                    }
                });
    }

    private void mapSubscribe(Observable<MediaResponse> observable) {
        subscription = observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MediaResponse>() {
                    @Override
                    public void call(MediaResponse mediaResponse) {
                        view.showMarkers(mediaResponse);
                    }
                });
    }

    @Override
    public void onLocationUpdate(Location location) {
        mapSubscribe(model.loadMediaByLocation(context, location));
    }

    @Override
    public void onDateChange(Location location, long from, long to) {
        mapSubscribe(model.loadMediaByLocationAndDate(context, location, from, to));
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        if (searchSubscription != null) {
            searchSubscription.unsubscribe();
        }
    }


}
