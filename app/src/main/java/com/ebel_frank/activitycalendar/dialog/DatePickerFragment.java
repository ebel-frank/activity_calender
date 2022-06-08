package com.ebel_frank.activitycalendar.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.activity.AddEventActivity;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "com.ebeledike.criminalintent.date";
    private static final String ARG_DATE = "date";
    private static final String REQUEST_CODE = "request_code";

    private DatePicker mDatePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        final int requestCode = getArguments().getInt(REQUEST_CODE);

        final Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        int year = calender.get(Calendar.YEAR);
        int month = calender.get(Calendar.MONTH);
        int day = calender.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        mDatePicker = v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        calender.set(year, month, day);
                        Date date = calender.getTime();
                        sendResult(requestCode, date);
                    }
                })
                .create();
    }

    private void sendResult(int requestCode, Date date) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        ((AddEventActivity)getActivity()).onActivityResult(requestCode, Activity.RESULT_OK, intent);
    }

    public static DatePickerFragment newInstance(Date date, int requestCode) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        args.putInt(REQUEST_CODE, requestCode);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
