package info5.sar.events.channel;

import info5.sar.events.channels.Broker;
import info5.sar.events.channels.Channel;
import info5.sar.utils.AcceptListener;
import info5.sar.utils.Executor;
import info5.sar.utils.BrokerManager;
import info5.sar.utils.ConnectListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CBroker extends Broker {
     private static final BrokerManager<CBroker> brokerManager = BrokerManager.getInstance(CBroker.class);

     private HashMap<Integer, AcceptListener> acceptListeners = new HashMap<Integer, AcceptListener>();
     private HashMap<Integer, BrokerWithListener> brokerWithListenersWaitingForConnect = new HashMap<Integer, BrokerWithListener>();


	protected CBroker(String name, Executor executor) {
        super(name, executor);
         try {
            brokerManager.registerBroker(name, this);
        } catch (Exception e) {
            System.out.println(
                    "Could not register Broker " + name + " because another broker with the same name already exist.");
            e.printStackTrace();
        }
	}

	@Override
	public boolean accept(int port, AcceptListener listener) {
		if (acceptListeners.containsKey(port)) {
            return false;
        }

        CBroker broker = this;
        Executor executor = broker.getPump();

        Runnable accept = new Runnable() {
            public void run() {
                BrokerWithListener brokerWithListener = brokerWithListenersWaitingForConnect.get(port);

                CChannel channel = new CChannel(broker);
                CChannel channel2 = new CChannel(brokerWithListener.getBroker(), channel);

                Runnable connect = new Runnable() {
                    public void run() {
                        brokerWithListener.getListener().connected(channel2);
                    }
                };

                Runnable accept = new Runnable() {
                    public void run() {
                        acceptListeners.get(port).accepted(channel);
                    }
                };

                executor.post(connect);
                executor.post(accept);
            }
        };

        executor.post(accept);
        return true;
	}



	@Override
	public boolean connect(String name, int port, ConnectListener listener) {
        if (acceptListeners.containsKey(port)) {
            return false;
        }

        CBroker broker = this;
        Executor executor = broker.getPump();

        Runnable connect = new Runnable() {
            public void run() {
                BrokerWithListener brokerWithListener = brokerWithListenersWaitingForConnect.get(port);

                CChannel channel = new CChannel(broker);
                CChannel channel2 = new CChannel(brokerWithListener.getBroker(), channel);

                Runnable connect = new Runnable() {
                    public void run() {
                        brokerWithListener.getListener().connected(channel2);
                    }
                };

                Runnable accept = new Runnable() {
                    public void run() {
                        acceptListeners.get(port).accepted(channel);
                    }
                };

                executor.post(connect);
                executor.post(accept);
            }
        };

        executor.post(connect);
        return true;
    }


}

class BrokerWithListener {
    private Broker broker;
    private ConnectListener listener;

    public BrokerWithListener(Broker broker, ConnectListener listener) {
        this.broker = broker;
        this.listener = listener;
    }

    public Broker getBroker() {
        return broker;
    }

    public ConnectListener getListener() {
        return listener;
    }
}
