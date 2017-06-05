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
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.UserLoginInfo;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.MessageCollector;
import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.PerformanceMonitorEvent;
import cbit.rmi.event.SimpleMessageCollector;
import cbit.rmi.event.SimpleMessageService;
import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.modeldb.LocalUserMetaDbServer;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.PerformanceMonitoringFacility;
import cbit.vcell.server.SimulationController;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.LocalDataSetController;
/**
 * The user's connection to the Virtual Cell.  It is obtained from the VCellServer
 * after the user has been authenticated.
 * Creation date: (Unknown)
 * @author: Jim Schaff.
 */
public class LocalVCellConnection implements VCellConnection, ExportListener, DataJobListener {
	private SimulationController simulationController = null;
	private SimulationControllerImpl simulationControllerImpl = null;
	private ExportServiceImpl exportServiceImpl = null;
	private DataSetControllerImpl dataSetControllerImpl = null;
	private UserMetaDbServer userMetaDbServer = null;
	private SimpleMessageService messageService = null;
	private SimpleMessageCollector messageCollector = null;
	//
	private UserLoginInfo userLoginInfo;

	//
	// database resources
	//
	private static ConnectionFactory conFactory = null;
	private static KeyFactory keyFactory = null;

	
	private SessionLog fieldSessionLog = null;
	private String fieldHost = null;
	private PerformanceMonitoringFacility performanceMonitoringFacility;
	private LocalDataSetController localDataSetController;

/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public LocalVCellConnection(UserLoginInfo userLoginInfo, String host, SessionLog sessionLog, SimulationDatabase simulationDatabase, DataSetControllerImpl dataSetControllerImpl, ExportServiceImpl exportServiceImpl) throws SQLException, FileNotFoundException {
	this.userLoginInfo = userLoginInfo;
	this.fieldHost = host;
	this.fieldSessionLog = sessionLog;
	this.simulationControllerImpl = new SimulationControllerImpl(sessionLog, simulationDatabase, this);
	sessionLog.print("new LocalVCellConnection(" + userLoginInfo.getUserName() + ")");
	messageService = new SimpleMessageService(userLoginInfo.getUser());
	messageCollector = new SimpleMessageCollector();
	messageCollector.addMessageListener(messageService);
	
	this.exportServiceImpl = exportServiceImpl;
	this.dataSetControllerImpl = dataSetControllerImpl;
	this.exportServiceImpl.addExportListener(this);
	this.dataSetControllerImpl.addDataJobListener(this);

	performanceMonitoringFacility = new PerformanceMonitoringFacility(this.userLoginInfo.getUser());	
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 2:59:05 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
@Override
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
@Override
public DataSetController getDataSetController() throws DataAccessException {
	getSessionLog().print("LocalVCellConnection.getDataSetController()");
	if (localDataSetController == null) {
		localDataSetController = new LocalDataSetController(this, getSessionLog(), dataSetControllerImpl, exportServiceImpl, getUserLoginInfo().getUser());
	}

	return localDataSetController;
}


/**
 * Insert the method's description here.
 * Creation date: (1/29/2003 5:07:46 PM)
 * @return java.lang.String
 */
public String getHost() {
	return fieldHost;
}


MessageCollector getMessageCollector() {
	return messageCollector;
}

/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @param simIdentifier java.lang.String
 */
private SessionLog getSessionLog() {
	return (fieldSessionLog);
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.solvers.MathController
 * @param mathDesc cbit.vcell.math.MathDescription
 * @exception java.rmi.RemoteException The exception description.
 */
@Override
public SimulationController getSimulationController() {
	if (simulationController == null){
		simulationController = new LocalSimulationController(getUserLoginInfo().getUser(),simulationControllerImpl,getSessionLog());
	}
	return simulationController;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
@Override
public UserLoginInfo getUserLoginInfo() {
	return userLoginInfo;
}


/**
 * This method was created by a SmartGuide.
 * @return DBManager
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
@Override
public UserMetaDbServer getUserMetaDbServer() throws DataAccessException {
	getSessionLog().print("LocalVCellConnection.getUserMetaDbServer(" + getUserLoginInfo().getUser() + ")");
	if (userMetaDbServer == null) {
		userMetaDbServer = new LocalUserMetaDbServer(conFactory, keyFactory, getUserLoginInfo().getUser(), getSessionLog());
	}
	return userMetaDbServer;
}


/**
 * This method was created in VisualAge.
 * @param conFactory cbit.sql.ConnectionFactory
 */
public static void setDatabaseResources(ConnectionFactory argConFactory, KeyFactory argKeyFactory) {
	conFactory = argConFactory;
	keyFactory = argKeyFactory;
}


public void dataJobMessage(DataJobEvent event) {
	if (getUserLoginInfo().getUser().equals(event.getUser())) {
		messageService.messageEvent(event);
	}
}


@Override
public void sendErrorReport(Throwable exception, ExtraContext extra)
		throws RemoteException {
	BeanUtils.sendErrorReport(exception,extra !=null ? extra.toString():null);
}


public void sendErrorReport(Throwable exception) {
	BeanUtils.sendErrorReport(exception);
}

public MessageEvent[] getMessageEvents() {
	return messageService.getMessageEvents();
}


public void reportPerformanceMonitorEvent(PerformanceMonitorEvent performanceMonitorEvent) {
	performanceMonitoringFacility.performanceMonitorEvent(performanceMonitorEvent);
	
}
}
