package com.binaryfork.onmap;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.binaryfork.onmap.instagram.Instagram;
import com.binaryfork.onmap.instagram.model.MediaResponse;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MediaHelper {

    public static void getInstagramMedia(final Context context, final Location location, Subscriber<MediaResponse> subscriber) {
        Observable<MediaResponse> myObservable = Observable
                .create(new Observable.OnSubscribe<MediaResponse>() {
                    @Override
                    public void call(Subscriber<? super MediaResponse> subscriber) {
                        Log.i("mapp", "WWW call");
                        subscriber.onNext(
                                Instagram.getInstance(context)
                                        .mediaService()
                                        .mediaSearch(location.getLatitude(), location.getLongitude()));
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        myObservable.subscribe(subscriber);
    }
}
