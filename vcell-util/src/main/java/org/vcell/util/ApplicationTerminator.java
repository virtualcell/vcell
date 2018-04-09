package org.vcell.util;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * exit application after time delay
 * does not prevent application exiting sooner
 * @author GWeatherby
 *
 */
public class ApplicationTerminator {
	private final TimeUnit unit;
	private final long wait;
	private final int exitCode; 
	private ExitProcessThread countdownThread;
	private static ApplicationTerminator arnold  = null;
	
	private static final Logger lg = LogManager.getLogger(ApplicationTerminator.class);
	
	/**
	 * commence count down to exit with specified code
	 * @param unit delay units
	 * @param wait delay
	 * @param exitCode code to run to operating system / caller
	 * @return Terminator object
	 * @throws IllegalStateException if countdown already begun
	 */
	public static ApplicationTerminator beginCountdown(TimeUnit unit, long wait, int exitCode) {
		if (arnold != null) {
			throw new IllegalStateException("Countdown " + arnold + " already begun");
		}
		arnold = new ApplicationTerminator(unit, wait, exitCode);
		arnold.start( );
		return arnold;
	}
	
	/**
	 * @return exitcode passed to {@link Terminator#beginCountdown(TimeUnit, long, int exitCode)}
	 */
	public int getExitCode() {
		return exitCode;
	}

	/**
	 * abort the count down;
	 * no-op if countdown not currently present
	 */
	public void abortExit( ) {
		if (countdownThread != null) {
			countdownThread.abortExit();
			countdownThread = null;
			arnold = null;
		}
		else {
			lg.info("abortExit ignored, no current countdown");
		}
	}
	
	@Override
	public String toString() {
		return "Terminator " + wait + " "  + unit + " exit code "  + exitCode;
	}

	private ApplicationTerminator(TimeUnit unit, long wait, int exitCode) {
		super();
		this.unit = unit;
		this.wait = wait;
		this.exitCode = exitCode;
		countdownThread = null;
	} 
	
	private void start( ) {
		if (countdownThread != null) {
			throw new IllegalStateException("Countdown already begun");
		}
		if (lg.isInfoEnabled()) {
			lg.info("starting countdown " + wait + " " + unit + " exit code " + exitCode);
		}
		countdownThread  = new ExitProcessThread(exitCode);
		countdownThread.start();
	}
	
	
	private class ExitProcessThread extends Thread {
		final int exitCode;
		boolean abortExit;
		public ExitProcessThread(int exitCode) {
			super("Exit Process Thread");
			setDaemon(true);
			this.exitCode = exitCode;
			abortExit = false;
		}
		
		@Override
		public void run() {
			try {
				long milliseconds = unit.toMillis(wait);
				if (lg.isInfoEnabled()) {
					lg.info("sleeping " + milliseconds + " milliseconds");
				}
				sleep(milliseconds);
				if (!abortExit) {
					lg.info("exiting system");
					System.exit(exitCode);
				}
				else {
					lg.info("system exit aborted by flag");
				}
			}
			catch (InterruptedException ie) {
				lg.info("system exit aborted by interrupt");
				if (!abortExit) {
					throw new RuntimeException("unexpected interrupt on shutdown thread");
					
				}
			}
		}
		
		public void abortExit( ) {
			abortExit = true;
			interrupt();
		}
		
	}
}
