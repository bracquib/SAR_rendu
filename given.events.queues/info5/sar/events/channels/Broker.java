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
package info5.sar.events.channels;



import info5.sar.utils.Executor;

/**
 *==================================================================== 
 * This is a place-holder class for fully event-oriented broker
 * of channels. 
 *==================================================================== 
 * 
 * Brokers are there to permit to establish channels.
 * Each broker must be uniquely named. 
 * Each broker may be used to accept on different ports concurrently,
 * creating a new channel for each newly accepted connection.
 */
public abstract class Broker {
  String name;
  Executor executor;
  /* 
    * Creates a new broker with the given name and executor.
    * The executor is used to run the broker's threads.
    * @param name the name of the broker.
    * @param executor the executor to run the broker's threads.
   * Each broker must be uniquely named. 
   * @throws IllegalArgumentException if the name is not unique.
   */
  protected Broker(String name, Executor executor){
    this.name = name;
    this.executor = executor;

  }
  public interface AcceptListener {
		void accepted(Channel queue);
	}
  
  public interface ConnectListener {
		void connected(Channel queue);

		void refused();
	}
  /*
   * @returns the name of this broker.
   */
  public String getName() { return name; }

  /*
   * @returns a channel connected to the given port.
   * @throws IllegalArgumentException if the port is already used.
   */
  public abstract boolean accept(int port, AcceptListener listener);

  public abstract boolean disconnect(int port);
  /*
   * @returns a channel connected to the given port on the given broker.
   * @throws IllegalArgumentException if the port is already used.
   */
  public abstract boolean connect(String name, int port, ConnectListener listener);
  
  
  public Executor getExecutor() {
		return executor;
	}

}
