package com.ebel_frank.activitycalendar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.ebel_frank.activitycalendar.R;

public class RateUs extends Dialog {
    public RateUs(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rate);

        final Button rateNow = findViewById(R.id.rate_now);
        final AppCompatRatingBar ratingBar = findViewById(R.id.rating_bar);
        rateNow.setOnClickListener(view -> {
            try {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id="+getContext().getPackageName())));
            } catch (Exception e) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id="+getContext().getPackageName())));
            }


        });
    }
}
