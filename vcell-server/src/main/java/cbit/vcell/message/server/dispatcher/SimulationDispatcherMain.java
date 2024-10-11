/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.dispatcher;

import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.dependency.server.VCellServerModule;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class SimulationDispatcherMain {
	public static Logger lg = LogManager.getLogger(SimulationDispatcher.class);
	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 */
	public static void main(String[] args) {

		if (args.length != 0) {
			System.out.println("No arguments expected: " + SimulationDispatcherMain.class.getName());
			System.exit(1);
		}

		try {
			OperatingSystemInfo.getInstance();
			PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);

			Injector injector = Guice.createInjector(new VCellServerModule());

			SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator();
			injector.injectMembers(simulationDispatcher);
		} catch (Throwable e) {
			lg.error("uncaught exception initializing SimulationDispatcher: "+e.getLocalizedMessage(), e);
			System.exit(1);
		}
	}


	private static final String REQUIRED_SERVICE_PROPERTIES[] = {
			PropertyLoader.vcellServerIDProperty,
			PropertyLoader.installationRoot,
			PropertyLoader.dbConnectURL,
			PropertyLoader.dbDriverName,
			PropertyLoader.dbUserid,
			PropertyLoader.dbPasswordFile,
			PropertyLoader.userTimezone,
			PropertyLoader.mongodbHostInternal,
			PropertyLoader.mongodbPortInternal,
			PropertyLoader.mongodbDatabase,
			PropertyLoader.jmsIntHostInternal,
			PropertyLoader.jmsIntPortInternal,
			PropertyLoader.jmsSimHostInternal,
			PropertyLoader.jmsSimPortInternal,
			PropertyLoader.jmsUser,
			PropertyLoader.jmsPasswordFile,
			PropertyLoader.htcUser,
			PropertyLoader.jmsBlobMessageUseMongo,
			PropertyLoader.maxJobsPerScan,
			PropertyLoader.maxOdeJobsPerUser,
			PropertyLoader.maxPdeJobsPerUser,
			PropertyLoader.slurm_partition,
			PropertyLoader.htcPowerUserMemoryMaxMB,
			PropertyLoader.htcMaxMemoryMB
		};

}
