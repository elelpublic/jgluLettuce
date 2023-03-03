package com.infodesire.jglu.pubsub;

/**
 * Handle pubsub messages
 */
public interface PubSubListener {

    /**
     * Do smething with an incoming message
     *
     * @param pubSubMessage Incoming message
     *
     */
    void handle( PubSubMessage pubSubMessage );

}
