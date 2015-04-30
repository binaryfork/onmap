package com.binaryfork.onmap.view.map.ui;

import com.devspark.robototextview.util.RobotoTextViewUtils;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;

import java.util.Date;

public class CalendarDecorator implements CalendarCellDecorator {
    @Override
    public void decorate(CalendarCellView cellView, Date date) {
        RobotoTextViewUtils.initTypeface(cellView, cellView.getContext(), null);
    }
}