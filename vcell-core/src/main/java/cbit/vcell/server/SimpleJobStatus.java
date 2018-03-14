/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.vcell.api.common.SimpleJobStatusRepresentation;
import org.vcell.api.common.events.SimulationJobStatusRepresentation;
import org.vcell.util.ComparableObject;
import org.vcell.util.document.User;

import cbit.vcell.solver.SimulationMetadata;

/**
 * Insert the type's description here.
 * Creation date: (9/3/2003 10:39:26 AM)
 * @author: Fei Gao
 */
public class SimpleJobStatus implements ComparableObject, Serializable {
	public final SimulationMetadata simulationMetadata;
	public final SimulationDocumentLink simulationDocumentLink;
	public final SimulationJobStatus jobStatus;
	public final StateInfo stateInfo;
	

/**
 * SimpleJobStatus constructor comment.
 */
public SimpleJobStatus(SimulationMetadata simulationMetadata, SimulationDocumentLink simulationDocumentLink, SimulationJobStatus jobStatus, StateInfo stateInfo) {	
	this.simulationMetadata = simulationMetadata;
	this.simulationDocumentLink = simulationDocumentLink;
	this.jobStatus = jobStatus;
	this.stateInfo = stateInfo;
}

public Object[] toObjects() {
	Long elapsedTime = null;
	if (jobStatus.getStartDate()!=null){
		if (jobStatus.getEndDate()!=null){
			elapsedTime = ((jobStatus.getEndDate().getTime()-jobStatus.getStartDate().getTime()));
		}else if (jobStatus.getSchedulerStatus().isRunning()){
			elapsedTime = ((System.currentTimeMillis()-jobStatus.getStartDate().getTime()));
		}
	}

	return new Object[] {
			(simulationDocumentLink instanceof BioModelLink)?("BM \""+((BioModelLink)simulationDocumentLink).bioModelName+"\", APP \""+((BioModelLink)simulationDocumentLink).simContextName+"\", SIM \""+simulationMetadata.simname+"\""):((simulationDocumentLink instanceof MathModelLink)?("MM \""+((MathModelLink)simulationDocumentLink).mathModelName+"\", SIM \""+simulationMetadata.simname+"\""):("")), 
			simulationMetadata.vcSimID.getOwner().getName(),
			new BigDecimal(simulationMetadata.vcSimID.getSimulationKey().toString()), 
			jobStatus.getJobIndex(),
			simulationMetadata.scanCount,  
			simulationMetadata.solverTaskDesc == null || simulationMetadata.solverTaskDesc.getSolverDescription() == null ? "" : simulationMetadata.solverTaskDesc.getSolverDescription().getDisplayLabel(), 		
			"<"+jobStatus.getSchedulerStatus().getDescription()+"> "+jobStatus.getSimulationMessage().getDisplayMessage(), 
			jobStatus.getComputeHost(), 
			jobStatus.getServerID().toString(),
			jobStatus.getTaskID(), 
			jobStatus.getSubmitDate(), 
			jobStatus.getStartDate(), 
			jobStatus.getEndDate(),
			elapsedTime, 
			new Long(simulationMetadata.getMeshSize())};
}

public SimpleJobStatusRepresentation toRep() {
	String vcellServerID = this.jobStatus.getServerID().toString();
	String simulationKey = this.jobStatus.getVCSimulationIdentifier().getSimulationKey().toString();
	int taskID = this.jobStatus.getTaskID();
	int jobIndex = this.jobStatus.getJobIndex();
	long submitDate = this.jobStatus.getSubmitDate().getTime();
	User owner = this.simulationMetadata.owner;
	SimpleJobStatusRepresentation .SchedulerStatus schedulerStatus = SimpleJobStatusRepresentation.SchedulerStatus.valueOf(this.jobStatus.getSchedulerStatus().name());
	SimpleJobStatusRepresentation.DetailedState detailedState = SimpleJobStatusRepresentation.DetailedState.valueOf(this.jobStatus.getSimulationMessage().getDetailedState().name());
	String detailedStateMessage = this.jobStatus.getSimulationMessage().getDisplayMessage();
	
	Long htcJobNumber = null;
	String htcComputeServer = null;
	SimpleJobStatusRepresentation.BatchSystemType htcBatchSystemType = null;
	Date simexe_startDate = null;
	Date simexe_latestUpdateDate = null;
	Date simexe_endDate = null;
	String computeHost = null;
	Boolean hasData = null;
	SimulationExecutionStatus simExeStatus = this.jobStatus.getSimulationExecutionStatus();
	if (simExeStatus!=null) {
		HtcJobID htcJobID = simExeStatus.getHtcJobID();
		if (htcJobID!=null) {
			htcJobNumber = htcJobID.getJobNumber();
			htcComputeServer = htcJobID.getServer();
			htcBatchSystemType = SimpleJobStatusRepresentation.BatchSystemType.valueOf(htcJobID.getBatchSystemType().name());
		}
		simexe_startDate = simExeStatus.getStartDate();
		simexe_latestUpdateDate = simExeStatus.getLatestUpdateDate();
		simexe_endDate = simExeStatus.getEndDate();
		computeHost = simExeStatus.getComputeHost();
		hasData = simExeStatus.hasData();
	}
	
	Integer queuePriority = null;
	Date queueDate = null;
	SimpleJobStatusRepresentation.SimulationQueueID queueId = null;
	SimulationQueueEntryStatus simQueueStatus = this.jobStatus.getSimulationQueueEntryStatus();
	if (simQueueStatus!=null) {
		queuePriority = simQueueStatus.getQueuePriority();
		queueDate = simQueueStatus.getQueueDate();
		queueId = SimpleJobStatusRepresentation.SimulationQueueID.valueOf(simQueueStatus.getQueueID().name());
	}
	
	SimpleJobStatusRepresentation rep = new SimpleJobStatusRepresentation(
			vcellServerID, simulationKey, taskID, jobIndex, new Date(submitDate), owner.getName(), owner.getID().toString(), 
			schedulerStatus, detailedState, detailedStateMessage, htcJobNumber, htcComputeServer, htcBatchSystemType, queuePriority, queueDate, queueId, 
			simexe_startDate, simexe_latestUpdateDate, simexe_endDate, computeHost, hasData);
	
	return rep;
}

}
