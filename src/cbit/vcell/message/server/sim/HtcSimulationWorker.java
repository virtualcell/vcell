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
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.ExecutableException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.util.xml.XmlUtil;
import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCPooledQueueConsumer;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.VCTopicConsumer;
import cbit.vcell.message.VCTopicConsumer.TopicListener;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.SimulationTaskMessage;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.ServiceSpec.ServiceType;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobID.BatchSystemType;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;
import cbit.vcell.message.server.htc.pbs.PbsProxy;
import cbit.vcell.message.server.htc.sge.SgeProxy;
import cbit.vcell.message.server.jmx.VCellServiceMXBean;
import cbit.vcell.message.server.jmx.VCellServiceMXBeanImpl;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
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
	private VCTopicConsumer serviceControlTopicConsumer = null;
	private VCMessageSession sharedMessageProducer = null;
	private VCPooledQueueConsumer pooledQueueConsumer = null;
	
	/**
	 * SimulationWorker constructor comment.
	 * @param argName java.lang.String
	 * @param argParentNode cbit.vcell.appserver.ComputationalNode
	 * @param argInitialContext javax.naming.Context
	 */
public HtcSimulationWorker(HtcProxy htcProxy, VCMessagingService vcMessagingService, ServiceInstanceStatus serviceInstanceStatus, SessionLog log, boolean bSlaveMode) throws DataAccessException, FileNotFoundException, UnknownHostException {
	super(vcMessagingService, serviceInstanceStatus, log, bSlaveMode);
	this.htcProxy = htcProxy;
}

public final String getJobSelector() {
	String jobSelector = "(" + MessageConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "')";
	
	return jobSelector;
}

public void init() {
	initControlTopicListener();
	initQueueConsumer();
}

private void initServiceControlTopicListener() {
	TopicListener listener = new TopicListener() {

		public void onTopicMessage(VCMessage message, VCMessageSession session) {
			try {
				String msgType = message.getStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY);
				
				if (msgType == null) {
					return;
				}
				
				if (msgType.equals(MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE)) {			
					final long simID = message.getLongProperty(MessageConstants.SIMKEY_PROPERTY);
					final int jobIndex = message.getIntProperty(MessageConstants.JOBINDEX_PROPERTY);
					final int taskID = message.getIntProperty(MessageConstants.TASKID_PROPERTY);
					String htcJobIdString = null;
					if (message.propertyExists(MessageConstants.HTCJOBID_PROPERTY)){
						htcJobIdString = message.getStringProperty(MessageConstants.HTCJOBID_PROPERTY);
					}
					final HtcJobID htcJobId = (htcJobIdString!=null) ? HtcJobID.fromDatabase(htcJobIdString) : null;
					
					Runnable runnable = new Runnable(){
						public void run() {
							HtcProxy threadLocalHtcProxy = htcProxy.cloneThreadsafe();
							try {
								try {
									Thread.sleep(10000); // sleep 10 seconds once ... give job time to kill itself gracefully
								} catch (InterruptedException e1) {
								}
								if (htcJobId!=null){
									threadLocalHtcProxy.killJob(htcJobId);
								}else{
									//
									// should only return one running job with this name (sim/job/task) (but handles more than one).
									//
									String simJobName = HtcProxy.createHtcSimJobName(new HtcProxy.SimTaskInfo(new KeyValue(simID+""), jobIndex, taskID));
									List<HtcJobID> htcJobIDs = threadLocalHtcProxy.getRunningSimulationJobIDs();
									Map<HtcJobID,HtcJobInfo> htcJobInfos = threadLocalHtcProxy.getJobInfos(htcJobIDs);
									for (HtcJobInfo htcJobInfo : htcJobInfos.values()){
										try {
											if (htcJobInfo.isFound() && htcJobInfo.getJobName().equals(simJobName)){
												threadLocalHtcProxy.killJob(htcJobInfo.getHtcJobID());
											}
										} catch (Exception e) {
											log.exception(e);
										}
									}
								}
							} catch (Exception e) {
								log.exception(e);
							} finally {
								threadLocalHtcProxy.getCommandService().close();
							}
						}
					};
					Thread killSimulationThread = new Thread(runnable, "Kill Simulation Thread (sim="+simID+", job="+jobIndex+", taskid="+taskID);
					killSimulationThread.setDaemon(true);
					killSimulationThread.start();
				}
			} catch (Exception ex) {
				log.exception(ex);
			}	
		}

	};
	VCMessageSelector selector = null;
	String threadName = "Service Control Topic Consumer (for killing sims)";
	serviceControlTopicConsumer = new VCTopicConsumer(VCellTopic.ServiceControlTopic, listener, selector, threadName, MessageConstants.PREFETCH_LIMIT_SERVICE_CONTROL);
	vcMessagingService.addMessageConsumer(serviceControlTopicConsumer);
}



