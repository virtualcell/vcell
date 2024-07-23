package swingthreads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.SwingUtilities;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * This class is based upon the 3rd version of SwingWorker (also known as
 * SwingWorker 3). Its job is to serve as a worker that dispatches tasks to the correct endpoints (swing or otherwise),
 * based on the type of AsynchClientTask requested.
 * <p>
 * SwingWorker 3 is an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on using this class, see:
 * <p>
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 * <p>
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 */
public abstract class VCellSwingWorker {
    private static final Logger lg = LogManager.getLogger(VCellSwingWorker.class);


    /**
     * Class to maintain reference to current worker thread
     * under separate synchronization control.
     */
    private static class TaskThread {
        private Thread thread;
        private final FutureTask<Hashtable<String, Object>> futureTask;

        TaskThread(Callable<Hashtable<String, Object>> callableTask) {
            this.futureTask = new FutureTask<>(callableTask);
            this.thread = new Thread(this.futureTask);
        }

        synchronized Thread get() {
            return this.thread;
        }

        synchronized void clear() {
            this.thread = null;
        }

        Future<Hashtable<String, Object>> start(){
            if (this.thread == null) return this.futureTask; // do not rerun the task! It's already done.
            this.thread.start();
            return this.futureTask;
        }
    }

    /**
     * set {@link VCellSwingWorker} thread name
     *
     * @param name may not be null
     * @throws IllegalArgumentException if name is null
     */
    public void setThreadName(String name) {
        if (name == null) throw new IllegalArgumentException("name may not be null");
        try {
            this.threadedTask.thread.setName(name);
        } catch (SecurityException e) {
            lg.warn("setSwingWorkerName fail", e);
        }
    }

    // Instance Variables
    private Hashtable<String, Object> hashResults;  // see getValue(), setValue()
    private final TaskThread threadedTask;

    /**
     * Get the value produced by the worker thread, or null if it
     * hasn't been constructed yet.
     */
    protected synchronized Hashtable<String, Object> getHashResults() {
        return this.hashResults;
    }

    /**
     * Set the value produced by worker thread
     */
    private synchronized void setHashResults(Hashtable<String, Object> x) {
        this.hashResults = x;
    }

    /**
     * A new method that interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    public void interrupt() {
        Thread t = this.threadedTask.get();
        if (t != null) t.interrupt();
        this.threadedTask.clear();
    }

    /**
     * Return the value created by the <code>construct</code> method.
     * Returns null if either the constructing thread or the current
     * thread was interrupted before a value was produced.
     *
     * @return the value created by the <code>construct</code> method
     */
    public Hashtable<String,  Object> get() {
        while (true) {
            Thread t = this.threadedTask.get();
            if (t == null) return this.getHashResults(); // thread finished!

            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // propagate
                return null;
            }
        }
    }

    /**
     * Start a thread that will call the <code>construct</code> method
     * and then exit.
     */
    public VCellSwingWorker() {
        Callable<Hashtable<String, Object>> doWork = new Callable<>() {
            @Override
            public Hashtable<String, Object> call() {
                try {
                    VCellSwingWorker.this.setHashResults(VCellSwingWorker.this.verifyAndRun()); // Invoke now, on the current thread
                } finally {
                    // At the time this is called, the thread is running
                    VCellSwingWorker.this.threadedTask.clear();
                    // We do this to make sure we never accidentally re-run the same thread.
                    // We must ensure "one and done" threading.
                }
                SwingUtilities.invokeLater(VCellSwingWorker.this::performSwingPostProcessing);
                return VCellSwingWorker.this.hashResults;
            }
        };

        this.threadedTask = new TaskThread(doWork);
    }

    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    public abstract Hashtable<String, Object> verifyAndRun();

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned.
     */
    public abstract void performSwingPostProcessing();

    /**
     * Start the worker thread.
     */
    public Future<Hashtable<String, Object>> start() {
        return this.threadedTask.start();
    }
}