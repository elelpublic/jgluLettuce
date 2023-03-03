package com.infodesire.rcache;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;

public class RedisBasedCacheWithRealRedisIntegrationTest {

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
    public void testCaching() {

        RedisBasedCache rcache = new RedisBasedCache( connection, prefix );
        assertFalse( rcache.has( "value1" ) );

        rcache.put( "value1", "First value!" );
        assertTrue( rcache.has( "value1" ) );

        assertEquals( "First value!", rcache.get( "value1" ) );
        List<String> keys = rcache.find( "value*" );
        assertEquals( 1, keys.size() );
        assertEquals( "value1", keys.get( 0 ) );

        rcache.remove( "value1" );
        assertFalse( rcache.has( "value1" ) );
        assertEquals( 0, rcache.find( "value1" ).size() );

    }

}