package ThreadPool;

import java.util.Queue;

/**
 * A thread in the ThreadPool, which waits for Runnable Tasks to perform, and performs them when notified
 * @author Tamati Rudd 18045626
 */
public class WorkerThread extends Thread {
    private boolean alive;
    private boolean needsToDie = false;
    private boolean resized = false;
    private Queue<Runnable> taskQueue; //synchronized object (monitor)

    /**
     * Construct a WorkerThread
     * @param taskQueue - the queue of tasks
     */
    public WorkerThread(Queue taskQueue) {
        alive = true;
        this.taskQueue = taskQueue;
        System.out.println("A thread has been started!");
    }

    /**
     * Start the WorkerThread by beginning the loop
     */
    @Override
    public void run() {
        runThread();
        System.out.println("A thread has died!");
    }

    /**
     * Stop ALL WorkerThreads (once Tasks complete), as the ThreadPool is being destroyed
     */
    public void die() {
        needsToDie = true;
    }

    /**
     * Stop SOME WorkerThreads, as the ThreadPool has been resized down
     */
    public void resized() {
        resized = true;
    }

    /**
     * Loop so long as the thread is alive, waiting for notification of tasks in the queue to run
     */
    public void runThread() {
        while (alive) {
            Runnable task = null;
            synchronized (taskQueue) {
                try {
                    //Wait if the queue is empty, keep working if it isn't
                    if (taskQueue.isEmpty()) {
                        if (!needsToDie) {
                            taskQueue.wait();
                        } else {
                            alive = false;
                        }
                    }
                    //WorkerThread has been notified, so there must be an item on the queue - retrieve it
                    task = taskQueue.poll();
                } catch (InterruptedException ex) {
                    return;
                }

            }

            //If there is a task to execute this iteration, execute it
            if (task != null) {
                task.run();
            }

            //If the ThreadPool has been resized down and this thread is to be removed, don't loop again
            if (resized)
                alive = false;
        }
    }
}
