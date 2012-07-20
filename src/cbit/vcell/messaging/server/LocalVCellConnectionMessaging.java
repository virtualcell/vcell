/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.server;
import java.io.FileNotFoundException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.MessageConstants;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.PerformanceMonitorEvent;
import cbit.rmi.event.SimpleMessageService;
import cbit.vcell.messaging.JmsClientMessaging;
import cbit.vcell.messaging.JmsConnection;
import cbit.vcell.messaging.JmsConnectionFactory;
import cbit.vcell.messaging.JmsMessageCollector;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.LocalVCellServer;
import cbit.vcell.server.PerformanceMonitoringFacility;
import cbit.vcell.server.SimulationController;
import cbit.vcell.server.URLFinder;
import cbit.vcell.server.UserLoginInfo;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.visit.VisitConnectionInfo;

/**
 * The user's connection to the Virtual Cell.  It is obtained from the VCellServer
 * after the user has been authenticated.
 * Creation date: (Unknown)
 * @author: Jim Schaff.
 */
@SuppressWarnings("serial")
public class LocalVCellConnectionMessaging extends UnicastRemoteObject implements VCellConnection, ExportListener ,DataJobListener{
	private long MAX_TIME_WITHOUT_POLLING_MS = 10*MessageConstants.MINUTE_IN_MS;
	private LocalDataSetControllerMessaging dataSetControllerMessaging = null;
	private LocalSimulationControllerMessaging simulationControllerMessaging = null;
	private LocalUserMetaDbServerMessaging userMetaDbServerMessaging = null;
	private SimpleMessageService messageService = null;

	private JmsConnection jmsConn = null;
	private JmsMessageCollector jmsMessageCollector = null;
	
	private UserLoginInfo userLoginInfo;
	
	private SessionLog fieldSessionLog = null;
	private LocalVCellServer fieldLocalVCellServer = null;
	private String fieldHost = null;

	private JmsClientMessaging dbClientMessaging = null;
	private JmsClientMessaging dataClientMessaging = null;
	private JmsClientMessaging simClientMessaging = null;
	private PerformanceMonitoringFacility performanceMonitoringFacility;

	public LocalVCellConnectionMessaging(UserLoginInfo userLoginInfo, String host, 
		SessionLog sessionLog, JmsConnectionFactory jmsConnFactory, LocalVCellServer aLocalVCellServer) 
		throws RemoteException, java.sql.SQLException, FileNotFoundException, javax.jms.JMSException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortVCellConnection,0));
	this.userLoginInfo = userLoginInfo;
	this.fieldHost = host;
	this.fieldSessionLog = sessionLog;
	this.fieldLocalVCellServer = aLocalVCellServer;
	jmsConn = jmsConnFactory.createConnection();
	jmsMessageCollector = new JmsMessageCollector(jmsConn, userLoginInfo.getUser(), fieldSessionLog);
	
	messageService = new SimpleMessageService();
	jmsMessageCollector.addMessageListener(messageService);
	
	sessionLog.print("new LocalVCellConnectionMessaging(" + userLoginInfo.getUser().getName() + ")");	
	fieldLocalVCellServer.getExportServiceImpl().addExportListener(this);
	fieldLocalVCellServer.getDataSetControllerImpl().addDataJobListener(this);
	
	performanceMonitoringFacility = new PerformanceMonitoringFacility(userLoginInfo.getUser(), sessionLog);	
}


/**
 * Insert the method's description here.
 * Creation date: (4/16/2004 10:42:29 AM)
 */
