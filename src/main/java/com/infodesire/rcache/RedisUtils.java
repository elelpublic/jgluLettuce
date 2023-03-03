package com.infodesire.rcache;

import com.infodesire.rcache.pubsub.PubSubListener;
import com.infodesire.rcache.pubsub.PubSubListenerAdapter;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * Some boilerplate code for working with redis via lettuce
 *
 */
public class RedisUtils {

    public static RedisURI createURI( String host, int port, String user, String password ) {
        RedisURI uri = new RedisURI( host, port, RedisURI.DEFAULT_TIMEOUT_DURATION );
        if( user != null ) {
            uri.setUsername( user );
        }
        if( password != null ) {
            uri.setPassword( password );
        }
        return uri;
    }

    public static RedisClient createClient( String host, int port, String user, String password ) {
        return RedisClient.create( createURI( host, port, user, password ) );
    }

    /**
     * Subscribe to one channel
     *
     * @param connection Redis connection
     * @param channel Name of channel
     *
     */
    public static void subscribe( StatefulRedisPubSubConnection<String, String> connection, String channel ) {

        connection.sync().subscribe( channel );

    }

    /**
     * Unsubscribe from channel
     *
     * @param connection Redis connection
     * @param channel Name of channel
     *
     */
    public static void unsubscribe( StatefulRedisPubSubConnection<String, String> connection,
                                  String channel ) {

        connection.sync().unsubscribe( channel );

    }


    /**
     * Subscribe to all channels with matching pattern names
     *
     * @param connection Redis connection
     * @param channelPattern Redis matching pattern
     *
     */
    public static void psubscribe( StatefulRedisPubSubConnection<String, String> connection,
                                  String channelPattern ) {

        connection.sync().psubscribe( channelPattern );

    }

    /**
     * Unsubscribe from all channels with matching pattern names
     *
     * @param connection Redis connection
     * @param channelPattern Redis matching pattern
     *
     */
    public static void punsubscribe( StatefulRedisPubSubConnection<String, String> connection,
                                    String channelPattern ) {

        connection.sync().punsubscribe( channelPattern );

    }

    /**
     * Add listener to connection which will receive all subscribed messages
     *
     * @param pubSubConnection Redis connection
     * @param listener Listener
     *
     */
    public static void addListener( StatefulRedisPubSubConnection<String, String> pubSubConnection, PubSubListener listener ) {
        pubSubConnection.addListener( new PubSubListenerAdapter( listener ) );
    }

    /**
     * Publish a message
     *
     * @param connection Redis connection
     * @param channel Name of channel
     * @param message Message text
     *
     */
    public static void publish( StatefulRedisConnection<String, String> connection, String channel, String message ) {
        connection.sync().publish( channel, message );
    }

}
