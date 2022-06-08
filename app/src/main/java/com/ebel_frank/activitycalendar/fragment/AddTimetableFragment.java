package com.ebel_frank.activitycalendar.fragment;

import static com.ebel_frank.activitycalendar.utils.Utils.convertTimeToString;
import static com.ebel_frank.activitycalendar.utils.Utils.getReminderInt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.database.AppDatabase;
import com.ebel_frank.activitycalendar.dialog.TimeCustomPickerFragment;
import com.ebel_frank.activitycalendar.dialog.TimePickerFragment;
import com.ebel_frank.activitycalendar.interfaces.DialogCloseListener;
import com.ebel_frank.activitycalendar.model.TimetableData;
import com.ebel_frank.activitycalendar.utils.NotificationUtils;
import com.ebel_frank.activitycalendar.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AddTimetableFragment extends DialogFragment {
    private static final String DIALOG_TIME = "DialogTime";
    private static final int FROM_TIME_REQUEST_CODE = 0;
    private static final int TO_TIME_REQUEST_CODE = 1;
    //private static final int ALARM_TIMETABLE_REQUEST_CODE = 2;  // to be used in future updates

    private Fragment fragment;
    private Date from_date = new Date();
    private Date to_date;
    private String chosenDay = "";
    private View chosenView;
    private Button fromTime, toTime;
    private Spinner reminder;
    private FloatingActionButton color;

    AddTimetableFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final AppDatabase database  = AppDatabase.getInstance(getContext());  // instance of the database class
        View root =  inflater.inflate(R.layout.dialog_add_timetable_fragment, container, false);

        final EditText subject = root.findViewById(R.id.timetable_name);
        color = root.findViewById(R.id.color);
        color.setOnClickListener(v -> openColorPicker(color));
        // sunday button
        root.findViewById(R.id.on_sun_btn)
                .setOnClickListener(v -> {
                    chosenDay = "Sun";
                    chosenDay(v);
                });
        root.findViewById(R.id.on_mon_btn)
                .setOnClickListener(v -> {
                    chosenDay = "Mon";
                    chosenDay(v);
                });
        // tuesday button
        root.findViewById(R.id.on_tue_btn)
                .setOnClickListener(v -> {
                    chosenDay = "Tue";
                    chosenDay(v);
                });
        // wednesday button
        root.findViewById(R.id.on_wed_btn)
                .setOnClickListener(v -> {
                    chosenDay = "Wed";
                    chosenDay(v);
                });
        // thursday button
        root.findViewById(R.id.on_thu_btn)
                .setOnClickListener(v -> {
                    chosenDay = "Thu";
                    chosenDay(v);
                });
        // friday button
        root.findViewById(R.id.on_fri_btn)
                .setOnClickListener(v -> {
                    chosenDay = "Fri";
                    chosenDay(v);
                });
        // saturday button
        root.findViewById(R.id.on_sat_btn)
                .setOnClickListener(v -> {
                    chosenDay = "Sat";
                    chosenDay(v);
                });

        Calendar c = Calendar.getInstance();
        c.setTime(from_date);
        c.set(Calendar.MINUTE, 0); // sets the minute to be 00
        from_date = c.getTime();

        fromTime = root.findViewById(R.id.from_time);
        convertTimeToString(fromTime, from_date.getTime());
        fromTime.setOnClickListener(v -> {
            TimeCustomPickerFragment dialog = TimeCustomPickerFragment.newInstance(from_date);
            dialog.setTargetFragment(AddTimetableFragment.this, FROM_TIME_REQUEST_CODE);
            dialog(dialog);
        });

        // This is used to make the to date ahead of the from date.
        c.setTime(from_date);
        c.add(Calendar.HOUR_OF_DAY, 1); // makes it an hour ahead
        to_date = c.getTime();

        toTime = root.findViewById(R.id.to_time);
        convertTimeToString(toTime, to_date.getTime());
        toTime.setOnClickListener(v -> {
            TimeCustomPickerFragment dialog = TimeCustomPickerFragment.newInstance(to_date);
            dialog.setTargetFragment(AddTimetableFragment.this, TO_TIME_REQUEST_CODE);
            dialog(dialog);
        });

        reminder = root.findViewById(R.id.reminder_time);
        reminder.setAdapter(new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_list_item_1, Utils.getEntries()
        ));

        root.findViewById(R.id.save_timetable)
                .setOnClickListener(v -> {
                    String title = subject.getText().toString().trim();
                    int chosenColor = color.getBackgroundTintList().getDefaultColor();

                    int reminderIndex = reminder.getSelectedItemPosition();
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(from_date);
                    c1.set(Calendar.MINUTE, -getReminderInt(reminderIndex));

                    if (title.isEmpty() || chosenDay.equals("")) {
                        Toast.makeText(getActivity(), "Enter a subject and select a day to save", Toast.LENGTH_SHORT).show();
                    } else if (from_date.getTime() == to_date.getTime()) {
                        // TODO: intentionally left blank. it won't save!
                    }else {
                        dismiss();
                        // I am using the id as the request code in setting its notification
                        int requestCode = (int) database.appDao().insert(new TimetableData(title, chosenDay, from_date.getTime(), to_date.getTime(), chosenColor, reminderIndex));
                        NotificationUtils.scheduleNotification(requireContext(), c1.getTime().getTime(), title, "", requestCode);
                        ((DialogCloseListener) fragment).reloadTimetable(requestCode);
                    }
                });

        return root;
    }

    private void dialog(DialogFragment dialog) {
        dialog.show(getFragmentManager(), DIALOG_TIME);
    }

    private void chosenDay(View view){
        if (chosenView != null)
        chosenView.setBackground(getResources().getDrawable(android.R.color.transparent, null));
        view.setBackground(getResources().getDrawable(R.drawable.button_beat_box, null));
        chosenView = view;
    }

    private void openColorPicker(final FloatingActionButton colorBtn) {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(getContext(), colorBtn.getBackgroundTintList().getDefaultColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // color is the color selected by the user.
                colorBtn.setBackgroundTintList(ColorStateList.valueOf(color));
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // cancel was selected by the user
            }
        });
        colorPicker.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.style.DialogAnimation; // this sets the dialog in motion
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;       // this keeps the dialog at the bottom of the screen
            window.setAttributes(wlp);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == FROM_TIME_REQUEST_CODE) {
            from_date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            convertTimeToString(fromTime, from_date.getTime());
        }
        if (requestCode == TO_TIME_REQUEST_CODE) {
            to_date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            convertTimeToString(toTime, to_date.getTime());
        }
        long duration = to_date.getTime() - from_date.getTime();
        if(TimeUnit.MILLISECONDS.toHours(duration) < 0 || (TimeUnit.MILLISECONDS.toMinutes(duration) % 60) < 0) {
            to_date = from_date;
            convertTimeToString(toTime, to_date.getTime());
        }
    }
}
