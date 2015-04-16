package com.binaryfork.onmap;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

public class BaseApplication extends Application {

    private static Context instance;

    public static Context get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            // Add Crashlytics reporting.
        }
    }
}
