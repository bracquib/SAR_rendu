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

public abstract class QueueBroker {
  private Broker broker;
  private Executor pump;
  public QueueBroker(Executor pump, Broker broker) {
    this.broker = broker;
    this.pump = pump;
  }

  public Executor getEventPump() { return pump; }
  public String getName() {
    return broker.getName();
  }

  public Broker getBroker() {
    return broker;
  }

  public interface AcceptListener {
    void accepted(MessageQueue queue);
  }

  public abstract boolean bind(int port, AcceptListener listener);

  public abstract boolean unbind(int port);

  public interface ConnectListener {
    void connected(MessageQueue queue);

    void refused();
  }

  public abstract boolean connect(String name, int port, ConnectListener listener);

}