private void initQueueConsumer() {
	
	this.sharedMessageProducer = vcMessagingService.createProducerSession();
	
	QueueListener queueListener = new QueueListener() {
		
		@Override
		public void onQueueMessage(VCMessage vcMessage, VCMessageSession session) throws RollbackException {
			SimulationTask simTask = null;
			try {
				SimulationTaskMessage simTaskMessage = new SimulationTaskMessage(vcMessage);
				simTask = simTaskMessage.getSimulationTask();
				File userdir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty),simTask.getUserName());
				HtcProxy clonedHtcProxy = htcProxy.cloneThreadsafe();
				HtcJobID pbsId = submit2PBS(simTask, clonedHtcProxy, log, userdir);
				synchronized (sharedMessageProducer) {
					WorkerEventMessage.sendAccepted(sharedMessageProducer, HtcSimulationWorker.class.getName(), simTask, ManageUtils.getHostName(), pbsId);
				}
			} catch (Exception e) {
				log.exception(e);
				if (simTask!=null){
					try {
						synchronized (sharedMessageProducer) {
							WorkerEventMessage.sendFailed(sharedMessageProducer,  HtcSimulationWorker.class.getName(), simTask, ManageUtils.getHostName(), SimulationMessage.jobFailed(e.getMessage()));
						}
					} catch (VCMessagingException e1) {
						log.exception(e1);
					}
				}
			}
		}
	};

	int numHtcworkerThreads = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.htcworkerThreadsProperty));
	this.pooledQueueConsumer = new VCPooledQueueConsumer(queueListener, log, numHtcworkerThreads, sharedMessageProducer);
	this.pooledQueueConsumer.initThreadPool();
	VCellQueue queue = VCellQueue.SimJobQueue;
	VCMessageSelector selector = vcMessagingService.createSelector(getJobSelector());
	String threadName = "SimJob Queue Consumer";
	queueConsumer = new VCQueueConsumer(queue, pooledQueueConsumer, selector, threadName, MessageConstants.PREFETCH_LIMIT_SIM_JOB_HTC);
	vcMessagingService.addMessageConsumer(queueConsumer);
}

