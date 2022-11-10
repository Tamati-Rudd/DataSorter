package ThreadPool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class maintains a pool of threads, which are assigned to complete incoming Runnable Tasks
 * @author Tamati Rudd 18045626
 */
public class ThreadPool {
    private int size;
    private int available;
    public Boolean acceptingTasks = true; 
    public ArrayList<WorkerThread> threads;
    public Queue<Runnable> taskQueue; //synchronized object (monitor)
    
    /**
     * Construct the ThreadPool
     * @param initialSize - the initial size of the ThreadPool
     */
    public ThreadPool(int initialSize) {
        taskQueue = new LinkedList();
        threads = new ArrayList<>();
        size = initialSize;
        for (int i = 0; i < size; i++) {
            WorkerThread t = new WorkerThread(taskQueue);
            threads.add(t);
            t.start();
            available++;
        }
    }
    
    /**
     * Get the number available threads - the amount of threads in the pool (working or not working)
     * @return the number of threads in the pool
     */
    public int getAvailable() {
        return available;
    }
    
    /**
     * Resize the ThreadPool by adding or killing some threads
     * @param newSize - the size the pool is to be changed to
     */
    public void resize(int newSize) {
        //Ensure the pool cannot be resized if it is being destroyed
        if (!acceptingTasks || newSize <= 0) 
            return;
        
        //Pool size is to decrease: kill some threads
        if (newSize < getAvailable()) { 
            int numToKill = getAvailable() - newSize;
            for (int i = 0; i < numToKill; i++) {
                WorkerThread t = threads.get(threads.size()-1);
                t.resized();
                threads.remove(t);
                available--;
            }
            synchronized(taskQueue) { //Notify all threads, so those removed in the resize die
                taskQueue.notifyAll();
            }
        //Pool size is to increase: add some threads
        } else if (newSize > getAvailable()) { 
            int numToCreate = newSize - getAvailable();
            for (int i = 0; i < numToCreate; i++) {
                WorkerThread t = new WorkerThread(taskQueue);
                threads.add(t);
                t.start();
                available++;
            }
        }
    }
    
    /**
     * Destroy the ThreadPool, making it unable to accept more tasks, and killing threads when their work is complete
     */
    public void destroyPool() {
        //Prevent the pool from accepting new tasks
        acceptingTasks = false;
        available = 0;

        //Tell ALL threads that they need to die upon completion of all existing tasks
        for (WorkerThread t : threads) {
            t.die();
        }
        threads.removeAll(threads);
        
        //Wake up any waiting threads, so they can die
        synchronized(taskQueue) {
            taskQueue.notifyAll();
        }
    }
    
    /**
     * Add a task to the queue, and notify threads there is a task to perform
     * If the pool is to be destroyed, do not accept new tasks
     * @param task - the runnable task to add
     * @return whether the task was added to the queue or not
     */
    public boolean perform(Runnable task) {
        if (!acceptingTasks) {
                System.out.println("TASK ADDITION BLOCKED!");
                return false;
            }
        
        synchronized(taskQueue) {
            taskQueue.offer(task);
            taskQueue.notify();
        }   
        return true;
    }
}