public void close() throws java.rmi.RemoteException {
	try {
		jmsConn.close();
	} catch (javax.jms.JMSException ex) {
		fieldSessionLog.exception(ex);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2006 3:32:25 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void dataJobMessage(cbit.rmi.event.DataJobEvent event) {
	// if it's from one of our jobs, pass it along so it will reach the client
	if (getUserLoginInfo().getUser().equals(event.getUser())) {
		messageService.messageEvent(event);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 2:59:05 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void exportMessage(ExportEvent event) {
	// if it's from one of our jobs, pass it along so it will reach the client
	if (getUserLoginInfo().getUser().equals(event.getUser())) {
		messageService.messageEvent(event);
	}
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.server.DataSetController
 * @exception java.lang.Exception The exception description.
 */
public DataSetController getDataSetController() throws RemoteException, DataAccessException {
	fieldSessionLog.print("LocalVCellConnectionMessaging.getDataSetController()");
	if (dataSetControllerMessaging == null) {
		try {
			dataClientMessaging = new JmsClientMessaging(jmsConn, fieldSessionLog, getUserLoginInfo());
			dataSetControllerMessaging = new LocalDataSetControllerMessaging(fieldSessionLog, getUserLoginInfo().getUser(), dataClientMessaging);
			fieldSessionLog.print("new dataClientMessaging=" + dataClientMessaging);
		} catch (javax.jms.JMSException ex) {
			fieldSessionLog.exception(ex);
			throw new DataAccessException(ex.getMessage());
		}
	}
	
	return dataSetControllerMessaging;
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 4:34:02 PM)
 * @return cbit.vcell.messaging.event.SimpleMessageServiceMessaging
 */
SimpleMessageService getMessageService() {
	return messageService;
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.solvers.MathController
 * @param mathDesc cbit.vcell.math.MathDescription
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationController getSimulationController() throws RemoteException {
	if (simulationControllerMessaging == null){
		try {
			simClientMessaging = new JmsClientMessaging(jmsConn, fieldSessionLog, getUserLoginInfo());
			simulationControllerMessaging = new LocalSimulationControllerMessaging(getUserLoginInfo().getUser(), simClientMessaging, fieldSessionLog);
			fieldSessionLog.print("new simClientMessaging=" + simClientMessaging);
		} catch (DataAccessException ex) {
			fieldSessionLog.exception(ex);
			throw new RuntimeException(ex.getMessage());
		} catch (javax.jms.JMSException ex) {
			fieldSessionLog.exception(ex);
			throw new RuntimeException(ex.getMessage());
		}
	}

	return simulationControllerMessaging;
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/01 11:15:49 PM)
 * @return cbit.vcell.server.URLFinder
 * @exception java.rmi.RemoteException The exception description.
 */
public URLFinder getURLFinder() {
	try {
		return new URLFinder(	new URL(PropertyLoader.getRequiredProperty(PropertyLoader.tutorialURLProperty)),
								new URL(PropertyLoader.getRequiredProperty(PropertyLoader.userGuideURLProperty)));
	} catch (java.net.MalformedURLException e){
		fieldSessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}

public VisitConnectionInfo createNewVisitConnection() {
	VisitConnectionInfo visitConnectionInfo = VisitConnectionInfo.createHardCodedVisitConnectionInfo(getUserLoginInfo().getUser());
	return visitConnectionInfo;
}

/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public UserLoginInfo getUserLoginInfo() {
	return userLoginInfo;
}


/**
 * This method was created by a SmartGuide.
 * @return DBManager
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public UserMetaDbServer getUserMetaDbServer() throws RemoteException, DataAccessException {
	fieldSessionLog.print("LocalVCellConnectionMessaging.getUserMetaDbServer(" + getUserLoginInfo().getUser() + ")");
	if (userMetaDbServerMessaging == null) {
		try {
			dbClientMessaging = new JmsClientMessaging(jmsConn, fieldSessionLog, getUserLoginInfo());
			userMetaDbServerMessaging = new LocalUserMetaDbServerMessaging(dbClientMessaging, getUserLoginInfo().getUser(), fieldSessionLog);
			fieldSessionLog.print("new dbClientMessaging=" + dbClientMessaging);
		} catch (javax.jms.JMSException ex) {
			fieldSessionLog.exception(ex);
			throw new DataAccessException(ex.getMessage());
		}			
	}
	return userMetaDbServerMessaging;
}

/**
 * Insert the method's description here.
 * Creation date: (4/16/2004 11:22:31 AM)
 */
public boolean isTimeout() throws java.rmi.RemoteException {
	return messageService.timeSinceLastPoll() > MAX_TIME_WITHOUT_POLLING_MS;
}


public void sendErrorReport(Throwable exception) throws RemoteException {
	VCMongoMessage.sendClientException(exception, getUserLoginInfo());
	BeanUtils.sendErrorReport(exception);
}

public MessageEvent[] getMessageEvents() throws RemoteException {
	MessageEvent[] messageEvents = messageService.getMessageEvents();
	VCMongoMessage.sendClientMessageEventsDelivered(messageEvents, getUserLoginInfo());
	return messageEvents;
}


public void reportPerformanceMonitorEvent(PerformanceMonitorEvent performanceMonitorEvent) throws RemoteException {
	performanceMonitoringFacility.performanceMonitorEvent(performanceMonitorEvent);
	
}

}
