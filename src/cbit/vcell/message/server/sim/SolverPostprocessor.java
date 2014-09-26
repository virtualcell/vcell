/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.sim;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.logging.Logging;
import org.vcell.util.logging.Logging.ConsoleDestination;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.jmx.VCellServiceMXBean;
import cbit.vcell.message.server.jmx.VCellServiceMXBeanImpl;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.solver.VCSimulationIdentifier;
/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 4:14:09 PM)
 * @author: Jim Schaff
 */
public class SolverPostprocessor  {

	public static void main(java.lang.String[] args) {
		if (args.length != 6) {
			System.out.println("Usage: " + SolverPostprocessor.class.getName() + " simKey username userKey jobindex taskid solverExitCode");
			System.exit(1);
		}

		Logging.init();
		Logging.changeConsoleLogging(ConsoleDestination.STD_ERR, ConsoleDestination.STD_OUT); 
		SessionLog log = new StdoutSessionLog("solverPostprocessor");
		VCMessagingService vcMessagingService = null;
		try {
			
			PropertyLoader.loadProperties();
			
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
 
	        vcMessagingService = VCMessagingService.createInstance(new ServerMessagingDelegate());
			VCMessageSession session = vcMessagingService.createProducerSession();
			if (solverExitCode==0){
				WorkerEventMessage workerEventMessage = WorkerEventMessage.sendWorkerExitNormal(session, SolverPostprocessor.class.getName(), hostName, vcSimID, jobIndex, taskID, solverExitCode);
				VCMongoMessage.sendWorkerEvent(workerEventMessage);
			}else{
				WorkerEventMessage workerEventMessage = WorkerEventMessage.sendWorkerExitError(session, SolverPostprocessor.class.getName(), hostName, vcSimID, jobIndex, taskID, solverExitCode);
				VCMongoMessage.sendWorkerEvent(workerEventMessage);
			}
			try {
				Thread.sleep(2000);
			}catch (InterruptedException e){
			}
		} catch (Throwable e) {
			log.exception(e);
		} finally {
			if (vcMessagingService!=null){
				try {
					vcMessagingService.closeAll();
				} catch (VCMessagingException e) {
					e.printStackTrace();
				}
			}
			VCMongoMessage.flush();
			System.exit(0);
		}
	}


}
