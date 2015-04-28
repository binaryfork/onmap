package com.binaryfork.onmap.mvp;

import android.content.Context;
import android.graphics.Bitmap;

import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.clustering.Clusterer;
import com.binaryfork.onmap.network.ApiSource;
import com.binaryfork.onmap.network.Media;
import com.binaryfork.onmap.network.MediaList;
import com.binaryfork.onmap.network.twitter.TweetMedia;
import com.binaryfork.onmap.util.DateUtils;
import com.binaryfork.onmap.util.VideoIconTransformation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.IOException;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PresenterImplementation implements
        Presenter {

    private final static String PICASSO_MAP_MARKER_TAG = "marker";

    private ApiSource apiSource = ApiSource.INSTAGRAM;

    private MapMediaView mapMediaView;
    private Clusterer clusterer;
    private Subscription subscription;
    private LatLng location;
    private Transformation videoIconTransformation;
    private ModelImplementation model;

    public PresenterImplementation() {
        videoIconTransformation = new VideoIconTransformation();
        model = new ModelImplementation();
    }

    @Override public void setMapMediaView(MapMediaView mapMediaView) {
        this.mapMediaView = mapMediaView;
        mapMediaView.showTime(DateUtils.getInterval(model.from, model.to));
    }

    @Override public void setupClusterer(Context context, GoogleMap map) {
        if (clusterer == null)
            clusterer = new Clusterer(context, map);
        clusterer.init(mapMediaView);
    }

    @Override public void changeSource(ApiSource apiSource) {
        this.apiSource = apiSource;
    }

    @Override public void setTime(long min, long max) {
        model.from = min;
        model.to = max;
        mapMediaView.showTime(DateUtils.getInterval(min, max));
        getMedia(location);
    }

    @Override public void setDistance(int distance) {
        model.distance = distance;
        getMedia(location);
    }

    @Override public void getMedia(LatLng location) {
        if (subscription != null)
            subscription.unsubscribe();
        this.location = location;
        // Cancel all loading map photos because all markers will be cleared.
        Picasso.with(BaseApplication.get()).cancelTag(PICASSO_MAP_MARKER_TAG);
        clusterer.clearItems();
        mapMediaView.showCenterMarker(model.distance);
        Observable<? extends MediaList> observable;
        switch (apiSource) {
            default:
            case INSTAGRAM:
                observable = model.instagram(location);
                break;
            case FLICKR:
                observable = model.flickr(location);
                break;
            case FOURSQUARE:
                observable = model.foursquare(location);
                break;
            case TWITTER:
                // Use the callback that makes observable from the list because TwitterCore's
                // Retrofit service does not provides observables.
                model.twitter(location, twitterApiCallback());
                return;
        }
        subscribe(observable
                .flatMap(new Func1<MediaList, Observable<Media>>() {
                    @Override public Observable<Media> call(MediaList mediaList) {
                        return Observable.from(mediaList.getList());
                    }
                }));
    }

    private void subscribe(Observable<? extends Media> observable) {
        subscription = observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Media>() {
                    @Override public void call(final Media media) {
                        markerBitmapObservable(media)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<Bitmap>() {
                                    @Override public void call(Bitmap bitmap) {
                                        clusterer.addItem(media, bitmap);
                                        clusterer.cluster();
                                    }
                                }, onError());
                    }
                }, onError(), onComplete());
    }

    private Action1<Throwable> onError() {
        return new Action1<Throwable>() {
            @Override public void call(Throwable throwable) {
                Timber.e(throwable, "Marker subscription error");
            }
        };
    }

    private Action0 onComplete() {
        return new Action0() {
            @Override public void call() {
                mapMediaView.allMarkesLoaded();
            }
        };
    }

    private Observable<Bitmap> markerBitmapObservable(final Media media) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(final Subscriber<? super Bitmap> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                    return;
                }
                RequestCreator picasso = Picasso.with(BaseApplication.get())
                        .load(media.getThumbnail())
                        .config(Bitmap.Config.RGB_565)
                        .resize(clusterer.getMarkerDimensions(), clusterer.getMarkerDimensions())
                        .tag(PICASSO_MAP_MARKER_TAG);
                if (media.isVideo()) {
                    picasso = picasso.transform(videoIconTransformation);
                }
                Bitmap bitmap = null;
                try {
                    bitmap = picasso.get();
                } catch (IOException e) {
                    Timber.w(e, "Picasso IO error");
                }
                subscriber.onNext(bitmap);
                subscriber.onCompleted();
            }
        });
    }

    private Callback<Search> twitterApiCallback() {
        return new Callback<Search>() {
            @Override public void success(Result<Search> result) {
                ArrayList<Media> arrayList = new ArrayList<>();
                for (Tweet tweet : result.data.tweets) {
                    TweetMedia tweetMedia = new TweetMedia(tweet);
                    arrayList.add(tweetMedia);
                }
                subscribe(Observable
                        .from(arrayList)
                        .subscribeOn(Schedulers.newThread())
                        .filter(new Func1<Media, Boolean>() {
                            @Override public Boolean call(Media media) {
                                return media.getPhotoUrl() != null &&
                                        (media.getLatitude() != 0 && media.getLongitude() != 0);
                            }
                        }));
            }

            @Override public void failure(TwitterException e) {
                Timber.e(e, "Twitter error");
            }
        };
    }

    @Override public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

}
