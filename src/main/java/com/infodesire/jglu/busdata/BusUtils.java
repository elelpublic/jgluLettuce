package com.infodesire.jglu.busdata;

import com.infodesire.jglu.RedisUtils;
import io.lettuce.core.api.StatefulRedisConnection;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for working with business objects
 *
 */
public class BusUtils {

    /**
     * Store a business object in redis
     *
     * @param connection Redis connection
     * @param object Business object
     *
     */
    public static void set( StatefulRedisConnection<String, String> connection,
                            BusObject object ) {

        RedisUtils.setMap( connection, object.getKey(), makeMap( object ) );

    }

    /**
     * Create a redisable map from a business object
     *
     * @param busObject Business object
     * @return A map which can easily be stored in redis
     *
     */
    public static Map<String, String> makeMap( BusObject busObject ) {
        Map<String, String> map = new HashMap<>();
        for( String field : busObject ) {
            map.put( field, busObject.get( field ).toString() );
        }
        return map;
    }

    /**
     * Load a business object from redis
     *
     * @param connection Redis connection
     * @param key Key of object in redis
     * @param template Template for objects of this type
     * @return Business object loaded from redis
     *
     */
    public static BusObject get( StatefulRedisConnection<String, String> connection,
                                 String key, BusTemplate template ) throws ParseException {

        BusObject object = new BusObject( key );
        Map<String, String> map = RedisUtils.getMap( connection, key );
        for( String fieldName : template ) {
            if( map.containsKey( fieldName ) ) {
                BusValue value = new BusValue( template.getType( fieldName ),
                        map.get( fieldName ) );
                object.set( fieldName, value );
            }
        }
        return object;

    }

}
