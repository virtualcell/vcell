/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap;
import javax.swing.event.EventListenerList;

import org.vcell.util.BigString;
import org.vcell.util.MessageConstants;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.MessageCollector;
import cbit.rmi.event.MessageData;
import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.MessageListener;
import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.rmi.event.VCellMessageEvent;
import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCTopicConsumer;
import cbit.vcell.message.VCTopicConsumer.TopicListener;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.StatusMessage;
import cbit.vcell.message.server.ManageConstants;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (5/9/2003 12:05:19 PM)
 * @author: Fei Gao
 */
public class ClientTopicMessageCollector implements MessageCollector, TopicListener {
	private EventListenerList listenerList = new EventListenerList();	
	private User user = null;	
	private VCMessagingService vcMessagingService = null;
	private SessionLog log = null;

	private long timeSinceLastMessage = System.currentTimeMillis();

/**
 * ClientStatusMonitor constructor comment.
 * @param jmsFactory cbit.vcell.messaging.JmsFactory
 * @param serviceName java.lang.String
 * @param queueName java.lang.String
 */
public ClientTopicMessageCollector(VCMessagingService vcMessagingService, User user0, SessionLog log0) {
	this.vcMessagingService = vcMessagingService;
	this.user = user0;
	this.log = log0;
}


/**
 * Insert the method's description here.
 * Creation date: (3/5/2004 9:28:52 AM)
 * @param listener cbit.rmi.event.MessageListener
 */
public void addMessageListener(MessageListener listener) {
	listenerList.add(MessageListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
protected synchronized void fireExportEvent(ExportEvent event) {
	fireMessageEvent(event);
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
protected synchronized void fireMessageEvent(MessageEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
		if (listeners[i] == MessageListener.class) {
			((MessageListener) listeners[i + 1]).messageEvent(event);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 2:44:30 PM)
 * @param event cbit.rmi.event.JobProgressEvent
 */
protected synchronized void fireSimulationJobStatusEvent(SimulationJobStatusEvent event) {
	fireMessageEvent(event);
}


/**
 * Insert the method's description here.
 * Creation date: (4/19/2004 1:05:20 PM)
 * @return long
 */
public long getTimeSinceLastMessage() {
	return timeSinceLastMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 3:58:52 PM)
 * @param message javax.jms.Message
 * @throws VCMessagingException 
 */
public void onTopicMessage(VCMessage message, VCMessageSession session) {	
	
	if (message == null) {
		return;
	}
	try {
		if (message.getObjectContent()==null){
			throw new Exception(this.getClass().getName()+".onTopicMessage: unimplemented message class "+message.show());
		}
		
		setTimeSinceLastMessage(System.currentTimeMillis());

		String msgType = message.getStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY);
		if(msgType == null){
			throw new Exception(this.getClass().getName()+".onTopicMessage: message type NULL for message "+message);
		}
		if (msgType.equals(MessageConstants.MESSAGE_TYPE_SIMSTATUS_VALUE)) {
					
			StatusMessage statusMessage = new StatusMessage(message);
			String userName = MessageConstants.USERNAME_PROPERTY_VALUE_ALL;
			if (message.propertyExists(MessageConstants.USERNAME_PROPERTY)){
				userName = message.getStringProperty(MessageConstants.USERNAME_PROPERTY);
			}
			
			SimulationJobStatus newJobStatus = statusMessage.getJobStatus();
			if (newJobStatus == null) {
				return;
			}
			
			VCSimulationIdentifier vcSimID = newJobStatus.getVCSimulationIdentifier();
			Double progress = statusMessage.getProgress();
			Double timePoint = statusMessage.getTimePoint();
			log.print("---onTopicMessage[" + newJobStatus + ", progress=" + progress + ", timepoint=" + timePoint + "]");
			
			fireSimulationJobStatusEvent(new SimulationJobStatusEvent(this, vcSimID.getID(), newJobStatus, progress, timePoint));		

		} else if(msgType.equals(MessageConstants.MESSAGE_TYPE_EXPORT_EVENT_VALUE)) {			
			ExportEvent event = (ExportEvent)message.getObjectContent();
			log.print("---onTopicMessage[ExportEvent[" + event.getVCDataIdentifier().getID() + "," + event.getProgress() + "]]");
			fireExportEvent(event);
		} else if(msgType.equals(MessageConstants.MESSAGE_TYPE_DATA_EVENT_VALUE)){
			DataJobEvent event = (DataJobEvent)message.getObjectContent();
			log.print("---onTopicMessage[DataEvent[vcdid=" + event.getVCDataIdentifier().getID() + "," + event.getProgress() + "]]");
			fireMessageEvent(event);
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_BROADCASTMESSAGE_VALUE)) {
			fireMessageEvent(new VCellMessageEvent(this, System.currentTimeMillis() + "", new MessageData((BigString)message.getObjectContent()), VCellMessageEvent.VCELL_MESSAGEEVENT_TYPE_BROADCAST));
		} else{
			throw new Exception(this.getClass().getName()+".onControlTopicMessage: Unimplemented message "+message.show());
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.exception(e);
	}
}


/**
 * onException method comment.
 */
public void init() {
	String clientMessageFilter = (user == null ? "" : MessageConstants.USERNAME_PROPERTY + "='" + user.getName() + "' OR ");
	clientMessageFilter += MessageConstants.USERNAME_PROPERTY + "='"+MessageConstants.USERNAME_PROPERTY_VALUE_ALL+"'";
	VCMessageSelector selector = vcMessagingService.createSelector(clientMessageFilter);
	VCTopicConsumer topicConsumer = new VCTopicConsumer(VCellTopic.ClientStatusTopic, this, selector, "Client Status Topic Consumer for user "+user.getName());
	vcMessagingService.addMessageConsumer(topicConsumer);
}


/**
 * Insert the method's description here.
 * Creation date: (3/5/2004 9:28:52 AM)
 * @param listener cbit.rmi.event.MessageListener
 */
public void removeMessageListener(MessageListener listener) {}


/**
 * Insert the method's description here.
 * Creation date: (4/19/2004 1:05:20 PM)
 * @param newTimeSinceLastMessage long
 */
public void setTimeSinceLastMessage(long newTimeSinceLastMessage) {
	timeSinceLastMessage = newTimeSinceLastMessage;
}

public void simulationJobStatusChanged(SimulationJobStatusEvent simJobStatusEvent) {
	System.out.println("jms message collector doesn't listen for SimulationJobStatusEvent");
}

public void onWorkerEvent(WorkerEvent event) {	
	System.out.println("jms message collector doesn't listen for WorkerEvent");
}
}
