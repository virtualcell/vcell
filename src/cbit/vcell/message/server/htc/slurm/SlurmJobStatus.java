package cbit.vcell.message.server.htc.slurm;

import java.util.Objects;

public enum SlurmJobStatus {
	RUNNING("running", null),
	EXITED("", null),
	PENDING("pending", null),
	DELETING("","dr"),
	/**
	 * rare fatal error trying to start job; possible cluster configuration error
	 */
	ERROR("","Eqw");

	private final static SlurmJobStatus[] STATE_CODED;
	static {
		STATE_CODED = new SlurmJobStatus[]{ DELETING, ERROR };
	}

	public boolean isRunning() {
		return this == RUNNING;
	}

	public boolean isExiting() {
		return this == EXITED;
	}

	/**
	 *  convert String to enum
	 * @param status string to match
	 * @param state state code to match
	 * @return matching enum value
	 * @throws IllegalArgumentException if status doesn't match known states
	 */

	public static SlurmJobStatus parseStatus(String status, String state) {
		/**
		 *        JobID    JobName  Partition    Account  AllocCPUS      State ExitCode
		 *        ------------ ---------- ---------- ---------- ---------- ---------- --------
		 *        4989         V_TEST_10+        amd      vcell          1 CANCELLED+      0:0
		 *        4990         V_TEST_10+    general      vcell          2  COMPLETED      0:0
		 *        4990.batch        batch                 vcell          2  COMPLETED      0:0
		 *        
		 */
		//first check state codes, more informative for specific states
		for (SlurmJobStatus js : STATE_CODED) {
			if (js.stateCode.equals(state)) {
				return js;
			}
		}

		for (SlurmJobStatus js : values( )) {
			if (js.statusString.equals(status)) {
				return js;
			}
		}
		throw new IllegalArgumentException("Unknown status string " + status);
	}

	private SlurmJobStatus(String statusString, String stateCode) {
		Objects.requireNonNull(statusString);
		this.statusString = statusString;
		this.stateCode = stateCode;
	}

	/**
	 * String qstat produces for status
	 */
	private final String statusString;
	/**
	 * state code for some states
	 */
	private final String stateCode;

}