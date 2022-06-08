package com.ebel_frank.activitycalendar.interfaces;

public abstract class Comparator implements Comparable<Comparator> {

    // This returns the time e.g 9:00 AM that I am using to sort the event and timetable
    public abstract Integer getFromTime();

    @Override
    public int compareTo(Comparator o) {
        return this.getFromTime().compareTo(o.getFromTime());
    }
}
