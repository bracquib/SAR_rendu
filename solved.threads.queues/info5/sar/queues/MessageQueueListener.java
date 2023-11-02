package info5.sar.queues;

public interface MessageQueueListener {
    /**
     * received a message
     *
     * @param msg - the message
     */
    void received(byte[] msg, Object cookie);

    /**
     * send a message
     *
     * @param bytes
     * @param offset
     * @param length
     * @param cookie
     */
    void sent(byte[] bytes, int offset, int length, Object cookie);

    /**
     * closed the queue
     */
    void closed();
}
