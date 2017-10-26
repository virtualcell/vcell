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

import java.io.File;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.KeyFactory;
import org.vcell.util.AuthenticationException;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.LocalAdminDbServer;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.VCellServer;
import cbit.vcell.server.VCellServerFactory;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
/**
 * This type was created in VisualAge.
 */
public class LocalVCellServerFactory implements VCellServerFactory {
	private VCellServer vcServer = null;
/**
 * LocalVCellConnectionFactory constructor comment.
 */
public LocalVCellServerFactory(String userid, UserLoginInfo.DigestedPassword digestedPassword, String hostName, VCMessagingService vcMessagingService, ConnectionFactory conFactory, KeyFactory keyFactory, SessionLog sessionLog) throws java.sql.SQLException, java.io.FileNotFoundException, DataAccessException {
	try {
		AdminDatabaseServer adminDbServer = new LocalAdminDbServer(conFactory,keyFactory,sessionLog);
		User adminUser = null;
		if (userid!=null && digestedPassword!=null){			
			adminUser = adminDbServer.getUser(userid,digestedPassword, false);
			if (adminUser==null){
				throw new PermissionException("failed to authenticate user userid "+userid);
			}
			if (!adminUser.getName().equals(PropertyLoader.ADMINISTRATOR_ACCOUNT)){
				throw new PermissionException("userid "+userid+" does not have sufficient privilage");
			}
		}
		AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory, sessionLog);
		DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory, sessionLog);
		SimulationDatabase simulationDatabase = new SimulationDatabaseDirect(adminDbTopLevel, databaseServerImpl, false, sessionLog);	
		Cachetable dataCachetable = new Cachetable(10*Cachetable.minute);
		DataSetControllerImpl datasetControllerImpl = new DataSetControllerImpl(sessionLog,dataCachetable, 
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty)), 
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirProperty)));

		vcServer = new LocalVCellServer(hostName, vcMessagingService, adminDbServer, simulationDatabase, datasetControllerImpl, 0);
	} catch (java.rmi.RemoteException e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage(),e);
	}
}
/**
 * getVCellConnection method comment.
 */
public VCellServer getVCellServer() throws AuthenticationException, ConnectionException {
	return vcServer;
}
}
