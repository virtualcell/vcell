/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.rmi.event;

import java.util.Date;

import org.vcell.api.common.events.SimulationJobStatusEventRepresentation;
import org.vcell.api.common.events.SimulationJobStatusRepresentation;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.HtcJobID.BatchSystemType;
import cbit.vcell.server.SimulationExecutionStatus;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.server.SimulationJobStatus.SimulationQueueID;
import cbit.vcell.server.SimulationQueueEntryStatus;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SimulationMessage.DetailedState;

/**
 * Insert the type's description here.
 * Creation date: (2/10/2004 1:30:21 PM)
 * @author: Fei Gao
 */
public class SimulationJobStatusEvent extends MessageEvent {
	private SimulationJobStatus jobStatus = null;
	private Double progress = null;
	private Double timePoint = null;
	private String username = null;
/**
 * SimulationStatusEvent constructor comment.
 * @param source java.lang.Object
 * @param messageSource cbit.rmi.event.MessageSource
 * @param messageData cbit.rmi.event.MessageData
 */
public SimulationJobStatusEvent(Object source, String simID, SimulationJobStatus jobStatus0, Double progress0, Double timePoint0, String username) {
	super(source, new MessageSource(source, simID), new MessageData(new Double[] { progress0, timePoint0 }));
	jobStatus = jobStatus0;
	progress = progress0;
	timePoint = timePoint0;
	this.username = username;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 1:30:21 PM)
 * @return int
 */
public int getEventTypeID() {
	return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 1:34:47 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatus
 */
public SimulationJobStatus getJobStatus() {
	return jobStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 9:50:48 AM)
 * @return double
 */
public Double getProgress() {
	return progress;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 2:50:01 PM)
 * @return java.lang.String
 */
public SimulationMessage getSimulationMessage() {
	if (jobStatus == null) {
		return null;
	}
	
	return jobStatus.getSimulationMessage();
}
/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 9:50:48 AM)
 * @return double
 */
public Double getTimepoint() {
	return timePoint;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 1:30:21 PM)
 * @return cbit.vcell.server.User
 */
public User getUser() {
	return null;
}

@Override
public boolean isIntendedFor(User user){
	if (user == null || username==null || username.equalsIgnoreCase(VCMessagingConstants.USERNAME_PROPERTY_VALUE_ALL)){
		return true;
	}
	return user.getName().equals(username);
}

/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 2:26:56 PM)
 * @return cbit.sql.KeyValue
 */
public VCSimulationIdentifier getVCSimulationIdentifier() {
	if (jobStatus == null) {
		return null;
	}

	return jobStatus.getVCSimulationIdentifier();
}

@Override
public boolean isSupercededBy(MessageEvent messageEvent) {
	if (messageEvent instanceof SimulationJobStatusEvent){
		SimulationJobStatusEvent simulationJobStatusEvent = (SimulationJobStatusEvent)messageEvent;
		
		SimulationJobStatus jobStatus2 = simulationJobStatusEvent.jobStatus;
		if (jobStatus != null && jobStatus2 != null 
				&& jobStatus.getVCSimulationIdentifier().equals(jobStatus2.getVCSimulationIdentifier()) 
				&& jobStatus.getJobIndex() == jobStatus2.getJobIndex()) {
			if (jobStatus.getSchedulerStatus().isRunning() && getProgress() != null && jobStatus2.getSchedulerStatus().isRunning() && simulationJobStatusEvent.getProgress() !=null){
				if (getProgress()<simulationJobStatusEvent.getProgress()){
					return true;
				}
			}
		}
	}
		
	return false;
}

