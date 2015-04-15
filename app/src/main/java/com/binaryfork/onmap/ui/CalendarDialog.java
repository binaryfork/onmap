package com.binaryfork.onmap.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import com.binaryfork.onmap.R;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CalendarDialog extends DialogFragment {

    public OnDateChangeListener onDateChangeListener;
    public long interval;

    @InjectView(R.id.datePicker) DatePicker datePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_calendar, null);
        ButterKnife.inject(this, view);

        builder.setView(view);

        builder
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setDate();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        onDateChangeListener.onDateChanged(calendar.getTimeInMillis() / 1000);
    }

    @OnClick({ R.id.hour, R.id.day, R.id.week, R.id.month })
    public void changeRange(View view) {
        switch (view.getId()) {
            case R.id.hour:
                interval = 60 * 60;
                break;
            case R.id.day:
                interval = 60 * 60 * 24;
                break;
            case R.id.week:
                interval = 60 * 60 * 24 * 7;
                break;
            case R.id.month:
                interval = 60 * 60 * 24 * 30;
                break;
        }
        onDateChangeListener.onRangeChanged(interval);
    }

    public interface OnDateChangeListener {
        void onDateChanged(long maximumTimestamp);
        void onRangeChanged(long rangeInterval);
    }
}