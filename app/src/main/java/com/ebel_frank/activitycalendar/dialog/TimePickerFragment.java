package com.ebel_frank.activitycalendar.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.activity.AddEventActivity;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_TIME = "com.ebeledike.iris.fragment.time";
    private static final String ARG_TIME = "time";
    private static final String REQUEST_CODE = "request_code";

    private TimePicker mTimePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        final int requestCode = getArguments().getInt(REQUEST_CODE);

        final Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        int hour = calender.get(Calendar.HOUR_OF_DAY);
        int minute = calender.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = v.findViewById(R.id.dialog_time_time_picker);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour = mTimePicker.getCurrentHour();
                        int minute = mTimePicker.getCurrentMinute();
                        calender.set(Calendar.HOUR_OF_DAY, hour);
                        calender.set(Calendar.MINUTE, minute);
                        Date date = calender.getTime();
                        sendResult(requestCode, date);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    public static TimePickerFragment newInstance(Date date, int requestCode) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        args.putInt(REQUEST_CODE, requestCode);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int requestCode, Date date) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        ((AddEventActivity) getActivity()).onActivityResult(requestCode, Activity.RESULT_OK, intent);
    }
}
