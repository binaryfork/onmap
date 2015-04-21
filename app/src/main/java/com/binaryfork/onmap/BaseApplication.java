package com.binaryfork.onmap;

import android.app.Application;
import android.content.Context;

import com.binaryfork.onmap.network.twitter.TwitterInstance;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class BaseApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "mAzg8gAN1xFBst86ctjnu9jvH";
    private static final String TWITTER_SECRET = "rS6y5gZ2IINcY6KQQEVQVYnZUlvOqQXMKrDtw6Ztb1RGIARnd5";


    private static Context instance;

    public static Context get() {
        return instance;
    }

    @Override public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
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
