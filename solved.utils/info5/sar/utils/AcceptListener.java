package info5.sar.utils;

import info5.sar.events.channels.Channel;
import info5.sar.queues.MessageQueue;

public interface AcceptListener {
	
	  /**
     * make an accept request
     *
     * @param channel - the channel
     */
    void accepted(Channel channel);

    void accepted(MessageQueue queue);


  
}
