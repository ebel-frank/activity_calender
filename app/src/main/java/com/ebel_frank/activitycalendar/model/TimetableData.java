package com.ebel_frank.activitycalendar.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.ebel_frank.activitycalendar.interfaces.Comparator;

import static com.ebel_frank.activitycalendar.utils.Utils.getTime;

@Entity(tableName = "Timetable")
public class TimetableData extends Comparator {
    // create id column
    @PrimaryKey(autoGenerate = true)
    private int _id;

    // create text column
    @ColumnInfo(name = "Title")
    private String title;
    @ColumnInfo(name = "Day")
    private String day;
    @ColumnInfo(name = "FromDate")
    private long fromDate;
    @ColumnInfo(name = "ToDate")
    private long toDate;
    @ColumnInfo(name = "Color")
    private int color;
    @ColumnInfo(name = "Reminder")
    private int reminder;

    public TimetableData(String title, String day,
                         long fromDate, long toDate,
                         int color, int reminder) {
        this.title = title;
        this.day = day;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.color = color;
        this.reminder = reminder;
    }

    public int get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getDay() {
        return day;
    }

    public long getFromDate() {
        return fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public int getColor() {
        return color;
    }

    public int getReminder() {
        return reminder;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    @Override
    public Integer getFromTime() {
        return getTime(this.getFromDate());
    }
}
