package com.binaryfork.onmap.mvp;

import android.content.Context;
import android.location.Location;

import com.binaryfork.onmap.instagram.model.MediaResponse;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class PresenterImplementation implements Presenter {

    private final Model model;
    private final View view;
    private final Context context;
    private Subscription subscription;

    public PresenterImplementation(Model model, View view, Context context) {
        this.model = model;
        this.view = view;
        this.context = context;
    }

    @Override
    public void onLocationUpdate(Location location) {
        subscription = model
                .loadMedia(context, location)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MediaResponse>() {
                    @Override
                    public void call(MediaResponse mediaResponse) {
                        view.showMarkers(mediaResponse);
                    }
                });
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
