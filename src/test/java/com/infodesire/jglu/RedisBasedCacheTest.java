package com.infodesire.jglu;

import com.infodesire.jglu.util.SocketUtils;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.embedded.RedisServer;

import static org.junit.Assert.*;

public class RedisBasedCacheTest {

    private RedisServer redisServer;
    private StatefulRedisConnection connection;
    private RedisClient redisClient;
    private RedisBasedCache cache;

    @Before
    public void setUp() throws Exception {
        int port = SocketUtils.getFreePort();
        redisServer = new RedisServer( port );
        redisServer.start();
        redisClient = RedisClient.create( "redis://localhost:" + port + "/0" );
        connection = redisClient.connect();
        cache = new RedisBasedCache( connection, "main." );
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
        redisClient.shutdown();
        redisServer.stop();
    }

    @Test
    public void testCaching() {

        assertFalse( cache.has( "key1" ) );
        assertEquals( 0, cache.find( "key*" ).size() );
        cache.put( "key1", "value1" );
        assertTrue( cache.has( "key1" ) );
        assertEquals( "value1", cache.get( "key1" ) );
        assertEquals( 1, cache.find( "key*" ).size() );

    }
    
}