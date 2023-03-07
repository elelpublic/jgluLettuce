package com.infodesire.jglu.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

    public static LocalDate d( int year, int month, int day ) {
        return LocalDate.of( year, month, day );
    }

    public static LocalDateTime d( int year, int month, int day, int hour, int minute, int second ) {
        return LocalDateTime.of( year, month, day, hour, minute, second );
    }

    public static LocalTime t( int hour, int minute, int second ) {
        return LocalTime.of( hour, minute, second );
    }

}
