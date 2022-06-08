package com.ebel_frank.activitycalendar.utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.adapter.SavedEventsAdapter;
import com.ebel_frank.activitycalendar.adapter.SavedNotesAdapter;
import com.ebel_frank.activitycalendar.adapter.TodayTaskAdapter;
import com.ebel_frank.activitycalendar.database.AppDatabase;
import com.ebel_frank.activitycalendar.interfaces.Comparator;
import com.ebel_frank.activitycalendar.model.EventData;
import com.ebel_frank.activitycalendar.model.NoteData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {

    static AlertDialog alert;
    private Utils() {}

    public static int getReminderInt(int index) {
        return (new int[]{0, 1, 5, 15, 30, 60, 180, 360, 720, 1440})[index];
    }

    // TODO
    public static String[] getEntries() {
        return new String[]{"1 minute", "5 minutes", "15 minutes", "30 minutes", "1 hour", "3 hours", "6 hours", "12 hours", "1 day"};
    };

    public static void showRadioBtnDialog(final Context context, final String[] items, final TextView textView) {
        alert = new AlertDialog.Builder(context)
                .setSingleChoiceItems(items, 0, (dialog, which) -> {

                    if (which == 0) {
                        textView.setText(items[0]);
                    } else if (which == items.length - 1) {
                        // TODO create custom dialog
                        return;
                    } else {
                        textView.setText(String.format("Repeats %s", items[which].toLowerCase()));

                    }
                    alert.dismiss();
                })
                .create();
        alert.show();
    }

    public static Bitmap textToBitmap(String text) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(72);
        //paint.setColor(R.color.ash);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(text) * 0.5f);
        int height = (int) (baseline * paint.descent() * 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    /**
     * Get the picture through uri and compress then returns bitmap
     *
     * @return
     */
    public static Bitmap getBitmapFromURI(Context context, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;

        //Image resolution is based on 480x800
        float hh = 800f;    //The height is set as 800f here
        float ww = 480f;    //Set the width here to 480f
        //Zoom ratio. Because it is a fixed scale, only one data of height or width is used for calculation
        int be = 1;     //be=1 means no scaling
        if (originalWidth > originalHeight && originalWidth > ww) {//If the width is large, scale according to the fixed size of the width
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//If the height is high, scale according to the fixed size of the width
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;

        //Proportional compression
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//Set scaling
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return bitmap;
    }

    public static int getEventIcon(byte n) {
        switch (n) {
            case 0:
                return R.drawable.ic_music;
            case 1:
                return R.drawable.ic_business;
            case 2:
                return R.drawable.ic_tech;
            case 3:
                return R.drawable.ic_social;
            case 4:
                return R.drawable.ic_edu;
            default:
                return R.drawable.ic_events;
        }
    }

    public static String convertDateToString(long date) {
        return  DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault()).format(date);
    }

    public static String getDay() {
        return new Date().toString().split(" ")[0];
    }

    public static Integer getTime(long time) {
        final Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date(time));
        return c1.get(Calendar.HOUR_OF_DAY);
    }

    public static List<EventData> getTodayEvent(List<EventData> events) {
        final Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();
        List<EventData> todayEvent = new ArrayList<>();
        if (events.size() == 0) {
            return todayEvent;
        }

        Date eventDate;
        for (int i=0, n = events.size(); i < n; i++) {
            EventData event = events.get(i);
            try {
                eventDate = new Date(event.getFromDate());
                c2.setTime(eventDate);
                int c1_day = c1.get(Calendar.DAY_OF_YEAR), c2_day = c2.get(Calendar.DAY_OF_YEAR);
                eventDate.setTime(event.getToDate());
                c2.setTime(eventDate);
                if (c1_day == c2_day || c1.getTime().getTime() < c2.getTime().getTime()) {
                    todayEvent.add(event);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return todayEvent;
    }

    public static void convertDateToString(Button btn, long date) {
        String dateText =convertDateToString(date);
        btn.setText(dateText);
    }

    public static String convertTimeToString(long date) {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(date);
    }

    public static void convertTimeToString(Button btn, long date) {
        String timeText = convertTimeToString(date);
        btn.setText(timeText);
    }

    public static void copyMusicFile(Context context, File musicFile, Uri musicUri) {
        try {
            InputStream myInput = context.getContentResolver().openInputStream(musicUri);
            OutputStream myOutput = new FileOutputStream(musicFile);
            byte[] buffer = new byte[4096];
            int length;
            assert myInput != null;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fadeAnimation(final View view, boolean status) {
        if (status) {
            view.animate().alpha(1f).setDuration(500);
            view.setVisibility(View.VISIBLE);
        } else {
            view.animate().alpha(0f).setDuration(500);
            view.setVisibility(View.GONE);
        }
    }

    public static Intent openNotificationSettingsForApp(String packageName, int appUid) {
        // Links to this app's notification settings.
        Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", packageName);
        intent.putExtra("app_uid", appUid);

        // for Android 8 and above
        intent.putExtra("android.provider.extra.APP_PACKAGE", packageName);
        return intent;
    }

    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NotificationUtils.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static class LoadData {

        private final AppDatabase database;
        private final RecyclerView recyclerView;
        private RecyclerView.Adapter adapter;
        private final int type;
        private final View defaultView;

        public LoadData(Context context, RecyclerView recyclerView, int type, View defaultView) {
            database = AppDatabase.getInstance(context);
            this.recyclerView = recyclerView;
            this.type = type;
            this.defaultView = defaultView;
        }

        private void onPreExecute() {

        }

        private void doInBackground() {
            try {
                switch(type) {
                    case 0:     // fot the Home Fragment
                        List<Comparator> totalTask = new ArrayList<>();
                        totalTask.addAll(database.appDao().getTodayTimetable(getDay()));
                        totalTask.addAll(getTodayEvent(database.appDao().getEventList()));
                        Collections.sort(totalTask);

                        adapter = new TodayTaskAdapter(totalTask, defaultView);
                        break;
                    case 1:     // fot the Event Fragment
                        Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();

                        String[] info = {"No past event", "No ongoing event", "No pending event", "No upcoming events"}
                                , titles = {"Past","Today","Tomorrow","Upcoming"};
                        List<Object> values = new ArrayList<>(Arrays.asList(titles));
                        List<EventData> events = database.appDao().getEventList();
                        try {
                            for (int i=0, n = events.size(); i < n; i++) {
                                EventData event = events.get(i);
                                long fromDate = event.getFromDate();
                                try {
                                    c2.setTime(new Date(fromDate));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                int c1_day = c1.get(Calendar.DAY_OF_YEAR), c2_day = c2.get(Calendar.DAY_OF_YEAR);
                                int c1_year = c1.get(Calendar.YEAR), c2_year = c2.get(Calendar.YEAR);
                                if (c1_year == c2_year) {
                                    if (c1_day > c2_day ) {
                                        values.add(values.indexOf(titles[0])+1, event);
                                    } else if (c1_day == c2_day) {
                                        values.add(values.indexOf(titles[1])+1, event);
                                    } else if (c1_day + 1 == c2_day) {
                                        values.add(values.indexOf(titles[2])+1, event);
                                    } else /*c1_day + 1 < c2_day*/ {
                                        values.add(values.indexOf(titles[3])+1, event);
                                    }
                                } // else don't increment to values; do nothing
                            }
                        } catch(Exception ex) {
                            // ignored
                        } finally {
                            for (int i=0; i < 4; i++) {
                                try {
                                    if (values.get(values.indexOf(titles[i])+1).getClass().equals(String.class)) {
                                        values.add(values.indexOf(titles[i])+1, info[i]);
                                    }
                                } catch(Exception e) {
                                    values.add(info[3]);
                                }
                            }
                        }

                        adapter = new SavedEventsAdapter(values);
                        break;
                    case 2:     // fot the Notes Fragment
                        List<NoteData> notes = database.appDao().getNotesList();
                        Collections.reverse(notes);
                        adapter = new SavedNotesAdapter(notes);
                        break;
                };
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        }

        private void onPostExecute() {
            recyclerView.setAdapter(adapter);
        }

        public void execute(Activity activity) {
            onPreExecute();
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(() -> {
                doInBackground();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute();
                    }
                });
            });
        }

    }

}
