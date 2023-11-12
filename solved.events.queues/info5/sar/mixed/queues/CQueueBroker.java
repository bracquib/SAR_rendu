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

import info5.sar.channels.Broker;
import info5.sar.events.queues.MessageQueue;
import info5.sar.events.queues.QueueBroker;
import info5.sar.utils.Executor;

/**
 * This is for the mixed implementation, mixing
 * event-oriented queues with threaded channels.
 */
public class CQueueBroker extends QueueBroker {

private Thread workerBind;
    private Thread workerConnect;
    private volatile boolean isRunning = true;

  public CQueueBroker(Executor pump, Broker broker) {
    super(pump,broker);
    System.out.println("CQueueBroker");
  }



      @Override
    public boolean bind(int port, AcceptListener listener) {
        System.out.println("bind");
        if (listener == null) {
            return false;
        }
        if (this.broker.isPortUsed(port)){
           return false; 
        }
        workerBind = new Thread(() -> {
    System.out.println("workerBind thread started");
    while (isRunning) {
    	info5.sar.channels.Channel channel = this.broker.accept(port);
            this.pump.post(() -> {
                System.out.println("before listener.accepted");
                listener.accepted(new CMessageQueue(this, channel, this.pump));
                System.out.println("after listener.accepted");
            });
        
    }
    System.out.println("workerBind thread ended");
});
        workerBind.start();
        System.out.println("bind end");
        return true;
    }

@Override
    public boolean unbind(int port) {
        isRunning = false;
        this.workerBind.interrupt();
        return true;
    }

  

 @Override
    public boolean connect(String name, int port, ConnectListener listener) {
        System.out.println("connect");
        if (listener == null) {
            return false;
        }
        workerConnect = new Thread(() -> {
            if (this.broker.isPortUsed(port)){
                System.out.println("refused from connect");
                this.pump.post(listener::refused);
            } else {
                System.out.println("connect from connect");
            	info5.sar.channels.Channel channel = this.broker.connect(name, port);
                this.pump.post(() ->  {
                    System.out.println("before listener.connected");
                    listener.connected(new CMessageQueue(this,channel, this.pump));
                    System.out.println("after listener.connected");
                });
            }
        });
        workerConnect.start();
        return true;
    }
}