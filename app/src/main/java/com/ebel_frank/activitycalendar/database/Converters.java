package com.ebel_frank.activitycalendar.database;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

public class Converters {

    @TypeConverter
    public static List<Integer> toIntArray(String value) {
        List<Integer> array = new ArrayList<>(3);
        String[] values = value.split(",");
        for (String s : values) {
            try {
                array.add(Integer.parseInt(s));
            } catch (Exception ignored) {
            }
        }
        return array;
    }

    @TypeConverter
    public static String fromIntArray(List<Integer> list) {
        String value = "";
        for(int i: list) {
            value += "," + i;
        }
        return value;
    }

    @TypeConverter
    public static String[] toStringArray(String value) {
        return value.split(",");
    }

    @TypeConverter
    public static String fromStringArray(String[] arr) {
        String value = "";
        for(String i: arr) {
            value += "," + i;
        }
        return value;
    }
}
