package com.infodesire.jglu.util;

import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

    public static Date d( int year, int month, int day ) {
        return new Calendar.Builder().setDate( year, month - 1, day ).build().getTime();
    }

    public static Date d( int year, int month, int day, int hour, int minute, int second ) {
        return new Calendar.Builder()
                .setDate( year, month - 1, day )
                .setTimeOfDay( hour, minute, second )
                .build().getTime();
    }

}
