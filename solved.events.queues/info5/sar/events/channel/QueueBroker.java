
package info5.sar.events.channel;

import info5.sar.events.channels.Broker;
import info5.sar.events.queues.MessageQueue;
import info5.sar.events.queues.QueueBroker.AcceptListener;
import info5.sar.events.queues.QueueBroker.ConnectListener;
import info5.sar.utils.Executor;

public abstract class QueueBroker {
  public Broker broker;
  protected Executor pump;
  public QueueBroker(Executor pump, Broker broker) {
    this.broker = broker;
    this.pump = pump;
  }

  public Executor getEventPump() { return pump; }
  public String getName() {
    return broker.getName();
  }

  public Broker getBroker() {
    return broker;
  }

  public interface AcceptListener {
    void accepted(MessageQueue queue);
  }

  public abstract boolean bind(int port, AcceptListener listener);

  public abstract boolean unbind(int port);

  public interface ConnectListener {
    void connected(MessageQueue queue);

    void refused();
  }

  public abstract boolean connect(String name, int port, ConnectListener listener);

}
