package com.infodesire.jglu.busdata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parse and format lists as values of business objects
 *
 *
 * ab|cd -> ab\|cd
 * ab\cd -> ab\\cd
 * ab\|cd -> ab\\\|cd
 *
 */
public class ListFormat {

    /**
     * Format a list of strings in one string using '|' as a separator.
     *
     * @param list List of strings
     * @return Concatenated element strings
     *
     */
    public static String format( List<String> list ) {
        if( list == null ) {
            return "";
        }
        else {
            return list
                    .stream()
                    .map( ListFormat::escape )
                    .map( String::trim )
                    .map( ListFormat::atLeastOneSpace )
                    .collect( Collectors.joining( "|" ) )
                    .trim();
        }
    }

    /**
     * Escape separators
     *
     * @param input Input string
     * @return Output string with operational chars escaped
     *
     */
    protected static String escape( String input ) {
        if( input == null ) {
            return "";
        }
        else {
            StringBuilder output = new StringBuilder();
            for( int i = 0; i < input.length(); i++ ) {
                char c = input.charAt( i );
                if( c == '|' ) {
                    output.append( "\\|" );
                }
                else if( c == '\\' ) {
                    output.append( "\\\\" );
                }
                else {
                    output.append( c );
                }
            }
            return output.toString();
        }
    }


    /**
     * Parse a pipe ("|") separated string and add elements to a list
     *
     * @param stringValue Input string
     * @return List of elements
     *
     */
    public static List<String> parse( String stringValue ) {
        if( stringValue == null || stringValue.trim().length() == 0 ) {
            return null;
        }
        else {
            List<String> result = new ArrayList<>();
            StringBuilder currentValue = new StringBuilder();
            int len = stringValue.length();
            for( int i = 0; i < len; i++ ) {
                char c = stringValue.charAt( i );
                if( c == '\\' ) {
                    if( i < len - 1 && stringValue.charAt( i+1 ) == '|' ) {
                        currentValue.append( '|' );
                        i++;
                    }
                    else if( i < len - 1 && stringValue.charAt( i+1 ) == '\\' ) {
                        currentValue.append( '\\' );
                        i++;
                    }
                }
                else if( c == '|' ) {
                    result.add( currentValue.toString().trim() );
                    currentValue = new StringBuilder();
                }
                else {
                    currentValue.append( c );
                }
            }
            result.add( currentValue.toString().trim() );
            return result;
        }
    }

    private static String atLeastOneSpace( String stringValue ) {
        if( stringValue.equals( "" ) ) {
            return " ";
        }
        else {
            return stringValue;
        }
    }

}
