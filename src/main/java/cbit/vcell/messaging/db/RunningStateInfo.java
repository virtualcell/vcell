package cbit.vcell.messaging.db;

public class RunningStateInfo extends StateInfo {
	public final double progress;
	public final double timepoint;
	public RunningStateInfo(double progress, double timepoint){
		this.progress = progress;
		this.timepoint = timepoint;
	}
}