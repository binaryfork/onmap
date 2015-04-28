package com.binaryfork.onmap.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

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
    @InjectView(R.id.title) TextView title;

    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_calendar, null);
        ButterKnife.inject(this, view);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, -10);

        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_YEAR, 1);

        datePicker.init(nextYear.getTime(), today.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE);
        datePicker.scrollToDate(new Date());
        List<CalendarCellDecorator> decorators = new ArrayList<CalendarCellDecorator>();
        decorators.add(new CalendarDecorator());
        datePicker.setDecorators(decorators);
        datePicker.setCellClickInterceptor(new CalendarPickerView.CellClickInterceptor() {
            @Override public boolean onCellClicked(Date date) {
                selectWeek(date);
                return true;
            }
        });

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

    private void selectWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        Date from = calendar.getTime();
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        // Don't exceed current time.
        Date to = calendar.getTime().after(new Date()) ? new Date() : calendar.getTime();
        datePicker.selectDate(from);
        datePicker.selectDate(to);
        title.setText(DateUtils.getInterval(from.getTime() / 1000, to.getTime() / 1000));
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