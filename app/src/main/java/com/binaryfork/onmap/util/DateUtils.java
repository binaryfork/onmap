package com.binaryfork.onmap.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {

    public static String getInterval(long maxTimestamp, long interval) {
        return formatDate(minTimestamp(maxTimestamp, interval)) + " - " + formatDate(maxTimestamp);
    }

    public static String formatDate(long seconds) {
        SimpleDateFormat serverFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return serverFormat.format(seconds * 1000);
    }

    public static long minTimestamp(long maxTimestamp, long interval) {
        return maxTimestamp - interval;
    }
}
