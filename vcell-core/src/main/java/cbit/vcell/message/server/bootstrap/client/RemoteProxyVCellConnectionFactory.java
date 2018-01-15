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

import org.vcell.util.AuthenticationException;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.UserLoginInfo;

import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.PerformanceMonitorEvent;
import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.SimulationController;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.server.VCellConnectionFactory;

public class RemoteProxyVCellConnectionFactory implements VCellConnectionFactory {

	UserLoginInfo userLoginInfo;
	private String apihost = null;
	private Integer apiport = null;
	
	public static class RemoteProxyException extends Exception {

		public RemoteProxyException(String message, Exception e) {
			super(message,e);
		}
		
	}

	public class RemoteProxyVCellConnection implements VCellConnection {
		
		private RemoteProxyVCellConnection() {
			
		}

		@Override
		public DataSetController getDataSetController() throws DataAccessException, RemoteProxyException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SimulationController getSimulationController() throws RemoteProxyException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public UserLoginInfo getUserLoginInfo() throws RemoteProxyException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public UserMetaDbServer getUserMetaDbServer() throws RemoteProxyException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void sendErrorReport(Throwable exception) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sendErrorReport(Throwable exception, ExtraContext extra) throws RemoteProxyException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public MessageEvent[] getMessageEvents() throws RemoteProxyException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void reportPerformanceMonitorEvent(PerformanceMonitorEvent performanceMonitorEvent)
				throws RemoteProxyException {
			// TODO Auto-generated method stub
			
		}
		
	}
/**
 * RMIVCellConnectionFactory constructor comment.
 */
public RemoteProxyVCellConnectionFactory(String apihost, Integer apiport, UserLoginInfo userLoginInfo) {
	this.apihost = apihost;
	this.apiport = apiport;
	this.userLoginInfo = userLoginInfo;	
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 12:03:11 PM)
 * @param userID java.lang.String
 * @param password java.lang.String
 */
public void changeUser(UserLoginInfo userLoginInfo) {
	this.userLoginInfo = userLoginInfo;	
}

public VCellConnection createVCellConnection() throws AuthenticationException, ConnectionException {
	return new RemoteProxyVCellConnection();
}

public static String getVCellSoftwareVersion(String apihost, Integer apiport) {
	// TODO Auto-generated method stub
	return null;
}
}
