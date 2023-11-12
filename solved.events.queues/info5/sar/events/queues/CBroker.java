package info5.sar.events.queues;

import java.util.Map;
import java.util.Queue;
import java.util.HashMap;
import java.util.LinkedList;

import info5.sar.events.channels.Broker;
import info5.sar.events.channels.Channel;
import info5.sar.events.tests.MyAcceptListener;
import info5.sar.events.tests.MyConnectListener;
import info5.sar.events.tests.MyListener;
import info5.sar.utils.Executor;

public class CBroker extends Broker {

	public static final Map<String, Broker> brokerManager = new HashMap<String, Broker>();
	
	private Map<Integer, AcceptListener> bindedPorts;
	private Map<Integer, Queue<Ticket>> portsWaitingQueues;
	// private Map<Inetger, CChannel> portsChannels;

	protected CBroker(String name, Executor pump) {
		super(name, pump);
		synchronized (brokerManager) {
			if (brokerManager.containsKey(name)) {
				throw new IllegalStateException();
			}
			brokerManager.put(name, this);
		}
		bindedPorts = new HashMap<Integer, AcceptListener>();
		portsWaitingQueues = new HashMap<Integer, Queue<Ticket>>();
	}

	@Override
	public boolean bind(int port, AcceptListener listener) {
		CBroker server = this;
		if (bindedPorts.containsKey(port))
			return false;
		Runnable bindOperation = new Runnable() {

			@Override
			public void run() {
				bindedPorts.put(port, listener);
				Queue<Ticket> queue = portsWaitingQueues.get(port);
				if (queue != null) {
					while (!queue.isEmpty()) {

						Ticket ticket = queue.poll();
						// TODO create both channels
						CChannel acceptChannel = new CChannel(server);
						CChannel connectChannel = new CChannel(ticket.getBroker(), acceptChannel);

						Runnable connected = new Runnable() {

							@Override
							public void run() {
								ticket.getListener().connected(connectChannel);

							}

						};
						// Probably works with only one Runnable for both connected/accepted but we will
						// not add another different case for clarity purpose
						Runnable accepted = new Runnable() {

							@Override
							public void run() {
								listener.accepted(acceptChannel);

							}

						};
						getPump().post(accepted);
						getPump().post(connected);

					}
				}
			}
		};
		getPump().post(bindOperation);
		return true;
	}

	@Override
	public boolean unbind(int port) {
		if (!bindedPorts.containsKey(port))
			return false;
		Runnable unbindOperation = new Runnable() {

			@Override
			public void run() {
				bindedPorts.remove(port);

			}
		};
		getPump().post(unbindOperation);
		return true;
	}

	@Override
	public boolean connect(String name, int port, ConnectListener listener) {
		CBroker asker = this;
		CBroker target;
		synchronized (brokerManager) {
			target = (CBroker) brokerManager.get(name);
		}
		if (target == null)
			return false;

		Runnable connectOperation = new Runnable() {

			@Override
			public void run() {
				if (target.bindedPorts.containsKey(port)) {
					CChannel acceptChannel = new CChannel(target);
					CChannel connectChannel = new CChannel(asker, acceptChannel);

					Runnable connected = new Runnable() {

						@Override
						public void run() {
							listener.connected(connectChannel);

						}

					};
					// Probably works with only one Runnable for both connected/accepted but we will
					// not add another different case for clarity purpose
					Runnable accepted = new Runnable() {

						@Override
						public void run() {
							target.bindedPorts.get(port).accepted(acceptChannel);
						}

					};
					getPump().post(accepted);
					getPump().post(connected);

				} else {
					Queue<Ticket> queue = target.portsWaitingQueues.get(port);
					if (queue == null) {
						queue = new LinkedList<Ticket>();
					}
					queue.add(new Ticket(asker, listener));
				}

			}

		};
		getPump().post(connectOperation);
		return true;
	}
	
	private class Ticket{
		Broker broker;
		ConnectListener listener;
		
		Ticket(CBroker b, ConnectListener l){
			broker = b;
			listener = l;
		}

		public Broker getBroker() {
			return broker;
		}

		public ConnectListener getListener() {
			return listener;
		}
	}
	
	
	
	public static void main(String args[]){
		Executor pompe = new Executor("PompeFunebre");
		pompe.start();
		CBroker un = new CBroker("un", pompe);
		CBroker deux = new CBroker("deux", pompe);
		
		CChannel uno = null;
		CChannel duo = null;
		
		MyAcceptListener al = new MyAcceptListener(uno);
		MyConnectListener cl = new MyConnectListener(duo);
		
		
		
		un.bind(0, al);
		deux.connect("un", 0, cl);
		
		byte b = 7;
		
		while(uno == null || duo == null) {
			try {
				uno = al.c;
				duo = cl.c;
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		uno.setListener(new MyListener());
		duo.setListener(new MyListener());
		uno.send(b);
		
	}

}



