package swingthreads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.SwingUtilities;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * This is the 3rd version of SwingWorker (also known as
 * SwingWorker 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on using this class, see:
 * <p>
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 * <p>
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 */
public abstract class SwingWorker {
    private static final Logger lg = LogManager.getLogger(SwingWorker.class);


    /**
     * Class to maintain reference to current worker thread
     * under separate synchronization control.
     */
    private static class ThreadVar {
        private Thread thread;

        ThreadVar(Thread t) {
            this.thread = t;
        }

        synchronized Thread get() {
            return this.thread;
        }

        synchronized void clear() {
            this.thread = null;
        }
    }

    /**
     * set {@link SwingWorker} thread name
     *
     * @param name may not be null
     * @throws IllegalArgumentException if name is null
     */
    public void setThreadName(String name) {
        if (name == null) throw new IllegalArgumentException("name may not be null");
        try {
            this.threadVar.thread.setName(name);
        } catch (SecurityException e) {
            lg.warn("setSwingWorkerName fail", e);
        }
    }

    // Instance Variables
    private Hashtable<String, Object> value;  // see getValue(), setValue()
    private final ThreadVar threadVar;

    /**
     * Get the value produced by the worker thread, or null if it
     * hasn't been constructed yet.
     */
    protected synchronized Hashtable<String, Object> getValue() {
        return this.value;
    }

    /**
     * Set the value produced by worker thread
     */
    private synchronized void setValue(Hashtable<String, Object> x) {
        this.value = x;
    }

    /**
     * A new method that interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    public void interrupt() {
        Thread t = this.threadVar.get();
        if (t != null) t.interrupt();
        this.threadVar.clear();
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
            Thread t = this.threadVar.get();
            if (t == null) return this.getValue(); // thread finished!

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
    public SwingWorker() {
        Runnable doWork = new Runnable() {
            public void run() {
                try {
                    SwingWorker.this.setValue(SwingWorker.this.verifyAndRun()); // Invoke now, on the current thread
                } finally {
                    // At the time this is called, the thread is running
                    SwingWorker.this.threadVar.clear();
                    // We do this to make sure we never accidentally re-run the same thread.
                    // We must ensure "one and done" threading.
                }
                SwingUtilities.invokeLater(SwingWorker.this::performSwingPostProcessing);
            }
        };

        this.threadVar = new ThreadVar(new Thread(doWork));
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
        Thread t = this.threadVar.get();
        FutureTask<Hashtable<String, Object>> ft = new FutureTask<>(t.start(), this.);
        if (t != null) {
            ft.run();
            return ft;
        }

        // Generally, getting here means we've already run the thread.
        return t.start();
    }
}