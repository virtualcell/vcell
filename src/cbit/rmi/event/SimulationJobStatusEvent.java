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

import org.vcell.util.document.User;

import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;

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

}
