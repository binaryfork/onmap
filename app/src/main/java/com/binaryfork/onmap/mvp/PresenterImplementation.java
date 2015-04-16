package com.binaryfork.onmap.mvp;

import android.content.Context;

import com.binaryfork.onmap.network.ApiSource;
import com.binaryfork.onmap.network.flickr.model.FlickrPhotos;
import com.binaryfork.onmap.network.instagram.model.InstagramItems;
import com.binaryfork.onmap.util.DateUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class PresenterImplementation implements Presenter {

    public ApiSource apiSource = ApiSource.INSTAGRAM;

    private final Model model;
    private final MarkersView markersView;
    private final MapMediaView mapMediaView;
    private Subscription subscription;
    private LatLng location;
    public long interval = 60 * 60 * 24; // 1 day
    private long maxTimestampSeconds;

    public PresenterImplementation(Model model, MarkersView view, MapMediaView mapMediaView) {
        this.model = model;
        this.markersView = view;
        this.mapMediaView = mapMediaView;
        toCurrentTime();
    }

    @Override
    public void backInTime() {
        maxTimestampSeconds = DateUtils.minTimestamp(maxTimestampSeconds, interval);
        mapMediaView.showTime(DateUtils.getInterval(maxTimestampSeconds, interval));
        getMedia(location);
    }

    @Override
    public void forwardInTime() {
    }

    @Override
    public void toCurrentTime() {
        maxTimestampSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
        mapMediaView.showTime(DateUtils.getInterval(maxTimestampSeconds, interval));
    }

    @Override
    public void setTime(long time) {
        maxTimestampSeconds = time;
        mapMediaView.showTime(DateUtils.getInterval(maxTimestampSeconds, interval));
        getMedia(location);
    }

    @Override
    public void setDistance(int distance) {

    }

    @Override
    public void getMedia(LatLng location) {
        this.location = location;
        markersView.setLocation(location);
        markersView.showCenterMarker();
        long from = DateUtils.minTimestamp(maxTimestampSeconds, interval);
        long to = maxTimestampSeconds;
        switch (apiSource) {
            case INSTAGRAM:
                subscription = model.loadMediaByLocationAndDate(location, from, to)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<InstagramItems>() {
                            @Override
                            public void call(InstagramItems mediaResponse) {
                                markersView.showMarkers(mediaResponse);
                            }
                        });
                break;
            case FLICKR:
                subscription = model.flickr(location)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<FlickrPhotos>() {
                            @Override
                            public void call(FlickrPhotos mediaResponse) {
                                markersView.showMarkers(mediaResponse);
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
