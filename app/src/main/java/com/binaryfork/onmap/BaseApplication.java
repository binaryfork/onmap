package com.binaryfork.onmap;

import android.app.Application;
import android.content.Context;

import com.binaryfork.onmap.model.twitter.TwitterInstance;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class BaseApplication extends Application {

    private static Context instance;

    public static Context get() {
        return instance;
    }

    @Override public void onCreate() {
        super.onCreate();
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
