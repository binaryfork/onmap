package com.binaryfork.onmap;

import android.app.Application;
import android.content.Context;

import com.binaryfork.onmap.model.twitter.TwitterInstance;
import com.binaryfork.onmap.presenter.MediaMapPresenter;
import com.binaryfork.onmap.view.mediaview.MediaViewAnimator;
import com.squareup.leakcanary.LeakCanary;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class BaseApplication extends Application {

    private static Context instance;
    public static MediaViewAnimator animator;

    public static MediaMapPresenter mediaMapPresenter;

    public static Context get() {
        return instance;
    }

    public static MediaMapPresenter getMediaMapPresenter() {
        return mediaMapPresenter;
    }

    public static void setMediaMapPresenter(MediaMapPresenter mediaMapPresenter) {
        BaseApplication.mediaMapPresenter = mediaMapPresenter;
    }

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_id), getString(R.string.twitter_sec));
        Fabric.with(this, new Twitter(authConfig));
        TwitterInstance.getInstance();
        instance = getApplicationContext();
        if (BuildConfig.DEBUG) {
            Timber.plant(new LineNumberTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private static class LineNumberTree extends Timber.DebugTree {
        @Override protected String createStackElementTag(StackTraceElement element) {
            return super.createStackElementTag(element) + ":" + element.getLineNumber();
        }
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            // Add Crashlytics reporting.
        }
    }
}
