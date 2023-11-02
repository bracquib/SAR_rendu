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

import info5.sar.channels.Broker;
import info5.sar.utils.Executor;

/**
 * This is for the full event-oriented implementation, 
 * using a single event pump (Executor).
 * You must specify, design, and code the event-oriented
 * version of the channels and their brokers.
 */
public class CQueueBroker extends QueueBroker {

  public CQueueBroker(Executor pump, Broker broker) {
    super(pump,broker);
    throw new RuntimeException("Not Implemented Yet");
  }

  @Override
  public boolean bind(int port, AcceptListener listener) {
    throw new RuntimeException("Not Implemented Yet");
  }

  @Override
  public boolean unbind(int port) {
    throw new RuntimeException("Not Implemented Yet");
  }

  @Override
  public boolean connect(String name, int port, ConnectListener listener) {
    throw new RuntimeException("Not Implemented Yet");
  }

}
