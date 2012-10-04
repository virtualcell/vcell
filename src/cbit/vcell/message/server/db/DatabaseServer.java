/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.db;
import java.util.Date;

import org.vcell.util.MessageConstants.ServiceType;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.VCellServerID;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCMessagingService.VCMessagingDelegate;
import cbit.vcell.message.VCRpcConsumer;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.messaging.admin.ManageUtils;
import cbit.vcell.messaging.admin.ServiceInstanceStatus;
import cbit.vcell.modeldb.DatabasePolicySQL;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class DatabaseServer extends ServiceProvider {
	private DatabaseServerImpl databaseServerImpl = null;
	private VCRpcConsumer rpcConsumer = null;	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/26/2004 9:49:08 AM)
	 */

/**
 * Scheduler constructor comment.
 */
public DatabaseServer(ServiceInstanceStatus serviceInstanceStatus, DatabaseServerImpl databaseServerImpl, VCMessagingService vcMessagingService, SessionLog log) throws Exception {
	super(vcMessagingService,serviceInstanceStatus,log);
	this.databaseServerImpl = databaseServerImpl;
}

private void init() throws Exception {
	rpcConsumer = new VCRpcConsumer(databaseServerImpl, VCellQueue.DbRequestQueue, ServiceType.DB, null, "Database RPC Server Thread");

	VCMessagingDelegate delegate = new VCMessagingDelegate() {
		public void onMessagingException(Exception e) {
			log.exception(e);
		}
	};
	vcMessagingService.setDelegate(delegate);
	vcMessagingService.addMessageConsumer(rpcConsumer);
	
	initControlTopicListener();
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	if (args.length < 1) {
		System.out.println("Missing arguments: " + DatabaseServer.class.getName() + " serviceOrdinal [logdir]");
		System.exit(1);
	}
	
	try {
		PropertyLoader.loadProperties();
		DatabasePolicySQL.bSilent = true;
		
		int serviceOrdinal = Integer.parseInt(args[0]);
		String logdir = null;
		if (args.length > 1) {
			logdir = args[1];
		}
		ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.DB, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);
		//initLog(serviceInstanceStatus, logdir);
		VCMongoMessage.serviceStartup(ServiceName.database, new Integer(serviceOrdinal), args);
		final SessionLog log = new StdoutSessionLog("DatabaseServer");
		
		ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
		KeyFactory	keyFactory = new OracleKeyFactory();
		DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory, log);
		
		VCMessagingService vcMessagingService = VCMessagingService.createInstance();
		
		DatabaseServer databaseServer = new DatabaseServer(serviceInstanceStatus, databaseServerImpl, vcMessagingService, log);
        databaseServer.init();
    } catch (Throwable e) {
	    e.printStackTrace(System.out); 
    }
}


}
