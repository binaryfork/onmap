package com.binaryfork.onmap.rx;

import com.binaryfork.onmap.model.Media;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class Events {

    private static final Subject<Object, Object> eventBus = new SerializedSubject<>(PublishSubject.create());

    public static void send(Object o) {
        eventBus.onNext(o);
    }

    public static Observable<Object> toObservable() {
        return eventBus;
    }

    public static class NavigateToMedia {
        public final Media media;
        public NavigateToMedia(Media media) {
            this.media = media;
        }
    }
}