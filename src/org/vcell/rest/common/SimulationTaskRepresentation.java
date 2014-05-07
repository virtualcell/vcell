package org.vcell.rest.common;

import cbit.vcell.messaging.db.SimpleJobStatusPersistent;
import cbit.vcell.messaging.db.SimpleJobStatusPersistent.BioModelLink;
import cbit.vcell.messaging.db.SimpleJobStatusPersistent.MathModelLink;
import cbit.vcell.messaging.db.SimulationExecutionStatusPersistent;
import cbit.vcell.messaging.db.SimulationJobStatusPersistent;

public class SimulationTaskRepresentation {
	
	public String simKey;
	
	public String simName;
	
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
	
	public String schedulerStatus;
	
	public boolean hasData;
	
	public int scanCount;
	
	public MathModelLink mathModelLink;
	
	public BioModelLink bioModelLink;
	
	public SimulationTaskRepresentation(){
		
	}
	
	
	
	public String getSimKey() {
		return simKey;
	}



	public String getSimName() {
		return simName;
	}



	public String getUserName() {
		return userName;
	}



	public String getUserKey() {
		return userKey;
	}



	public String getHtcJobId() {
		return htcJobId;
	}



	public String getStatus() {
		return status;
	}



	public long getStartdate() {
		return startdate;
	}



	public int getJobIndex() {
		return jobIndex;
	}



	public int getTaskId() {
		return taskId;
	}



	public String getMessage() {
		return message;
	}



	public String getSite() {
		return site;
	}



	public String getComputeHost() {
		return computeHost;
	}



	public String getSchedulerStatus() {
		return schedulerStatus;
	}



	public boolean isHasData() {
		return hasData;
	}

	public int getScanCount(){
		return scanCount;
	}

	public MathModelLink getMathModelLink(){
		return mathModelLink;
	}



	public BioModelLink getBioModelLink() {
		return bioModelLink;
	}



	public SimulationTaskRepresentation(SimulationJobStatusPersistent simJobStatus, int scanCount){
		this.simKey = simJobStatus.getVCSimulationIdentifier().getSimulationKey().toString();
		this.userName = simJobStatus.getVCSimulationIdentifier().getOwner().getName();
		this.userKey = simJobStatus.getVCSimulationIdentifier().getOwner().getID().toString();
		this.jobIndex = simJobStatus.getJobIndex();
		this.taskId = simJobStatus.getTaskID();
		SimulationExecutionStatusPersistent simulationExecutionStatus = simJobStatus.getSimulationExecutionStatus();
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
		this.hasData = simJobStatus.hasData();
	}

	public SimulationTaskRepresentation(SimpleJobStatusPersistent simJobStatus) {
		this.simKey = simJobStatus.getVCSimulationIdentifier().getSimulationKey().toString();
		this.userName = simJobStatus.getVCSimulationIdentifier().getOwner().getName();
		this.userKey = simJobStatus.getVCSimulationIdentifier().getOwner().getID().toString();
		this.jobIndex = simJobStatus.getJobIndex();
		this.taskId = simJobStatus.getTaskID();
		this.schedulerStatus = simJobStatus.getSchedulerStatus().getDescription();
		this.htcJobId = (simJobStatus.getHtcJobID()!=null)?(simJobStatus.getHtcJobID().toDatabase()):(null);
		this.status = simJobStatus.getSchedulerStatus().getDescription();
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
		this.hasData = simJobStatus.hasData();
		this.scanCount = simJobStatus.getScanCount();
		this.simName = simJobStatus.getSimName();
		this.bioModelLink = simJobStatus.getBioModelLink();
		this.mathModelLink = simJobStatus.getMathModelLink();
	}
	
}
