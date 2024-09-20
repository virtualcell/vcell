package cbit.vcell.client.server;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.resource.VCellExecutorService;

/**
 * Repeatedly attempt to reconnect to RMI server. Notify listeners of time remaining until next attempt.
 */
public class Reconnector {
    private static final long MAX_WAIT_SECONDS = TimeUnit.MINUTES.toSeconds(5);
    private static final Logger lg = LogManager.getLogger(Reconnector.class);
    private final ClientServerManager csm;
    private final Set<ReconnectListener> listeners;
    private final AtomicBoolean active;
    private final AtomicBoolean paused;
    /**
     * current wait between attempts
     */
    private long waitTime;
    /**
     * countdown to next attempt
     */
    private long remaining;
    /**
     * slow down time between reconnects
     */
    private short reconnectTries;
    private ScheduledFuture<?> countdownHandle;

    /**
     * @param csm client non null
     */
    public Reconnector(ClientServerManager csm) {
        Objects.requireNonNull(csm);
        this.csm = csm;
        Set<ReconnectListener> rl = Collections.newSetFromMap(new IdentityHashMap<ReconnectListener, Boolean>());
        this.listeners = Collections.synchronizedSet(rl);
        this.active = new AtomicBoolean(false);
        this.paused = new AtomicBoolean(false);
        this.countdownHandle = null;
    }

    /**
     * start if not already running.
     */
    void start() {
        if (this.active.compareAndSet(false, true)) { //returns false if already active
            lg.debug("starting");
            this.reconnectTries = 0;
            this.waitTime = 0;
            this.remaining = 1;
            this.countdownHandle = VCellExecutorService.get().scheduleAtFixedRate(() -> this.countdown(), 1, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * stop if running
     */
    void stop() {
        if (this.active.compareAndSet(true, false)) { //only if running
            lg.debug("stopping");
            Objects.requireNonNull(this.countdownHandle); //logic error if this null
            this.countdownHandle.cancel(true);
            this.countdownHandle = null;
            this.listeners.clear();
        }
    }

    /**
     * @return true if trying to reconnect
     */
    public boolean isActive() {
        return this.active.get();
    }

    /**
     * pause notifications, but keep running
     *
     * @param p
     */
    void notificationPause(boolean p) {
        if (lg.isDebugEnabled()) {
            lg.debug("set paused to " + p);
        }
        this.paused.set(p);
    }

    /**
     * add listener if not already present.
     * Assignment is transient and will be cleared when this is not longer active
     *
     * @param rl
     */
    public void addListener(ReconnectListener rl) {
        this.listeners.add(rl);
    }

    /**
     * Runnable implementation
     */
    private void countdown() {
        try {
            lg.debug("countdown");
            if (!this.active.get() || this.paused.get()) {
                return;
            }
            --this.remaining;
            if (lg.isDebugEnabled()) {
                lg.debug(this.remaining + " seconds remaining, " + this.listeners.size() + " listening");
            }

            Runnable r = () -> this.listeners.stream().forEach((rl) -> rl.refactorCountdown(this.remaining));
            SwingUtilities.invokeLater(r);
            if (this.remaining <= 0) {
                lg.debug("attempting reconnect");
                this.csm.reconnect();
                if (this.waitTime < MAX_WAIT_SECONDS) {
                    this.waitTime = Math.min(MAX_WAIT_SECONDS, ++this.reconnectTries * 10);
                }
                this.remaining = this.waitTime;
                if (lg.isDebugEnabled()) {
                    lg.debug("new time " + this.remaining);
                }
            }
        } catch (Exception e) {
            lg.error(e.getMessage(), e);
        }
    }
}
