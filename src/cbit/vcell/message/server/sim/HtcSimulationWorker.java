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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.StringTokenizer;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.ExecutableException;
import org.vcell.util.MessageConstants;
import org.vcell.util.MessageConstants.ServiceType;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.VCellServerID;

import cbit.util.xml.XmlUtil;
import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.messages.SimulationTaskMessage;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobID.BatchSystemType;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.pbs.PbsProxy;
import cbit.vcell.message.server.htc.sge.SgeProxy;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.Solver;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverFactory;
import cbit.vcell.solvers.AbstractCompiledSolver;
import cbit.vcell.solvers.AbstractSolver;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 4:14:09 PM)
 * @author: Jim Schaff
 */
public class HtcSimulationWorker extends ServiceProvider  {
	private HtcProxy htcProxy = null;

	private VCQueueConsumer queueConsumer = null;
	/**
	 * SimulationWorker constructor comment.
	 * @param argName java.lang.String
	 * @param argParentNode cbit.vcell.appserver.ComputationalNode
	 * @param argInitialContext javax.naming.Context
	 */
public HtcSimulationWorker(HtcProxy htcProxy, VCMessagingService vcMessagingService, ServiceInstanceStatus serviceInstanceStatus, SessionLog log) throws DataAccessException, FileNotFoundException, UnknownHostException {
	super(vcMessagingService, serviceInstanceStatus, log);
	this.htcProxy = htcProxy;
}

public final String getJobSelector() {
	String jobSelector = "(" + MessageConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "')";
	String computeResources =  PropertyLoader.getRequiredProperty(PropertyLoader.htcComputeResources);
	StringTokenizer st = new StringTokenizer(computeResources, " ,");	
	jobSelector += " AND ((" + MessageConstants.COMPUTE_RESOURCE_PROPERTY + " IS NULL) OR (" + MessageConstants.COMPUTE_RESOURCE_PROPERTY + " IN (";
	int count = 0;
	while (st.hasMoreTokens()) {
		if (count > 0) {
			jobSelector = ", ";
		}
		jobSelector += "'" + st.nextToken() + "'";
		count ++;
	}
	jobSelector += ")))";
	
	return jobSelector;
}

private HtcJobID submit2PBS(SimulationTask simTask, File userdir) throws XmlParseException, IOException, SolverException, ExecutableException {

	HtcJobID jobid = null;
	
	String subFile = simTask.getSimulationJob().getSimulationJobID() + htcProxy.getSubmissionFileExtension();
	String jobname = HtcProxy.createHtcSimJobName(simTask.getSimKey(), simTask.getSimulationJob().getJobIndex());   //"S_" + simTask.getSimKey() + "_" + simTask.getSimulationJob().getJobIndex();
	
	Solver realSolver = (AbstractSolver)SolverFactory.createSolver(log, userdir, simTask, true);
	
	if (realSolver instanceof AbstractCompiledSolver) {
		String simTaskXmlText = XmlHelper.simTaskToXML(simTask);
		String simTaskFilePath = forceUnixPath(new File(userdir,simTask.getSimulationJobID()+simTask.getTaskID()+".simtask.xml").toString());

		if (htcProxy.getCommandService() instanceof CommandServiceSsh){
			// write simTask file locally, and send it to server, and delete local copy.
			File tempFile = File.createTempFile("simTask", "xml");
			XmlUtil.writeXMLStringToFile(simTaskXmlText, tempFile.getAbsolutePath(), true);
			this.htcProxy.getCommandService().pushFile(tempFile, simTaskFilePath);
			tempFile.delete();
		}else{
			// write final file directly.
			XmlUtil.writeXMLStringToFile(simTaskXmlText, simTaskFilePath, true);
		}
		
		// compiled solver ...used to be only single executable, now we pass 2 commands to PBSUtils.submitJob that invokes SolverPreprocessor.main() and then the native executable
		String[] preprocessorCmd = new String[] { 
				PropertyLoader.getRequiredProperty(PropertyLoader.simulationPreprocessor), 
//				serviceInstanceStatus.getServerID().toString().toLowerCase(), 
				simTaskFilePath, 
				forceUnixPath(userdir.getAbsolutePath())
		};
		String[] nativeExecutableCmd = ((AbstractCompiledSolver)realSolver).getMathExecutableCommand();
		for (int i=0;i<nativeExecutableCmd.length;i++){
			nativeExecutableCmd[i] = forceUnixPath(nativeExecutableCmd[i]);
		}
		nativeExecutableCmd = BeanUtils.addElement(nativeExecutableCmd, "-tid");
		nativeExecutableCmd = BeanUtils.addElement(nativeExecutableCmd, String.valueOf(simTask.getTaskID()));
		
		jobid = htcProxy.submitJob(jobname, subFile, preprocessorCmd, nativeExecutableCmd, 1, simTask.getEstimatedMemorySizeMB());
		if (jobid == null) {
			throw new RuntimeException("Failed. (error message: submitting to job scheduler failed).");
		}
		
	} else {
		
		File inputFile = new File(getBaseName(userdir,simTask)+SimDataConstants.JAVA_INPUT_EXTENSION);
		String[] command = new String[] { 
				PropertyLoader.getRequiredProperty(PropertyLoader.javaSimulationExecutable), 
				VCellServerID.getSystemServerID().toString(), 
				forceUnixPath(inputFile.getParent()), 
				forceUnixPath(inputFile.getName()), 
				String.valueOf(simTask.getSimulationJob().getJobIndex()),
				"-tid", 
				String.valueOf(simTask.getTaskID())
		};

		jobid = htcProxy.submitJob(jobname, subFile, command, 1, simTask.getEstimatedMemorySizeMB());
		if (jobid == null) {
			throw new RuntimeException("Failed. (error message: submitting to job scheduler failed).");
		}
	}
	return jobid;
}

private String forceUnixPath(String filePath){
	return filePath.replace("C:","").replace("D:","").replace("\\","/");
}

private String getBaseName(File userdir, SimulationTask simTask) {
	return (new File(userdir, simTask.getSimulationJob().getSimulationJobID()).getPath());
}


private void initQueueConsumer() {
	QueueListener listener = new QueueListener() {
		
		public void onQueueMessage(VCMessage vcMessage, VCMessageSession session) throws RollbackException {
			SimulationTask simTask = null;
			try {
				SimulationTaskMessage simTaskMessage = new SimulationTaskMessage(vcMessage);
				simTask = simTaskMessage.getSimulationTask();
				File userdir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty),simTask.getUserName());
				
				HtcJobID pbsId = submit2PBS(simTask, userdir);
				
				WorkerEventMessage.sendAccepted(session, HtcSimulationWorker.this, simTask, ManageUtils.getHostName(), pbsId);
			} catch (Exception e) {
				log.exception(e);
				if (simTask!=null){
					try {
						WorkerEventMessage.sendFailed(session,  HtcSimulationWorker.this, simTask, ManageUtils.getHostName(), SimulationMessage.jobFailed(e.getMessage()));
					} catch (VCMessagingException e1) {
						log.exception(e1);
					}
				}
			}
		}
	};

	
	VCellQueue queue = VCellQueue.SimJobQueue;
	VCMessageSelector selector = vcMessagingService.createSelector(getJobSelector());
	String threadName = "SimJob Queue Consumer";
	queueConsumer = new VCQueueConsumer(queue, listener, selector, threadName);
	vcMessagingService.addMessageConsumer(queueConsumer);
}

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	if (args.length != 3 && args.length != 6) {
		System.out.println("Missing arguments: " + HtcSimulationWorker.class.getName() + " serviceOrdinal (logdir|-) (PBS|SGE) [pbshost userid pswd] ");
		System.exit(1);
	}
 		
	//
	// Create and install a security manager
	//
	try {
		PropertyLoader.loadProperties();
		
		int serviceOrdinal = Integer.parseInt(args[0]);	
		VCMongoMessage.serviceStartup(ServiceName.pbsWorker, new Integer(serviceOrdinal), args);
		String logdir = args[1];
		BatchSystemType batchSystemType = BatchSystemType.valueOf(args[2]);
		
		CommandService commandService = null;
		if (args.length==6){
			String pbsHost = args[3];
			String pbsUser = args[4];
			String pbsPswd = args[5];
			commandService = new CommandServiceSsh(pbsHost,pbsUser,pbsPswd);
			AbstractSolver.bMakeUserDirs = false; // can't make user directories, they are remote.
		}else{
			commandService = new CommandServiceLocal();
		}
		HtcProxy htcProxy = null;
		switch(batchSystemType){
			case PBS:{
				htcProxy = new PbsProxy(commandService);
				break;
			}
			case SGE:{
				htcProxy = new SgeProxy(commandService);
				break;
			}
		}
		
		ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.PBSCOMPUTE, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);
		//initLog(logdir);
		
		VCMessagingService vcMessagingService = VCMessagingService.createInstance();
		
		htcProxy.checkServerStatus();

		SessionLog log = new StdoutSessionLog(serviceInstanceStatus.getID());
		HtcSimulationWorker simulationWorker = new HtcSimulationWorker(htcProxy, vcMessagingService, serviceInstanceStatus, log);
		simulationWorker.initControlTopicListener();
		simulationWorker.initQueueConsumer();
	} catch (Throwable e) {
		e.printStackTrace(System.out);
		VCMongoMessage.sendException(e);
		VCMongoMessage.flush();
		System.exit(-1);
	}
}




}
