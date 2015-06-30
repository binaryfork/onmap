package com.binaryfork.onmap.presenter;

import android.content.Context;
import android.graphics.Bitmap;

import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.components.clustering.Clusterer;
import com.binaryfork.onmap.model.ApiSource;
import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.model.MediaList;
import com.binaryfork.onmap.model.ModelImplementation;
import com.binaryfork.onmap.model.instagram.model.InstagramItems;
import com.binaryfork.onmap.model.twitter.TweetMedia;
import com.binaryfork.onmap.components.transform.BorderTransformation;
import com.binaryfork.onmap.util.DateUtils;
import com.binaryfork.onmap.components.transform.VideoIconTransformation;
import com.binaryfork.onmap.view.map.MediaMapView;
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

public class MediaMapPresenterImplementation implements
        MediaMapPresenter {

    private final static String PICASSO_MAP_MARKER_TAG = "marker";

    private ApiSource apiSource = ApiSource.INSTAGRAM;

    private MediaMapView mediaMapView;
    private Clusterer clusterer;
    private Subscription subscription;
    private LatLng location;
    private Transformation videoIconTransformation;
    private ModelImplementation model;
    private ArrayList<Media> loadedMedia;

    public MediaMapPresenterImplementation() {
        videoIconTransformation = new VideoIconTransformation();
        model = new ModelImplementation();
    }

    @Override public void setMediaMapView(MediaMapView mediaMapView) {
        this.mediaMapView = mediaMapView;
        mediaMapView.showTime(DateUtils.getInterval(model.from, model.to));
    }

    @Override public void setupClusterer(Context context, GoogleMap map) {
        if (clusterer == null)
            clusterer = new Clusterer(context, map);
        clusterer.init(mediaMapView);
    }

    @Override public void changeSource(ApiSource apiSource) {
        this.apiSource = apiSource;
    }

    @Override public ApiSource getSource() {
        return apiSource;
    }

    @Override public void setTime(long min, long max) {
        model.from = min;
        model.to = max;
        mediaMapView.showTime(DateUtils.getInterval(min, max));
        loadMedia(location);
    }

    @Override public void setDistance(int distance) {
        model.distance = distance;
        loadMedia(location);
    }

    @Override public void loadMedia(LatLng location) {
        Timber.i("", "what");
        if (subscription != null)
            subscription.unsubscribe();
        this.location = location;
        Timber.i("", "what");
        // Cancel all loading map photos because all markers will be cleared.
        Picasso.with(BaseApplication.get()).cancelTag(PICASSO_MAP_MARKER_TAG);
        clusterer.clearItems();
        mediaMapView.showCenterMarker(model.distance);
        Observable<? extends MediaList> observable;
        Timber.i("", "what");
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
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<MediaList, Observable<Media>>() {
                    @Override public Observable<Media> call(MediaList mediaList) {
                        provideMediaListToSearchView(mediaList);
                        return Observable.from(mediaList.getList());
                    }
                }));
    }

    @Override public LatLng getLocation() {
        return location;
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
                                        if (media != null && bitmap != null) {
                                            clusterer.addItem(media, bitmap);
                                            clusterer.cluster();
                                        }
                                    }
                                }, onError());
                    }
                }, onError(), onComplete());
    }

    private Action1<Throwable> onError() {
        return new Action1<Throwable>() {
            @Override public void call(Throwable throwable) {
                Timber.e(throwable, "Marker subscription error");
                mediaMapView.allMarkesLoaded();
            }
        };
    }

    private Action0 onComplete() {
        return new Action0() {
            @Override public void call() {
                mediaMapView.allMarkesLoaded();
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
                } else {
                    picasso = picasso.transform(new BorderTransformation());
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
                                return media.getThumbnail() != null && media.getPhotoUrl() != null &&
                                        (media.getLatitude() != 0 || media.getLongitude() != 0);
                            }
                        }));
            }

            @Override public void failure(TwitterException e) {
                Timber.e(e, "Twitter error");
                mediaMapView.allMarkesLoaded();
            }
        };
    }

    @Override public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public ArrayList<Media> getLoadedMedia() {
        return loadedMedia;
    }

    private void provideMediaListToSearchView(MediaList mediaList) {
        loadedMedia = (ArrayList<Media>) mediaList.getList();
        Timber.i("wwwwwwwws " + loadedMedia.size());
        // TODO remove this
    //    mediaMapView.provideMediaList(loadedMedia);
    }

    @Override public void randomLocation() {
        model.instagramPopular(15).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<InstagramItems>() {
            @Override public void call(InstagramItems instagramItems) {
                for (Media media : instagramItems.getList()) {
                    if (media.getLatitude() == 0 && media.getLongitude() == 0) {
                    } else {
                        LatLng loc = new LatLng(media.getLatitude(), media.getLongitude());
                        if (loc.equals(location))
                            continue;
                        mediaMapView.onRandomLocation(loc);
                        break;
                    }
                }
            }
        });
    }
}
