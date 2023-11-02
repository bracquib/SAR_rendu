package info5.sar.queues;

import info5.sar.channels.Broker;

public class CQueueBroker extends QueueBroker {
    public CQueueBroker(Broker broker) {
        super(broker);
    }

    @Override
    public MessageQueue accept(int port) {
        return new CMessageQueue(this, this.broker.accept(port));
    }

    @Override
    public MessageQueue connect(String name, int port) {
        return new CMessageQueue(this, this.broker.connect(name, port));
    }

    @Override
    public boolean bind(int port, AcceptListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean unbind(int port) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean connect(String name, int port, ConnectListener listener) {
        throw new UnsupportedOperationException();
    }


}
