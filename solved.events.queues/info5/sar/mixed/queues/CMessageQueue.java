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
package info5.sar.mixed.queues;

import info5.sar.events.channels.Channel;
import info5.sar.events.queues.MessageQueue;
import info5.sar.events.queues.QueueBroker;

/**
 * This is for the mixed implementation, mixing
 * event-oriented queues with threaded channels.
 */
public class CMessageQueue extends MessageQueue {
	 private Thread workerReader;
    private Thread workerWriter;
    private QueueBroker queueBroker;
    private final info5.sar.channels.Channel channel;
    private Listener listener;
    private boolean isClosed = false;

    public CMessageQueue(QueueBroker broker, info5.sar.channels.Channel channel2) {
        this.channel = channel2;
        this.queueBroker = broker;
    }
  @Override
  public QueueBroker broker() {
    return this.queueBroker;
  }

  @Override
  public void setListener(Listener l) {
    this.listener = l;
  }

  @Override
  public boolean send(byte[] bytes) {
  System.out.println("send: " + bytes.length);
    if (this.isClosed) {
      return false;
    }
    System.out.println("send rentre dans le thread");
    this.workerWriter = new Thread(() -> {
      try {
        this.channel.write(bytes, 0, bytes.length);
        System.out.println("write: " + bytes.length);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    this.workerWriter.start();
    return true;
  }

  @Override
  public void close() {
    System.out.println("close");
    this.isClosed = true;
    this.workerReader.interrupt();
    this.workerWriter.interrupt();
  }

  @Override
  public boolean closed() {
    return this.isClosed;
  }

  @Override
  public String getRemoteName() {
    return this.channel.getRemoteName();
  }

}
