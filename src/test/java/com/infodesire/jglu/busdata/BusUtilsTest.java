package com.infodesire.jglu.busdata;

import com.infodesire.jglu.RedisUtils;
import com.infodesire.jglu.util.SocketUtils;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.embedded.RedisServer;

import java.text.ParseException;

import static com.infodesire.jglu.busdata.BusType.INTEGER;
import static com.infodesire.jglu.busdata.BusType.STRING;
import static org.junit.Assert.assertEquals;

public class BusUtilsTest {

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
    public void testStoreRetrieve() throws ParseException {

        RedisUtils.SIMULATE_HSET_WITH_EMBEDDED_REDIS = true;

        RedisClient client = RedisUtils.createClient( "localhost", port, null, null );
        StatefulRedisConnection<String, String> connection = client.connect();

        BusTemplate template = new BusTemplate();
        template.setType( "Id", INTEGER );
        template.setType( "Name", BusType.STRING );

        assertEquals( 0, (long) connection.sync().exists( "employee:1" ) );

        BusObject e1 = new BusObject( "employee:1" );
        e1.set( "Id", new BusValue( INTEGER, "2102" ) );
        e1.set( "Name", new BusValue( BusType.STRING, "DeMorgan" ) );

        BusUtils.set( connection, e1 );
        assertEquals( 1, (long) connection.sync().exists( "employee:1" ) );

        BusObject e2 = BusUtils.get( connection, e1.getKey(), template );
        assertEquals( "2102", e2.get( "Id" ).toString() );
        assertEquals( INTEGER, e2.get( "Id" ).getType() );
        assertEquals( "DeMorgan", e2.get( "Name" ).toString() );
        assertEquals( STRING, e2.get( "Name" ).getType() );

    }

}