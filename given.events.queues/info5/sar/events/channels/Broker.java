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
  /* 
   * Each broker must be uniquely named. 
   * @throws IllegalArgumentException if the name is not unique.
   */
  protected Broker(String name) {
    this.name = name;
  }
  
  /*
   * @returns the name of this broker.
   */
  public String getName() { return name; }
  
}
