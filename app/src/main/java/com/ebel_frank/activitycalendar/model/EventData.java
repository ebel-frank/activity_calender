package com.ebel_frank.activitycalendar.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.ebel_frank.activitycalendar.interfaces.Comparator;

import java.util.List;

import static com.ebel_frank.activitycalendar.utils.Utils.getTime;

@Entity(tableName = "Events")
public class EventData extends Comparator {
    // create id column
    @PrimaryKey(autoGenerate = true)
    private int _id;

    // create text column
    @ColumnInfo(name = "EventName")
    private String eventName;
    @ColumnInfo(name = "Description")
    private String description;
    @ColumnInfo(name = "Category")
    private byte category;
    @ColumnInfo(name = "FromDate")
    private long fromDate;
    @ColumnInfo(name = "ToDate")
    private long toDate;
    @ColumnInfo(name = "AllDay")
    private boolean allDay;
    @ColumnInfo(name = "Reminder")
    private List<Integer> reminder;

    public EventData(int _id, String eventName, String description,
                     byte category, long fromDate, long toDate,
                     boolean allDay, List<Integer> reminder) {
        this._id = _id;
        this.eventName = eventName;
        this.description = description;
        this.category = category;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.allDay = allDay;
        this.reminder = reminder;
    }

    @Ignore
    public EventData(String eventName, String description,
                     byte category, long fromDate, long toDate,
                     boolean allDay, List<Integer> reminder) {
        this.eventName = eventName;
        this.description = description;
        this.category = category;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.allDay = allDay;
        this.reminder = reminder;
    }

    public int get_id() {
        return _id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }

    public byte getCategory() {
        return category;
    }

    public long getFromDate() {
        return fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public List<Integer> getReminder() {
        return reminder;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(byte category) {
        this.category = category;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public void setReminder(List<Integer> reminder) {
        this.reminder = reminder;
    }

    @Override
    public Integer getFromTime() {
        return getTime(this.getFromDate());
    }
}
