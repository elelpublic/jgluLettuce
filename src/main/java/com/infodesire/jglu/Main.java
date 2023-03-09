package com.infodesire.jglu;

import com.infodesire.jglu.util.CliUtils;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
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
import java.util.Deque;
import java.util.List;
import java.util.Map;

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

    RedisClient redisClient = RedisClient.create( "redis://localhost:6379/0" );
    StatefulRedisConnection<String, String> connection = redisClient.connect();

    try {

      BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
      long time0 = -1;

      while( true ) {

        if( time0 != -1 ) {
          // show total runtime of last command
          long time1 = System.currentTimeMillis();
          long runtime = time1 - time0;
          print( "" );
          print( "execution time: " + runtime + "ms" );
        }

        print( "" );
        print( "(? for help)" );
        print( "jglu> " );

        String line = in.readLine().trim();
        Deque<String> input = CliUtils.parseCommandLine( line );

        print( "" );
        time0 = System.currentTimeMillis();
        List<String> keys;

        try {
          
          if( input.size() == 0 ) {
            showCommands();
          }
          else {

            String command = input.pop();
            
            if( command.equals( "exit" ) ) {
              return;
            }
            else if( command.equals( "?" ) || command.equals( "help" ) ) {
              showCommands();
            }
            else if( command.equals( "set" ) ) {

              boolean ok = false;
              if( !input.isEmpty() ) {
                String key = input.pop();
                if( !input.isEmpty() ) {
                  String value = input.pop();
                  connection.sync().set( key, value );
                  ok = true;
                }
              }

              if( ok ) {
                print( "OK" );
              }
              else {
                showCommands( "Syntax error in line: " + line );
              }

            }
            else if( command.equals( "get" ) ) {

              if( !input.isEmpty() ) {
                print( line + ": " + connection.sync().get( input.pop() ) );
              }
              else {
                showCommands( "Syntax error in line: " + line );
              }

            }
            else if( command.equals( "hgetall" ) ) {

              if( !input.isEmpty() ) {
                Map<String, String> fields = connection.sync().hgetall( input.pop() );
                for( Map.Entry<String, String> entry : fields.entrySet() ) {
                  print( entry.getKey() + ": " + entry.getKey() );
                }
              }
              else {
                showCommands( "Syntax error in line: " + line );
              }


            }
            else if( command.equals( "del" ) ) {

              if( !input.isEmpty() ) {
                Long count = connection.sync().del( input.pop() );
                print( "Deleted " + count + " keys" );
              }
              else {
                showCommands( "Syntax error in line: " + line );
              }

            }
            else if( command.equals( "has" ) ) {

              if( !input.isEmpty() ) {
                print( line + " exists: " + connection.sync().exists( input.pop() )  );
              }
              else {
                showCommands( "Syntax error in line: " + line );
              }

            }
            else if( command.equals( "keys" ) ) {

              if( !input.isEmpty() ) {
                keys = connection.sync().keys( input.pop() );
                int index = 1;
                for( String key : keys ) {
                  print( "#" + index + ": " + key );
                  index++;
                }
                print( "total keys found: " + keys.size() );
              }
              else {
                showCommands( "Syntax error in line: " + line );
              }

            }
            else if( command.equals( "scan" )
                    || command.equals( "scancount" ) ) {

              boolean quiet = command.equals( "scancount" );

              boolean ok = false;
              if( !input.isEmpty() ) {
                String cursor = input.pop();
                if( !input.isEmpty() ) {
                  String match = input.pop();
                  ScanArgs scanArgs = new ScanArgs();
                  scanArgs.match( match );
                  if( !input.isEmpty() ) {
                    scanArgs.limit( Long.parseLong( input.pop() ) );
                  }
                  ScanCursor scanCursor = new ScanCursor( cursor, false );
                  KeyScanCursor<String> c = connection.sync().scan( scanCursor, scanArgs );
                  int index = 1;
                  List<String> keysScanned = c.getKeys();
                  for( String keyScanned : keysScanned ) {
                    if( !quiet ) {
                      print( "#" + index + ": " + keyScanned );
                    }
                    index++;
                  }
                  print( "keys scanned: " + keysScanned.size() );
                }

              }
              else {
                showCommands( "Syntax error in line: " + line );
              }

            }
            else if( command.equals( "count" ) ) {

              if( !input.isEmpty() ) {
                int count = connection.sync().keys( input.pop() ).size();
                print( "" + count );
              }
              else {
                showCommands( "Syntax error in line: " + line );
              }

            }
            else if( command.equals( "delall" ) ) {

              if( !input.isEmpty() ) {
                keys = connection.sync().keys( input.pop() );
                int total = keys.size();
                print( "Found " + total );
                print( "To delete them all enter DELETE_THEM_ALL!" );
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
                      int rate = (int) Math.round( ( (double) count / (double) time ) * 1000 );
                      int percent = (int) Math.round( ( (double) index / (double) total ) * 100.0 );
                      print( index + "/" + total + " (" + percent + " %) " + rate + " del/s" );
                      t0 = t1;
                      c0 = index;
                    }
                    connection.sync().del( key );
                    index++;
                  }
                  print( "Deleted " + keys.size() + " keys." );
                }
                else {
                  print( "Code was not DELETE_THEM_ALL! - so will NOT delete." );
                }
              }
              else {
                showCommands( "Syntax error in line: " + line );
              }

            }
            else {
              showCommands( "Unknown command " + line );
            }
            
          }

        }
        catch( Throwable ex ) {
          ex.printStackTrace();
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
    showCommands( null );
  }

  private static void showCommands( String message ) {

    print( "" );

    if( message != null ) {
      print( message );
      print( "" );
    }

    print( "" );
    print( "Commands:" );
    print( "? or help ........................... show this help" );
    print( "exit ................................ leave interactive shell" );
    print( "" );
    print( "set key value ....................... set key value pair" );
    print( "get key ............................. get key for value" );
    print( "hgetall key ......................... get all fields and values of a map with this pattern" );
    print( "has key ............................. test if key exists" );
    print( "keys pattern ........................ find keys matching this pattern" );
    print( "scan cursor pattern [count] ......... scan keys matching this pattern (use 0 for initial cursor)" );
    print( "scancount cursor pattern [count] .... scan keys matching this pattern (use 0 for initial cursor)" );
    print( "count pattern ....................... count keys matching this pattern" );
    print( "delall pattern ...................... delete keys matching this pattern" );
    print( "" );

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