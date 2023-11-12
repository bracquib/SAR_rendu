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

import info5.sar.channels.DisconnectedException;
import info5.sar.events.channel.CChannel;
import info5.sar.utils.Listener;

/**
 *==================================================================== 
 * This is a place-holder class for fully event-oriented channels.
 *==================================================================== 
 * 
 * Channel is a point-to-point stream of bytes.
 * Full-duplex, each end point can be used to read or write.
 * A connected channel is FIFO and lossless. 
 * A channel can be disconnected at any time, from either side.
 */
public abstract class Channel {
  Broker broker;
  CChannel linkedChannel;

  protected Channel(Broker broker) {
    this.broker = broker;
  }

  protected Channel(Broker broker, CChannel c) {
    this.broker = broker;
    linkedChannel = c;
  }

  // added for helping debugging applications.
  public abstract String getRemoteName();
  
  public Broker getBroker() {
    return broker;
  }


  public abstract void close();

  public abstract boolean closed();

public abstract void setListener(Listener l);


public abstract int send(byte b) throws DisconnectedException ;



}
