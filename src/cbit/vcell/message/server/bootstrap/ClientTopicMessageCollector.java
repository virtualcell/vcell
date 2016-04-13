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
import org.vcell.util.SessionLog;

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
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCTopicConsumer;
import cbit.vcell.message.VCTopicConsumer.TopicListener;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.StatusMessage;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (5/9/2003 12:05:19 PM)
 * @author: Fei Gao
 */
public class ClientTopicMessageCollector implements MessageCollector, TopicListener {
	private EventListenerList listenerList = new EventListenerList();	
	private VCMessagingService vcMessagingService = null;
	private SessionLog log = null;

	private long timeSinceLastMessage = System.currentTimeMillis();
	private VCTopicConsumer topicConsumer = null;

/**
 * ClientStatusMonitor constructor comment.
 * @param jmsFactory cbit.vcell.messaging.JmsFactory
 * @param serviceName java.lang.String
 * @param queueName java.lang.String
 */
public ClientTopicMessageCollector(VCMessagingService vcMessagingService, SessionLog log) {
	this.vcMessagingService = vcMessagingService;
	this.log = log;
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

		String msgType = message.getStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY);
		if(msgType == null){
			throw new Exception(this.getClass().getName()+".onTopicMessage: message type NULL for message "+message);
		}
		if (msgType.equals(MessageConstants.MESSAGE_TYPE_SIMSTATUS_VALUE)) {
			String messageUserName = message.getStringProperty(VCMessagingConstants.USERNAME_PROPERTY);
			StatusMessage statusMessage = new StatusMessage(message);
			String userName = VCMessagingConstants.USERNAME_PROPERTY_VALUE_ALL;
			if (message.propertyExists(VCMessagingConstants.USERNAME_PROPERTY)){
				userName = message.getStringProperty(VCMessagingConstants.USERNAME_PROPERTY);
			}
			
			SimulationJobStatus newJobStatus = statusMessage.getJobStatus();
			if (newJobStatus == null) {
				return;
			}
			
			VCSimulationIdentifier vcSimID = newJobStatus.getVCSimulationIdentifier();
			Double progress = statusMessage.getProgress();
			Double timePoint = statusMessage.getTimePoint();
			
			fireSimulationJobStatusEvent(new SimulationJobStatusEvent(this, vcSimID.getID(), newJobStatus, progress, timePoint, messageUserName));		
		} else if(msgType.equals(MessageConstants.MESSAGE_TYPE_EXPORT_EVENT_VALUE)) {	
			String messageUserName = message.getStringProperty(VCMessagingConstants.USERNAME_PROPERTY);
			ExportEvent event = (ExportEvent)message.getObjectContent();
			fireExportEvent(event);
		} else if(msgType.equals(MessageConstants.MESSAGE_TYPE_DATA_EVENT_VALUE)){
			String messageUserName = message.getStringProperty(VCMessagingConstants.USERNAME_PROPERTY);
			DataJobEvent event = (DataJobEvent)message.getObjectContent();
			fireMessageEvent(event);
		} else if (msgType.equals(MessageConstants.MESSAGE_TYPE_BROADCASTMESSAGE_VALUE)) {
			String messageUserName = message.getStringProperty(VCMessagingConstants.USERNAME_PROPERTY);
			fireMessageEvent(new VCellMessageEvent(this, System.currentTimeMillis() + "", new MessageData((BigString)message.getObjectContent()), VCellMessageEvent.VCELL_MESSAGEEVENT_TYPE_BROADCAST,messageUserName));
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
	VCMessageSelector selector = null;
	topicConsumer = new VCTopicConsumer(VCellTopic.ClientStatusTopic, this, selector, "Client Status Topic Consumer",MessageConstants.PREFETCH_LIMIT_CLIENT_TOPIC);
	vcMessagingService.addMessageConsumer(topicConsumer);
}


/**
 * Insert the method's description here.
 * Creation date: (3/5/2004 9:28:52 AM)
 * @param listener cbit.rmi.event.MessageListener
 */
public void removeMessageListener(MessageListener listener) {
	listenerList.remove(MessageListener.class, listener);
}


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


public void close() {
	if (topicConsumer!=null){
		vcMessagingService.removeMessageConsumer(topicConsumer);
		topicConsumer = null;
	}
}
}
