package com.infodesire.jglu.busdata;

import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class BusValueTest {

    @Test
    public void testString() throws ParseException {

        assertEquals( "", new BusValue( BusType.STRING, "" ).toString() );
        assertEquals( "Hello World.", new BusValue( BusType.STRING, "Hello World." ).toString() );

        assertTrue( new BusValue( BusType.STRING, "" ).isNull() );
        assertTrue( new BusValue( BusType.STRING, null ).isNull() );
        assertFalse( new BusValue( BusType.STRING, "1" ).isNull() );

    }

    @Test
    public void testBoolean() throws ParseException {

        assertEquals( "1", new BusValue( BusType.BOOLEAN, "1" ).toString() );
        assertEquals( "1", new BusValue( BusType.BOOLEAN, "true" ).toString() );

        assertEquals( "0", new BusValue( BusType.BOOLEAN, "0" ).toString() );
        assertEquals( "0", new BusValue( BusType.BOOLEAN, "false" ).toString() );
        assertEquals( "0", new BusValue( BusType.BOOLEAN, "hello" ).toString() );

        assertTrue( new BusValue( BusType.BOOLEAN, "" ).isNull() );
        assertTrue( new BusValue( BusType.BOOLEAN, null ).isNull() );
        assertFalse( new BusValue( BusType.BOOLEAN, "1" ).isNull() );

    }

    @Test
    public void testInteger() throws ParseException {

        assertEquals( "1", new BusValue( BusType.INTEGER, "1" ).toString() );

        assertEquals( "0", new BusValue( BusType.INTEGER, "0" ).toString() );
        assertEquals( "1000", new BusValue( BusType.INTEGER, "1000" ).toString() );
        assertEquals( "-1", new BusValue( BusType.INTEGER, "-1" ).toString() );
        assertEquals( "-1000", new BusValue( BusType.INTEGER, "-1000" ).toString() );

        try {
            new BusValue( BusType.INTEGER, "a" );
            fail( "ParseException should have been thrown" );
        }
        catch( ParseException ex ) {}

        assertTrue( new BusValue( BusType.INTEGER, "" ).isNull() );
        assertTrue( new BusValue( BusType.INTEGER, null ).isNull() );
        assertFalse( new BusValue( BusType.INTEGER, "1" ).isNull() );

    }


    @Test
    public void testFloat() throws ParseException {

        assertEquals( "1.0", new BusValue( BusType.FLOAT, "1" ).toString() );

        assertEquals( "0.0", new BusValue( BusType.FLOAT, "0" ).toString() );
        assertEquals( "1000.02", new BusValue( BusType.FLOAT, "1000.02" ).toString() );
        assertEquals( "-1.0", new BusValue( BusType.FLOAT, "-1" ).toString() );
        assertEquals( "-1000.0", new BusValue( BusType.FLOAT, "-1000" ).toString() );

        try {
            new BusValue( BusType.FLOAT, "a" );
            fail( "ParseException should have been thrown" );
        }
        catch( ParseException ex ) {}

        assertTrue( new BusValue( BusType.FLOAT, "" ).isNull() );
        assertTrue( new BusValue( BusType.FLOAT, null ).isNull() );
        assertFalse( new BusValue( BusType.FLOAT, "1" ).isNull() );

    }

    @Test
    public void testDate() throws ParseException {

        // normal -------------------------------------------------------

        assertEquals( "2023-03-07", new BusValue( d( 2023, 03, 7 ), true, false ).toString() );
        assertEquals( "2023-03-07", new BusValue( d( 2023, 03, 7, 12, 11, 20 ), true, false ).toString() );

        assertEquals( d( 2000, 1, 1 ), new BusValue( BusType.DATE, "2000-01-01" ).getDate() );
        assertEquals( d( 2000, 1, 1 ), new BusValue( BusType.DATE, "2000-01-01_07-20-00" ).getDate() );

        // exceptions -------------------------------------------------------

        try {
            new BusValue( null, false, false );
            fail( "There should be an exception when both date and time are false." );
        }
        catch( RuntimeException ex ) {}

        try {
            new BusValue( BusType.DATE, "abc" );
            fail( "There should be a parse exception." );
        }
        catch( ParseException ex ) {}


        // null and empty handling -------------------------------------------------------

        assertFalse( new BusValue( BusType.DATE, "1997-06-04" ).isNull() );
        assertEquals( "", new BusValue( BusType.DATE, null ).toString() );
        assertTrue( new BusValue( BusType.DATE, null ).isNull() );
        assertEquals( null, new BusValue( BusType.DATE, null ).getDate() );
        assertEquals( null, new BusValue( BusType.DATE, "" ).getDate() );

    }

    @Test
    public void testDateTime() throws ParseException {

        // normal -------------------------------------------------------

        assertEquals( "2023-03-07_00:00:00", new BusValue( d( 2023, 03, 7 ), true, true ).toString() );
        assertEquals( "2023-03-07_12:11:20", new BusValue( d( 2023, 03, 7, 12, 11, 20 ), true, true ).toString() );

        assertEquals( d( 2000, 1, 1, 7, 20, 0 ), new BusValue( BusType.DATETIME, "2000-01-01_07:20:00" ).getDate() );

        // exceptions -------------------------------------------------------

        try {
            new BusValue( null, false, false );
            fail( "There should be an exception when both date and time are false." );
        }
        catch( RuntimeException ex ) {}

        try {
            new BusValue( BusType.DATETIME, "abc" );
            fail( "There should be a parse exception." );
        }
        catch( ParseException ex ) {}

        try {
            new BusValue( BusType.DATETIME, "2000-01-01" );
            fail( "There should be a parse exception." );
        }
        catch( ParseException ex ) {}

        // null and empty handling -------------------------------------------------------

        assertFalse( new BusValue( BusType.DATETIME, "1997-06-04_10:12:20" ).isNull() );
        assertEquals( "", new BusValue( BusType.DATETIME, null ).toString() );
        assertTrue( new BusValue( BusType.DATETIME, null ).isNull() );
        assertEquals( null, new BusValue( BusType.DATETIME, null ).getDate() );
        assertEquals( null, new BusValue( BusType.DATETIME, "" ).getDate() );

    }

    @Test
    public void testTime() throws ParseException {

        // normal -------------------------------------------------------

        assertEquals( "12:10:41", new BusValue( d( 0, 0, 0, 12, 10, 41 ), false, true ).toString() );
        assertEquals( "12:11:20", new BusValue( d( 2023, 03, 7, 12, 11, 20 ), false, true ).toString() );

        assertEquals( d( 1970, 1, 1, 7, 20, 0 ), new BusValue( BusType.TIME, "07:20:00" ).getDate() );

        // exceptions -------------------------------------------------------

        try {
            new BusValue( null, false, false );
            fail( "There should be an exception when both date and time are false." );
        }
        catch( RuntimeException ex ) {}

        try {
            new BusValue( BusType.TIME, "abc" );
            fail( "There should be a parse exception." );
        }
        catch( ParseException ex ) {}

        try {
            new BusValue( BusType.TIME, "2000-01-01_10:10:20" );
            fail( "There should be a parse exception." );
        }
        catch( ParseException ex ) {}

        // null and empty handling -------------------------------------------------------

        assertFalse( new BusValue( BusType.TIME, "10:12:20" ).isNull() );
        assertEquals( "", new BusValue( BusType.TIME, null ).toString() );
        assertTrue( new BusValue( BusType.TIME, null ).isNull() );
        assertEquals( null, new BusValue( BusType.TIME, null ).getDate() );
        assertEquals( null, new BusValue( BusType.TIME, "" ).getDate() );

    }

    public Date d( int year, int month, int day ) {
        return new Calendar.Builder().setDate( year, month - 1, day ).build().getTime();
    }

    public Date d( int year, int month, int day, int hour, int minute, int second ) {
        return new Calendar.Builder()
                .setDate( year, month - 1, day )
                .setTimeOfDay( hour, minute, second )
                .build().getTime();
    }

}