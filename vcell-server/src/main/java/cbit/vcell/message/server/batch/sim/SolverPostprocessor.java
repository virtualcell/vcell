/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.batch.sim;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ApplicationTerminator;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.jmx.VCellServiceMXBean;
import cbit.vcell.message.server.jmx.VCellServiceMXBeanImpl;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.OperatingSystemInfo.OsType;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.simdata.PortableCommandWrapper;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.util.NativeLoader;
/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 4:14:09 PM)
 * @author: Jim Schaff
 */
public class SolverPostprocessor  {
	private static final int NUM_STD_ARGS = 6;
	private static Logger lg = LogManager.getLogger(SolverPostprocessor.class);

	public static void main(java.lang.String[] args) {
		if (args.length < NUM_STD_ARGS) {
			System.out.println("Usage: " + SolverPostprocessor.class.getName() + " simKey username userKey jobindex taskid solverExitCode [postProcessorCommandFile]");
			System.exit(1);
		}

		VCMessagingService vcMessagingService = null;

		try {

			PropertyLoader.loadProperties(POST_PROCESSOR_PROPERTIES);
			ResourceUtil.setNativeLibraryDirectory();
			NativeLoader.setOsType(OsType.LINUX);

			KeyValue simKey = new KeyValue(args[0]);
			String userName = args[1];
			KeyValue userKey = new KeyValue(args[2]);
			int jobIndex = Integer.parseInt(args[3]);
			int taskID = Integer.parseInt(args[4]);
			int solverExitCode = Integer.parseInt(args[5]);

			User owner = new User(userName,userKey);
			VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
			String hostName = ManageUtils.getHostName();

			VCMongoMessage.serviceStartup(ServiceName.solverPostprocessor, Integer.valueOf(simKey.toString()), args);

			//
			// JMX registration
			//
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			mbs.registerMBean(new VCellServiceMXBeanImpl(), new ObjectName(VCellServiceMXBean.jmxObjectName));

			vcMessagingService = new VCMessagingServiceActiveMQ();
    		String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostInternal);
    		int jmsport = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortInternal));
			vcMessagingService.setConfiguration(new ServerMessagingDelegate(), jmshost, jmsport);
			VCMessageSession session = vcMessagingService.createProducerSession();
			WorkerEventMessage workerEventMessage;
			if (solverExitCode==0){

				Exception postProcessingException = null;
				if (args.length > NUM_STD_ARGS) {
					String fname = args[NUM_STD_ARGS];
					if (lg.isDebugEnabled()) {
						lg.debug("processing " + fname);
					}
					postProcessingException = runPostprocessingCommands(fname,lg);
				}
				if (lg.isDebugEnabled()) {
					lg.debug("postProcessingException is " + postProcessingException);
				}

				if (postProcessingException == null) {
					lg.debug("sendWorkerExitNormal");
					workerEventMessage = WorkerEventMessage.sendWorkerExitNormal(session, SolverPostprocessor.class.getName(), hostName, vcSimID, jobIndex, taskID, solverExitCode);
				}
				else {
					lg.debug("sendWorkerExitError postprocessing");
					workerEventMessage = WorkerEventMessage.sendWorkerExitError(session, postProcessingException, hostName, vcSimID, jobIndex, taskID,
							SimulationMessage.WorkerExited(postProcessingException));
				}

			}else{ //solverExitCode != 0
				lg.debug("sendWorkerExitError solverExitCode");
				workerEventMessage = WorkerEventMessage.sendWorkerExitError(session, SolverPostprocessor.class.getName(), hostName, vcSimID, jobIndex, taskID, solverExitCode);
			}
			lg.debug(workerEventMessage);
			VCMongoMessage.sendWorkerEvent(workerEventMessage);

		} catch (Throwable e) {
			lg.error(e.getMessage(),e);
		} finally {
			if (vcMessagingService!=null){
				try {
					vcMessagingService.close();
				} catch (VCMessagingException e) {
					e.printStackTrace();
				}
			}
			ApplicationTerminator.beginCountdown(TimeUnit.SECONDS, 10,0);
			VCMongoMessage.shutdown( );
			System.exit(0);
		}
	}

	/**
	 * find any {@link PortableCommand}s in file, execute
	 * @param filename
	 * @param lg logger to use
	 * @return exception, if any, null if all command succeeded
	 */
	private static Exception runPostprocessingCommands(String filename, Logger lg) {
		Collection<PortableCommand> commands = PortableCommandWrapper.getCommands(filename);
		for (PortableCommand cmd : commands) {
				if (lg.isDebugEnabled()) {
					lg.debug("processing " + cmd.getClass( ).getName());
				}
			if (cmd.execute() != 0) {
				if (lg.isWarnEnabled()) {
					lg.warn("post processing auxiliary command failed",cmd.exception());
				}
				return cmd.exception();
			}
		}
		return null;
	}
	private static final String POST_PROCESSOR_PROPERTIES[] = {
			PropertyLoader.primarySimDataDirInternalProperty,
			PropertyLoader.secondarySimDataDirInternalProperty
		};

}
