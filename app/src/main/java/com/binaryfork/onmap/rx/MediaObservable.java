package com.binaryfork.onmap.rx;

import rx.Observable;
import rx.Subscriber;

public abstract class MediaObservable<T> implements Observable.OnSubscribe<T> {
    public abstract T fire();

    @Override
    public void call(Subscriber<? super T> subscriber) {
        try {
            T data = fire();
            subscriber.onNext(data);
            subscriber.onCompleted();
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }
}