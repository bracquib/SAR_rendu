package info5.sar.queues;



import static info5.sar.utils.Log.log;

import info5.sar.channels.Broker;
import info5.sar.utils.BrokerManager;

public abstract class QueueBroker {
    protected static final BrokerManager<QueueBroker> queueBrokerManager = BrokerManager.getInstance(QueueBroker.class);
    protected final Broker broker;

    /*
     * Each queue broker must be uniquely named.
     * @throws IllegalArgumentException if the name is not unique.
     */
    public QueueBroker(Broker broker) {
        this.broker = broker;
        try {
            queueBrokerManager.registerBroker(broker.getName(), this);
        } catch (Exception e) {
            System.out.println(
                    "Could not register QueueBroker " + broker.getName() + " because another broker with the same name already exist.");
        }
        log("Created a new queue broker with his associated broker with name " + broker.getName() + ".");
    }

    /*
     * @returns the name of this queue broker.
     */
    public String getName() {
        return broker.getName();
    }

    /*
     * Indicate that this queue broker will accept one connection
     * on the given port and return a fully connected queue.
     * This is a thread-safe blocking rendez-vous.
     * @throws IllegalArgumentException if there is already
     *         an accept pending on the given port.
     */
    public abstract MessageQueue accept(int port);

    /*
     * Attempts a connection to the given port, via
     * the queue broker with the given name.
     * If such a queue broker cannot be found, this method returns null.
     * If the queue broker is found, this connect will block until
     * an accept on the given port is pending.
     * This is a thread-safe blocking rendez-vous.
     * Note: multiple accepts from different tasks with
     *       the same name and port are legal
     */
    public abstract MessageQueue connect(String name, int port);

    /*
     * Bind the given port to this queue broker.
     * This is an event-oriented way to accept connections.
     * @param port - the port to bind
     * @param listener - the listener to notify when a connection is accepted
     * @return true if the port was bound, false if the port was already bound
     */
    public abstract boolean bind(int port, AcceptListener listener);

    /*
     * Unbind the given port from this queue broker.
     * @param port - the port to unbind
     * @return true if the port was unbound, false if the port was not bound
     */
    public abstract boolean unbind(int port);

    /*
     * Connect to the given port on the given queue broker.
     * This is an event-oriented way to connect to a queue broker.
     * @param name - the name of the queue broker
     * @param port - the port to connect to
     * @param listener - the listener to notify when the connection is established
     * @return true if the connection was made, false if the connection was refused
     */
    public abstract boolean connect(String name, int port, ConnectListener listener);
    
    public Broker getBroker() { return broker; }
    

  }