private HtcJobID submit2PBS(SimulationTask simTask, HtcProxy clonedHtcProxy, SessionLog log, File userdir) throws XmlParseException, IOException, SolverException, ExecutableException {

	HtcJobID jobid = null;
	String htcLogDirString = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDir);
    if (!htcLogDirString.endsWith("/")){
    	htcLogDirString = htcLogDirString+"/";
    }
    String jobname = HtcProxy.createHtcSimJobName(new HtcProxy.SimTaskInfo(simTask.getSimKey(), simTask.getSimulationJob().getJobIndex(), simTask.getTaskID()));   //"S_" + simTask.getSimKey() + "_" + simTask.getSimulationJob().getJobIndex()+ "_" + simTask.getTaskID();
	String subFile = htcLogDirString+jobname + clonedHtcProxy.getSubmissionFileExtension();
	
	Solver realSolver = (AbstractSolver)SolverFactory.createSolver(log, userdir, simTask, true);
	
	String simTaskXmlText = XmlHelper.simTaskToXML(simTask);
	String simTaskFilePath = forceUnixPath(new File(userdir,simTask.getSimulationJobID()+"_"+simTask.getTaskID()+".simtask.xml").toString());
	
	if (clonedHtcProxy.getCommandService() instanceof CommandServiceSsh){
		// write simTask file locally, and send it to server, and delete local copy.
		File tempFile = File.createTempFile("simTask", "xml");
		XmlUtil.writeXMLStringToFile(simTaskXmlText, tempFile.getAbsolutePath(), true);
		clonedHtcProxy.getCommandService().pushFile(tempFile, simTaskFilePath);
		tempFile.delete();
	}else{
		// write final file directly.
		XmlUtil.writeXMLStringToFile(simTaskXmlText, simTaskFilePath, true);
	}
	
	final String SOLVER_EXIT_CODE_REPLACE_STRING = "SOLVER_EXIT_CODE_REPLACE_STRING";

	KeyValue simKey = simTask.getSimKey();
	User simOwner = simTask.getSimulation().getVersion().getOwner();
	String[] postprocessorCmd = new String[] { 
			PropertyLoader.getRequiredProperty(PropertyLoader.simulationPostprocessor), 
			simKey.toString(),
			simOwner.getName(), 
			simOwner.getID().toString(),
			Integer.toString(simTask.getSimulationJob().getJobIndex()),
			Integer.toString(simTask.getTaskID()),
			SOLVER_EXIT_CODE_REPLACE_STRING
	};

	if (realSolver instanceof AbstractCompiledSolver) {
		
		// compiled solver ...used to be only single executable, now we pass 2 commands to PBSUtils.submitJob that invokes SolverPreprocessor.main() and then the native executable
		String[] preprocessorCmd = new String[] { 
				PropertyLoader.getRequiredProperty(PropertyLoader.simulationPreprocessor), 
				simTaskFilePath, 
				forceUnixPath(userdir.getAbsolutePath())
		};
		String[] nativeExecutableCmd = ((AbstractCompiledSolver)realSolver).getMathExecutableCommand();
		for (int i=0;i<nativeExecutableCmd.length;i++){
			nativeExecutableCmd[i] = forceUnixPath(nativeExecutableCmd[i]);
		}
		nativeExecutableCmd = BeanUtils.addElement(nativeExecutableCmd, "-tid");
		nativeExecutableCmd = BeanUtils.addElement(nativeExecutableCmd, String.valueOf(simTask.getTaskID()));
		
		jobid = clonedHtcProxy.submitJob(jobname, subFile, preprocessorCmd, nativeExecutableCmd, 1, simTask.getEstimatedMemorySizeMB(), postprocessorCmd, SOLVER_EXIT_CODE_REPLACE_STRING);
		if (jobid == null) {
			throw new RuntimeException("Failed. (error message: submitting to job scheduler failed).");
		}
		
	} else {
		
		String[] command = new String[] { 
				PropertyLoader.getRequiredProperty(PropertyLoader.javaSimulationExecutable), 
				simTaskFilePath,
				forceUnixPath(userdir.getAbsolutePath())
		};

		jobid = clonedHtcProxy.submitJob(jobname, subFile, command, 1, simTask.getEstimatedMemorySizeMB(), postprocessorCmd, SOLVER_EXIT_CODE_REPLACE_STRING);
		if (jobid == null) {
			throw new RuntimeException("Failed. (error message: submitting to job scheduler failed).");
		}
	}
	return jobid;
}
private static String forceUnixPath(String filePath){
	return filePath.replace("C:","").replace("D:","").replace("\\","/");
}


@Override
public void stopService(){
	this.pooledQueueConsumer.shutdownAndAwaitTermination();
	super.stopService();
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
		initLog(serviceInstanceStatus, logdir);
		
		//
		// JMX registration
		//
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		mbs.registerMBean(new VCellServiceMXBeanImpl(), new ObjectName(VCellServiceMXBean.jmxObjectName));
 
        VCMessagingService vcMessagingService = VCMessagingService.createInstance();
		
		htcProxy.checkServerStatus();

		SessionLog log = new StdoutSessionLog(serviceInstanceStatus.getID());
		HtcSimulationWorker simulationWorker = new HtcSimulationWorker(htcProxy, vcMessagingService, serviceInstanceStatus, log, false);
		simulationWorker.init();
		
	} catch (Throwable e) {
		e.printStackTrace(System.out);
		VCMongoMessage.sendException(e);
		VCMongoMessage.flush();
		System.exit(-1);
	}
}


}
