package info5.sar.events.channel;

import java.util.ArrayList;
import java.util.HashMap;

import info5.sar.events.channels.Broker;
import info5.sar.events.channels.Channel;
import info5.sar.queues.ClosedException;
import info5.sar.utils.CircularBuffer;
import info5.sar.utils.Executor;
import info5.sar.utils.WriterReaderListener;

public class CChannel extends Channel {
    private CChannel remoteChannel;
	private boolean closed = true;
	private CircularBuffer bufferRead = new CircularBuffer(1024);
	private CircularBuffer bufferWrite = new CircularBuffer(1024);

	private HashMap<String, ArrayList<Runnable>> runnables = new HashMap<>();


	public CChannel(Broker broker) {
		super(broker);
		runnables.put("read", new ArrayList<Runnable>());
        runnables.put("write", new ArrayList<Runnable>());	
		remoteChannel = null;
		closed = true;
	}

	public CChannel(Broker broker, CChannel channel) {
		super(broker,channel);
		remoteChannel = channel;
		bufferRead = channel.bufferWrite;
		bufferWrite = channel.bufferRead;
		channel.remoteChannel = this;
		channel.closed = false;
		runnables.put("read", new ArrayList<Runnable>());
        runnables.put("write", new ArrayList<Runnable>());	
		closed = false;
	}

	@Override
	public String getRemoteName() {
		return remoteChannel.getBroker().getName();
	}

	public CChannel getRemoteChannel() {
		return remoteChannel;
	}



	@Override
	public void close() {
		closed = true;
	}

	@Override
	public boolean closed() {
		return closed;
	}

	@Override
	public boolean write(byte[] bytes, int offset, int length,WriterReaderListener listener) throws ClosedException {
		if (closed() ) {
			throw new ClosedException();
		}
		if (remoteChannel.closed()){
			listener.write(length);
			return true;
		}
		
		Broker broker = getBroker();
		Executor executor = broker.getExecutor();

		Runnable write = new Runnable() {
			@Override
			public void run() {
				if (bufferWrite.full()) {
					remoteChannel.runnables.get("write").add(this);
				}
				else {
					int wrote = 0;
					while (!bufferWrite.full() && wrote < length) {
						bufferWrite.push(bytes[offset + wrote++]);
					}
					listener.write(wrote);
					while (!runnables.get("read").isEmpty()) {
						executor.post(runnables.get("read").remove(0));
					}
				}
			}
		};
		executor.post(write);
		return true;
	}
		

	@Override
	public boolean read(byte[] bytes, int offset, int length,WriterReaderListener listener) throws ClosedException {
		if (closed() || (remoteChannel.closed() && bufferRead.empty())) {
			throw new ClosedException();
		}
		Broker broker = getBroker();
		Executor executor = broker.getExecutor();

		Runnable read = new Runnable() {
			@Override
			public void run() {
				if (bufferRead.empty()) {
					remoteChannel.runnables.get("read").add(this);
				}
				else {
					int read = 0;
					while (!bufferRead.full() && read < length) {
						bytes[offset + read++] = bufferRead.pull();
					}
					listener.read(read);
					while (!runnables.get("write").isEmpty()) {
						executor.post(runnables.get("write").remove(0));
					}
				}
			}

		};
		executor.post(read);
		return true;
	}

		
				
}
