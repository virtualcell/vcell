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
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.VCTopicConsumer;
import cbit.vcell.message.VCTopicConsumer.TopicListener;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.MessageConstants;
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
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.solvers.AbstractSolver;
/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 4:14:09 PM)
 * @author: Jim Schaff
 */
public class HtcSimulationWorker extends ServiceProvider  {
	private static final int NUM_HTC_THREADS = 10;
	private static final int MAX_HTC_TASKS_IN_POOL = 10;
	private ExecutorService executorService = null;
	private ArrayList<Future<Boolean>> messageProcessorFutures = new ArrayList<Future<Boolean>>();

	private HtcProxy htcProxy = null;

	private VCQueueConsumer queueConsumer = null;
	private VCTopicConsumer serviceControlTopicConsumer = null;
	private VCMessageSession sharedMessageProducer = null;
	/**
	 * SimulationWorker constructor comment.
	 * @param argName java.lang.String
	 * @param argParentNode cbit.vcell.appserver.ComputationalNode
	 * @param argInitialContext javax.naming.Context
	 */
public HtcSimulationWorker(HtcProxy htcProxy, VCMessagingService vcMessagingService, ExecutorService executorService, ServiceInstanceStatus serviceInstanceStatus, SessionLog log) throws DataAccessException, FileNotFoundException, UnknownHostException {
	super(vcMessagingService, serviceInstanceStatus, log);
	this.htcProxy = htcProxy;
	this.executorService = executorService;
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
	
	QueueListener listener = new QueueListener() {
		
		public void onQueueMessage(VCMessage vcMessage, VCMessageSession session) throws RollbackException {
			HtcMessageProcessor messageProcessor = new HtcMessageProcessor(vcMessage, sharedMessageProducer, htcProxy.cloneThreadsafe(), log);
			//
			// remove completed "Futures"
			//
			while (true){
				Iterator<Future<Boolean>> iter = messageProcessorFutures.iterator();
				while (iter.hasNext()){
					Future<Boolean> future = iter.next();
					if (future.isDone()){
						iter.remove();
					}
				}
				if (messageProcessorFutures.size()<MAX_HTC_TASKS_IN_POOL){
					// there is room in thread pool for this simulation
					// we will submit this job and return from the callback.
					break;
				}else{
					// block until some tasks finish.
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			Future<Boolean> messageProcessorFuture = executorService.submit(messageProcessor);
			messageProcessorFutures.add(messageProcessorFuture);
		}
	};

	
	VCellQueue queue = VCellQueue.SimJobQueue;
	VCMessageSelector selector = vcMessagingService.createSelector(getJobSelector());
	String threadName = "SimJob Queue Consumer";
	queueConsumer = new VCQueueConsumer(queue, listener, selector, threadName, MessageConstants.PREFETCH_LIMIT_SIM_JOB_HTC);
	vcMessagingService.addMessageConsumer(queueConsumer);
}

private void shutdownAndAwaitTermination(ExecutorService pool) {
	pool.shutdown(); // Disable new tasks from being submitted
	try {
		// Wait a while for existing tasks to terminate
		if (!pool.awaitTermination(20, TimeUnit.SECONDS)) {
			pool.shutdownNow(); // Cancel currently executing tasks
			// Wait a while for tasks to respond to being cancelled
			if (!pool.awaitTermination(20, TimeUnit.SECONDS))
				log.alert("Pool did not terminate");
		}
	} catch (InterruptedException ie) {
		// (Re-)Cancel if current thread also interrupted
		pool.shutdownNow();
		// Preserve interrupt status
		Thread.currentThread().interrupt();
	}
}

@Override
public void stopService(){
	shutdownAndAwaitTermination(executorService);
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
		
		ExecutorService executorService = Executors.newFixedThreadPool(NUM_HTC_THREADS);
		

		htcProxy.checkServerStatus();

		SessionLog log = new StdoutSessionLog(serviceInstanceStatus.getID());
		HtcSimulationWorker simulationWorker = new HtcSimulationWorker(htcProxy, vcMessagingService, executorService, serviceInstanceStatus, log);
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
