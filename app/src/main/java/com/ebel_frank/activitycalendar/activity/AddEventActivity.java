package com.ebel_frank.activitycalendar.activity;

import static com.ebel_frank.activitycalendar.utils.Utils.getReminderInt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.adapter.ReminderAdapter;
import com.ebel_frank.activitycalendar.database.AppDatabase;
import com.ebel_frank.activitycalendar.dialog.DatePickerFragment;
import com.ebel_frank.activitycalendar.dialog.TimePickerFragment;
import com.ebel_frank.activitycalendar.model.EventData;
import com.ebel_frank.activitycalendar.utils.NotificationUtils;
import com.ebel_frank.activitycalendar.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AddEventActivity extends AppCompatActivity {

    private EditText eventName, description;
    private Button fromDate, fromTime, toDate, toTime;
    private View categoryDeSelect;
    List<Integer> reminderList = new ArrayList<>();

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String EXTRA_ID = "com.ebeledike.iris._id";

    private static final int FROM_DATE_REQUEST_CODE = 0;
    private static final int FROM_TIME_REQUEST_CODE = 2;
    private static final int TO_DATE_REQUEST_CODE = 1;
    private static final int TO_TIME_REQUEST_CODE = 3;
    private final int SPEECH_REQUEST_CODE = 4;

    private boolean isAllDay= false;

    private Date from_date = new Date();
    private Date to_date;
    private final Calendar calender = Calendar.getInstance();
    private AppDatabase database;
    private int _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // assigns all required variables
        eventName = findViewById(R.id.event_name);
        description = findViewById(R.id.description);
        ImageView speechToText = findViewById(R.id.speech);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) speechToText.setVisibility(View.VISIBLE);
                else speechToText.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        fromDate = findViewById(R.id.from_tab_date);
        fromTime = findViewById(R.id.from_tab_time);
        toDate = findViewById(R.id.to_tab_date);
        toTime = findViewById(R.id.to_tab_time);
        SwitchCompat allDayCheckBox = findViewById(R.id.all_day);
        RecyclerView reminders = findViewById(R.id.reminders);
        TextView addReminder = findViewById(R.id.add_reminder);

        database = AppDatabase.getInstance(this);   // instance of the database class

        _id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (_id != -1) {
            EventData eventData = database.appDao().getEvent(_id);
            eventName.setText(eventData.getEventName());
            description.setText(eventData.getDescription());

            categoryDeSelect = selectedView(eventData.getCategory());
            chosenView(categoryDeSelect, null);

            from_date = new Date(eventData.getFromDate());
            to_date = new Date(eventData.getToDate());
            if (eventData.isAllDay()) {
                setTimeVisibility(View.INVISIBLE);
            }
            allDayCheckBox.setChecked(eventData.isAllDay());
            reminderList = eventData.getReminder();
        }

        Utils.convertDateToString(fromDate, from_date.getTime());
        fromDate.setOnClickListener(v -> {
            DatePickerFragment dialog = DatePickerFragment.newInstance(from_date, FROM_DATE_REQUEST_CODE);
            dialog(dialog, DIALOG_DATE);
        });

        Utils.convertTimeToString(fromTime, from_date.getTime());
        fromTime.setOnClickListener(v -> {
            TimePickerFragment dialog = TimePickerFragment.newInstance(from_date, FROM_TIME_REQUEST_CODE);
            dialog(dialog, DIALOG_TIME);
        });

        if(_id == -1) {
            // This is used to make the to date ahead of the from date.
            calender.setTime(from_date);
            calender.add(Calendar.HOUR_OF_DAY, 1); // makes it a day ahead
            to_date = calender.getTime();
        }

        Utils.convertDateToString(toDate, to_date.getTime());
        toDate.setOnClickListener(v -> {
            DatePickerFragment dialog = DatePickerFragment.newInstance(to_date, TO_DATE_REQUEST_CODE);
            dialog(dialog, DIALOG_DATE);
        });

        Utils.convertTimeToString(toTime, to_date.getTime());
        toTime.setOnClickListener(v -> {
            TimePickerFragment dialog = TimePickerFragment.newInstance(to_date, TO_TIME_REQUEST_CODE);
            dialog(dialog, DIALOG_TIME);
        });

        allDayCheckBox.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_regular));
        allDayCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isAllDay = true;
                setTimeVisibility(View.INVISIBLE);
            }else{
                isAllDay = false;
                setTimeVisibility(View.VISIBLE);
            }
        });

        reminders.setLayoutManager(new LinearLayoutManager(this));
        ReminderAdapter reminderAdapter = new ReminderAdapter(reminderList, addReminder);
        reminders.setAdapter(reminderAdapter);

    }

    public void saveEvent(View view) {
        String event_name = eventName.getText().toString();
        String event_description = description.getText().toString();
        byte category;
        Collections.sort(reminderList);
        if(isAllDay) {
            calender.setTime(from_date);
            calender.set(Calendar.HOUR_OF_DAY, 0);
            calender.set(Calendar.MINUTE, 0);
            from_date = calender.getTime();
            //----------------------------//
            calender.setTime(to_date);
            calender.set(Calendar.HOUR_OF_DAY, 23);
            calender.set(Calendar.MINUTE, 59);
            to_date = calender.getTime();
        }
        calender.setTime(from_date);
        if (!reminderList.isEmpty()) {
            calender.add(Calendar.MINUTE, -getReminderInt(reminderList.get(0)));
        }
        long time = calender.getTime().getTime();
        if(event_name.isEmpty()){
            eventName.setError("Required");
        } else if (categoryDeSelect == null) {
            ((TextView)findViewById(R.id.category)).setError("");
        } else if (time <= new Date().getTime()) {
            Toast.makeText(AddEventActivity.this, "Reminder is too close", Toast.LENGTH_SHORT).show();
        } else if (_id != -1) {
            category = selectedView(categoryDeSelect);
            database.appDao().update(new EventData(_id, event_name, event_description, category, from_date.getTime(), to_date.getTime(), isAllDay, reminderList));
            NotificationUtils.scheduleNotification(this, time, event_name, event_description, _id);
            finish();
        }else{
            category = selectedView(categoryDeSelect);
            // I am using the id as the request code in setting its notification
            int requestCode = (int) database.appDao().insert(new EventData(event_name, event_description, category, from_date.getTime(), to_date.getTime(), isAllDay, reminderList));
            NotificationUtils.scheduleNotification(this, time, event_name, event_description, requestCode);
            finish();
        }
    }

    private View selectedView(byte n) {
        switch (n) {
            case 0:
                return findViewById(R.id.music);
            case 1:
                return findViewById(R.id.business);
            case 2:
                return findViewById(R.id.technology);
            case 3:
                return findViewById(R.id.social);
            case 4:
                return findViewById(R.id.education);
            default:
                return findViewById(R.id.other);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private byte selectedView(View view) {
        switch (view.getId()) {
            case R.id.music:
                return 0;
            case R.id.business:
                return 1;
            case R.id.technology:
                return 2;
            case R.id.social:
                return 3;
            case R.id.education:
                return 4;
            default:
                return 5;
        }
    }

    public void choseCategory(View view) {
        chosenView(view, categoryDeSelect);
        categoryDeSelect = view;
    }

    private void chosenView(View selected, View previous){
        if (previous != null)
        previous.setBackground(ContextCompat.getDrawable(this, R.drawable.search_background));
        selected.setBackground(ContextCompat.getDrawable(this, R.drawable.notes_background));
    }

    private void setTimeVisibility(int visibility) {
        fromTime.setVisibility(visibility);
        toTime.setVisibility(visibility);
    }

    public void dialog(DialogFragment dialog, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        dialog.show(fm, tag);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK && data == null) return;
        switch (requestCode) {
            case FROM_DATE_REQUEST_CODE:
                from_date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                Utils.convertDateToString(fromDate, from_date.getTime());
                break;
            case FROM_TIME_REQUEST_CODE:
                from_date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                Utils.convertTimeToString(fromTime, from_date.getTime());
                break;
            case TO_DATE_REQUEST_CODE:
                to_date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                Utils.convertDateToString(toDate, to_date.getTime());
                break;
            case TO_TIME_REQUEST_CODE:
                to_date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                Utils.convertTimeToString(toTime, to_date.getTime());
                break;
            case SPEECH_REQUEST_CODE:
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                description.setText(result.get(0));
                break;
        }
        long duration = to_date.getTime() - from_date.getTime();
        if(TimeUnit.MILLISECONDS.toDays(duration) < 1) {
            Calendar c = Calendar.getInstance();
            c.setTime(from_date);
            int day_of_year = c.get(Calendar.DAY_OF_YEAR);
            c.setTime(to_date);
            c.set(Calendar.DAY_OF_YEAR, day_of_year);
            to_date = c.getTime();
            Utils.convertDateToString(toDate, to_date.getTime());
        }
        //Log.d("TAG", ""+(TimeUnit.MILLISECONDS.toMinutes(duration)));
        if(TimeUnit.MILLISECONDS.toHours(duration) < 0 || (TimeUnit.MILLISECONDS.toMinutes(duration) % 60) < 0) {
            to_date = from_date;
            Utils.convertTimeToString(toTime, to_date.getTime());
        }
    }

    public static Intent newIntent(Context context, int _id) {
        Intent i = new Intent(context, AddEventActivity.class);
        i.putExtra(EXTRA_ID, _id);
        return i;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } else {
            Toast.makeText(AddEventActivity.this, "Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
        }
    }

}
