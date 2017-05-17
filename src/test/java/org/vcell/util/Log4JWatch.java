package org.vcell.util;

import org.apache.log4j.Logger;
import org.vcell.util.logging.WatchLogging;

/**
 * test dynamic switching of log4j level
 * @author GWeatherby
 *
 */
public class Log4JWatch {

	public static void main(String[] args) {
		WatchLogging.init(500);
		Logger lg = Logger.getLogger(Log4JWatch.class);
		long start = System.currentTimeMillis();
		for (;;) {
			long now = System.currentTimeMillis();
			double seconds = (now -start) / 1000.0;
			System.out.println(seconds + " seconds" );
			lg.debug("debug");
			lg.info("info");
			lg.warn("warn");
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
			}
		}
	}

}
