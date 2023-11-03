package info5.sar.queues;

import info5.sar.channels.Channel;
import info5.sar.channels.DisconnectedException;
import static info5.sar.utils.Log.log;
public class CMessageQueue extends MessageQueue {
    private final Channel channel;
    private QueueBroker queueBroker;

    protected CMessageQueue(QueueBroker queueBroker, Channel channel) {
        super(queueBroker);
        this.channel = channel;
    }

    @Override
    public void send(byte[] bytes, int offset, int length) throws ClosedException { 
        byte[] lengthBytes = new byte[1];
        lengthBytes[0] = (byte) length;
        log("lengthbytes:"+lengthBytes[0]);
        try {
			channel.write(lengthBytes, 0, 1);
		} catch (DisconnectedException e) {
			// TODO Auto-generated catch block
			throw new ClosedException();
		}
        
    	try {
        while (length > 0) {
            int sent;
			
				sent = channel.write(bytes, offset, length);
			
            offset += sent;
            length -= sent;
        }
    	} catch (DisconnectedException e) {
			// TODO Auto-generated catch block
			throw new ClosedException();
		}
    }

    @Override
    public byte[] receive() throws ClosedException {
        byte[] lengthBytes = new byte[1];
        try {
            channel.read(lengthBytes, 0, 1);
        } catch (DisconnectedException e) {
            throw new ClosedException();
        }
        int length = lengthBytes[0];
        byte[] bytes = new byte[length];
        int offset = 0;
        try {
            while (length > 0) {
                int received = channel.read(bytes, offset, length);
                offset += received;
                length -= received;
            }
        } catch (DisconnectedException e) {
            throw new ClosedException();
        }
        return bytes;
    }

    @Override
    public void close() {
        channel.disconnect();
    }

    @Override
    public boolean closed() {
        return channel.disconnected();
    }

    @Override
    public void addListener(MessageQueueListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean send(byte[] bytes, Object cookie) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean send(byte[] bytes, int offset, int length, Object cookie) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void processed(byte[] bytes, Object cookie) {
        throw new UnsupportedOperationException();
    }

	@Override
	public QueueBroker broker() {
		// TODO Auto-generated method stub
		return null;
	}
}
