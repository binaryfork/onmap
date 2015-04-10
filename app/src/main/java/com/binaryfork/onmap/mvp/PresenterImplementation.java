package com.binaryfork.onmap.mvp;

import android.content.Context;

import com.binaryfork.onmap.network.ApiSource;
import com.binaryfork.onmap.network.flickr.model.FlickrPhotos;
import com.binaryfork.onmap.network.instagram.model.InstagramItems;
import com.google.android.gms.maps.model.LatLng;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class PresenterImplementation implements Presenter {

    private final Model model;
    private final MarkersView view;
    private final Context context;
    private Subscription subscription;

    public ApiSource apiSource = ApiSource.INSTAGRAM;

    public PresenterImplementation(Model model, MarkersView view, Context context) {
        this.model = model;
        this.view = view;
        this.context = context;
    }

    @Override
    public void getMediaByLocationAndDate(LatLng location, long from, long to) {
        switch (apiSource) {
            case INSTAGRAM:
                subscription = model.loadMediaByLocationAndDate(context, location, from, to)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<InstagramItems>() {
                            @Override
                            public void call(InstagramItems mediaResponse) {
                                view.showMarkers(mediaResponse);
                            }
                        });
                break;
            case FLICKR:
                subscription = model.flickr(context, location)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<FlickrPhotos>() {
                            @Override
                            public void call(FlickrPhotos mediaResponse) {
                                view.showMarkers(mediaResponse);
                            }
                        });
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }


}
