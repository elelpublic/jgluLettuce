package com.infodesire.jglu.busdata;

import org.junit.Test;

import java.util.List;

import static com.infodesire.jglu.util.ListUtils.l;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ListFormatTest {

    @Test
    public void escape() {

        assertEquals( "", ListFormat.escape( null ) );
        assertEquals( "", ListFormat.escape( "" ) );
        assertEquals( "\\|", ListFormat.escape( "|" ) );
        assertEquals( "\\\\", ListFormat.escape( "\\" ) );
        assertEquals( "\\\\\\|", ListFormat.escape( "\\|" ) );
        assertEquals( "abc", ListFormat.escape( "abc" ) );
        assertEquals( "ab\\|c", ListFormat.escape( "ab|c" ) );
        assertEquals( "ab\\|\\|c", ListFormat.escape( "ab||c" ) );
        assertEquals( "ab\\|\\|\\|c", ListFormat.escape( "ab|||c" ) );
        assertEquals( "ab\\|cd\\|ef", ListFormat.escape( "ab|cd|ef" ) );
        assertEquals( "ab\\|cd\\|\\|ef", ListFormat.escape( "ab|cd||ef" ) );
        assertEquals( "ab\\|", ListFormat.escape( "ab|" ) );
        assertEquals( "\\|ab", ListFormat.escape( "|ab" ) );

    }

    @Test
    public void format() {

        testFormatParseAndBack( "", ( null ) );
        testFormatParseAndBack( "a", ( l( "a" ) ) );
        testFormatParseAndBack( "", ( l()) );
        testFormatParseAndBack( "|", ( l( "", "" )) );
        testFormatParseAndBack( "a|", ( l( "a", "" )) );
        testFormatParseAndBack( "|b", ( l( "", "b" )) );
        testFormatParseAndBack( "a|b", ( l( "a", "b" )) );
        testFormatParseAndBack( "a|b|c", ( l( "a", "b", "c" )) );
        testFormatParseAndBack( "a|b|c|", ( l( "a", "b", "c", "" )) );
        testFormatParseAndBack( "a| |c", ( l( "a", "", "c" )) );
        testFormatParseAndBack( "| |c", ( l( "", "", "c" )) );
        testFormatParseAndBack( "| |", ( l( "", "", "" )) );
        testFormatParseAndBack( "a|\\||c", ( l( "a", "|", "c" )) );
        testFormatParseAndBack( "a|\\|\\||c", ( l( "a", "||", "c" )) );
        testFormatParseAndBack( "\\||\\|\\||c", ( l( "|", "||", "c" )) );
        testFormatParseAndBack( "\\\\", ( l( "\\" ) ) );

        // special whitespace treatment
        testFormatParseAndBack( "a| |b", ( l( "a", "", "b" )) );
        testFormatParseAndBack( "| |b", ( l( "", "", "b" )) );
        testFormatParseAndBack( "| |", ( l( "", "", "" )) );
        testFormatParseAndBack( "| |", ( l( "", "", "" )) );

    }

    @Test
    public void parse() {

        assertNull( ListFormat.parse( null ) );
        assertNull( ListFormat.parse( "" ) );
        assertNull( ListFormat.parse( " " ) );

        testFormatParseAndBack( l( "a" ), ( "a" ) );
        testFormatParseAndBack( l( "a", "b" ), ( "a|b" ) );
        testFormatParseAndBack( l( "a", "b", "c" ), ( "a|b|c" ) );
        testFormatParseAndBack( l( "a", "b", "" ), ( "a|b|" ) );
        testFormatParseAndBack( l( "", "b", "" ), ( "|b|" ) );
        testFormatParseAndBack( l( "", "", "" ), ( "| |" ) );

        testFormatParseAndBack( l( "", "" ), ( "|" ) );
        testFormatParseAndBack( l( "|" ), ( "\\|" ) );

        testFormatParseAndBack( l( "", "", "", "" ), ( "| | |" ) );
        assertEquals( l( "", "", "", "" ), ListFormat.parse( "|||" ) );

        testFormatParseAndBack( l( "", "", "", "", "" ), ( "| | | |" ) );
        assertEquals( l( "", "", "", "", "" ), ListFormat.parse( "||||" ) );

        testFormatParseAndBack( l( "|||" ), ( "\\|\\|\\|" ) );
        testFormatParseAndBack( l( "||||" ), ( "\\|\\|\\|\\|" ) );
        testFormatParseAndBack( l( "||", "||" ), ( "\\|\\||\\|\\|" ) );
        testFormatParseAndBack( l( "\\"  ), ( "\\\\" ) );
        testFormatParseAndBack( l( "\\|", "|"  ), ( "\\\\\\||\\|" ) );

    }

    private void testFormatParseAndBack( List<String> list, String string ) {
        testFormatParseAndBack( string, list );
    }


    private void testFormatParseAndBack( String string, List<String> list ) {

        String formatted = ListFormat.format( list );
        assertEquals( "String should be " + string + "but was " + formatted, string, formatted );

        List<String> parsed = ListFormat.parse( string );
        assertEquals( "List should be " + list + " but was: " + parsed, list, parsed );

    }

}