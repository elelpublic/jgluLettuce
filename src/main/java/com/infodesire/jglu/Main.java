package com.infodesire.jglu;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.List;

public class Main {

  static {
    System.setProperty( "log4j2.configurationFile", "src/main/conf/log4j2.xml" );
  }

  private static Logger loggerMain = LoggerFactory.getLogger( "main" );
  private static Logger loggerAll = LoggerFactory.getLogger( "all" );

  private static Options options;

  public static void main( String... args ) throws IOException, ParseException {

    loggerAll.info( "Logging to file target/main.log" );
    loggerMain.info( "App startup" );

    print( "jglu 0.1" );
    print( "Running as " + SystemUtils.getUserName() + "@" + InetAddress.getLocalHost().getHostName() );
    print( "" );

    options = createOptions();

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    List<String> argslist = cmd.getArgList();

    if( cmd.hasOption( "i" ) ) {
      interactiveShell();
    }
    else {

      if( argslist.isEmpty() ) {
        showUsage( "No command given." );
        return;
      }

      String command = argslist.get( 0 );

      if( command.equals( "help" ) ) {
        showUsage( "" );
        return;
      }

    }

    loggerMain.info( "App shutdown" );

  }

  private static void interactiveShell() throws IOException {

    RedisClient redisClient = RedisClient.create( "redis://password@localhost:6379/0" );
    StatefulRedisConnection<String, String> connection = redisClient.connect();

    try {

      BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );

      while( true ) {

        System.out.println( "" );
        System.out.println( "(? for help)" );
        System.out.println( "jglu> " );

        String line = in.readLine().trim();
        System.out.println( "" );

        if( line.equalsIgnoreCase( "exit" ) ) {
          return;
        }

        List<String> keys;
        if( line.equalsIgnoreCase( "?" ) || line.equalsIgnoreCase( "help" ) ) {
          showCommands();
        }
        else if( line.startsWith( "set " ) ) {
          line = line.substring( 3 ).trim();

          int sep = line.indexOf( " " );
          if( sep == -1 ) {
            System.out.println( "Syntax error in line: set " + line );
            showCommands();
          }
          else {
            String key = line.substring( 0, sep ).trim();
            String value = line.substring( sep ).trim();
            connection.sync().set( key, value );
            System.out.println( "OK" );
          }

        }
        else if( line.startsWith( "get " ) ) {
          line = line.substring( 3 ).trim();

          System.out.println( line + ": " + connection.sync().get( line ) );

        }
        else if( line.startsWith( "del " ) ) {
          line = line.substring( 3 ).trim();

          Long count = connection.sync().del( line );
          System.out.println( "Deleted " + count + " keys" );

        }
        else if( line.startsWith( "has " ) ) {
          line = line.substring( 3 ).trim();

          System.out.println( line + " exists: " + ( ( connection.sync().exists( line ) == 1 ) ) );

        }
        else if( line.startsWith( "find " ) ) {
          line = line.substring( 4 ).trim();

          keys = connection.sync().keys( line );
          int index = 1;
          for( String key : keys ) {
            System.out.println( "#" + index + ": " + key );
            index++;
          }

        }
        else if( line.startsWith( "count " ) ) {
          line = line.substring( 5 ).trim();

          int count = connection.sync().keys( line ).size();
          System.out.println( count );

        }
        else if( line.startsWith( "delall " ) ) {
          line = line.substring( 6 ).trim();

          keys = connection.sync().keys( line );
          System.out.println( "Found " + keys.size() );
          System.out.println( "To delete them all enter DELETE_THEM_ALL!" );
          line = in.readLine();
          int index = 1;
          if( line.trim().equals( "DELETE_THEM_ALL!" ) ) {
            long t0 = System.currentTimeMillis();
            long c0 = 0;
            for( String key : keys ) {
              if( index % 1000 == 0 ) {
                long t1 = System.currentTimeMillis();
                long time = t1 - t0;
                long count = index - c0;
                long rate = ( count / time ) * 1000;
                System.out.println( rate + " del/s" );
                t0 = t1;
                c0 = index;
              }
              connection.sync().del( key );
              index++;
            }
            System.out.println( "Deleted " + keys.size() + " keys." );
          }
          else {
            System.out.println( "Code was not DELETE_THEM_ALL! - so will NOT delete." );
          }

        }
        else {
          System.out.println( "Unknown command " + line );
          showCommands();
        }

      }

    }
    finally {
      if( connection != null ) {
        connection.close();
      }
      if( redisClient != null ) {
        redisClient.shutdown();
      }
    }

  }

  private static void showCommands() {
    System.out.println( "" );
    System.out.println( "Commands:" );
    System.out.println( "? or help ............... show this help" );
    System.out.println( "exit .................... leave interactive shell" );
    System.out.println( "" );
    System.out.println( "set key value ........... set key value pair" );
    System.out.println( "get key ................. get key for value" );
    System.out.println( "has key ................. test if key exists" );
    System.out.println( "find pattern ............ find keys matching this pattern" );
    System.out.println( "count pattern ........... count keys matching this pattern" );
    System.out.println( "delall pattern .......... delete keys matching this pattern" );
    System.out.println( "" );
  }


  public static Options createOptions() {

    // create Options object
    Options options = new Options();

    options.addOption(
            Option.builder()
                    .argName( "interactive" )
                    .option( "i" )
                    .desc( "interactive mode" )
                    .build()
    );

    return options;

  }


  private static void showUsage( String message ) {

    HelpFormatter formatter = new HelpFormatter();
    print( message );
    formatter.printHelp("hello [options] [command]", options);
    print( "" );
    print( "commands:" );

  }

  private static void print( String line ) {
    System.out.println( line );
  }

}