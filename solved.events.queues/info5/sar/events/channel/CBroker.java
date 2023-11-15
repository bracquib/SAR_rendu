package info5.sar.events.channel;

import info5.sar.events.channels.Broker;
import info5.sar.events.channels.Broker.ConnectListener;
import info5.sar.events.channels.Channel;
import info5.sar.utils.Executor;
import info5.sar.utils.BrokerManager;
import java.util.ArrayList;
import java.util.HashMap;

public class CBroker extends Broker {
	private static final BrokerManager<CBroker> brokerManager = BrokerManager.getInstance(CBroker.class);
	  protected Executor executor;

	private HashMap<Integer, AcceptListener> ports = new HashMap<Integer, AcceptListener>();
	private HashMap<Integer, ArrayList<BrokerWithListener>> brokerWithListenersWaitingForConnect = new HashMap<Integer, ArrayList<BrokerWithListener>>();
	private HashMap<Integer, ArrayList<Channel>> channels = new HashMap<Integer, ArrayList<Channel>>();

	public CBroker (String name, Executor executor) {
		super(name,executor);
		System.out.println("Creating CBroker with name: " + name);
		try {
			brokerManager.registerBroker(name, this);
		} catch (Exception e) {
			System.out.println(
					"Could not register Broker " + name + " because another broker with the same name already exist.");
		}
	}

	@Override
	public boolean accept(int port, AcceptListener listener) {
		System.out.println("(entre dans accept:Accepting connection on port: " + port);
		if (ports.containsKey(port)) {
			return false;
		}

		CBroker broker = this;
		Executor executor = broker.getExecutor();

		Runnable accept = new Runnable() {
			public void run() {
				ports.put(port, listener);
				channels.put(port, new ArrayList<Channel>());
				ArrayList<Channel> channelsArray = channels.get(port);
				ArrayList<BrokerWithListener> brokerWithListeners = brokerWithListenersWaitingForConnect.get(port);

				if (brokerWithListeners != null) {
					while (brokerWithListeners.size() > 0) {
						BrokerWithListener brokerWithListener = brokerWithListeners.get(0);
						brokerWithListeners.remove(0);

						CChannel channel = new CChannel(broker);
						CChannel remoteChannel = new CChannel(brokerWithListener.getBroker(), channel);

						channelsArray.add(channel);

						Runnable connect = new Runnable() {
							public void run() {
								brokerWithListener.getListener().connected(remoteChannel);
								listener.accepted(channel);
							}
						};
						System.out.println("accept: post connect");
						executor.post(connect);
					}
					ports.remove(port);
				}
			}
		};
		executor.post(accept);
		return true;
	}

	@Override
	public boolean disconnect(int port) {
		System.out.println("entre dans disconnect:Disconnecting from port: " + port);
		if (!ports.containsKey(port)) {
			return false;
		}

		Executor executor = getExecutor();

		Runnable disconnect = new Runnable() {
			public void run() {
				ArrayList<Channel> channelsArray = channels.get(port);
				for (Channel channel : channelsArray) {
					channel.close();
				}
				channels.remove(port);
				ports.remove(port);
			}
		};
		executor.post(disconnect);
		return true;
	}

	@Override
	public boolean connect(String name, int port, ConnectListener listener) {
		System.out.println("entre dans connect:Connecting to broker: " + name + " on port: " + port);
		CBroker broker = this;
		CBroker remoteBroker = brokerManager.getBroker(name);

		Executor executor = getExecutor();

        HashMap<Integer, AcceptListener> remotePorts = remoteBroker.ports;
		ArrayList<Channel> remoteChannels = remoteBroker.channels.get(port);

		if (remoteBroker == null) {
			return false;
		}

		Runnable connect = new Runnable() {
			public void run() {
				if (remotePorts.containsKey(port)) {
					CChannel channel = new CChannel(broker);
					CChannel remoteChannel = new CChannel(remoteBroker, channel);

					remoteChannels.add(channel);

					Runnable connect2 = new Runnable() {
						public void run() {
							listener.connected(remoteChannel);
							remotePorts.get(port).accepted(channel);
						}
					};
					System.out.println("connect: post connect");
					executor.post(connect2);
				} else {
					ArrayList<BrokerWithListener> brokerWithListeners = brokerWithListenersWaitingForConnect.get(port);
					if (brokerWithListeners == null) {
						brokerWithListeners = new ArrayList<BrokerWithListener>();
						brokerWithListenersWaitingForConnect.put(port, brokerWithListeners);
					}
					brokerWithListeners.add(new BrokerWithListener(remoteBroker, listener));
				}
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
