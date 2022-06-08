package com.ebel_frank.activitycalendar.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ebel_frank.activitycalendar.model.EventData;
import com.ebel_frank.activitycalendar.model.NoteData;
import com.ebel_frank.activitycalendar.model.QuoteData;
import com.ebel_frank.activitycalendar.model.TimetableData;
import com.fstyle.library.helper.AssetSQLiteOpenHelperFactory;

// Add database entities
@Database(entities = {EventData.class, TimetableData.class, NoteData.class, QuoteData.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "ActivityDB.db";
    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DATABASE_NAME)
                    .openHelperFactory(new AssetSQLiteOpenHelperFactory())
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
            .build();
        }
        return INSTANCE;
    }

    public abstract AppDao appDao();

}
