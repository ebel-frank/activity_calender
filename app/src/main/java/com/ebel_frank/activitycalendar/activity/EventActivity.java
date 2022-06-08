package com.ebel_frank.activitycalendar.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.database.AppDatabase;
import com.ebel_frank.activitycalendar.model.EventData;
import com.ebel_frank.activitycalendar.utils.NotificationUtils;
import com.ebel_frank.activitycalendar.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    private static final String EXTRA_ID = "com.ebeledike.iris._id";
    private AppDatabase database;
    private int _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = AppDatabase.getInstance(this);   // instance of the database class

        _id = getIntent().getIntExtra(EXTRA_ID, 0);
        EventData eventData = database.appDao().getEvent(_id);
        setUI(eventData);

        findViewById(R.id.delete).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete this event?")
                .setMessage(getString(R.string.delete_warning))
                .setPositiveButton(R.string.delete, (dialog1, which1) -> {
                    database.appDao().delete(eventData);
                    NotificationUtils.cancelNotification(EventActivity.this, _id);
                    finish();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show());
    }

    private void setUI(EventData eventData) {
        long toDate = eventData.getToDate();
        if (new Date().getTime() > new Date(toDate).getTime()) {
            findViewById(R.id.complete).setVisibility(View.VISIBLE);
        }

        ((ImageView) findViewById(R.id.category)).setImageResource(getCategory(eventData.getCategory()));
        ((TextView) findViewById(R.id.event_name)).setText(eventData.getEventName());

        SimpleDateFormat sDF = new SimpleDateFormat("d MMMM y, hh:mm a", Locale.getDefault());
        ((TextView) findViewById(R.id.date)).setText(sDF.format(eventData.getFromDate()));

        if (!eventData.getReminder().isEmpty()) {
            findViewById(R.id.reminder_layout).setVisibility(View.VISIBLE);
            TextView reminder = findViewById(R.id.reminder);
            StringBuilder text = new StringBuilder();
            for (int i=0, n=eventData.getReminder().size(); i<n; i++) {
                text.append(Utils.getEntries()[eventData.getReminder().get(i)]).append(" before\n");
            }
            reminder.setText(text);
        }

        ((TextView) findViewById(R.id.description)).setText(eventData.getDescription());
    }

    private int getCategory(byte category) {
        switch (category) {
            case 0:
                return R.mipmap.music;
            case 1:
                return R.mipmap.business;
            case 2:
                return R.mipmap.technology;
            case 3:
                return R.mipmap.social;
            case 4:
                return R.mipmap.education;
            default:
                return R.mipmap.events;
        }
    }

    public static Intent newIntent(Context context, int _id) {
        Intent i = new Intent(context, EventActivity.class);
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
}
