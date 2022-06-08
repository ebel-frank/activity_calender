package com.ebel_frank.activitycalendar.utils;

import static android.content.Context.MODE_PRIVATE;
import static com.ebel_frank.activitycalendar.utils.Utils.getReminderInt;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.activity.MainActivity;
import com.ebel_frank.activitycalendar.activity.SettingsActivity;
import com.ebel_frank.activitycalendar.database.AppDatabase;
import com.ebel_frank.activitycalendar.model.EventData;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class NotificationUtils {

    private static int ALARM_REQUEST_CODE;
    static final String CHANNEL_ID = "com.ebel_frank.activitycalendar.utils";
    private static final String STOP_MUSIC = "stopMusic";

    public static void scheduleNotification(Context context, long time, String title, String text, int requestCode) {
        ALARM_REQUEST_CODE = requestCode;
        Context PackageContext = context.getApplicationContext();
        Intent intent = new Intent(PackageContext, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        PendingIntent pending = PendingIntent.getBroadcast(PackageContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Schedule notification
        AlarmManager manager = (AlarmManager) PackageContext.getSystemService(Context.ALARM_SERVICE);
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
    }

    public static void scheduleNotification(Context context, long time, String text, int requestCode) {
        ALARM_REQUEST_CODE = requestCode;
        Context PackageContext = context.getApplicationContext();
        Intent intent = new Intent(PackageContext, NotificationReceiver.class);
        intent.putExtra("text", text);
        PendingIntent pending = PendingIntent.getBroadcast(PackageContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Schedule notification
        AlarmManager manager = (AlarmManager) PackageContext.getSystemService(Context.ALARM_SERVICE);
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
    }

    private static void resetReminder(Context context, int requestCode) {
        EventData event = AppDatabase.getInstance(context).appDao().getEvent(requestCode);
        if (event.getReminder().size() == 2) {
            final Calendar calender = Calendar.getInstance();
            calender.setTime(new Date(event.getFromDate()));
            calender.add(Calendar.MINUTE, -getReminderInt(event.getReminder().get(1)));
            NotificationUtils.scheduleNotification(context, calender.getTime().getTime(),
                    event.getEventName(), event.getDescription(), requestCode);
            AppDatabase.getInstance(context).appDao().update(event);
        }
    }

    public static void cancelNotification(Context context, int requestCode) {
        Context PackageContext = context.getApplicationContext();
        Intent intent = new Intent(PackageContext, NotificationReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(PackageContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Cancel notification
        AlarmManager manager = (AlarmManager) PackageContext.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pending);
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        File musicFile;
        static MediaPlayer notification_alert;

        public static File getMusicFile(Context context) {
            File externalFileDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            if (externalFileDir == null) {
                return null;
            }
            return new File(externalFileDir, "-8hor72iz6on9s38");
        }

        public static void stopMusic() {
            if (notification_alert != null) {
                notification_alert.stop();
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences preferences = context.getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE);
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            boolean vibrate = preferences.getBoolean("vibrate", false);
            if (vibrate && vibrator.hasVibrator()) {
                // starts time delay
                // vibrate 500 milliseconds and sleep 1000 milliseconds
                // vibrate 1000 milliseconds and sleep 2000 milliseconds
                long[] pattern = {0, 500, 1000, 1000, 2000, 500, 1000, 1000, 2000, 1000, 2000};
                vibrator.vibrate(pattern, -1); // does not repeat
            }
            boolean notify =  preferences.getBoolean("notification", false);
            if (notify) {
                musicFile = getMusicFile(context);
                if (!musicFile.exists()) {
                    notification_alert = MediaPlayer.create(context, R.raw.notify);
                    notification_alert.start();
                } else {
                    try {
                        notification_alert = new MediaPlayer();
                        notification_alert.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        notification_alert.setDataSource(context, Uri.fromFile(musicFile));
                        notification_alert.prepare();
                        notification_alert.start();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                // Creates the intent needed to show the notification
                Intent notificationIntent = new Intent(context, MainActivity.class);
                notificationIntent.putExtra(STOP_MUSIC, STOP_MUSIC);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent contentIntent = PendingIntent.getActivity(context, ALARM_REQUEST_CODE, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                notificationIntent = new Intent(context, StopMusic.class);
                notificationIntent.putExtra(STOP_MUSIC, STOP_MUSIC);
                PendingIntent snoozeIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Build notification based on Intent
                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_menu_today)
                        .setContentTitle(intent.getStringExtra("title"))
                        .setContentText(intent.getStringExtra("text"))
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .setOngoing(true)   // Can't cancel your notification (except NotificationManager.cancel(); )
                        .addAction(R.mipmap.ic_launcher, "DISMISS", snoozeIntent)
                        .setOnlyAlertOnce(true)  // allow notification for only first time/
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setVibrate(new long[2])
                        .build();

                // Show notification
                NotificationManagerCompat manager = NotificationManagerCompat.from(context);
                manager.notify(ALARM_REQUEST_CODE, notification);
            }
        }
    }

    public static class StopMusic extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(STOP_MUSIC) != null) {
                resetReminder(context, ALARM_REQUEST_CODE);
                NotificationReceiver.stopMusic();
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(ALARM_REQUEST_CODE);
            }
        }
    }
}
