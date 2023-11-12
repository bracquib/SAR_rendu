package info5.sar.events.channel;

import info5.sar.channels.DisconnectedException;
import info5.sar.events.channels.Broker;
import info5.sar.events.channels.Channel;
import info5.sar.utils.Listener;

public class CChannel extends Channel {
public CChannel linkedChannel;
	private boolean closed;
	private Listener listener;

	public CChannel(Broker broker) {
		super(broker);
		linkedChannel = null;
		closed = true;
		listener = null;
	}

	public CChannel(Broker broker, CChannel c) {
		super(broker,c);
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

	@Override
	public int send(byte b) throws DisconnectedException {
		if (closed) {
			throw new DisconnectedException("Disconnected");
		}

		linkedChannel.getBroker().getPump().post(new Runnable() {
			@Override
			public void run() {
				linkedChannel.listener.received(Byte.valueOf(b));
			}
		});
		return b;

}
}
