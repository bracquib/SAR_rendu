package info5.sar.utils;

import info5.sar.events.channels.Channel;
import info5.sar.queues.MessageQueue;

public interface ConnectListener {

    /**
     * make a connect request
     *
     * @param channel - the channel
     */
    void connected(Channel channel);

    void connected(MessageQueue queue);

    /**
     * make a refuse request
     */
    void refused();
}
