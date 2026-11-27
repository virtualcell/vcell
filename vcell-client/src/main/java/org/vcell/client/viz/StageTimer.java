package org.vcell.client.viz;

import org.apache.logging.log4j.Logger;

/**
 * Wall-clock time per stage of one field-viewer request, so a slow request can be diagnosed from the client log
 * ({@code ~/.vcell/logs/vcellrun_<site>.log}): which stage took the time, a data-server round trip, a mesh read,
 * the sampling, or the JSON. {@link #done} logs one line with every stage at debug level, and at warn level
 * when the whole request took longer than {@link #SLOW_MILLIS} — the client logs at warn by default, so a slow
 * request is in the log without changing its configuration.
 * <p>
 * {@code kymograph sim=SimID_1_0_ var=A path=… total 8412 ms: source 3 ms, mesh 812 ms, timeseries 7301 ms, …}
 */
final class StageTimer {

	/** A request slower than this is logged at warn level. */
	static final long SLOW_MILLIS = 5_000;

	private final Logger logger;
	private final String what;
	private final long start = System.nanoTime();
	private long lap = start;
	private final StringBuilder stages = new StringBuilder();

	/**
	 * @param what the request, as the log line names it (e.g. {@code "kymograph sim=… var=…"})
	 */
	StageTimer(Logger logger, String what) {
		this.logger = logger;
		this.what = what;
	}

	/** Ends the current stage, naming it. */
	void lap(String stage) {
		long now = System.nanoTime();
		stages.append(stages.length() > 0 ? ", " : "").append(stage).append(' ').append(millis(now - lap)).append(" ms");
		lap = now;
	}

	/** Logs the stages, with {@code detail} (e.g. the size of the result) when not null. */
	void done(String detail) {
		long total = millis(System.nanoTime() - start);
		if (total > SLOW_MILLIS ? logger.isWarnEnabled() : logger.isDebugEnabled()) {
			String line = what + " total " + total + " ms: " + stages + (detail == null ? "" : " (" + detail + ")");
			if (total > SLOW_MILLIS) {
				logger.warn("slow field viewer request: {}", line);
			} else {
				logger.debug(line);
			}
		}
	}

	/** The time since this timer started. */
	long elapsedMillis() {
		return millis(System.nanoTime() - start);
	}

	private static long millis(long nanos) {
		return Math.round(nanos / 1e6);
	}
}
