package info5.sar.events.queues;

import info5.sar.utils.WriterReaderListener;

import java.nio.ByteBuffer;
import java.util.Arrays;

import info5.sar.events.channels.Channel;
import info5.sar.events.queues.MessageQueue.Listener;
import info5.sar.queues.ClosedException;

public class WriterReaderAutomata {
	private enum State {
		READ_SIZE, READ_CONTENT, WRITE_SIZE, WRITE_CONTENT
	};

	private State state = State.READ_SIZE;

	private boolean isRunning = false;
	byte[] buffer = new byte[Integer.BYTES];

	private CMessageQueue queue;

	public WriterReaderAutomata(CMessageQueue queue) {
		this.queue = queue;
	}

	public void start() {
		if (!isRunning) {
			isRunning = true;
			startAux();
		}
	}

	private void startAux() {
		Listener listener = queue.listener;
		Channel channel = queue.channel;

		WriterReaderListener writeReaderListener = new WriterReaderListener() {
			int remaining = Integer.BYTES;

			@Override
			public void read(int bytes) {
				remaining -= bytes;

				switch (state) {
				case READ_SIZE:
					if (remaining > 0) {
						try {
							channel.read(buffer, buffer.length - remaining, remaining, this);
						} catch (ClosedException e) {
							// TODO: handle exception
							listener.closed();
						}
					} else {
						ByteBuffer buffer2 = ByteBuffer.wrap(buffer);
						int size = buffer2.getInt();
						remaining = size;
						buffer = new byte[size];

						try {
							channel.read(buffer, 0, size, this);
						} catch (ClosedException e) {
							listener.closed();
						}
					}
					break;
				case READ_CONTENT:
					if (remaining > 0) {
						try {
							channel.read(buffer, buffer.length - remaining, remaining, this);
						} catch (ClosedException e) {
							// TODO: handle exception
							listener.closed();
						}
					} else {
						listener.received(Arrays.copyOf(buffer, buffer.length));
						remaining = Integer.BYTES;
						buffer = new byte[Integer.BYTES];
						state = State.READ_SIZE;

						try {
							channel.read(buffer, 0, remaining, this);
						} catch (ClosedException e) {
							listener.closed();
						}

					}
					break;
				case WRITE_SIZE:
				case WRITE_CONTENT:
					break;
				}
			}

			@Override
			public void write(int bytes) {
				// TODO Auto-generated method stub

			}
		};

		try {
			channel.read(buffer, 0, Integer.BYTES, writeReaderListener);
		} catch (ClosedException e) {
			// nothing to do here
		}

	}

	public boolean write(byte[] bytes){
	    byte[] buffer2 = new byte[Integer.BYTES];
	    ByteBuffer buffer3 = ByteBuffer.wrap(buffer2).putInt(bytes.length);
	    buffer2 = buffer3.array();

	    Listener listener = queue.listener;
        Channel channel = queue.channel;

        WriterReaderListener writeReaderListener = new WriterReaderListener() {
			int remaining = Integer.BYTES;

			state = State.WRITE_SIZE;

			@Override
			public void read(int bytes) {
				// TODO Auto-generated method stub
			}

			public void write(int bytes) {
				remaining -= bytes;

				switch (state) {
                case WRITE_SIZE:
                    if (remaining > 0) {
                        try {
                            channel.write(buffer, Integer.BYTES - remaining, remaining, this);
                        } catch (ClosedException e) {
							// nothing to do here
                        }
                    }
                    break;
                case WRITE_CONTENT:
                    if (remaining > 0) {
                        try {
                            channel.write(buffer, buffer.length - remaining, remaining, this);
                        } catch (ClosedException e) {
							// nothing to do here
                        }
                    }
                    break;
                case READ_SIZE:
                case READ_CONTENT:
                    break;
                }
            }
        };

        try {
            channel.write(buffer2, 0, Integer.BYTES, writeReaderListener);
        } catch (ClosedException e) {
          // nothing to do here
        }

        return true;
    }
}