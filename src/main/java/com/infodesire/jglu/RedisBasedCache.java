package com.infodesire.jglu;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A cache using only redis for storage
 */
public class RedisBasedCache {

    private final StatefulRedisConnection connection;
    private final String prefix;

    /**
     * Create cache
     *
     * @param connection Redis connection
     * @param prefix Optional prefix to every key of this cache
     *
     */
    public RedisBasedCache( StatefulRedisConnection connection, String prefix ) {
        this.connection = connection;
        this.prefix = prefix;
    }

    public void put( String key, String value ) {
        RedisCommands syncCommands = connection.sync();
        syncCommands.set( makeKey( key ), value );
    }

    public String get( String key ) {
        RedisCommands syncCommands = connection.sync();
        return (String) syncCommands.get( makeKey( key ) );
    }

    public boolean has( String key ) {
        RedisCommands syncCommands = connection.sync();
        return syncCommands.exists( makeKey( key ) ) == 1;
    }

    public void remove( String key ) {
        RedisCommands syncCommands = connection.sync();
        syncCommands.del( makeKey( key ) );
    }

    /**
     * Fina all keys matching the pattern
     *
     * h?llo matches hello, hallo and hxllo
     * h*llo matches hllo and heeeello
     * h[ae]llo matches hello and hallo, but not hillo
     * h[^e]llo matches hallo, hbllo, ... but not hello
     * h[a-b]llo matches hallo and hbllo
     *
     * @see <a href="https://redis.io/commands/keys/">https://redis.io/commands/keys/</a>
     *
     * @param pattern Pattern
     * @return List of matching patterns found
     *
     */
    public List<String> find( String pattern ) {
        RedisCommands syncCommands = connection.sync();
        return (List<String>) syncCommands.keys( makeKey( pattern ) )
                .stream()
                .map( key -> removePrefix( (String) key ) )
                .collect( Collectors.toList() );
    }

    private String makeKey( String key ) {
        return prefix == null ? key : prefix + key;
    }

    private String removePrefix( String key ) {
        if( prefix != null ) {
            if( key.startsWith( prefix ) ) {
                key = key.substring( prefix.length() );
            }
        }
        return key;
    }

}
