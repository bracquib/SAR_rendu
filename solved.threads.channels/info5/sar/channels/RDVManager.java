/*
 * Author: Kyllian Gricourt
 */

package info5.sar.channels;

import java.util.Dictionary;
import java.util.Hashtable;

public class RDVManager {

    Dictionary<Integer, RDV> connectionByPort = new Hashtable<Integer, RDV>();

    public void connectSideRDV(int port) {
        RDV c;
        synchronized (connectionByPort) {
            c = connectionByPort.get(port);
            if (c == null) {
                c = new RDV();
                connectionByPort.put(port, c);
            }
        }
        c.connect();
        synchronized (connectionByPort) {

            connectionByPort.notify();
        }
    }

    public void acceptSideRDV(int port) {
        RDV c;
        synchronized (connectionByPort) {
            c = connectionByPort.get(port);
            if (c == null) {
                c = new RDV();
                connectionByPort.put(port, c);
            } else if (c.accepted) {
                throw new IllegalArgumentException("Port already bound");
            }
        }
        c.accept();

        synchronized (connectionByPort) {
            while (c.accepted) {
                try {
                    connectionByPort.wait();
                } catch (InterruptedException e) {
                    // Do nothing, wait again
                }
            }
        }
    }

    public boolean isPortUsed(int port) {
    synchronized (connectionByPort) {
        return connectionByPort.get(port) != null;
    }
}

    /*
     * This class is a rendez-vous It wait for at least one connect and one accept
     * for the execution to continue on both sides. If multiple connect are waiting,
     * an accept will relase only one connect. Idem for accept.
     * In the specification, we shouldn't have multiple accept but this behavior is handled
     * by the RDVManager.
     */
    class RDV {
        boolean connected;
        boolean accepted;

        RDV() {
            connected = false;
            accepted = false;
        }

        synchronized void connect() {
            while (connected) { // Un seul thread dans la suite du code jusqu'à ce que le RDV soit fini
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // Do nothing, wait again
                }
            }
            connected = true;
            while (!accepted) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // Do nothing, wait again
                }
            }
            this.notifyAll();
            accepted = false;
        }

        synchronized void accept() {
            while (accepted) { // Un seul thread dans la suite du code jusqu'à ce que le RDV soit fini
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // Do nothing, wait again
                }
            }

            accepted = true;
            while (!connected) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // Do nothing, wait again
                }
            }
            this.notifyAll();
            connected = false;
        }
    }

}
