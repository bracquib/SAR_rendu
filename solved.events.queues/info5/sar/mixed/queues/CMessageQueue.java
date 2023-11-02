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

import info5.sar.events.queues.MessageQueue;
import info5.sar.events.queues.QueueBroker;

/**
 * This is for the mixed implementation, mixing
 * event-oriented queues with threaded channels.
 */
public class CMessageQueue extends MessageQueue {

  @Override
  public QueueBroker broker() {
    throw new RuntimeException("Not Implemented Yet");
  }

  @Override
  public void setListener(Listener l) {
    throw new RuntimeException("Not Implemented Yet");
  }

  @Override
  public boolean send(byte[] bytes) {
    throw new RuntimeException("Not Implemented Yet");
  }

  @Override
  public void close() {
    throw new RuntimeException("Not Implemented Yet");
  }

  @Override
  public boolean closed() {
    throw new RuntimeException("Not Implemented Yet");
  }

  @Override
  public String getRemoteName() {
    throw new RuntimeException("Not Implemented Yet");
  }

}
