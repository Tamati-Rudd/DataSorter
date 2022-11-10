package Tasks;

import java.util.ArrayList;

/**
 * An abstract class representing a single Task to be performed by a thread in the ThreadPool
 * @author Tamati Rudd 18045626
 * @param <E> parameters needed for the task
 * @param <F> information to be passed to listeners
 */
public abstract class Task<E, F> implements Runnable {
    protected int id;
    protected E params;
    protected F listenerInfo;
    protected ArrayList<TaskObserver> listeners;
    
    /**
     * Construct a new Task
     * Get a unique ID from the UniqueIdentifier singleton
     * @param params any parameters needed by the task
     */
    public Task(E params) {
        UniqueIdentifier instance = UniqueIdentifier.getInstance();
        id = instance.assignId();
        this.params = params;
        this.listenerInfo = null;
        listeners = new ArrayList<>();
    }
    
    /**
     * Perform the task - implemented in subclass(es)
     */
    public abstract void run();

    /**
     * Get the unique ID of this Task
     * @return this Task's unique ID
     */
    public int getId() {
       return id;
    }
    
    /**
     * Add a TaskObserver to the list of listeners for this task
     * @param o a TaskObserver object that desires to receive progress updates from this Task
     */
    public void addListener(TaskObserver o) {
        listeners.add(o);
    }
    
    /**
     * Remove a TaskObserver to the list of listeners for this task
     * @param o a TaskObserver object that no longer desires to receive progress updates from this Task
     */
    public void removeListener(TaskObserver o) {
        listeners.remove(o);
    }
    
    /**
     * Notify all listening TaskObservers of Task progress
     * @param progress Task progress information
     */
    protected void notifyAll(F progress) {
        for (TaskObserver o : listeners) {
            o.process(progress);
        }
    }
}
