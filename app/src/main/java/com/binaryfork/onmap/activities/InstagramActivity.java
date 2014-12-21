package com.binaryfork.onmap.activities;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.binaryfork.onmap.instagram.InstagramRequest;
import com.binaryfork.onmap.instagram.InstagramService;
import com.binaryfork.onmap.instagram.model.Media;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

public abstract class InstagramActivity extends FragmentActivity {

    private SpiceManager spiceManager = new SpiceManager(InstagramService.class);

    private SpiceManager getSpiceManager() {
        return spiceManager;
    }

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

    protected void loadInstagramMedia(Location location) {
        InstagramRequest instagramRequest = new InstagramRequest(location.getLatitude(), location.getLongitude());
        getSpiceManager().execute(instagramRequest,
                instagramRequest.getRequestCacheKey(),
                DurationInMillis.ONE_HOUR,
                new InstagramRequestListener());
    }

    private class InstagramRequestListener implements RequestListener<Media.MediaResponse> {

        @Override
        public void onRequestFailure(SpiceException e) {
            e.printStackTrace();
            Log.e("Instagram", "Failure " + e.getMessage());

        }

        @Override
        public void onRequestSuccess(Media.MediaResponse mediaResponse) {
            Log.e("Instagram", "Success ");
            instagramMediaLoaded(mediaResponse.data);
        }
    }
}
