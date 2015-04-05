package com.binaryfork.onmap.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    private Calendar calendar;

    public DateUtils(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getWeekInterval() {
        return formatDate(weekAgoTime()) + " - " + formatDate(calendar.getTimeInMillis());
    }

    public void previousWeek() {
        calendar.add(Calendar.DAY_OF_WEEK, -7);
    }

    public void nextWeek() {
        calendar.add(Calendar.DAY_OF_WEEK, 7);
    }

    public String formatDate(long millis) {
        SimpleDateFormat serverFormat = new SimpleDateFormat("dd MMM");
        return serverFormat.format(millis);
    }

    public long weekAgoTime() {
        return (calendar.getTimeInMillis() - 1000 * 60 * 60 * 24 * 7) / 1000;
    }

    public long currentTime() {
        return calendar.getTimeInMillis() / 1000;
    }
}
