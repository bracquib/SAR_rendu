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

import info5.sar.utils.Executor;
import info5.sar.events.channels.Broker;
import info5.sar.utils.AcceptListener;
import info5.sar.utils.ConnectListener;


public abstract class QueueBroker {
  protected Broker broker;
  protected Executor executor;
  
  public QueueBroker(Broker broker, Executor executor) {
    this.broker = broker;
    this.executor = executor;
  }

public Executor getExecutor() {
    return executor;
  }
  
  public String getName() {
    return broker.getName();
  }

  public Broker getBroker() {
    return broker;
  }

  public abstract boolean bind(int port, AcceptListener listener);

  public abstract boolean unbind(int port);

  public abstract boolean connect(String name, int port, ConnectListener listener);

public Executor getEventPump() {
	return executor;
}

}
