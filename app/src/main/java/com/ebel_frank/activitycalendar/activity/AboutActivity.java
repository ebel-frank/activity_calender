package com.ebel_frank.activitycalendar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ebel_frank.activitycalendar.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button share = findViewById(R.id.share);
        share.setOnClickListener(v -> {
            Intent intent = share(AboutActivity.this);
            startActivity(Intent.createChooser(intent, "Share link via"));
        });

        ((TextView)findViewById(R.id.developer_text)).setText(
                Html.fromHtml("Designed and Developed by<br><b>Horizons App Team</b>")
        );
    }

    public static Intent share(Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String text = "\nHey,\nLet me recommend you this application, It helps keep all your events in order: ";
        String link = "https://play.google.com/store/apps/details?id=" + context.getPackageName();
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, text+link+"\n");
        return shareIntent;
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
