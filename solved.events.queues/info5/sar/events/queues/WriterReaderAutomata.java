package info5.sar.events.queues;

import java.nio.ByteBuffer;
import java.util.Arrays;

import info5.sar.events.channels.Channel;
import info5.sar.events.queues.MessageQueue.Listener;
import info5.sar.queues.ClosedException;
import info5.sar.utils.WriterReaderListener;

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
			public void read(int x) {

				remaining -= x;

				if (state == State.READ_SIZE) {
					if (remaining > 0) {
						try {
							channel.read(buffer, (buffer.length - remaining), remaining, this);
						} catch (ClosedException e) {
							listener.closed();
						}

					} else {
						ByteBuffer buffer1 = ByteBuffer.wrap(buffer);
						int size = buffer1.getInt();
						buffer = new byte[size];
						remaining = size;
						state = State.READ_CONTENT;
						try {
							channel.read(buffer, 0, size, this);
						} catch (ClosedException e) {
							listener.closed();
						}

					}
				} else if (state == State.READ_CONTENT) {
					if (remaining > 0) {
						try {
							channel.read(buffer, (buffer.length - remaining), remaining, this);
						} catch (ClosedException e) {
							listener.closed();
						}

					} else {
						System.out.println("Taille du tableau a retourné : " + buffer.length);
						listener.received(Arrays.copyOf(buffer, buffer.length));
						buffer = new byte[Integer.BYTES];
						remaining = Integer.BYTES;
						state = State.READ_SIZE;
						try {
							channel.read(buffer, 0, remaining, this);
						} catch (ClosedException e) {
							listener.closed();
						}

					}

				}
			}

			@Override
			public void write(int bytes) {
				// TODO Auto-generated method stub
				
			}
		};
		try {
			channel.read(buffer, 0, 4, writeReaderListener);
		} catch (ClosedException e) {
			// rine
		}
	}

	public boolean write(byte[] bytes) {
	    byte[] buffer2 = new byte[Integer.BYTES];
	    ByteBuffer buffer3 = ByteBuffer.wrap(buffer2).putInt(bytes.length);
	    final byte[] buffer4 = buffer3.array();
	    Listener listener = queue.listener;
	    Channel channel = queue.channel;

	    WriterReaderListener writeReaderListener = new WriterReaderListener() {
	        int remaining = Integer.BYTES;
	        State state = State.WRITE_SIZE;

	        @Override
	        public void write(int writtenBytes) {
	            remaining -= writtenBytes;

	            if (state == State.WRITE_CONTENT) {
	                if (remaining > 0) {
	                    try {
	                        channel.write(bytes, (bytes.length - remaining), remaining, this);
	                    } catch (ClosedException e) {
	                        // Nothing to do here
	                    }
	                }
	            } else if (state == State.WRITE_SIZE) {
	                if (remaining > 0) {
	                    try {
	                        channel.write(buffer4, (Integer.BYTES - remaining), remaining, this);
	                    } catch (ClosedException e) {
	                        // Nothing to do here
	                    }
	                } else {
	                    state = State.WRITE_CONTENT;
	                    remaining = bytes.length;
	                    try {
	                        channel.write(bytes, (bytes.length - remaining), remaining, this);
	                    } catch (ClosedException e) {
	                        // Nothing to do here
	                    }
	                }
	            }
	        }

	        @Override
	        public void read(int bytesRead) {
	            // TODO Auto-generated method stub

	        }
	    };

	    try {
	        channel.write(buffer4, 0, Integer.BYTES, writeReaderListener);
	    } catch (ClosedException e) {
	        // Nothing to do here
	    }

	    return true;
	}

}