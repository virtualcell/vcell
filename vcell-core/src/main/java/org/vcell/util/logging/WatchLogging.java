package org.vcell.util.logging;


/**
 * alternate entry to {@link Logging#init()} to watch properties log
 * @author GWeatherby
 *
 */
@NoLogging
public class WatchLogging {
	private static final long INITIAL = -1;
	
	static long WATCH_LOGGING_DELAY_MILLIS = INITIAL;
	
	/**
	 * init logging and monitor configuration file for changes
	 * @param timeDelayMillis wait time between checks
	 */
	public static void init(long timeDelayMillis) {
		singleInitCheck();
		WATCH_LOGGING_DELAY_MILLIS = timeDelayMillis;
	}
	
	/**
	 * init logging and monitor configuration file for changes
	 * @param timeDelayMillis wait time between checks
	 * @param propertyName if set, use value as delay in lieu of timeDelayMillis (may not be null) 
	 */
	public static void init(long timeDelayMillis, String propertyName) {
		singleInitCheck();
		WATCH_LOGGING_DELAY_MILLIS = timeDelayMillis; 
		String intvlProp = System.getProperty(propertyName);
		if (intvlProp != null) {
			try {
				WATCH_LOGGING_DELAY_MILLIS = Long.parseLong(intvlProp);
			}
			catch (NumberFormatException nfe) {
				System.err.println("invalid " + propertyName +  " property " + intvlProp);
			}
		}
	}
	
	/**
	 * print warning if more than one call to any of {@link #init(long)}, {@link #init(String)}
	 */
	private static void singleInitCheck( ) {
		if (WATCH_LOGGING_DELAY_MILLIS != INITIAL) {
			System.err.println("multiple calls to " + WatchLogging.class.getName() + ".init( ... )");
		}
		WATCH_LOGGING_DELAY_MILLIS = 0; 
	}

}
