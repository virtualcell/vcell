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

import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.service.VCellServiceHelper;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCPooledQueueConsumer;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCRpcMessageHandler;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.bootstrap.ServiceType;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class DatabaseServer extends ServiceProvider {
	private DatabaseServerImpl databaseServerImpl = null;
	private VCQueueConsumer rpcConsumer = null;	
	private VCRpcMessageHandler rpcMessageHandler = null;
	private VCPooledQueueConsumer pooledQueueConsumer = null;
	private VCMessageSession sharedProducerSession = null;
	
	private DatabaseCleanupThread databaseCleanupThread = null;

	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/26/2004 9:49:08 AM)
	 */

	
	
	public class DatabaseCleanupThread extends Thread {

		Object notifyObject = new Object();
		DatabaseServerImpl databaseServerImpl = null;

		public DatabaseCleanupThread(DatabaseServerImpl databaseServerImpl) {
			super();
			this.databaseServerImpl = databaseServerImpl;
			setDaemon(true);
			setName("Database Cleanup Thread");
		}

		public void run() {

			while (true) {

				try {
					if (lg.isDebugEnabled()) lg.debug("starting database cleanup");
					
					databaseServerImpl.cleanupDatabase();
					
					if (lg.isDebugEnabled()) lg.debug("done with database cleanup");
				} catch (Exception ex) {
					lg.error(ex.getMessage(), ex);
				}

				try { 
					// run every 15 minutes
					Thread.sleep(15*60*1000);
				}catch (InterruptedException e) {
				}

			}
		}
	}
	
	
/**
 * Scheduler constructor comment.
 */
public DatabaseServer(ServiceInstanceStatus serviceInstanceStatus, DatabaseServerImpl databaseServerImpl, VCMessagingService vcMessagingService_int, boolean bSlaveMode) throws Exception {
	super(vcMessagingService_int, null, serviceInstanceStatus,bSlaveMode);
	this.databaseServerImpl = databaseServerImpl;
}

public void init() throws Exception {
	int numDatabaseThreads = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.databaseThreadsProperty, "5"));
	this.sharedProducerSession = vcMessagingService_int.createProducerSession();
	rpcMessageHandler = new VCRpcMessageHandler(databaseServerImpl, VCellQueue.DbRequestQueue);
	this.pooledQueueConsumer = new VCPooledQueueConsumer(rpcMessageHandler, numDatabaseThreads, sharedProducerSession);
	this.pooledQueueConsumer.initThreadPool();
	rpcConsumer = new VCQueueConsumer(VCellQueue.DbRequestQueue, this.pooledQueueConsumer, null, "Database RPC Server Thread", MessageConstants.PREFETCH_LIMIT_DB_REQUEST);

	vcMessagingService_int.addMessageConsumer(rpcConsumer);
	
	this.databaseCleanupThread = new DatabaseCleanupThread(databaseServerImpl);
	this.databaseCleanupThread.start();
}



@Override
public void stopService() {
	this.pooledQueueConsumer.shutdownAndAwaitTermination();
	super.stopService();
}

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	OperatingSystemInfo.getInstance();

	if (args.length != 0) {
		System.out.println("Unexpected arguments: " + DatabaseServer.class.getName());
		System.exit(1);
	}
	
	try {
		PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);		

		int serviceOrdinal = 99;
		VCMongoMessage.serviceStartup(ServiceName.database, new Integer(serviceOrdinal), args);

		//
		// JMX registration
		//
//		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
//		mbs.registerMBean(new VCellServiceMXBeanImpl(), new ObjectName(VCellServiceMXBean.jmxObjectName));
 				
		ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), 
				ServiceType.DB, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);
		
		ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory();
		KeyFactory keyFactory = conFactory.getKeyFactory();
		DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory);
		
		VCMessagingService vcMessagingService = new VCMessagingServiceActiveMQ();
		String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
		int jmsport = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
		vcMessagingService.setConfiguration(new ServerMessagingDelegate(), jmshost, jmsport);
		
		DatabaseServer databaseServer = new DatabaseServer(serviceInstanceStatus, databaseServerImpl, vcMessagingService, false);
        databaseServer.init();
    } catch (Throwable e) {
	    lg.error(e);
    }
}

private static final String REQUIRED_SERVICE_PROPERTIES[] = {
		PropertyLoader.vcellServerIDProperty,
		PropertyLoader.vcellSoftwareVersion,
		PropertyLoader.installationRoot,
		PropertyLoader.dbConnectURL,
		PropertyLoader.dbDriverName,
		PropertyLoader.dbUserid,
		PropertyLoader.dbPasswordFile,
		PropertyLoader.mongodbHostInternal,
		PropertyLoader.mongodbPortInternal,
		PropertyLoader.mongodbDatabase,
		PropertyLoader.jmsIntHostInternal,
		PropertyLoader.jmsIntPortInternal,
		PropertyLoader.jmsUser,
		PropertyLoader.jmsPasswordFile,
		PropertyLoader.jmsBlobMessageUseMongo,
	};


}
