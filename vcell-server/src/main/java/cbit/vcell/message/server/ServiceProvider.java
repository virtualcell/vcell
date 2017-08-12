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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.vcell.util.SessionLog;

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
	protected ServiceInstanceStatus serviceInstanceStatus = null;
	protected VCMessagingService vcMessagingService = null;
	protected VCTopicConsumer vcTopicConsumer = null;
	protected SessionLog log = null;
	public final boolean bSlaveMode;

/**
 * JmsMessaging constructor comment.
 */
protected ServiceProvider(VCMessagingService vcMessageService, ServiceInstanceStatus serviceInstanceStatus, SessionLog log, boolean bSlaveMode) {
	this.log = log;
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
					log.print("sending reply [" + reply.toString() + "]");
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
					log.print("sending reply [" + reply.toString() + "]");
					
				} else if (msgType.equals(MessageConstants.MESSAGE_TYPE_STOPSERVICE_VALUE)) {
					serviceID = message.getStringProperty(MessageConstants.SERVICE_ID_PROPERTY);
					if (serviceID != null && serviceID.equalsIgnoreCase(serviceInstanceStatus.getID()))  {
						stopService();
					}
				}
			} catch (Exception ex) {
				log.exception(ex);
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
					vcMessagingService.closeAll();
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

/**
 * @param serviceInstanceStatus
 * @param logDirectory, if null, use stdout
 * @return {@link OutputStream} created or null
 * @throws FileNotFoundException
 */
public static OutputStream initLog(ServiceInstanceStatus serviceInstanceStatus, String logDirectory) throws FileNotFoundException {
	if (serviceInstanceStatus == null) {
		throw new RuntimeException("initLog: serviceInstanceStatus can't be null");		
	}
	if (logDirectory != null && !logDirectory.trim().equals("-")) {
		File logdir = new File(logDirectory);
		if (!logdir.exists()) {
			throw new RuntimeException("Log directory " + logDirectory + " doesn't exist");
		}
			
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");
		File logfile = new File(logdir, serviceInstanceStatus.getServerID()+"_"+serviceInstanceStatus.getType().getName()+"_"+serviceInstanceStatus.getOrdinal() + "_"+dateFormat.format(new Date())+".log");
		FileOutputStream fos = new FileOutputStream(logfile); // don't append, auto flush
		java.io.PrintStream ps = new PrintStream(fos,true); // don't append, auto flush
		System.out.println("logging to file " + logfile.getAbsolutePath());
		System.setOut(ps);
		System.setErr(ps);
		return fos;
	}else{
		System.out.println("logging to stdout");
		return null;
	}
}




}
