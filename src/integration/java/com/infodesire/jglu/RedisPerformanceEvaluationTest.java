package com.infodesire.jglu;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class RedisPerformanceEvaluationTest {

    private static String prefix;
    private static RedisClient redisClient;
    private static StatefulRedisConnection<String, String> connection;


    @BeforeClass
    public static void beforeClass() {
        prefix = "RedisPerformanceEvaluationTest." + System.currentTimeMillis() + ".";
        redisClient = RedisClient.create("redis://localhost:6379/0");
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
    public void testBiggerData() {

        RedisCommands syncCommands = connection.sync();

        long t0 = System.currentTimeMillis();
        long c0 = 0;

        for( int i = 0; i < 100000; i++ ) {
            syncCommands.set( prefix + i, "" + i );
            if( i % 1000 == 0 ) {
                long t1 = System.currentTimeMillis();
                long time = t1 - t0;
                long count = i - c0;
                long rate = ( count / time ) * 1000;
                System.out.println( rate + " set/s" );
                t0 = t1;
                c0 = i;
            }
        }

    }

}