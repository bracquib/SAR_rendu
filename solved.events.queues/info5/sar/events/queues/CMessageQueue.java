/*
 * Copyright (C) 2023 Pr. Olivier Gruber                                    
 *                                                                       
 * This program is free software: you can redistribute it and/or modify  
 * it under the terms of the GNU General Public License as published by  
 * the Free Software Foundation, either version 3 of the License, or     
 * (at your option) any later version.                                   
 *                                                                       
 * This program is distributed in the hope that it will be useful,       
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         
 * GNU General Public License for more details.                          
 *                                                                       
 * You should have received a copy of the GNU General Public License     
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package info5.sar.events.queues;

import info5.sar.events.channels.Channel;

/**
 * This is for the full event-oriented implementation, using a single event pump
 * (Executor). You must specify, design, and code the event-oriented version of
 * the channels and their brokers.
 */
public class CMessageQueue extends MessageQueue {
	protected Channel channel;
	protected Listener listener;

	private WriterReaderAutomata automata;

    protected CMessageQueue(QueueBroker broker, Channel channel) {
		super(broker);
        this.channel = channel;
        this.automata = new WriterReaderAutomata(this);
    }

	@Override
	public QueueBroker broker() {
		return broker;
	}

	@Override
	public void setListener(Listener l) {
		this.listener = l;
		automata.start();
	}

	@Override
	public boolean send(byte[] bytes) {
		return automata.write(bytes);
	}

	@Override
	public void close() {
		channel.close();
	}

	@Override
	public boolean closed() {
		return channel.closed();
	}

	@Override
	public String getRemoteName() {
		return channel.getRemoteName();
	}

}
