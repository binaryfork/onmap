package com.binaryfork.onmap.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.util.DateUtils;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class CalendarDialog extends DialogFragment {

    public OnDateChangeListener onDateChangeListener;

    @InjectView(R.id.calendar_view) CalendarPickerView datePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_calendar, null);
        ButterKnife.inject(this, view);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, -10);

        Date today = new Date();
        datePicker.init(nextYear.getTime(), today)
                .inMode(CalendarPickerView.SelectionMode.RANGE);
        List<CalendarCellDecorator> decorators = new ArrayList<CalendarCellDecorator>();
        decorators.add(new CalendarDecorator());
        datePicker.setDecorators(decorators);

        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setDate();
                    }
                })
                .setNeutralButton("Show Recent", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        removeDate();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    private void setDate() {
        int size = datePicker.getSelectedDates().size();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datePicker.getSelectedDates().get(size - 1));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long startDay = datePicker.getSelectedDates().get(0).getTime() / 1000;
        long endDay = datePicker.getSelectedDates().get(size - 1).getTime() / 1000;
        Timber.i("day start %s", DateUtils.formatDate(startDay));
        Timber.i("day end %s", DateUtils.formatDate(endDay));
        onDateChangeListener.onDateChanged(startDay, endDay);
    }

    private void removeDate() {
        onDateChangeListener.onDateChanged(0, 0);
    }

    public interface OnDateChangeListener {
        void onDateChanged(long min, long max);
    }
}