public static SimulationJobStatusEvent fromJsonRep(Object eventSource, SimulationJobStatusEventRepresentation eventRep) {
	String simid = Simulation.createSimulationID(new KeyValue(eventRep.jobStatus.simulationKey));
	int jobIndex = eventRep.jobStatus.jobIndex;
	int taskID = eventRep.jobStatus.taskID;
	VCellServerID serverID = VCellServerID.getServerID(eventRep.jobStatus.vcellServerID);
	KeyValue simkey = new KeyValue(eventRep.jobStatus.simulationKey);
	User owner = new User(eventRep.jobStatus.owner_userid,new KeyValue(eventRep.jobStatus.onwer_userkey));
	VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simkey, owner);
	Date submitDate = eventRep.jobStatus.submitDate;
	
	SchedulerStatus schedulerStatus = null;
	if (eventRep.jobStatus.schedulerStatus!=null) {
		schedulerStatus = SchedulerStatus.valueOf(eventRep.jobStatus.schedulerStatus.name());
	}
	
	HtcJobID htcJobID = null;
	Long htcJobNumber = eventRep.jobStatus.htcJobNumber;
	SimulationJobStatusRepresentation.BatchSystemType htcBatchSystemType = eventRep.jobStatus.htcBatchSystemType;
	if (htcJobNumber!=null) {
		htcJobID = new HtcJobID(htcJobNumber.toString(), BatchSystemType.valueOf(htcBatchSystemType.name()));
	}
	
	SimulationMessage simMessage = null;
	DetailedState detailedState = DetailedState.valueOf(eventRep.jobStatus.detailedState.name());
	String message = eventRep.jobStatus.detailedStateMessage;
	if (detailedState!=null) {
		simMessage = SimulationMessage.create(detailedState, message, htcJobID);
	}
	
	SimulationQueueEntryStatus simQueueStatus = null;
	Date queueDate = eventRep.jobStatus.queueDate;
	Integer queuePriority = eventRep.jobStatus.queuePriority;
	SimulationJobStatusRepresentation.SimulationQueueID queueId2 = eventRep.jobStatus.queueId;
	if (queueDate!=null && queuePriority!=null) {
		simQueueStatus = new SimulationQueueEntryStatus(queueDate,queuePriority,SimulationQueueID.valueOf(queueId2.name()));
	}
	
	SimulationExecutionStatus simExeStatus = null;
	Date startDate = eventRep.jobStatus.simexe_startDate;
	String computeHost = eventRep.jobStatus.computeHost;
	Date latestUpdateDate = eventRep.jobStatus.simexe_latestUpdateDate;
	Date endDate = eventRep.jobStatus.simexe_endDate;
	Boolean hasData = eventRep.jobStatus.hasData;
	if (latestUpdateDate!=null) {
		simExeStatus = new SimulationExecutionStatus(startDate, computeHost, latestUpdateDate, endDate, hasData, htcJobID);
	}
	
	SimulationJobStatus jobStatus = new SimulationJobStatus(
			serverID,vcSimID,jobIndex,
			submitDate,schedulerStatus,taskID,
			simMessage,simQueueStatus,simExeStatus);
	
	Double progress = eventRep.progress;
	Double timepoint = eventRep.timePoint;
	String username = eventRep.username;
	SimulationJobStatusEvent event = new SimulationJobStatusEvent(eventSource, simid, jobStatus, progress, timepoint, username);
	return event;
}

public SimulationJobStatusEventRepresentation toJsonRep() {
	String vcellServerID = this.jobStatus.getServerID().toString();
	Date timeDateStamp = this.jobStatus.getTimeDateStamp();
	String simulationKey = this.jobStatus.getVCSimulationIdentifier().getSimulationKey().toString();
	int taskID = this.jobStatus.getTaskID();
	int jobIndex = this.jobStatus.getJobIndex();
	Date submitDate = this.jobStatus.getSubmitDate();
	String owner_userid = this.username;
	String onwer_userkey = this.jobStatus.getVCSimulationIdentifier().getOwner().getID().toString();
	SimulationJobStatusRepresentation.SchedulerStatus schedulerStatus = 
			SimulationJobStatusRepresentation.SchedulerStatus.valueOf(
					this.jobStatus.getSchedulerStatus().name());
	SimulationJobStatusRepresentation.DetailedState detailedState =
			SimulationJobStatusRepresentation.DetailedState.valueOf(
					this.jobStatus.getSimulationMessage().getDetailedState().name());
	String detailedStateMessage = this.jobStatus.getSimulationMessage().getDisplayMessage();
	
	Long htcJobNumber = null;
	String htcComputeServer = null;
	SimulationJobStatusRepresentation.BatchSystemType htcBatchSystemType = null;
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
			htcBatchSystemType = SimulationJobStatusRepresentation.BatchSystemType.valueOf(htcJobID.getBatchSystemType().name());
		}
		simexe_startDate = simExeStatus.getStartDate();
		simexe_latestUpdateDate = simExeStatus.getLatestUpdateDate();
		simexe_endDate = simExeStatus.getEndDate();
		computeHost = simExeStatus.getComputeHost();
		hasData = simExeStatus.hasData();
	}
	
	
	Integer queuePriority = null;
	Date queueDate = null;
	SimulationJobStatusRepresentation.SimulationQueueID queueId = null;
	SimulationQueueEntryStatus simQueueStatus = this.jobStatus.getSimulationQueueEntryStatus();
	if (simQueueStatus!=null) {
		queuePriority = simQueueStatus.getQueuePriority();
		queueDate = simQueueStatus.getQueueDate();
		queueId = SimulationJobStatusRepresentation.SimulationQueueID.valueOf(simQueueStatus.getQueueID().name());
	}

	SimulationJobStatusRepresentation jobStatus = new SimulationJobStatusRepresentation(
			vcellServerID, timeDateStamp, simulationKey, 
			taskID, jobIndex, submitDate, owner_userid, onwer_userkey, 
			schedulerStatus, detailedState, detailedStateMessage, 
			htcJobNumber, htcComputeServer, htcBatchSystemType, 
			queuePriority, queueDate, queueId, 
			simexe_startDate, simexe_latestUpdateDate, simexe_endDate, computeHost, hasData);
	SimulationJobStatusEventRepresentation eventRep = new SimulationJobStatusEventRepresentation(jobStatus, progress, timePoint, username);
	return eventRep;
}

}
