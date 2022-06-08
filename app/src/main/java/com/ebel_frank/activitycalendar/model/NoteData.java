package com.ebel_frank.activitycalendar.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Notes")
public class NoteData {
    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "Title")
    private String title;
    @ColumnInfo(name = "Content")
    private String content;
    @ColumnInfo(name = "Date")
    private long date;
    @ColumnInfo(name = "ContentIcon")
    private String[] contentIcon;

    public NoteData(int _id, String title, String content, long date, String[] contentIcon) {
        this._id = _id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.contentIcon = contentIcon;
    }

    @Ignore
    public NoteData(String title, String content, long date, String[] contentIcon) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.contentIcon = contentIcon;
    }

    @Ignore
    public NoteData(){}

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String[] getContentIcon() {
        return contentIcon;
    }

    public void setContentIcon(String[] contentIcon) {
        this.contentIcon = contentIcon;
    }

    public boolean isEmpty() {
        return title.isEmpty() && content.isEmpty();
    }
}
