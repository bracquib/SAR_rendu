package info5.sar.queues;

import info5.sar.queues.MessageQueue;

public interface AcceptListener {
    /**
     * make an accept request
     *
     * @param queue - the message queue
     */
    void accepted(MessageQueue queue);
}
