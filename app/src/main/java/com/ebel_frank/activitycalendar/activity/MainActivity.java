package com.ebel_frank.activitycalendar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.interfaces.setTitleSubtitleText;
import com.ebel_frank.activitycalendar.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements setTitleSubtitleText {

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);
        title = findViewById(R.id.layout_title);

        Utils.createNotificationChannel(MainActivity.this);

        findViewById(R.id.tips).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TipsActivity.class)));

        findViewById(R.id.settings).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void setTitleTxt(int title) {
        this.title.setText(title);
    }

}
