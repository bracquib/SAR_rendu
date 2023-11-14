/*
 * Author: Kyllian Gricourt modify by us
 */

package info5.sar.channels;
import java.util.Hashtable;

import info5.sar.utils.BrokerManager;
public class CBroker extends Broker {

    private static final BrokerManager<CBroker> brokerManager = BrokerManager.getInstance(CBroker.class);
    Hashtable<Integer, CChannel> waitingChannelByPort;
    RDVManager rdv = new RDVManager();

    public CBroker(String name) {
        super(name);
        try {
            brokerManager.registerBroker(name, this);
        } catch (Exception e) {
            System.out.println(
                    "Could not register Broker " + name + " because another broker with the same name already exist.");
            
        }
        waitingChannelByPort = new Hashtable<Integer, CChannel>();
    }

    @Override
    public Channel accept(int port) {
        CChannel channel = new CChannel(this);
        synchronized (waitingChannelByPort) {
            waitingChannelByPort.put(port, channel);
        }

        rdv.acceptSideRDV(port);

        synchronized (waitingChannelByPort) {
            while (channel.distantChannel == null) {
                try {
                    waitingChannelByPort.wait();
                } catch (InterruptedException e) {
                    // Do nothing, wait again
                }
            }
            waitingChannelByPort.notifyAll();
        }
        return channel;

    }

    @Override
    public Channel connect(String name, int port) {
        CBroker b = brokerManager.getBroker(name);

        Channel channel = b.connectOnBroker(port);

        return channel;
    }

    public CChannel connectOnBroker(int port) {

        rdv.connectSideRDV(port);

        CChannel channel;
        synchronized (waitingChannelByPort) {
            channel = new CChannel(this, waitingChannelByPort.get(port));
            waitingChannelByPort.notifyAll();
        }

        return channel;
    }
    public boolean isPortUsed(int port) {
        return rdv.isPortUsed(port);
    }
}
