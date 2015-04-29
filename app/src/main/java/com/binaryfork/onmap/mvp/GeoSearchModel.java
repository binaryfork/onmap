package com.binaryfork.onmap.mvp;

import android.widget.TextView;

import com.binaryfork.onmap.network.google.GoogleGeo;
import com.binaryfork.onmap.network.google.model.GeocodeResults;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;

public class GeoSearchModel {

    public void subscribe(TextView input, Action1<GeocodeResults> onComplete) {
        textChangedObservable(input)
                .debounce(100, TimeUnit.MILLISECONDS)
                .switchMap(geocodeResults())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete);
    }

    private Observable<String> textChangedObservable(TextView input) {
        return WidgetObservable
                .text(input)
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
                return GoogleGeo.getInstance()
                        .geo()
                        .mediaSearch(query);
            }
        };
    }
}
