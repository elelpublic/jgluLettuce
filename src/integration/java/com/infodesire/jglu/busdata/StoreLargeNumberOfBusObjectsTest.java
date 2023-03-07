package com.infodesire.jglu.busdata;

import com.infodesire.jglu.RedisBasedCache;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.infodesire.jglu.util.DateTimeUtils.d;
import static org.junit.Assert.*;

public class StoreLargeNumberOfBusObjectsTest {

    private static String prefix;
    private static RedisClient redisClient;
    private static StatefulRedisConnection<String, String> connection;


    @BeforeClass
    public static void beforeClass() {
        prefix = "RedisBasedCacheWithRealRedisIntegrationTest." + System.currentTimeMillis() + ".";
        redisClient = RedisClient.create("redis://password@localhost:6379/0");
        connection = redisClient.connect();
    }
    
    @AfterClass
    public static void afterClass() {
        RedisCommands<String, String> command = connection.sync();
        for( String key : command.keys( prefix + "*" ) ) {
            command.del( key );
        }
        connection.close();
        redisClient.shutdown();
    }

    @Test
    public void testStoringAndReading() {

        int OBJECT_COUNT = 100000;

        LocalDate date = LocalDate.of( 2023, 3, 7 );

        for( int i = 0; i < OBJECT_COUNT; i++ ) {

            String id = "" + i;
            BusObject busObject = new BusObject( prefix + id );
            busObject.set( "Id", new BusValue( id ) );
            busObject.set( "Name", new BusValue( "Project " + id ) );
//            busObject.set( "Start", new BusValue( Date.value, true, false ) );
//            LocalDate.of(  )
//            busObject.set( "End", new BusValue( date, true, false ) );

        }


    }

}