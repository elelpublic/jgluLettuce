package com.infodesire.jglu.util;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * Utilities for a command line app
 *
 */
public class CliUtils {

    public static Deque<String> parseCommandLine( String input ) {

        if( input == null || input.trim().length() == 0 ) {
            return null;
        }

        Deque<String> result = new ArrayDeque<>();
        result.addAll( Arrays.asList( input.split( " " ) ) );
        return result;

    }

}
