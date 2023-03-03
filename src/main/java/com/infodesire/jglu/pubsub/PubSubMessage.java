package com.infodesire.jglu.pubsub;

public class PubSubMessage {

    private final String pattern;
    private final String channel;
    private final String message;
    private final Long count;
    private final Type type;

    public static enum Type {
        CHANNEL_MESSAGE,
        PATTERN_MESSAGE,
        CHANNEL_SUBSCRIBED,
        PATTERN_SUBSCRIBED,
        CHANNEL_UNSUBSCRIBED,
        PATTERN_UNSUBSCRIBED
    }

    public PubSubMessage( Type type, String pattern, String channel, String message, Long count ) {
        this.type = type;
        this.pattern = pattern;
        this.channel = channel;
        this.message = message;
        this.count = count;
    }
    public String toString() {
        return type + " \"" + (message == null ? "" : message ) + "\"" + " (chnl/ptrn: "
                + (channel == null ? "" : channel ) + "/" + (pattern == null ? "" : pattern ) + ") ["
                + (count == null ? "" : count ) + "]";
    }

    public String getPattern() {
        return pattern;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public Long getCount() {
        return count;
    }

    public Type getType() {
        return type;
    }

}

