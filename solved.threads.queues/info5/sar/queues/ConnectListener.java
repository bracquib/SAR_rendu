package info5.sar.queues;

import info5.sar.queues.MessageQueue;

public interface ConnectListener {
    /**
     * make a connect request
     *
     * @param queue - the message queue
     */
    void connected(MessageQueue queue);

    /**
     * make a refuse request
     */
    void refused();
}
