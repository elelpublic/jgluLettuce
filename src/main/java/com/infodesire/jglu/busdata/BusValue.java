package com.infodesire.jglu.busdata;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * An immutable value in a business object of one of the most used types in such objects.
 * <p>
 *
 * This class mainly deals with serialization and deserialization in redis.
 * Actual values and manipulation are not subject of this class.
 *
 */
public class BusValue {

    static final FastDateFormat dateFormat = FastDateFormat.getInstance( "yyyy-MM-dd" );
    static final FastDateFormat dateTimeFormat = FastDateFormat.getInstance( "yyyy-MM-dd_HH:mm:ss" );
    static final FastDateFormat timeFormat = FastDateFormat.getInstance( "HH:mm:ss" );

    private BusType type;
    private Object value;

    /**
     * Create value by parsing a string
     *
     * @param type Type of value
     * @param stringValue String representation of value (created by toString())
     *
     */
    public BusValue( BusType type, String stringValue ) throws ParseException {
        
        this.type = type;

        if( stringValue == null || stringValue.trim().length() == 0 ) {
            value = null;
        }
        else if( type == BusType.STRING ) {
            set( stringValue  );
        }
        else if( type == BusType.BOOLEAN ) {
            set( stringValue.equals( "true" ) || stringValue.equals( "1" ) );
        }
        else if( type == BusType.INTEGER ) {
            try {
                set( Long.parseLong( stringValue ) );
            }
            catch( NumberFormatException ex ) {
                throw new ParseException( stringValue, 0 );
            }
        }
        else if( type == BusType.FLOAT ) {
            try {
                set( Double.parseDouble( stringValue ) );
            }
            catch( NumberFormatException ex ) {
                throw new ParseException( stringValue, 0 );
            }
        }
        else if( type == BusType.DATE ) {
            value = dateFormat.parse( stringValue );
        }
        else if( type == BusType.DATETIME) {
            value = dateTimeFormat.parse( stringValue );
        }
        else if( type == BusType.TIME ) {
            value = timeFormat.parse( stringValue );
        }
        else if( type == BusType.LIST ) {
            set( ListFormat.parse( stringValue ) );
        }
        else {
            throw new RuntimeException( "Parsing business values of type " + type
                + "not implemented yet." );
        }

    }

    /**
     * Create a string typed values
     *
     */
    public BusValue( String stringValue ) {
        set( stringValue );
    }

    /**
     * Create boolean typed value
     *
     */
    public BusValue( Boolean booleanValue ) {
        set( booleanValue );
    }

    /**
     * Create integer typed value
     *
     */
    public BusValue( Long intValue ) {
        set( intValue );
    }

    /**
     * Create integer typed value
     *
     */
    public BusValue( Double floatValue ) {
        set( floatValue );
    }

    public BusValue( Date dateValue, boolean date, boolean time ) {
        set( dateValue, date, time );
    }

    public BusValue( List<String> listValue ) {
        set( listValue );
    }

    private void set( List<String> listValue ) {
        this.type = BusType.LIST;
        if( listValue == null || listValue.isEmpty() ) {
            value = null;
        }
        else {
            value = listValue;
        }
    }

    private void set( Date dateValue, boolean date, boolean time ) {
        if( !( date || time ) ) {
            throw new RuntimeException( "Date or time or both must be set." );
        }
        if( date ) {
            if( time ) {
                type = BusType.DATETIME;
            }
            else {
                type = BusType.DATE;
            }
        }
        else {
            type = BusType.TIME;
        }
        value = dateValue;
    }

    private void set( Double floatValue ) {
        this.type = BusType.FLOAT;
        value = floatValue;
    }

    private void set( Long intValue ) {
        this.type = BusType.INTEGER;
        value = intValue;
    }

    private void set( Boolean booleanValue ) {
        this.type = BusType.BOOLEAN;
        value = booleanValue;
    }

    private void set( String stringValue ) {
        this.type = BusType.STRING;
        if( stringValue == null ) {
            value = null;
        }
        else {
            value = stringValue.trim();
            if( ((String) value).length() == 0 ) {
                value = null;
            }
        }
    }

    /**
     * @return Parseable string representation of value
     *
     */
    public String toString() {

        if( value == null ) {
            return "";
        }

        if( type == BusType.STRING ) {
            return (String) value;
        }
        else if( type == BusType.BOOLEAN ) {
            return ((Boolean) value) ? "1" : "0";
        }
        else if( type == BusType.INTEGER ) {
            return "" + value;
        }
        else if( type == BusType.FLOAT ) {
            return "" + value;
        }
        else if( type == BusType.DATE ) {
            return dateFormat.format( (Date) value );
        }
        else if( type == BusType.DATETIME ) {
            return dateTimeFormat.format( (Date) value );
        }
        else if( type == BusType.TIME ) {
            return timeFormat.format( (Date) value );
        }
        else if( type == BusType.LIST ) {
            return ListFormat.format( (List<String>) value );
        }
        else {
            throw new RuntimeException( "Serializing business values of type " + type
                    + "not implemented yet." );
        }

    }

    public BusType getType() {
        return type;
    }

    public boolean isNull() {
        return value == null;
    }

    public Date getDate() {
        return (Date) value;
    }

}
