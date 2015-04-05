package com.binaryfork.onmap.mvp;

import android.content.Context;

import com.binaryfork.onmap.network.model.MediaResponse;
import com.google.android.gms.maps.model.LatLng;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class PresenterImplementation implements
        Presenter {

    private final Model model;
    private final MarkersView view;
    private final Context context;
    private Subscription subscription;

    public PresenterImplementation(Model model, MarkersView view, Context context) {
        this.model = model;
        this.view = view;
        this.context = context;
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
    public void onLocationUpdate(LatLng location) {
        mapSubscribe(model.loadMediaByLocation(context, location));
    }

    @Override
    public void onDateChange(LatLng location, long from, long to) {
        mapSubscribe(model.loadMediaByLocationAndDate(context, location, from, to));
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }


}
