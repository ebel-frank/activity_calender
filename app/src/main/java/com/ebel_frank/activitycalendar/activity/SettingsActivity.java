package com.ebel_frank.activitycalendar.activity;

import static com.ebel_frank.activitycalendar.utils.Utils.copyMusicFile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.dialog.RateUs;
import com.ebel_frank.activitycalendar.utils.NotificationUtils;
import com.ebel_frank.activitycalendar.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS = "com.ebel_frank.activitycalendar_preferences";
    public static final String KEY_SOUND_TITLE = "sound_title";
    private static SharedPreferences preferences;
    private static String selectedAudioTitle;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        Preference about, rate_us, share, contact, sound;
        SwitchPreference darkTheme, notification;
        private static final int SOUND_REQUEST_CODE = 0;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, final String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);


            darkTheme = findPreference("darkTheme");
            assert darkTheme != null;
            darkTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((boolean)newValue) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            });

            sound = findPreference("sound");
            assert sound != null;
            sound.setSummary(preferences.getString(KEY_SOUND_TITLE, "Default sound"));
            sound.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(Intent.createChooser(intent, "Search with:"), SOUND_REQUEST_CODE);
                return true;
            });

            notification = findPreference("notification");
            assert notification != null;
            notification.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((boolean)newValue && !NotificationManagerCompat.from(requireContext().getApplicationContext()).areNotificationsEnabled()) {
                        // Because the user took an action to create a notification, we create a prompt to let
                        // the user re-enable notifications for this application again.
                        Snackbar snackbar = Snackbar.make(
                                requireView(),
                                "You need to enable notifications for this app",
                                Snackbar.LENGTH_LONG)
                                .setAction("ENABLE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Links to this app's notification settings
                                        startActivity(Utils.openNotificationSettingsForApp(requireActivity().getPackageName(), requireActivity().getApplicationInfo().uid));
                                    }
                                });
                        snackbar.show();
                        return  false;
                }
                return true;
            });

            about = findPreference("about");
            assert about != null;
            about.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getContext(), AboutActivity.class));
                return true;
            });
            rate_us = findPreference("rate");
            assert rate_us != null;
            rate_us.setOnPreferenceClickListener(preference -> {
                RateUs rateUs = new RateUs(requireContext());
                rateUs.getWindow().setBackgroundDrawable(
                        new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                rateUs.show();
                return true;
            });
            share = findPreference("share");
            assert share != null;
            share.setOnPreferenceClickListener(preference -> {
                Intent intent = AboutActivity.share(getActivity());
                startActivity(Intent.createChooser(intent, "Share link via"));
                return true;
            });
            contact = findPreference("contact");
            assert contact != null;
            contact.setOnPreferenceClickListener(preference -> {
                Intent contactIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                contactIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ebelfrank.dev@gmail.com"});
                contactIntent.putExtra(Intent.EXTRA_SUBJECT,"Activity Calender errors and suggestions");
                contactIntent.putExtra(Intent.EXTRA_TEXT, "Hello Frank, ");
                try {
                    startActivity(contactIntent);
                }catch(android.content.ActivityNotFoundException ex){
                    Toast.makeText(getActivity(), "No email client installed", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK && data != null && requestCode == SOUND_REQUEST_CODE) {
                selectedAudioTitle = getFileName(data.getData());
                sound.setSummary(selectedAudioTitle);
                editor = preferences.edit();
                editor.putString(KEY_SOUND_TITLE, selectedAudioTitle);
                editor.apply();
            }
        }

        private String getFileName(Uri uri) {
            String[] title_array = Objects.requireNonNull(uri.getPath()).split("/");
            String filename = title_array[title_array.length - 1];
            copyMusicFile(getContext(), NotificationUtils.NotificationReceiver.getMusicFile(requireContext()), uri);
            return filename;
        }
    }
}

