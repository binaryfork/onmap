package com.binaryfork.onmap.mvp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.network.ApiSource;
import com.binaryfork.onmap.network.Media;
import com.binaryfork.onmap.network.MediaList;
import com.binaryfork.onmap.util.DateUtils;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PresenterImplementation implements Presenter, Serializable {

    private final static String PICASSO_MAP_MARKER_TAG = "marker";

    public ApiSource apiSource = ApiSource.INSTAGRAM;

    private final MapMediaView mapMediaView;
    private Subscription subscription;
    private LatLng location;
    private long minTimestampSeconds;
    private long maxTimestampSeconds;
    private Drawable videoIcon;

    public PresenterImplementation(MapMediaView mapMediaView) {
        this.mapMediaView = mapMediaView;
        mapMediaView.showTime(DateUtils.getInterval(minTimestampSeconds, maxTimestampSeconds));
    }

    @Override
    public void setTime(long min, long max) {
        minTimestampSeconds = min;
        maxTimestampSeconds = max;
        mapMediaView.showTime(DateUtils.getInterval(minTimestampSeconds, maxTimestampSeconds));
        getMedia(location);
        videoIcon = BaseApplication.get().getResources().getDrawable(android.R.drawable.ic_media_play);
    }

    @Override
    public void setDistance(int distance) {
    }

    @Override
    public void getMedia(LatLng location) {
        this.location = location;
        // Cancel all loading map photos because all markers will be cleared.
        Picasso.with(BaseApplication.get()).cancelTag(PICASSO_MAP_MARKER_TAG);
        mapMediaView.clearMap();
        mapMediaView.showCenterMarker();
        long from = minTimestampSeconds;
        long to = maxTimestampSeconds;
        Observable<? extends MediaList> observable;
        Model model = new ModelImplementation();
        switch (apiSource) {
            default:
            case INSTAGRAM:
                observable = model.loadMediaByLocationAndDate(location, from, to);
                break;
            case FLICKR:
                observable = model.flickr(location);
                break;
        }
        subscription = observable
                .flatMap(new Func1<MediaList, Observable<Media>>() {
                    @Override public Observable<Media> call(MediaList mediaList) {
                        return Observable.from(mediaList.getList());
                    }
                })
                .flatMap(new Func1<Media, Observable<Media>>() {
                    @Override public Observable<Media> call(Media media) {
                        return mediaWithThumbBitmap(media);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Media>() {
                    @Override public void call(Media media) {
                        mapMediaView.addMarker(media);
                    }
                });
    }

    private Observable<Media> mediaWithThumbBitmap(final Media media) {
        return Observable.create(new Observable.OnSubscribe<Media>() {
            @Override
            public void call(final Subscriber<? super Media> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                    return;
                }
                Bitmap bitmap = null;
                try {
                    bitmap = Picasso.with(BaseApplication.get())
                            .load(media.getThumbnail())
                            .tag(PICASSO_MAP_MARKER_TAG)
                            .get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (bitmap != null && media.isVideo())
                    bitmap = drawVideoIcon(bitmap);
                media.setThumbBitmap(bitmap);
                subscriber.onNext(media);
                subscriber.onCompleted();
            }
        });
    }

    private Bitmap drawVideoIcon(Bitmap bitmap) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        if (videoIcon == null)
            return null;
        videoIcon.setBounds(canvas.getClipBounds());
        videoIcon.draw(canvas);
        canvas.drawBitmap(mutableBitmap, 0, 0, null);
        bitmap.recycle();
        return mutableBitmap;
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }


}
