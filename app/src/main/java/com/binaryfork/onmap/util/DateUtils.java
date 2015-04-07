package com.binaryfork.onmap.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {

    public static String getWeekInterval(long maxTimestamp) {
        return formatDate(weekAgoTime(maxTimestamp)) + " - " + formatDate(maxTimestamp);
    }

    public static String formatDate(long seconds) {
        SimpleDateFormat serverFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return serverFormat.format(seconds * 1000);
    }

    public static long weekAgoTime(long timestamp) {
        return timestamp - 60 * 60 * 24 * 1;
    }
}
