package com.infodesire.jglu.pubsub;

import io.lettuce.core.pubsub.RedisPubSubListener;

/**
 * Base class for redis pubsub subscribers
 *
 */
public class PubSubListenerAdapter implements RedisPubSubListener<String, String> {

    private final PubSubListener listener;

    public PubSubListenerAdapter( PubSubListener listener ) {
        this.listener = listener;
    }

    public void message( String channel, String message ) {
        listener.handle( new PubSubMessage( PubSubMessage.Type.CHANNEL_MESSAGE,
                null, channel, message, null ) );
    }
    public void message( String pattern, String channel, String message ) {
        listener.handle( new PubSubMessage( PubSubMessage.Type.PATTERN_MESSAGE,
                pattern, channel, message, null ) );
    }
    public void subscribed( String channel, long count ) {
        listener.handle( new PubSubMessage( PubSubMessage.Type.CHANNEL_SUBSCRIBED,
                null, channel, null, count ) );
    }
    public void psubscribed( String pattern, long count ) {
        listener.handle( new PubSubMessage( PubSubMessage.Type.PATTERN_SUBSCRIBED,
                pattern, null, null, count ) );
    }
    public void unsubscribed( String channel, long count ) {
        listener.handle( new PubSubMessage( PubSubMessage.Type.CHANNEL_UNSUBSCRIBED,
                null, channel, null, count ) );
    }
    public void punsubscribed( String pattern, long count ) {
        listener.handle( new PubSubMessage( PubSubMessage.Type.PATTERN_UNSUBSCRIBED,
                pattern, null, null, count ) );
    }

}
