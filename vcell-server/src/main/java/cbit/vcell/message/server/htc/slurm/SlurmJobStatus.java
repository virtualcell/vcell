package cbit.vcell.message.server.htc.slurm;

public enum SlurmJobStatus {
	 BOOTFAIL("BF","BOOT_FAIL","Job terminated due to launch failure, typically due to a hardware failure (e.g. unable to boot the node or block and the job can not be requeued)."),
	 CANCELLED("CA","CANCELLED","Job was explicitly cancelled by the user or system administrator. The job may or may not have been initiated."),
	 COMPLETED("CD","COMPLETED","Job has terminated all processes on all nodes with an exit code of zero."),
	 CONFIGURING("CF","CONFIGURING","Job has been allocated resources, but are waiting for them to become ready for use (e.g. booting)."),
	 COMPLETING("CG","COMPLETING","Job is in the process of completing. Some processes on some nodes may still be active."),
	 DEADLINE("DL","DEADLINE","Job missed its deadline."),
	 FAILED("F","FAILED","Job terminated with non-zero exit code or other failure condition."),
	 NODE_FAIL("NF","NODE_FAIL","Job terminated due to failure of one or more allocated nodes."),
	 PENDING("PD","PENDING","Job is awaiting resource allocation. Note for a job to be selected in this state it must have 'EligibleTime' in the requested time interval or different from 'Unknown'. The 'EligibleTime' is displayed by the 'scontrol show job' command. For example jobs submitted with the '--hold' option will have 'EligibleTime=Unknown' as they are pending indefinitely."),
	 PREEMPTED("PR","PREEMPTED","Job terminated due to preemption."),
	 RUNNING("R","RUNNING","Job currently has an allocation."),
	 RESIZING("RS","RESIZING","Job is about to change size."),
	 SUSPENDED("S","SUSPENDED","Job has an allocation, but execution has been suspended."),
	 TIMEOUT("TO","TIMEOUT","Job terminated upon reaching its time limit.");

	public static SlurmJobStatus parseStatus(String state) {
		if (state.contains(" ")){
			state = state.split(" ")[0];
		}
		for (SlurmJobStatus js : values( )) {
			if (state.equalsIgnoreCase(js.longName)){
				return js;
			}
		}
		for (SlurmJobStatus js : values( )) {
			if (state.equalsIgnoreCase(js.shortName)){
				return js;
			}
		}
		throw new IllegalArgumentException("Unknown state string " + state);
	}

	public final String shortName;
	public final String longName;
	public final String description;
	
	private SlurmJobStatus(String shortName, String longName, String description) {
		this.shortName = shortName;
		this.longName = longName;
		this.description = description;
	}

	public boolean isRunning() {
		return this == RUNNING || this == CONFIGURING || this == RESIZING;
	}

	public boolean isExiting() {
		return this == COMPLETING;
	}

	public boolean isFailed() {
		return this == FAILED;
	}

	public boolean isCompleted() {
		return this == COMPLETED;
	}

}