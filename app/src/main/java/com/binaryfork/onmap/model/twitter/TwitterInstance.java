package com.binaryfork.onmap.model.twitter;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;

import timber.log.Timber;

public class TwitterInstance {

    private static TwitterApiClient instance;

    public static TwitterApiClient getInstance() {
        if (instance == null) {
            TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                @Override public void success(Result<AppSession> result) {
                    instance = new TwitterApiClient(result.data);
                    Timber.i("Twitter is authenticated with guest session.");
                }

                @Override public void failure(TwitterException e) {
                    Timber.e("Twitter session error: %s", e.getMessage());
                }
            });
        }
        return instance;
    }

}
