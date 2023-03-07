package com.infodesire.jglu;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RedisBasedCacheWithRealRedisIntegrationTest {

    private static String prefix;
    private static RedisClient redisClient;
    private static StatefulRedisConnection<String, String> connection;


    @BeforeClass
    public static void beforeClass() {
        prefix = "RedisBasedCacheWithRealRedisIntegrationTest." + System.currentTimeMillis() + ".";
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
    public void testCaching() {

        RedisBasedCache redisCache = new RedisBasedCache( connection, prefix );
        assertFalse( redisCache.has( "value1" ) );

        redisCache.put( "value1", "First value!" );
        assertTrue( redisCache.has( "value1" ) );

        assertEquals( "First value!", redisCache.get( "value1" ) );
        List<String> keys = redisCache.find( "value*" );
        assertEquals( 1, keys.size() );
        assertEquals( "value1", keys.get( 0 ) );

        redisCache.remove( "value1" );
        assertFalse( redisCache.has( "value1" ) );
        assertEquals( 0, redisCache.find( "value1" ).size() );

    }

}