package com.infodesire.jglu.pubsub;

import com.google.common.collect.EvictingQueue;

/**
 * A subscriber (=pubsub listener) which keeps the last n messages in memory
 *
 */
public class CollectingPubSubListener implements PubSubListener {

    private EvictingQueue<PubSubMessage> queue;

    public CollectingPubSubListener( int maxNumberOfMessages ) {
        queue = EvictingQueue.create( maxNumberOfMessages );
    }

    @Override
    public void handle( PubSubMessage pubSubMessage ) {
        synchronized( queue ) {
            queue.offer( pubSubMessage );
        }
    }

    public PubSubMessage pop() {
        synchronized( queue ) {
            return queue.poll();
        }
    }

    public PubSubMessage peek() {
        synchronized( queue ) {
            return queue.peek();
        }
    }

    public boolean isEmpty() {
        synchronized( queue ) {
            return queue.isEmpty();
        }
    }

}
