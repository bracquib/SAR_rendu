
* Overview: Broker / Channel

A channel is a communication channel, a point-to-point stream of bytes.
Full-duplex, each end point can be used to read or write.
A connected channel is FIFO and lossless, see Section "Disconnecting"
for details about disconnection.

** Connecting

A channel is established, in a fully connected state, when a connect 
matches an accept. When connecting, the given name is the one of the remote broker, the given port is the one of an accept on the remote broker.

There is no precedence between connect and accept, they let listener and they are called once the connection is established.

When connecting, we must distinguish between two cases:
(i) there is no accept yet and (ii) there is not such broker. 
When the named broker does not exist, the connect returns false, else he wait to be called with his listener.

** Writing

When writing, the given byte array contains the bytes to write from the given offset and the number of bytes to write is the given length.
The method "write" returns true and will called the listener of the reader.

Nota Bene: a channel is a stream, so although the write operation does take a range of bytes to write from an array of bytes, the semantics is one that writes one byte at a time in the stream.

The method "write" let a listener if there is no room to write any byte and will be called if enough space appears

If the method has been waiting the calling task and the channel becomes disconnected, the listener will be call and it will throw an exception (DisconnectedException).

** Reading

When reading, the given byte array will contain the bytes read,
starting at the given offset, the number of bytes to read is the
given length. The method "read" will return the number of bytes read, that may not be zero or negative.

The method "read" let a listener if there is no bytes to read and it will be called if enough bytes appears.

The end of stream is the same as being as the channel being disconnected, so the listner will throw an exception (DisconnectedException). 

Note: classical C programming would have the method "read" return -1, 
indicating the end of stream has been reached and no bytes has been read. 
We will use an exception to show proper object-oriented design since 
the end of stream is an exceptional situation. 

** Disconnecting

A channel can be disconnected at any time, from either side. So this requires an asynchronous protocol to disconnect a channel.

Note: since we are not considering only one task per end point (no multi-tasking), if there is a task blocked on an operation, the disconnect may only happen from the other side. We will talk about the local side versus remote side.

When the remote side disconnects a channel, there may be still bytes in transit.
By in transit, we mean bytes that were written by that remote side, before it disconnected the channel, and that have not been read on a local side. 
Therefore, the local side should not be considered disconnected until these bytes have been read. This is to simplify programming around disconnecting a channel, as we will discuss shortly.

But if a local channel appears as not disconnected while its far side has been disconnected, then, we need to decide how should local write operations should behave. The simplest is to drop the bytes silently, as if they were written. Again, this is to simplify programming around disconnecting a channel, so let's discuss it now.

The above behavior may seem counter-intuitive but it is the easier one on developers.
First, it is likely that a communication will end by writing some bytes and then disconnecting locally. Something like saying "bye" and then hanging up.
It is therefore important that the other side may read the last bytes written. 
Second, dropping written bytes may seem wrong but it is just leveraging an unavoidable truth: written bytes may be dropped even though channels are FIFO and lossless. Indeed, it is not at all different than if the bytes were written before the other side disconnected a channel without reading all pending bytes. 
In both cases, the bytes would be dropped.

Nota Bene: one should resist the temptation to adopt an immediate synchronous disconnection. First, it would not be possible if our channels would not be implemented over shared memory. Disconnecting would imply sending a control message to inform the other side and thus the disconnect protocol would be asynchronous. Second, it would become a harder programming model.

How would someone write a bye-and-hangup pattern?

With our programming model, it is as simple as write "bye" and disconnect.

Without the ability to read all written bytes, even from a remotely disconnected channel, the protocol becomes more complex. The side A would have to write "bye" and then would have to wait until the side B has read these bytes. The only way to do so would be to wait for the channel to become disconnected. So the side B would have to read the bytes "bye" and then disconnect. But then, some bytes in transit, written by side B, might not be readable by side A. So that side B must not disconnect but rather reply with "bye" reply, acting as a flush operation on the out stream, and then wait for the channel to become disconnected. 
Only when side A receives the reply "bye" can it disconnect the channel.

Note that the above protocol can still be put in place, but only if necessary.
