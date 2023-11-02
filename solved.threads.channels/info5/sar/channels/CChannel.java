/*
 * Author: Kyllian Gricourt modify by us
 */

package info5.sar.channels;

import info5.sar.utils.CircularBuffer;

public class CChannel extends Channel {

    CircularBuffer cb;
    CChannel distantChannel;
    private boolean disconnected;

    public CChannel(Broker broker) {
        super(broker);
        disconnected = false;
        cb = new CircularBuffer(1000);
    }

    public CChannel(Broker broker, CChannel channel) {
        this(broker);
        distantChannel = channel;
        distantChannel.distantChannel = this;
    }

    @Override
    public int read(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (disconnected && cb.empty()) {
            throw new DisconnectedException("Disconnected");
        }

        int index = offset;
        synchronized (cb) {
            while (index < offset + length && !cb.empty() || index == offset) {
                while (cb.empty()) {
                    if (distantChannel.disconnected()) {
                        throw new DisconnectedException("Distant channel disconnected and empty buffer");
                    }
                    try {
                        cb.wait();
                    } catch (InterruptedException e) {
                    }
                }
                bytes[index++] = cb.pull();
            }
            cb.notifyAll();
        }
        return index - offset;
    }

    @Override
    public int write(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (disconnected) {
            throw new DisconnectedException("Disconnected");
        }

        return distantChannel.receive(bytes, offset, length);
    }

    @Override
    public void disconnect() {
        disconnected = true;
        synchronized (cb) {
            cb.notifyAll();
        }
        if (!distantChannel.disconnected()) {
            distantChannel.disconnect();
        }

    }

    @Override
    public boolean disconnected() {
        return disconnected;
    }

    public int receive(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (disconnected) {
            throw new DisconnectedException("Disconnected");
        }

        int index = offset;
        synchronized (cb) {
            while (index < offset + length && !cb.full() || index == offset) {

                while (cb.full()) {
                    try {
                        cb.wait();
                    } catch (InterruptedException e) {
                    }
                }
                cb.push(bytes[index++]);

            }
            cb.notifyAll();
        }
        return index - offset;
    }

	@Override
	public String getRemoteName() {
		// TODO Auto-generated method stub
		return distantChannel.toString();
	}

}
