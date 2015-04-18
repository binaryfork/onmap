package com.binaryfork.onmap.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {

    public static String getInterval(long minTimestamp, long maxTimestamp) {
        if (minTimestamp == 0 || maxTimestamp == 0)
            return "Recent";
        else
            return formatDate(minTimestamp) + " - " + formatDate(maxTimestamp);
    }

    public static String formatDate(long seconds) {
        SimpleDateFormat serverFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return serverFormat.format(seconds * 1000);
    }

    public static long minTimestamp(long maxTimestamp, long interval) {
        return maxTimestamp - interval;
    }
}
