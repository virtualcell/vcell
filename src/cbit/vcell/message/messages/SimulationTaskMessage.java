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


import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

/**
 * Insert the type's description here.
 * Creation date: (12/31/2003 11:39:39 AM)
 * @author: Fei Gao
 */
public class SimulationTaskMessage {
	private SimulationTask simTask = null;

/**
 * SimulationMessageHelper constructor comment.
 */
public SimulationTaskMessage(SimulationTask simTask0) {
	super();
	simTask = simTask0;
}


/**
 * Insert the method's description here.
 * Creation date: (2/20/2004 11:02:09 AM)
 * @param message javax.jms.Message
 */
public SimulationTaskMessage(VCMessage message) throws XmlParseException {
	parse(message);
}


/**
 * Insert the method's description here.
 * Creation date: (2/20/2004 11:01:40 AM)
 * @return cbit.vcell.solver.Simulation
 */
public SimulationTask getSimulationTask() {
	return simTask;
}


/**
 * Insert the method's description here.
 * Creation date: (12/29/2003 2:51:01 PM)
 * @return cbit.vcell.solver.Simulation
 * @param xml java.lang.String
 * @throws  
 * @throws XmlParseException 
 */
private void parse(VCMessage message) throws XmlParseException {
	if (message == null || message.getTextContent()==null){
		return;
	}		
		
	String xmlString = message.getTextContent();
	try {
		simTask = XmlHelper.XMLToSimTask(xmlString);
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage(),e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 11:08:17 AM)
 * @return javax.jms.Message
 * @param session cbit.vcell.messaging.VCellSession
 * @throws VCMessagingException 
 */
public void sendSimulationTask(VCMessageSession session) throws VCMessagingException {
	session.sendQueueMessage(VCellQueue.SimJobQueue, toMessage(session),true,null);
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 11:08:17 AM)
 * @return javax.jms.Message
 * @param session cbit.vcell.messaging.VCellSession
 * @throws VCMessagingException 
 */
private VCMessage toMessage(VCMessageSession session) throws VCMessagingException {
	VCMessage message;
	try {
		message = session.createTextMessage(XmlHelper.simTaskToXML(simTask));
	} catch (XmlParseException e) {
		e.printStackTrace(System.out);
		throw new VCMessagingException("failed to restore Simulation Task from XML",e);
	}		

	message.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE); // must have
	message.setIntProperty(MessageConstants.JOBINDEX_PROPERTY, simTask.getSimulationJob().getJobIndex()); // must have
	message.setIntProperty(MessageConstants.TASKID_PROPERTY, simTask.getTaskID()); // must have
	
	message.setStringProperty(VCMessagingConstants.USERNAME_PROPERTY, simTask.getUserName()); // might be used to remove from the job queue when do stopSimulation
	message.setLongProperty(MessageConstants.SIMKEY_PROPERTY, Long.parseLong(simTask.getSimKey() + "")); // might be used to remove from the job queue when do stopSimulation

	message.setDoubleProperty(MessageConstants.SIZE_MB_PROPERTY, simTask.getEstimatedMemorySizeMB()); // for worker message filter
	
	if (simTask.getComputeResource() != null) {
		message.setStringProperty(MessageConstants.COMPUTE_RESOURCE_PROPERTY, simTask.getComputeResource()); // for worker message filter
	}

	FieldDataIdentifierSpec[] fieldDataIDs = simTask.getSimulationJob().getFieldDataIdentifierSpecs();
	if (fieldDataIDs != null && fieldDataIDs.length > 0) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < fieldDataIDs.length; i ++) {
			sb.append(fieldDataIDs[i].toCSVString() + "\n");
		}
		message.setStringProperty(MessageConstants.FIELDDATAID_PROPERTY, sb.toString());
	}
	return message;
}
}
