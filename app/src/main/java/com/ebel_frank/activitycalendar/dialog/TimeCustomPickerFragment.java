package com.ebel_frank.activitycalendar.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ebel_frank.activitycalendar.R;

import java.util.Calendar;
import java.util.Date;

public class TimeCustomPickerFragment extends DialogFragment {
    public static final String EXTRA_TIME = "com.ebeledike.iris.fragment.time";
    private NumberPicker hour_picker, minute_picker, amPm_picker;

    private static final String ARG_TIME = "time";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        final Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        int hour = calender.get(Calendar.HOUR_OF_DAY) % 12;

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_custom_time, null);

        hour_picker = v.findViewById(R.id.hour_picker);
        minute_picker = v.findViewById(R.id.minute_picker);
        amPm_picker = v.findViewById(R.id.amPm_picker);

        String[] hourString = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
        String[] minuteString = { "00", "30" };
        String[] amPmString = { "AM", "PM" };

        hour_picker.setDisplayedValues(hourString);
        hour_picker.setMinValue(1);
        hour_picker.setMaxValue(hourString.length);
        hour_picker.setValue(hour); // i will continue from here.

        minute_picker.setDisplayedValues(minuteString);
        minute_picker.setMinValue(0);
        minute_picker.setMaxValue(minuteString.length - 1);

        amPm_picker.setDisplayedValues(amPmString);
        amPm_picker.setMinValue(0);
        amPm_picker.setMaxValue(amPmString.length - 1);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour = hour_picker.getValue();
                        int minute = minute_picker.getValue();
                        int amPm = amPm_picker.getValue();
                        if(minute == 1) {
                            calender.set(Calendar.MINUTE, 30);
                        }else {
                            calender.set(Calendar.MINUTE, minute);
                        }
                        if(hour == 12 && amPm == 0) {
                            hour = 0;
                        }
                        if(amPm == 1) {
                            if(hour == 12) {
                                hour = 12;
                            }else{
                                hour += 12;
                            }
                        }
                        calender.set(Calendar.HOUR_OF_DAY, hour);
                        Date date = calender.getTime();
                        sendResult(date);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    public static TimeCustomPickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        TimeCustomPickerFragment fragment = new TimeCustomPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(Date date) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        if(getTargetFragment() == null){
            return;
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
