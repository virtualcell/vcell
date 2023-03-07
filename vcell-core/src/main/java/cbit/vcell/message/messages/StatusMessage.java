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

public class StatusMessage {
	private SimulationJobStatus jobStatus = null;
	private Double timePoint = null;
	private Double progress = null;

	private java.lang.String userName = null;

public StatusMessage(SimulationJobStatus jobStatus0, String userName0, Double progress0, Double timepoint0) {
	jobStatus = jobStatus0;
	userName = userName0;
	progress = progress0;
	timePoint = timepoint0;
}

public StatusMessage(VCMessage message) {
	parseMessage(message);
}

public SimulationJobStatus getJobStatus() {
	return jobStatus;
}

public java.lang.Double getProgress() {
	return progress;
}

public SimulationJobStatus getSimulationJobStatus() {
	return jobStatus;
}

public java.lang.Double getTimePoint() {
	return timePoint;
}

public java.lang.String getUserName() {
	return userName;
}

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
		throw new RuntimeException("Wrong message", ex);
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

public void sendToClient(VCMessageSession session) throws VCMessagingException {
	VCMessage message = toMessage(session);
	session.sendTopicMessage(VCellTopic.ClientStatusTopic, message);
}

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

public String toString() {
	return "StatusMessage [" + jobStatus.getSimulationMessage().getDisplayMessage() + "," + progress + "," + timePoint + "]";
}
}
