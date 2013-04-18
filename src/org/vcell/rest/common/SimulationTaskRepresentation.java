package org.vcell.rest.common;

import cbit.vcell.messaging.db.SimpleJobStatus;
import cbit.vcell.messaging.db.SimulationExecutionStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;

public class SimulationTaskRepresentation {
	
	public String simKey;
	
	public String userName;

	public String userKey;

	public String htcJobId;

	public String status;

	public long startdate;

	public int jobIndex;

	public int taskId;

	public String message;
	
	public String site;
	
	public String computeHost;
	
	public SimulationTaskRepresentation(){
		
	}
	
	public SimulationTaskRepresentation(SimulationJobStatus simJobStatus){
		this.simKey = simJobStatus.getVCSimulationIdentifier().getSimulationKey().toString();
		this.userName = simJobStatus.getVCSimulationIdentifier().getOwner().getName();
		this.userKey = simJobStatus.getVCSimulationIdentifier().getOwner().getID().toString();
		this.jobIndex = simJobStatus.getJobIndex();
		this.taskId = simJobStatus.getTaskID();
		SimulationExecutionStatus simulationExecutionStatus = simJobStatus.getSimulationExecutionStatus();
		if (simulationExecutionStatus!=null && simulationExecutionStatus.getHtcJobID()!=null){
			this.htcJobId = simulationExecutionStatus.getHtcJobID().toDatabase();
		}
		this.status = simJobStatus.getSchedulerStatus().getDescription();
		if (simJobStatus.getStartDate()!=null){
			this.startdate = simJobStatus.getStartDate().getTime();
		}
		if (simJobStatus.getSimulationMessage()!=null){
			this.message = simJobStatus.getSimulationMessage().getDisplayMessage();
		}
		if (simJobStatus.getServerID()!=null){
			this.site = simJobStatus.getServerID().toCamelCase();
		}
		if (simJobStatus.getComputeHost()!=null){
			this.computeHost = simJobStatus.getComputeHost();
		}

	}

	public SimulationTaskRepresentation(SimpleJobStatus simJobStatus) {
		this.simKey = simJobStatus.getVCSimulationIdentifier().getSimulationKey().toString();
		this.userName = simJobStatus.getVCSimulationIdentifier().getOwner().getName();
		this.userKey = simJobStatus.getVCSimulationIdentifier().getOwner().getID().toString();
		this.jobIndex = simJobStatus.getJobIndex();
		this.taskId = simJobStatus.getTaskID();
//		SimulationExecutionStatus simulationExecutionStatus = simJobStatus.getSimulationExecutionStatus();
//		if (simulationExecutionStatus!=null && simulationExecutionStatus.getHtcJobID()!=null){
//			this.htcJobId = simulationExecutionStatus.getHtcJobID().toDatabase();
//		}
//		this.status = simJobStatus.getSchedulerStatus().getDescription();
		if (simJobStatus.getStartDate()!=null){
			this.startdate = simJobStatus.getStartDate().getTime();
		}
		if (simJobStatus.getStatusMessage()!=null){
			this.message = simJobStatus.getStatusMessage();
		}
		if (simJobStatus.getServerID()!=null){
			this.site = simJobStatus.getServerID();
		}
		if (simJobStatus.getComputeHost()!=null){
			this.computeHost = simJobStatus.getComputeHost();
		}
	}
	
}
