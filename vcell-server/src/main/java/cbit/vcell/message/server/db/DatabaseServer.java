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

import cbit.vcell.message.*;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.EnvironmentConfigProvider;
import cbit.vcell.resource.PropertyLoader;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.dependency.server.VCellServerModule;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class DatabaseServer {
	public static final Logger lg = LogManager.getLogger(DatabaseServer.class);

	private final VCMessagingService vcMessagingService_int;
	private final DatabaseServerImpl databaseServerImpl;
	private VCQueueConsumer rpcConsumer = null;	
	private VCRpcMessageHandler rpcMessageHandler = null;
	private VCPooledQueueConsumer pooledQueueConsumer = null;
	private VCMessageSession sharedProducerSession = null;
	
	private DatabaseCleanupThread databaseCleanupThread = null;


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
	
	
public DatabaseServer() throws SQLException, DataAccessException {
	ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory();
	KeyFactory keyFactory = conFactory.getKeyFactory();
	this.databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory);

	this.vcMessagingService_int = new VCMessagingServiceActiveMQ();
	String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
	int jmsport = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
	this.vcMessagingService_int.setConfiguration(new ServerMessagingDelegate(), jmshost, jmsport);
}

public void init() throws Exception {
	int numDatabaseThreads = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.databaseThreadsProperty, "5"));
	this.sharedProducerSession = vcMessagingService_int.createProducerSession();
	rpcMessageHandler = new VCRpcMessageHandler(databaseServerImpl, VCellQueue.DbRequestQueue);
	this.pooledQueueConsumer = new VCPooledQueueConsumer(rpcMessageHandler, numDatabaseThreads, sharedProducerSession);
	this.pooledQueueConsumer.initThreadPool();
	rpcConsumer = new VCQueueConsumer(VCellQueue.DbRequestQueue, this.pooledQueueConsumer, null, "Database RPC Server Thread", MessageConstants.PREFETCH_LIMIT_DB_REQUEST);

	vcMessagingService_int.addMessageConsumer(rpcConsumer);
	
	//
	// Only one site per database should run the cleanup, and it should be prod.
	//
	// The work is not site-scoped: it removes simulations, math descriptions, geometries,
	// simulation contexts and models that no document anywhere references. dev, stage and prod
	// all point at the same Oracle instance, so before this flag every one of them ran this every
	// 15 minutes over the same rows -- a second site cleans nothing extra and only adds snapshot
	// windows and lock contention to a delete that is already sensitive to concurrent saves.
	//
	// Opt-in: a housekeeping task on a shared database should not run just because a service
	// started. Both outcomes are logged, so a site that cleans nothing says so.
	//
	boolean bCleanupEnabled = Boolean.parseBoolean(
			PropertyLoader.getProperty(PropertyLoader.dbCleanupEnabled, "false"));
	if (bCleanupEnabled) {
		lg.info("database cleanup ENABLED on this site (" + PropertyLoader.dbCleanupEnabled + "=true); "
				+ "exactly one site per database should be doing this");
		this.databaseCleanupThread = new DatabaseCleanupThread(databaseServerImpl);
		this.databaseCleanupThread.start();
	} else {
		lg.info("database cleanup DISABLED on this site (" + PropertyLoader.dbCleanupEnabled + "!=true); "
				+ "another site sharing this database is expected to run it");
	}
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
		// A standalone service takes its configuration from the container environment. The desktop
		// client, the CLI and the admin tools deliberately do not -- they run on machines whose
		// environment VCell does not control -- so this is installed per service rather than being
		// the default in PropertyLoader. vcell-rest installs CDIVCellConfigProvider for the same reason.
		PropertyLoader.setConfigProvider(new EnvironmentConfigProvider());
		PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);

		Injector injector = Guice.createInjector(new VCellServerModule());

		DatabaseServer databaseServer = injector.getInstance(DatabaseServer.class);
        databaseServer.init();
    } catch (Throwable e) {
	    lg.error("DatabaseService failed", e);
		System.exit(1);
    }
}


private static final String REQUIRED_SERVICE_PROPERTIES[] = {
		PropertyLoader.vcellServerIDProperty,
		PropertyLoader.vcellSoftwareVersion,
		PropertyLoader.installationRoot,
		PropertyLoader.dbConnectURL,
		PropertyLoader.dbDriverName,
		PropertyLoader.userTimezone,
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
