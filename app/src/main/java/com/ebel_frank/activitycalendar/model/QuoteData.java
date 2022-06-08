package com.ebel_frank.activitycalendar.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Quotes")
public class QuoteData {
    // create id column
    @PrimaryKey(autoGenerate = true)
    private int _id;

    // create text column
    @ColumnInfo(name = "Quote")
    private String quote;
    @ColumnInfo(name = "Author")
    private String author;

    public int get_id() {
        return _id;
    }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
