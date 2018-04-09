/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/**
 * Insert the type's description here.
 * Creation date: (7/2/2003 3:00:11 PM)
 * @author: Fei Gao
 */
public abstract class ServiceProvider {
	public static final Logger lg = LogManager.getLogger(ServiceProvider.class);

	protected ServiceInstanceStatus serviceInstanceStatus = null;
	protected VCMessagingService vcMessagingService = null;
	protected VCTopicConsumer vcTopicConsumer = null;
	public final boolean bSlaveMode;

/**
 * JmsMessaging constructor comment.
 */
protected ServiceProvider(VCMessagingService vcMessageService, ServiceInstanceStatus serviceInstanceStatus, boolean bSlaveMode) {
	this.vcMessagingService = vcMessageService;
	this.serviceInstanceStatus = serviceInstanceStatus;
	this.bSlaveMode = bSlaveMode;
}


/**
 * Insert the method's description here.
 * Creation date: (12/17/2003 10:46:29 AM)
 */
public void closeTopicConsumer() {
	if (vcTopicConsumer != null) {
		vcMessagingService.removeMessageConsumer(vcTopicConsumer);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/3/2003 10:28:36 AM)
 * @return java.lang.String
 */
private final String getDaemonControlFilter() {
	return VCMessagingConstants.MESSAGE_TYPE_PROPERTY + " NOT IN " 
		+ "('" + MessageConstants.MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE + "'"
		+ ",'" + MessageConstants.MESSAGE_TYPE_REFRESHSERVERMANAGER_VALUE + "'"
		+ ",'" + MessageConstants.MESSAGE_TYPE_IAMALIVE_VALUE + "'"
		+ ")";		
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 5:29:47 PM)
 */
public void initControlTopicListener() {
	if (bSlaveMode){
		return;
	}
	
	TopicListener listener = new TopicListener() {

		public void onTopicMessage(VCMessage message, VCMessageSession session) {
			try {
				String msgType = message.getStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY);
				String serviceID = null;
				
				if (msgType == null) {
					return;
				}
				
				if (msgType.equals(MessageConstants.MESSAGE_TYPE_ISSERVICEALIVE_VALUE)) {			
					VCMessage reply = session.createObjectMessage(ServiceProvider.this.serviceInstanceStatus);
					reply.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_IAMALIVE_VALUE);
					reply.setStringProperty(MessageConstants.SERVICE_ID_PROPERTY, serviceInstanceStatus.getID());
					if (lg.isTraceEnabled()) lg.trace("sending reply [" + reply.toString() + "]");
					if (message.getReplyTo() != null) {
						reply.setCorrelationID(message.getMessageID());
						session.sendTopicMessage((VCellTopic)message.getReplyTo(), reply);
					} else {
						session.sendTopicMessage(VCellTopic.DaemonControlTopic, reply);
					}
				} else if (msgType.equals(MessageConstants.MESSAGE_TYPE_ASKPERFORMANCESTATUS_VALUE)) {				
					VCMessage reply = session.createObjectMessage(serviceInstanceStatus);
					reply.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE);
					reply.setStringProperty(MessageConstants.SERVICE_ID_PROPERTY, serviceInstanceStatus.getID());
					session.sendTopicMessage(VCellTopic.DaemonControlTopic, reply);			
					if (lg.isTraceEnabled()) lg.trace("sending reply [" + reply.toString() + "]");
					
				} else if (msgType.equals(MessageConstants.MESSAGE_TYPE_STOPSERVICE_VALUE)) {
					serviceID = message.getStringProperty(MessageConstants.SERVICE_ID_PROPERTY);
					if (serviceID != null && serviceID.equalsIgnoreCase(serviceInstanceStatus.getID()))  {
						stopService();
					}
				}
			} catch (Exception ex) {
				lg.error(ex.getMessage(),ex);
			}	
		}

	};
	VCMessageSelector selector = vcMessagingService.createSelector(getDaemonControlFilter());
	String threadName = "Daemon Control Topic Consumer";
	vcTopicConsumer = new VCTopicConsumer(VCellTopic.DaemonControlTopic, listener, selector, threadName, MessageConstants.PREFETCH_LIMIT_DAEMON_CONTROL);
	vcMessagingService.addMessageConsumer(vcTopicConsumer);
}


/**
 * Insert the method's description here.
 * Creation date: (12/10/2003 8:42:49 AM)
 */
public void stopService() {
	if (bSlaveMode){
		return;
	}
	try {
		Thread t = new Thread() {
			public void run() {
				try {
					vcMessagingService.close();
				} catch (VCMessagingException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		t.join(3000);	
	} catch (InterruptedException ex) {
	} finally {
		System.exit(0);
	}
}

}
