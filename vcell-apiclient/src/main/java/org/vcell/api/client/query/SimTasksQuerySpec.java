package org.vcell.api.client.query;

public class SimTasksQuerySpec {
	public String submitLow = "";
	public String submitHigh = "";		//
	public String startRow = "1";		// "1"
	public String maxRows = "10";		// "10"
	public String serverId = "";		// "alpha"
	public String computeHost = "";	// "signode10"
	public String simId = "";
	public String jobId = "";
	public String taskId = "";
	public String hasData = "all";		// "all", "yes", "no"
	public String waiting = "on";
	public String queued = "on";
	public String dispatched = "on";
	public String running = "on";
	public String completed = "on";
	public String failed = "on";
	public String stopped = "on";
	
	public String getQueryString() {
		return "submitLow"+"="+submitLow+"&"+
				"submitHigh"+"="+submitHigh+"&"+
				"startRow"+"="+startRow+"&"+
				"maxRows"+"="+maxRows+"&"+
				"serverId"+"="+serverId+"&"+
				"computeHost"+"="+computeHost+"&"+
				"simId"+"="+simId+"&"+
				"jobId"+"="+jobId+"&"+
				"taskId"+"="+taskId+"&"+
				"hasData"+"="+hasData+"&"+
				"waiting"+"="+waiting+"&"+
				"queued"+"="+queued+"&"+
				"dispatched"+"="+dispatched+"&"+
				"running"+"="+running+"&"+
				"completed"+"="+completed+"&"+
				"failed"+"="+failed+"&"+
				"stopped"+"="+stopped;
	}
}
