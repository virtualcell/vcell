/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap.client;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.UserLoginInfo;

import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.PerformanceMonitorEvent;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.ErrorUtils;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.PerformanceMonitoringFacility;
import cbit.vcell.server.SimulationController;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.server.VCellConnection;

/**
 * The user's connection to the Virtual Cell.  It is obtained from the VCellServer
 * after the user has been authenticated.
 * Creation date: (Unknown)
 * @author: Jim Schaff.
 */
public class LocalVCellConnectionMessaging implements VCellConnection {
	private LocalDataSetControllerMessaging dataSetControllerMessaging = null;
	private LocalSimulationControllerMessaging simulationControllerMessaging = null;
	private LocalUserMetaDbServerMessaging userMetaDbServerMessaging = null;
	
	private UserLoginInfo userLoginInfo;
	
	private RpcSender rpcSender;
	
	private SessionLog fieldSessionLog = null;

	private PerformanceMonitoringFacility performanceMonitoringFacility;

	public LocalVCellConnectionMessaging(UserLoginInfo userLoginInfo, SessionLog sessionLog, RpcSender rpcSender) {
		
		this.userLoginInfo = userLoginInfo;
		this.fieldSessionLog = sessionLog;
		this.rpcSender = rpcSender;
		
		sessionLog.print("new LocalVCellConnectionMessaging(" + userLoginInfo.getUser().getName() + ")");	
		
		performanceMonitoringFacility = new PerformanceMonitoringFacility(userLoginInfo.getUser()	);	
	}	

	
@Override
public DataSetController getDataSetController() throws DataAccessException {
	fieldSessionLog.print("LocalVCellConnectionMessaging.getDataSetController()");
	if (dataSetControllerMessaging == null) {
		dataSetControllerMessaging = new LocalDataSetControllerMessaging(getUserLoginInfo(), rpcSender, fieldSessionLog);
	}
	return dataSetControllerMessaging;
}

@Override
public SimulationController getSimulationController() {
	if (simulationControllerMessaging == null){
		simulationControllerMessaging = new LocalSimulationControllerMessaging(getUserLoginInfo(), rpcSender, fieldSessionLog);
	}
	return simulationControllerMessaging;
}

@Override
public UserLoginInfo getUserLoginInfo() {
	return userLoginInfo;
}

@Override
public UserMetaDbServer getUserMetaDbServer() throws DataAccessException {
	fieldSessionLog.print("LocalVCellConnectionMessaging.getUserMetaDbServer(" + getUserLoginInfo().getUser() + ")");
	if (userMetaDbServerMessaging == null) {
		userMetaDbServerMessaging = new LocalUserMetaDbServerMessaging(getUserLoginInfo(), rpcSender, fieldSessionLog);
	}
	return userMetaDbServerMessaging;
}

@Override
public void sendErrorReport(Throwable exception) {
	VCMongoMessage.sendClientException(exception, getUserLoginInfo());
	ErrorUtils.sendErrorReport(exception, null);
}

@Override
public void sendErrorReport(Throwable exception, ExtraContext extra) {
	VCMongoMessage.sendClientException(exception, getUserLoginInfo());
	ErrorUtils.sendErrorReport(exception,(extra == null?null:extra.toString()));
	
}

public void reportPerformanceMonitorEvent(PerformanceMonitorEvent performanceMonitorEvent) {
	performanceMonitoringFacility.performanceMonitorEvent(performanceMonitorEvent);
}


@Override
public MessageEvent[] getMessageEvents() throws RemoteProxyException {
	// TODO Auto-generated method stub
	return null;
}

}
