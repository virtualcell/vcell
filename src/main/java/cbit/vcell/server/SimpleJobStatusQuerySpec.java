package cbit.vcell.server;

import java.io.Serializable;

public class SimpleJobStatusQuerySpec implements Serializable {
	public Long submitLowMS = null;
	public Long submitHighMS = null;
	public Long startLowMS = null;
	public Long startHighMS = null;
	public Long endLowMS = null;
	public Long endHighMS = null;
	public int startRow = 1;
	public int maxRows = 100;
	public String serverId = null;		// "alpha"
	public String computeHost = null;	// "signode10"
	public Long simId = null;
	public Long jobId = null;
	public Long taskId = null;
	public Boolean hasData = null;		// "all" (null), "yes", "no"
	public boolean waiting = true;
	public boolean queued = true;
	public boolean dispatched = true;
	public boolean running = true;
	public boolean completed = true;
	public boolean failed = true;
	public boolean stopped = true;
	public String userid = null;
}
