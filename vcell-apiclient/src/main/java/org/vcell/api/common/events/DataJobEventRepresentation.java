package org.vcell.api.common.events;

public class DataJobEventRepresentation {
	
	public final int eventType;
	public final Double progress;
	public final String username;
	public final String userkey;
	public final long jobid;
	public final boolean isBackgroundTask;
	public final String dataIdString;
	public final String dataKey;
	
	public DataJobEventRepresentation(
			int eventType, Double progress, String username, String userkey, long jobid,
			boolean isBackgroundTask, String dataIdString, String dataKey) {
		
		this.eventType = eventType;
		this.progress = progress;
		this.username = username;
		this.userkey = userkey;
		this.jobid = jobid;
		this.isBackgroundTask = isBackgroundTask;
		this.dataIdString = dataIdString;
		this.dataKey = dataKey;
	}
	

}
