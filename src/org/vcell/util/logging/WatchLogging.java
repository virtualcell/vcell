package org.vcell.util.logging;

/**
 * alternate entry to {@link Logging#init()} to watch properties log
 * @author GWeatherby
 *
 */
@NoLogging
public class WatchLogging {
	static long WATCH_LOGGING_DELAY_MILLIS = 0;
	
	/**
	 * init logging and monitor configuration file for changes
	 * @param timeDelayMillis
	 */
	public static void init(long timeDelayMillis) {
		WATCH_LOGGING_DELAY_MILLIS = timeDelayMillis;
		Logging.init();
	}

}
