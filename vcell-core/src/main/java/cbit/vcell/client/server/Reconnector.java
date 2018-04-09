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
		Set<ReconnectListener> rl = Collections.newSetFromMap(new IdentityHashMap<ReconnectListener,Boolean>( ));
		listeners = Collections.synchronizedSet(rl);
		active = new AtomicBoolean(false);
		paused = new AtomicBoolean(false);
		countdownHandle = null;
	}

	/**
	 * start if not already running.
	 */
	void start( ) {
		if (active.compareAndSet(false,true)) { //returns false if already active
			lg.debug("starting");
			reconnectTries = 0;
			waitTime = 0;
			remaining = 1;
			countdownHandle = VCellExecutorService.get().scheduleAtFixedRate(( ) ->countdown( ), 1,1, TimeUnit.SECONDS);
		}
	}

	/**
	 * stop if running
	 */
	void stop( ) {
		if (active.compareAndSet(true,false)) { //only if running
			lg.debug("stopping");
			Objects.requireNonNull(countdownHandle); //logic error if this null
			countdownHandle.cancel(true);
			countdownHandle = null;
			listeners.clear();
		}
	}

	/**
	 * @return true if trying to reconnect
	 */
	public boolean isActive( ) {
		return active.get();
	}

	/**
	 * pause notifications, but keep running
	 * @param p
	 */
	void notificationPause(boolean p) {
		if (lg.isDebugEnabled()) {
			lg.debug("set paused to " + p);
		}
		paused.set(p);
	}

	/**
	 * add listener if not already present.
	 * Assignment is transient and will be cleared when this is not longer active
	 * @param rl
	 */
	public void addListener(ReconnectListener rl) {
		listeners.add(rl);
	}

	/**
	 * Runnable implementation
	 */
	private void countdown( ) {
		try {
			lg.debug("countdown");
			if (!active.get( ) || paused.get()) {
				return;
			}
			--remaining;
			if (lg.isDebugEnabled()) {
				lg.debug(remaining + " seconds remaining, " + listeners.size() + " listening");
			}

			Runnable r = () -> listeners.stream().forEach((rl) -> rl.refactorCountdown(remaining));
			SwingUtilities.invokeLater( r );
			if (remaining <= 0) {
				lg.debug("attempting reconnect");
				csm.reconnect();
				if (waitTime < MAX_WAIT_SECONDS) {
					waitTime = Math.min(MAX_WAIT_SECONDS, ++reconnectTries * 10) ;
				}
				remaining = waitTime;
				if (lg.isDebugEnabled()) {
					lg.debug("new time " + remaining);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
