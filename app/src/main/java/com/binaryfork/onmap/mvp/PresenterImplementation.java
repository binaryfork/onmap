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
    private final Context context;
    private Subscription subscription;
    private long maxTimestampSeconds;
    private LatLng location;

    public PresenterImplementation(Model model, MarkersView view, MapMediaView mapMediaView, Context context) {
        this.model = model;
        this.markersView = view;
        this.mapMediaView = mapMediaView;
        this.context = context;
        toCurrentTime();
    }

    @Override
    public void backInTime() {
        maxTimestampSeconds = DateUtils.weekAgoTime(maxTimestampSeconds);
        mapMediaView.showTime(DateUtils.getWeekInterval(maxTimestampSeconds));
        getMedia(location);
    }

    @Override
    public void forwardInTime() {
    }

    @Override
    public void toCurrentTime() {
        maxTimestampSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
        mapMediaView.showTime(DateUtils.getWeekInterval(maxTimestampSeconds));
    }

    @Override
    public void setTime(long time) {
        maxTimestampSeconds = time;
        mapMediaView.showTime(DateUtils.getWeekInterval(maxTimestampSeconds));
        getMedia(location);
    }

    @Override
    public void getMedia(LatLng location) {
        this.location = location;
        markersView.setLocation(location);
        markersView.showCenterMarker();
        long from = DateUtils.weekAgoTime(maxTimestampSeconds);
        long to = maxTimestampSeconds;
        switch (apiSource) {
            case INSTAGRAM:
                subscription = model.loadMediaByLocationAndDate(context, location, from, to)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<InstagramItems>() {
                            @Override
                            public void call(InstagramItems mediaResponse) {
                                markersView.showMarkers(mediaResponse);
                            }
                        });
                break;
            case FLICKR:
                subscription = model.flickr(context, location)
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
