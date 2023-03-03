package com.infodesire.rcache;

import com.infodesire.rcache.pubsub.CollectingPubSubListener;
import com.infodesire.rcache.pubsub.PubSubMessage;
import static com.infodesire.rcache.pubsub.PubSubMessage.Type.*;
import com.infodesire.rcache.util.SocketUtils;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.embedded.RedisServer;

import static org.junit.Assert.*;

public class RedisUtilsTest {

    private RedisServer redisServer;
    private int port;

    @Before
    public void setUp() throws Exception {
        port = SocketUtils.getFreePort();
        redisServer = new RedisServer( port );
        redisServer.start();
    }

    @After
    public void tearDown() throws Exception {
        redisServer.stop();
    }

    @Test
    public void testConnect() {

        RedisClient client = RedisUtils.createClient( "localhost", port, null, null );

        StatefulRedisConnection<String, String> connection = client.connect();
        assertEquals( Long.valueOf( 0 ), connection.sync().exists( "hello" ) );
        connection.sync().set( "hello", "world" );
        assertEquals( Long.valueOf( 1 ), connection.sync().exists( "hello" ) );
        assertEquals( "world", connection.sync().get( "hello" ) );

    }

    @Test
    public void testPublistSubscribe() throws InterruptedException {

        RedisClient client = RedisUtils.createClient( "localhost", port, null, null );
        StatefulRedisPubSubConnection<String, String> pubSubConnection = client.connectPubSub();
        StatefulRedisConnection<String, String> connection = client.connect();

        CollectingPubSubListener listener = new CollectingPubSubListener( 10 );

        RedisUtils.addListener( pubSubConnection, listener );

        RedisUtils.subscribe( pubSubConnection, "worldnews" );
        Thread.sleep( 10 );

        assertFalse( listener.isEmpty() );
        PubSubMessage message = listener.pop();
        assertEquals( CHANNEL_SUBSCRIBED, message.getType() );
        assertTrue( listener.isEmpty() );

        RedisUtils.publish( connection, "worldnews", "Hello hello!" );
        Thread.sleep( 10 );

        assertFalse( listener.isEmpty() );
        message = listener.pop();
        assertEquals( CHANNEL_MESSAGE, message.getType() );
        assertEquals( "Hello hello!", message.getMessage() );
        assertTrue( listener.isEmpty() );

        RedisUtils.unsubscribe( pubSubConnection, "worldnews" );
        Thread.sleep( 10 );

        assertFalse( listener.isEmpty() );
        message = listener.pop();
        assertEquals( CHANNEL_UNSUBSCRIBED, message.getType() );
        assertTrue( listener.isEmpty() );

        RedisUtils.psubscribe( pubSubConnection, "*news" );
        Thread.sleep( 10 );

        assertFalse( listener.isEmpty() );
        message = listener.pop();
        assertEquals( PATTERN_SUBSCRIBED, message.getType() );
        assertTrue( listener.isEmpty() );

        RedisUtils.publish( connection, "worldnews", "Good evening!" );
        Thread.sleep( 10 );

        assertFalse( listener.isEmpty() );
        message = listener.pop();
        assertEquals( PATTERN_MESSAGE, message.getType() );
        assertEquals( "Good evening!", message.getMessage() );
        assertEquals( "*news", message.getPattern() );
        assertTrue( listener.isEmpty() );

        RedisUtils.punsubscribe( pubSubConnection, "*news" );
        Thread.sleep( 10 );

        assertFalse( listener.isEmpty() );
        message = listener.pop();
        assertEquals( PATTERN_UNSUBSCRIBED, message.getType() );
        assertTrue( listener.isEmpty() );

        RedisUtils.publish( connection, "worldnews", "Good evening!" );
        Thread.sleep( 10 );

        assertTrue( listener.isEmpty() ); // no longer subscribed

    }

}