package com.binaryfork.onmap;

import android.app.Application;

import timber.log.Timber;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new LineNumberTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private static class LineNumberTree extends Timber.DebugTree {
        @Override
        protected String createTag() {
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            String lineNumber = ":" + stackTrace[5].getLineNumber();
            return super.createTag() + lineNumber;
        }
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.HollowTree {
        @Override public void i(String message, Object... args) {
            // TODO e.g., Crashlytics.log(String.format(message, args));
        }

        @Override public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override public void e(Throwable t, String message, Object... args) {
            e(message, args);

            // TODO e.g., Crashlytics.logException(t);
        }
    }
}
