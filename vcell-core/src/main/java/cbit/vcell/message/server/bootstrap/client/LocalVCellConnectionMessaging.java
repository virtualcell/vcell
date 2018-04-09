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
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.DataAccessException;
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
	private static Logger lg = LogManager.getLogger(LocalVCellConnectionMessaging.class);
	
	private UserLoginInfo userLoginInfo;
	
	private RpcSender rpcSender;
	
	public LocalVCellConnectionMessaging(UserLoginInfo userLoginInfo, RpcSender rpcSender) {
		this.userLoginInfo = userLoginInfo;
		this.rpcSender = rpcSender;
	}	

	
@Override
public DataSetController getDataSetController() throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("LocalVCellConnectionMessaging.getDataSetController()");
	if (dataSetControllerMessaging == null) {
		dataSetControllerMessaging = new LocalDataSetControllerMessaging(getUserLoginInfo(), rpcSender);
	}
	return dataSetControllerMessaging;
}

@Override
public SimulationController getSimulationController() {
	if (simulationControllerMessaging == null){
		simulationControllerMessaging = new LocalSimulationControllerMessaging(getUserLoginInfo(), rpcSender);
	}
	return simulationControllerMessaging;
}

@Override
public UserLoginInfo getUserLoginInfo() {
	return userLoginInfo;
}

@Override
public UserMetaDbServer getUserMetaDbServer() throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("LocalVCellConnectionMessaging.getUserMetaDbServer(" + getUserLoginInfo().getUser() + ")");
	if (userMetaDbServerMessaging == null) {
		userMetaDbServerMessaging = new LocalUserMetaDbServerMessaging(getUserLoginInfo(), rpcSender);
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

@Override
public MessageEvent[] getMessageEvents() throws RemoteProxyException, IOException {
	return rpcSender.getMessageEvents();
}

}
