package cbit.vcell.message.server.htc.sge;

public enum SGEJobStatus {
	RUNNING("running"),
	EXITED(""),
	PENDING("pending");

	public boolean isRunning() {
		return this == RUNNING;
	}

	public boolean isExiting() {
		return this == EXITED;
	}
	
	/**
	 *  convert String to enum
	 * @param status string to match
	 * @return matching enum value
	 * @throws IllegalArgumentException if status doesn't match known states
	 */
	
	public static SGEJobStatus parseStatus(String status) {
		for (SGEJobStatus js : values( )) {
			if (status.equals(js.statusString)) {
				return js;
			}
		}
		throw new IllegalArgumentException("Unknown status string " + status);
	}
	
	private SGEJobStatus(String statusString) {
		this.statusString = statusString;
	}

	/**
	 * String qstat produces for status
	 */
	private final String statusString;

}