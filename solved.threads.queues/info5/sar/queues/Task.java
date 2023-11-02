package info5.sar.queues;

import static info5.sar.utils.Log.log;

import info5.sar.queues.QueueBroker;

public class Task extends Thread {
    protected QueueBroker queueBroker;
    protected Runnable boot;
    protected boolean alive;
    protected boolean dead;

    /*
     * Constructs a task, associated with the given
     * broker. A task is a thread, with the thread name
     * being the given name.
     */
    public Task(String name, QueueBroker queueBroker) {
        super(name);
        this.queueBroker = queueBroker;
    }

    public QueueBroker getBroker() {
        return queueBroker;
    }

    public boolean alive() {
        return alive;
    }

    public boolean dead() {
        return dead;
    }

    /*
     * NEVER CALL THIS METHOD DIRECTLY.
     * It is called internally by the starting thread.
     * This is due to a poorly designed class Thread in Java
     * that should not implement Runnable but have a protected
     * method "run" that invokes a runnable, exactly like the
     * one below.
     */
    @Override
    public void run() {
        try {
            boot.run();
        } catch (Throwable th) {
            log(th);
        } finally {
            alive = false;
            dead = true;
        }
    }

    /*
     * DO NOT INVOKE start() on a task, see Task:start(Runnable).
     */
    @Override
    public void start() {
        throw new IllegalStateException("Do not call start() on a task!");
    }

    /*
     * This is the way to start a task.
     */
    public void start(Runnable r) {
        if (alive || dead)
            throw new IllegalStateException();
        alive = true;
        boot = r;
        super.start();
    }

}
