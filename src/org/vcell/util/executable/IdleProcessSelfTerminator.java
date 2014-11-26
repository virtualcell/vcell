package org.vcell.util.executable;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

/**
 * thread which monitors current process and exits when an elapsed time has passed 
 * since a notification event
 * @author gweatherby
 *
 */
public class IdleProcessSelfTerminator extends Thread {
	
	private final AtomicLong lastTime;
	private final long idleSeconds;
	private static Logger lg = Logger.getLogger(IdleProcessSelfTerminator.class);
	
	/**
	 * create in state {@link Thread.State.NEW}; {@link #start()} must be called to activate
	 * @param label prefix for thread label
	 * @param idleSeconds maximum idle time before process exits 
	 */
	public IdleProcessSelfTerminator(String label, long idleSeconds) {
		super(label + " idleProcessSelfTerminator");
		this.idleSeconds = idleSeconds;
		setDaemon(true);
		lastTime = new AtomicLong( );
		beat( ); //initial setting
	}
	
	/**
	 * receive message from client process is active; resets time until exit 
	 */
	public void beat( ) {
		lastTime.set(System.currentTimeMillis());
	}

	/**
	 * run continuously, exit current process when time since {@link #start()} or {@link #beat()} 
	 * > {@link #idleSeconds}
	 * 
	 */
	@Override
	public void run() {
		try {
			for (;;) {
				long now = System.currentTimeMillis();
				long elapsed = now - lastTime.get( );
				final long waitMilliseconds = idleSeconds * 1000 - elapsed;
				if (lg.isDebugEnabled()) {
					lg.debug("waiting " + waitMilliseconds);
				}
				Thread.sleep(waitMilliseconds);
				
				now = System.currentTimeMillis();
				elapsed = (now - lastTime.get( ))/1000; 
				final boolean isTimeToExit = elapsed >= idleSeconds;
				if (lg.isDebugEnabled()) {
					lg.debug("seconds since beat( ): " + elapsed  + " seconds, threshold is "
							+ idleSeconds + " time to exit is " + isTimeToExit);
				}
				
				if (isTimeToExit) {
					String msg = getName( ) + " timeout " + elapsed + " seconds";
					lg.fatal(msg);
					System.err.println(msg);
					System.exit(1);
				}
			}
		}
			
		catch (Exception e) {
			lg.error("IdleProcessSelfTerminator exception shutdown",e);
			System.err.println("IdleProcessSelfTerminator exception shutdown");
			e.printStackTrace(System.err);
		}
	}
}