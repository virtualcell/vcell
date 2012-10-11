package cbit.vcell.message.server.dispatcher;

import java.util.Date;

import org.vcell.util.document.KeyValue;

import cbit.vcell.message.server.htc.HtcJobID;

public class SimulationTaskProcessState {
	final KeyValue simKey;
	final int jobIndex;
	final int taskID;
	
	Date startDate = null;
	Date latestUpdateDate = null;
	Date endDate = null;
	
	Boolean hasData = null;
	Double progress = null;
	Double timePoint = null;
	
	HtcJobID htcJobID;
	//PBSJobStatus pbsJobStatus;
	//PBSJobExitCode pbsJobExitCode;
	
	String computeHost;
	
	public SimulationTaskProcessState(KeyValue simKey, int jobIndex, int taskID) {
		this.simKey = simKey;
		this.jobIndex = jobIndex;
		this.taskID = taskID;
	}

	public SimulationTaskProcessState(SimulationTaskProcessState simTaskProcessState) {
		this.simKey = simTaskProcessState.simKey;
		this.jobIndex = simTaskProcessState.jobIndex;
		this.taskID = simTaskProcessState.taskID;
		this.startDate = simTaskProcessState.startDate;
		this.latestUpdateDate = simTaskProcessState.latestUpdateDate;
		this.endDate = simTaskProcessState.endDate;
		this.hasData = simTaskProcessState.hasData;
		this.progress = simTaskProcessState.progress;
		this.timePoint = simTaskProcessState.timePoint;
		this.htcJobID = simTaskProcessState.htcJobID;
		//this.pbsJobStatus = simTaskProcessState.pbsJobStatus;
		//this.pbsJobExitCode = simTaskProcessState.pbsJobExitCode;
		this.computeHost = simTaskProcessState.computeHost;
	}

}
