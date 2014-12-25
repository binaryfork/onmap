package com.binaryfork.onmap.activities;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.binaryfork.onmap.instagram.InstagramService;
import com.binaryfork.onmap.instagram.model.Media;
import com.binaryfork.onmap.instagram.requests.BaseRequest;
import com.binaryfork.onmap.instagram.requests.PhotoMapRequest;
import com.binaryfork.onmap.instagram.requests.PhotoMapTimestampRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

public abstract class InstagramActivity extends FragmentActivity {

    protected SpiceManager spiceManager = new SpiceManager(InstagramService.class);
    private BaseRequest instagramRequest;

    abstract void instagramMediaLoaded(List<Media> list);

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    protected void loadInstagramMediaByTime(double lat, double lon, long min, long max) {
        if (instagramRequest != null) {
            spiceManager.cancel(instagramRequest);
        }
        instagramRequest = new PhotoMapTimestampRequest(lat, lon, min / 1000, max / 1000);
        spiceManager.execute(instagramRequest,
                instagramRequest.getRequestCacheKey(),
                DurationInMillis.ONE_HOUR,
                new InstagramRequestListener());
    }

    protected void loadInstagramMedia(double lat, double lon) {
        if (instagramRequest != null) {
            spiceManager.cancel(instagramRequest);
        }
        instagramRequest = new PhotoMapRequest(lat, lon);
        spiceManager.execute(instagramRequest,
                instagramRequest.getRequestCacheKey(),
                DurationInMillis.ONE_HOUR,
                new InstagramRequestListener());
    }

    private class InstagramRequestListener implements RequestListener<Media.MediaResponse> {

        @Override
        public void onRequestFailure(SpiceException e) {
            instagramRequest = null;
            e.printStackTrace();
            Log.e("Instagram", "Failure " + e.getMessage());
        }

        @Override
        public void onRequestSuccess(Media.MediaResponse mediaResponse) {
            instagramRequest = null;
            Log.e("Instagram", "Success ");
            instagramMediaLoaded(mediaResponse.data);
        }
    }
}
