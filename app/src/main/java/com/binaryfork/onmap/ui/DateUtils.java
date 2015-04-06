package com.binaryfork.onmap.ui;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {

    public static String getWeekInterval(long maxTimestamp) {
        return formatDate(weekAgoTime(maxTimestamp)) + " - " + formatDate(maxTimestamp);
    }

    public static String formatDate(long millis) {
        SimpleDateFormat serverFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return serverFormat.format(millis);
    }

    public static long weekAgoTime(long timestamp) {
        return timestamp - 1000 * 60 * 60 * 24 * 7;
    }
}
