package info5.sar.events.queues;

import info5.sar.events.channels.Broker;
import info5.sar.events.channels.Channel;

public class CChannel extends Channel {

	// private CircularBuffer readBuffer;
	// private CircularBuffer writeBuffer;
	private CChannel linkedChannel;
	private boolean closed;
	private Listener listener;

	public CChannel(Broker broker) {
		super(broker);
		linkedChannel = null;
		closed = true;
		listener = null;
	}

	public CChannel(Broker broker, CChannel c) {
		super(broker);
		linkedChannel = c;
		closed = false;
		c.linkedChannel = this;
		c.closed = false;
		listener = null;
	}

	@Override
	public String getRemoteName() {
		return linkedChannel.getBroker().getName();
	}

	@Override
	public void setListener(Listener l) {
		listener = l;
	}

	@Override
	public boolean send(byte b) {
		if (linkedChannel.listener == null)
			return false;

		Runnable delivery = new Runnable() {

			@Override
			public void run() {
				linkedChannel.listener.received(Byte.valueOf(b));

			}
		};

		getBroker().getPump().post(delivery);
		return true;
	}

	@Override
	public void close() {
		closed = true;
		Runnable close = new Runnable() {
			@Override
			public void run() {
				listener.closed();
				linkedChannel.close();
			}
		};
		getBroker().getPump().post(close);
	}

	@Override
	public boolean closed() {
		return closed;
	}
	
	public CChannel getLinkedChannel() {
		return linkedChannel;
	}
}
