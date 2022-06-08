package com.ebel_frank.activitycalendar.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ebel_frank.activitycalendar.model.EventData;
import com.ebel_frank.activitycalendar.model.NoteData;
import com.ebel_frank.activitycalendar.model.QuoteData;
import com.ebel_frank.activitycalendar.model.TimetableData;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AppDao {
/* Event Data Queries*/

    // Insert query
    @Insert(onConflict = REPLACE)
    long insert(EventData data);

    // Delete query
    @Delete
    void delete(EventData data);

    // Update query
    @Update
    void update(EventData data);

    // Get a data query
    @Query("SELECT * FROM Events WHERE _id = :id")
    EventData getEvent(int id);

    // Get all data query
    @Query("SELECT * FROM Events")
    List<EventData> getEventList();

/* Timetable Data Queries*/

    @Insert(onConflict = REPLACE)
    long insert(TimetableData data);

    @Delete
    void delete(TimetableData data);

    @Update
    void update(TimetableData data);

    @Query("SELECT * FROM Timetable WHERE _id = :id")
    TimetableData getTimetable(int id);

    @Query("SELECT * FROM Timetable WHERE Day = :day")
    List<TimetableData> getTodayTimetable(String day);

    @Query("SELECT * FROM Timetable")
    List<TimetableData> getTimetableList();

/* Notes Data Queries*/

    @Insert(onConflict = REPLACE)
    void insert(NoteData data);

    @Delete
    void delete(NoteData data);

    @Update
    void update(NoteData data);

    @Query("SELECT * FROM Notes WHERE _id = :id")
    NoteData getNote(int id);

    @Query("SELECT * FROM Notes")
    List<NoteData> getNotesList();

/* Quotes Data Queries*/

    @Query("SELECT * FROM Quotes WHERE _id = :id")
    QuoteData getQuote(int id);

//    // Delete all query
//    @Delete
//    void reset(List<NoteData> mainData);

}
