package com.infodesire.jglu.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class SocketUtils {


  /**
   * Find a free port
   *
   * @return A single free port
   * @throws IOException error creating port
   *
   */
  public static int getFreePort() throws IOException {
    ServerSocket serverSocket = new ServerSocket( 0 );
    int port = serverSocket.getLocalPort();
    serverSocket.close();
    return port;
  }

  /**
   * Find multiple free ports
   *
   * @param number Numbet of free ports to create and return
   * @return List of free ports to be used
   * @throws IOException
   */
  public static List<Integer> getFreePorts( int number ) throws IOException {

    IOException exception = null;
    List<ServerSocket> sockets = new ArrayList<>();
    List<Integer> ports = new ArrayList<>();
    for( int i = 0; i < number; i++ ) {
      try {
        ServerSocket serverSocket = new ServerSocket( 0 );
        ports.add( serverSocket.getLocalPort() );
        sockets.add( serverSocket );
      }
      catch( IOException ex ) {
        exception = ex;
        break;
      }
    }

    sockets.stream().forEach( SocketUtils::closeQuietly );

    if( exception != null ) {
      throw exception;
    }

    return ports;

  }

  public static void closeQuietly( ServerSocket serverSocket ) {
    try {
      serverSocket.close();
    }
    catch( IOException ex ) {}
  }


}
