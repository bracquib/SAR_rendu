package info5.sar.queues;

import info5.sar.queues.MessageQueueListener;

/**
 * A message queue.
 */

public abstract class MessageQueue {
    QueueBroker queueBroker;
    abstract public QueueBroker broker();
    

    /**
     * create a new message queue
     */
    protected MessageQueue(QueueBroker queueBroker) {
        this.queueBroker = queueBroker;
    }

    /**
     * @return the queue broker
     */
    public QueueBroker getQueueBroker() {
        return queueBroker;
    }

    /**
     * send a message
     *
     * @param bytes  - the message
     * @param offset
     * @param length
     * @throws ClosedException if the queue is disconnected
     */
    abstract public void send(byte[] bytes, int offset, int length) throws ClosedException;

    /**
     * receive a message
     *
     * @return the message
     * @throws ClosedException if the queue is disconnected
     */
    abstract public byte[] receive() throws ClosedException;
    /**
     * close the queue
     */
    public abstract void close();

    /**
     * check if the queue is closed
     *
     * @return true if the queue is closed
     */
    public abstract boolean closed();

    /**
     * add a listener to the queue
     *
     * @param listener - the listener
     */
    public abstract void addListener(MessageQueueListener listener);

    /**
     * event-oriented send
     * drop a message to be sent
     * let the ownership of the bytes array to the application
     *
     * @param bytes
     * @param cookie
     * @return true if the message will be sent, false if the queue is closed or full and the message will be dropped
     */
    public abstract boolean send(byte[] bytes, Object cookie);

    /**
     * event-oriented send
     * drop a message to be sent
     * let the ownership of the bytes array to the application
     *
     * @param bytes
     * @param offset
     * @param length
     * @param cookie
     * @return
     */
    public abstract boolean send(byte[] bytes, int offset, int length, Object cookie);

    /**
     * event-oriented
     * process a message that has been received
     * recover ownership of the bytes array
     *
     * @param bytes
     * @param cookie
     */
    public abstract void processed(byte[] bytes, Object cookie);
}


