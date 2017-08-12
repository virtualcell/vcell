/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.messages;


import cbit.vcell.message.MessagePropertyNotFoundException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.server.SimulationJobStatus;

/**
 * Insert the type's description here.
 * Creation date: (2/5/2004 12:35:20 PM)
 * @author: Fei Gao
 */
public class StatusMessage {
	private SimulationJobStatus jobStatus = null;
	private Double timePoint = null;
	private Double progress = null;

	private java.lang.String userName = null;

/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public StatusMessage(SimulationJobStatus jobStatus0, String userName0, Double progress0, Double timepoint0) {
	jobStatus = jobStatus0;
	userName = userName0;
	progress = progress0;
	timePoint = timepoint0;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public StatusMessage(VCMessage message) {
	parseMessage(message);
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 2:17:03 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatus
 */
public SimulationJobStatus getJobStatus() {
	return jobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 2:17:03 PM)
 * @return java.lang.Double
 */
public java.lang.Double getProgress() {
	return progress;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:56:45 PM)
 * @return cbit.vcell.solver.SimulationInfo
 */
public SimulationJobStatus getSimulationJobStatus() {
	return jobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 2:17:03 PM)
 * @return java.lang.Double
 */
public java.lang.Double getTimePoint() {
	return timePoint;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 10:24:41 AM)
 * @return java.lang.String
 */
public java.lang.String getUserName() {
	return userName;
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 2:19:48 PM)
 * @param message javax.jms.Message
 */
private void parseMessage(VCMessage message) {
	if (message == null) {
		throw new RuntimeException("Null message");
	}

	try {
		String msgType = message.getStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY);
		if (msgType != null && !msgType.equals(MessageConstants.MESSAGE_TYPE_SIMSTATUS_VALUE)) {
			throw new RuntimeException("Wrong message");
		}
	} catch (MessagePropertyNotFoundException ex) {
		ex.printStackTrace(System.out);
		throw new RuntimeException("Wrong message");
	}
			
	if (message.getObjectContent()==null){
		throw new IllegalArgumentException("Expecting object message.");
	}

	Object obj = message.getObjectContent();
	if (!(obj instanceof SimulationJobStatus)) {
		throw new IllegalArgumentException("Expecting " + SimulationJobStatus.class.getName() + " in message.");
	}

	jobStatus = (SimulationJobStatus)obj;
	if (message.propertyExists(MessageConstants.SIMULATION_STATUS_PROGRESS_PROPERTY)){
		progress = message.getDoubleProperty(MessageConstants.SIMULATION_STATUS_PROGRESS_PROPERTY);
	}
	
	if (message.propertyExists(MessageConstants.SIMULATION_STATUS_TIMEPOINT_PROPERTY)){
		timePoint = message.getDoubleProperty(MessageConstants.SIMULATION_STATUS_TIMEPOINT_PROPERTY);
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 1:59:04 PM)
 * @return javax.jms.Message
 * @param session cbit.vcell.messaging.VCellSession
 * @throws VCMessagingException 
 */
public void sendToClient(VCMessageSession session) throws VCMessagingException {
	VCMessage message = toMessage(session);
	session.sendTopicMessage(VCellTopic.ClientStatusTopic, message);
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 1:59:04 PM)
 * @return javax.jms.Message
 * @param session cbit.vcell.messaging.VCellSession
 */
private VCMessage toMessage(VCMessageSession session) {
	VCMessage message = session.createObjectMessage(jobStatus);
	message.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_SIMSTATUS_VALUE);
	message.setStringProperty(VCMessagingConstants.USERNAME_PROPERTY, userName);
	if (progress != null) {
		message.setDoubleProperty(MessageConstants.SIMULATION_STATUS_PROGRESS_PROPERTY, progress.doubleValue());
	}
	if (timePoint != null) {
		message.setDoubleProperty(MessageConstants.SIMULATION_STATUS_TIMEPOINT_PROPERTY, timePoint.doubleValue());
	}

	return message;
}


/**
 * Insert the method's description here.
 * Creation date: (2/13/2004 9:55:17 AM)
 * @return java.lang.String
 */
public String toString() {
	return "StatusMessage [" + jobStatus.getSimulationMessage().getDisplayMessage() + "," + progress + "," + timePoint + "]";
}
}
