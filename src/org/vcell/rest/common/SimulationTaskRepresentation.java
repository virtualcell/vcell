package org.vcell.rest.common;

import cbit.vcell.server.BioModelLink;
import cbit.vcell.server.MathModelLink;
import cbit.vcell.server.RunningStateInfo;
import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.WaitingStateInfo;

public class SimulationTaskRepresentation {
	
	public String simKey;
	
	public final String simName;
	
	public final String userName;

	public final String userKey;

	public final String htcJobId;

	public final String status;

	public final long submitdate;

	public final long startdate;

	public final long enddate;

	public final int jobIndex;

	public final int taskId;

	public final String message;
	
	public final String site;
	
	public final String computeHost;
	
	public final String schedulerStatus;
	
	public final boolean hasData;
	
	public final int scanCount;
	
	public final double progress;
	
	public final int myQueueOrdinal;
	
	public final int globalQueueOrdinal;
	
	public final MathModelLink mathModelLink;
	
	public final BioModelLink bioModelLink;
	
	
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



	public long getSubmitdate() {
		return submitdate;
	}


	public long getStartdate() {
		return startdate;
	}


	public long getEnddate() {
		return enddate;
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
	
	public int getMyQueueOrdinal(){
		return myQueueOrdinal;
	}
	
	public int getGlobalQueueOrdinal(){
		return globalQueueOrdinal;
	}

	public MathModelLink getMathModelLink(){
		return mathModelLink;
	}



	public BioModelLink getBioModelLink() {
		return bioModelLink;
	}


	public SimulationTaskRepresentation(SimpleJobStatus simJobStatus) {
		this.simKey = simJobStatus.simulationMetadata.vcSimID.getSimulationKey().toString();
		this.userName = simJobStatus.simulationMetadata.vcSimID.getOwner().getName();
		this.userKey = simJobStatus.simulationMetadata.vcSimID.getOwner().getID().toString();
		this.jobIndex = simJobStatus.jobStatus.getJobIndex();
		this.taskId = simJobStatus.jobStatus.getTaskID();
		this.schedulerStatus = simJobStatus.jobStatus.getSchedulerStatus().getDescription();
		this.htcJobId = (simJobStatus.jobStatus.getSimulationExecutionStatus().getHtcJobID()!=null)?(simJobStatus.jobStatus.getSimulationExecutionStatus().getHtcJobID().toDatabase()):(null);
		this.status = simJobStatus.jobStatus.getSchedulerStatus().getDescription();
		if (simJobStatus.jobStatus.getSubmitDate()!=null){
			this.submitdate = simJobStatus.jobStatus.getSubmitDate().getTime();
		}else{
			this.submitdate = 0;
		}
		if (simJobStatus.jobStatus.getStartDate()!=null){
			this.startdate = simJobStatus.jobStatus.getStartDate().getTime();
		}else{
			this.startdate = 0;
		}
		if (simJobStatus.jobStatus.getEndDate()!=null){
			this.enddate = simJobStatus.jobStatus.getEndDate().getTime();
		}else{
			this.enddate = 0;
		}
		if (simJobStatus.jobStatus.getSimulationMessage()!=null){
			this.message = simJobStatus.jobStatus.getSimulationMessage().getDisplayMessage();
		}else{
			this.message = null;
		}
		if (simJobStatus.jobStatus.getServerID()!=null){
			this.site = simJobStatus.jobStatus.getServerID().toString();
		}else{
			this.site = null;
		}
		if (simJobStatus.jobStatus.getComputeHost()!=null){
			this.computeHost = simJobStatus.jobStatus.getComputeHost();
		}else{
			this.computeHost = null;
		}
		this.hasData = simJobStatus.jobStatus.hasData();
		this.scanCount = simJobStatus.simulationMetadata.scanCount;
		this.simName = simJobStatus.simulationMetadata.simname;
		if (simJobStatus.simulationDocumentLink instanceof BioModelLink){
			this.bioModelLink = (BioModelLink)simJobStatus.simulationDocumentLink;
		}else{
			this.bioModelLink = null;
		}
		if (simJobStatus.simulationDocumentLink instanceof MathModelLink){
			this.mathModelLink = (MathModelLink)simJobStatus.simulationDocumentLink;
		}else{
			this.mathModelLink = null;
		}
		if (simJobStatus.stateInfo instanceof WaitingStateInfo){
			WaitingStateInfo waitingStateInfo = (WaitingStateInfo)simJobStatus.stateInfo;
			this.myQueueOrdinal = waitingStateInfo.myQueueOrdinal;
			this.globalQueueOrdinal = waitingStateInfo.globalQueueOrdinal;
		}else{
			this.myQueueOrdinal = -1;
			this.globalQueueOrdinal = -1;
		}
		if (simJobStatus.stateInfo instanceof RunningStateInfo){
			RunningStateInfo runningStateInfo = (RunningStateInfo)simJobStatus.stateInfo;
			this.progress = runningStateInfo.progress;
		}else{
			this.progress = -1.0;
		}
		
	}
	
}
