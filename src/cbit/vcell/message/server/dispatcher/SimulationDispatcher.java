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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.vcell.util.ExecutableException;
import org.vcell.util.MessageConstants;
import org.vcell.util.MessageConstants.ServiceType;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.WorkerEvent;
import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobID.BatchSystemType;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.pbs.PbsProxy;
import cbit.vcell.message.server.htc.sge.SgeProxy;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatusInfo;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.modeldb.ResultSetCrawler;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.AbstractSolver;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class SimulationDispatcher extends ServiceProvider {
	public static final String METHOD_NAME_STARTSIMULATION = "startSimulation";
	public static final String METHOD_NAME_STOPSIMULATION = "stopSimulation";

	private SimulationDatabase simulationDatabase = null;
	private VCQueueConsumer workerEventConsumer = null;
	private VCQueueConsumer simRequestConsumer = null;
	
	private SimulationDispatcherEngine simDispatcherEngine = new SimulationDispatcherEngine();

	private DispatchThread dispatchThread = null;
	private SimulationMonitorThread simMonitorThread = null;
	private VCMessageSession dispatcherQueueSession = null;
	private VCMessageSession simMonitorThreadSession = null;
	
	private HtcProxy htcProxy = null;

	public class DispatchThread extends Thread {
		public DispatchThread() {
			super();
			setDaemon(true);
			setName("Simulation Dispatch Thread");
		}

		public void run() {
			
			while (true) {

				try {
					SimulationJobStatusInfo[] allActiveJobs = simulationDatabase.getActiveJobs(getHTCPartitionShareServerIDs());
					ArrayList<SimulationJobStatusInfo> allActiveJobsList = new ArrayList<SimulationJobStatusInfo>(Arrays.asList(allActiveJobs));
					if (allActiveJobs != null && allActiveJobs.length > 0) {
						int htcMaxJobs = getHTCPartitionMaximumJobs();
						int maxOdePerUser = BatchScheduler.getMaxOdeJobsPerUser();
						int maxPdePerUser = BatchScheduler.getMaxPdeJobsPerUser();
						VCellServerID serverID = VCellServerID.getSystemServerID();
						
						SimulationJobStatusInfo nextJob = BatchScheduler.schedule(allActiveJobsList.toArray(new SimulationJobStatusInfo[0]), htcMaxJobs, maxOdePerUser, maxPdePerUser, serverID, log);
						
						while (nextJob != null) {
							
							SimulationJobStatus jobStatus = nextJob.getSimJobStatus();
							VCSimulationIdentifier vcSimID = jobStatus.getVCSimulationIdentifier();
							
							simDispatcherEngine.onDispatch(vcSimID, jobStatus.getJobIndex(), jobStatus.getTaskID(), simulationDatabase, dispatcherQueueSession, log);
							
							Thread.yield();
							
							allActiveJobsList.remove(nextJob);
							nextJob = BatchScheduler.schedule(allActiveJobsList.toArray(new SimulationJobStatusInfo[0]), htcMaxJobs, maxOdePerUser, maxPdePerUser, serverID, log);
						}
					}
				} catch (Exception ex) {
					log.exception(ex);
				}

				// if there are no messages or no qualified jobs or exceptions, sleep for a while
				// this will be interrupted if there is a start request.
				try {
					sleep(10 * MessageConstants.SECOND_IN_MS);
				} catch (InterruptedException ex) {
				}
			} // while(true)
		}
	}

	class SimulationMonitorThread extends Thread {

		Object notifyObject = new Object();
		
		public SimulationMonitorThread(HtcProxy htcProxy) {
			super();
			setDaemon(true);
			setName("Simulation Monitor Thread");
		}
		
		public void run() {
			while (true){

				try {
					killZombieProcesses();
				} catch (ExecutableException e1) {
					log.exception(e1);
				}
				
				//
				// flush the message queue and measure processing time.
				//
				long startFlushTimeMS = System.currentTimeMillis();
				try {
					flushWorkerEventQueue();
				} catch (Exception e1) {
					log.exception(e1);
				}
				long endFlushTimeMS = System.currentTimeMillis();
				long messageFlushTimeMS = endFlushTimeMS - startFlushTimeMS;
				
				//
				// abort unresponsive jobs
				//
				try {
					abortStalledSimulationTasks(messageFlushTimeMS);
				} catch (Exception e1) {
					log.exception(e1);
				}
				
				//
				// sleep 30 seconds and try again
				//
				try { sleep(MessageConstants.MINUTE_IN_MS*2); } catch (InterruptedException e){}
			}
		}
		
		private void flushWorkerEventQueue() throws VCMessagingException{
			VCMessage message = simMonitorThreadSession.createObjectMessage(new Long(VCMongoMessage.getServiceStartupTime()));
			message.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY,MessageConstants.MESSAGE_TYPE_FLUSH_VALUE);
			synchronized (notifyObject) {
				simMonitorThreadSession.sendQueueMessage(VCellQueue.WorkerEventQueue, message);
				try {
					long waitTime = MessageConstants.MINUTE_IN_MS*5;
					long startWaitTime = System.currentTimeMillis();
					notifyObject.wait(waitTime);
					long endWaitTime = System.currentTimeMillis();
					if ((endWaitTime-startWaitTime)>=waitTime){
						throw new VCMessagingException("worker event queue flush timed out (>"+waitTime+" s), considerable message backlog?");
					}
				} catch (InterruptedException e) {
				}
			}
		}
		
		private void killZombieProcesses() throws ExecutableException{
			TreeMap<HtcJobID, String> runningSimulations = htcProxy.getRunningSimulationJobIDs();
			for (HtcJobID htcJobID : runningSimulations.keySet()){
				try {
					String simJobName = runningSimulations.get(htcJobID);
					HtcProxy.SimTaskInfo simTaskInfo = HtcProxy.getSimTaskInfoFromSimJobName(simJobName);
					SimulationJobStatus simJobStatus = simulationDatabase.getSimulationJobStatus(simTaskInfo.simId, simTaskInfo.jobIndex, simTaskInfo.taskId);
					String failureMessage = null;
					boolean killJob = false;
					if (simJobStatus==null){
						failureMessage = "no jobStatus found in database for running htc job";
						killJob = true;
					}else if (simJobStatus.getSchedulerStatus().isDone()){
						failureMessage = "jobStatus Done in database for running htc job";
						if (simJobStatus.getSimulationExecutionStatus()==null){
							killJob = true;
						}else{
							long elapsedTimeMS = System.currentTimeMillis() - simJobStatus.getSimulationExecutionStatus().getLatestUpdateDate().getTime();
							if (elapsedTimeMS > 10000){
								killJob = true;
							}
						}
					}
					if (killJob){
						VCMongoMessage.sendZombieJob(simJobStatus,failureMessage,htcJobID);
						htcProxy.killJob(htcJobID);
					}
				}catch (Exception e){
					log.exception(e);
				}
			}
		}
		
		private void abortStalledSimulationTasks(long messageFlushTimeMS) throws SQLException{
			
			//
			// message queue has already been flushed ... and the time it took was recorded in messageFlushTimeMS
			//
			// because of this, we don't have to worry about killing jobs prematurely.
			//
			
			long currentTimeMS = System.currentTimeMillis();
			SimulationJobStatus[] jobStatusArray = simulationDatabase.getObsoleteSimulations(MessageConstants.INTERVAL_DATABASE_SERVER_FAIL_SECONDS + (messageFlushTimeMS/1000));
			for (SimulationJobStatus jobStatus : jobStatusArray){
				String failureMessage = "failed: timed out";
				System.out.println("obsolete job detected at timestampMS="+currentTimeMS+", status=(" + jobStatus + ")\n\n");
				SimulationStateMachine simStateMachine = simDispatcherEngine.getSimulationStateMachine(jobStatus.getVCSimulationIdentifier().getSimulationKey(), jobStatus.getJobIndex());
				System.out.println(simStateMachine.show());
				VCMongoMessage.sendObsoleteJob(jobStatus,failureMessage,simStateMachine);
				simDispatcherEngine.onSystemAbort(jobStatus, failureMessage, simulationDatabase, simMonitorThreadSession, log);
				if (jobStatus.getSimulationExecutionStatus()!=null && jobStatus.getSimulationExecutionStatus().getHtcJobID()!=null){
					HtcJobID htcJobId = jobStatus.getSimulationExecutionStatus().getHtcJobID();
					try {
						htcProxy.killJob(htcJobId);
					} catch (HtcJobNotFoundException e) {
						e.printStackTrace();
					} catch (ExecutableException e) {
						e.printStackTrace();
					} catch (HtcException e) {
						e.printStackTrace();
					}
				}
			}			
		}
	}

	/**
	 * Scheduler constructor comment.
	 */
	public SimulationDispatcher(HtcProxy htcProxy, VCMessagingService vcMessagingService, ServiceInstanceStatus serviceInstanceStatus, SimulationDatabase simulationDatabase, SessionLog log) throws Exception {
		super(vcMessagingService,serviceInstanceStatus,log);
		this.simulationDatabase = simulationDatabase;
		this.htcProxy = htcProxy;
	}


	public void init(){
		
		//
		// set up consumer for WorkerEvent messages
		//
		QueueListener workerEventListener = new QueueListener() {
			public void onQueueMessage(VCMessage vcMessage, VCMessageSession session) throws RollbackException {
				onWorkerEventMessage(vcMessage, session);
			}
		};
		VCMessageSelector workerEventSelector = null;
		String threadName = "Worker Event Consumer";
		workerEventConsumer = new VCQueueConsumer(VCellQueue.WorkerEventQueue, workerEventListener, workerEventSelector, threadName);
		vcMessagingService.addMessageConsumer(workerEventConsumer);

		//
		// set up consumer for Simulation Request (non-blocking RPC) messages
		//
		QueueListener simRequestListener = new QueueListener() {
			public void onQueueMessage(VCMessage vcMessage, VCMessageSession session) throws RollbackException {
				onSimRequestMessage(vcMessage, session);	
			}
		};
		VCMessageSelector simRequestSelector = null;
		threadName = "Sim Request Consumer";
		simRequestConsumer = new VCQueueConsumer(VCellQueue.SimReqQueue, simRequestListener, simRequestSelector, threadName);
		vcMessagingService.addMessageConsumer(simRequestConsumer);
		
		this.dispatcherQueueSession = vcMessagingService.createProducerSession();
		
		this.dispatchThread = new DispatchThread();
		this.dispatchThread.start();
		
		initControlTopicListener();
		
		this.simMonitorThreadSession = vcMessagingService.createProducerSession();
		this.simMonitorThread = new SimulationMonitorThread(htcProxy);
		this.simMonitorThread.start();
	}



	/**
	 * @param vcMessage
	 * @param session
	 */
	private void onSimRequestMessage(VCMessage vcMessage, VCMessageSession session) {
		try {
			Object objectContent = vcMessage.getObjectContent();
			if (objectContent==null){
				return;
			}

			if (!(objectContent instanceof VCRpcRequest)) {
				return;
			}

			VCRpcRequest request = (VCRpcRequest)objectContent;

			VCSimulationIdentifier vcSimID = (VCSimulationIdentifier)request.getArguments()[0];
			User user = request.getUser();

			if (request.getMethodName().equals(METHOD_NAME_STARTSIMULATION)) {
				
				simDispatcherEngine.onStartRequest(vcSimID, user, simulationDatabase, session, dispatcherQueueSession, log);

			} else if (request.getMethodName().equals(METHOD_NAME_STOPSIMULATION)) {
				
				simDispatcherEngine.onStopRequest(vcSimID, user, simulationDatabase, session, log);

			}
		} catch (Exception e) {
			log.exception(e);
		}
	}


	/**
	 * @param vcMessage
	 * @param session
	 */
	private void onWorkerEventMessage(VCMessage vcMessage, VCMessageSession session) {
		try {
			//
			// process WorkerEventQueue flush message
			//
			if (vcMessage.propertyExists(MessageConstants.MESSAGE_TYPE_PROPERTY) && vcMessage.getStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY).equals(MessageConstants.MESSAGE_TYPE_FLUSH_VALUE)){
				if (simMonitorThread!=null){
					try {
						synchronized (simMonitorThread.notifyObject){
							simMonitorThread.notifyObject.notify();
						}
					}catch (IllegalMonitorStateException e){
						e.printStackTrace();
					}
					VCMongoMessage.sendInfo("flushed worker event queue");
				}
				return;
			}

			WorkerEventMessage workerEventMessage = new WorkerEventMessage(simulationDatabase, vcMessage);
			WorkerEvent workerEvent = workerEventMessage.getWorkerEvent();
			simDispatcherEngine.onWorkerEvent(workerEvent, simulationDatabase, session, log);

		} catch (Exception ex) {
			log.exception(ex);
		}
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (2/21/2006 8:59:36 AM)
	 * @return int
	 */
	private static int getHTCPartitionMaximumJobs() {
		return Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.htcPartitionMaximumJobs));
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (2/21/2006 9:01:20 AM)
	 * @return cbit.vcell.messaging.db.VCellServerID[]
	 */
	private static VCellServerID[] getHTCPartitionShareServerIDs() {
		try {
			String lsfPartitionShareServerIDs = PropertyLoader.getRequiredProperty(PropertyLoader.htcPartitionShareServerIDs);
			StringTokenizer st = new StringTokenizer(lsfPartitionShareServerIDs, " ,");
			VCellServerID[] serverIDs = new VCellServerID[st.countTokens() + 1]; // include the current system ServerID
			serverIDs[0] = VCellServerID.getSystemServerID();

			int count = 1;
			while (st.hasMoreTokens()) {			
				serverIDs[count] = VCellServerID.getServerID(st.nextToken());
				count ++;			
			}
			return serverIDs;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 */
	public static void main(java.lang.String[] args) {
		if (args.length != 3 && args.length != 6) {
			System.out.println("Missing arguments: " + SimulationDispatcher.class.getName() + " serviceOrdinal (logdir|-) (PBS|SGE) [pbshost userid pswd] ");
			System.exit(1);
		}

		try {
			PropertyLoader.loadProperties();		

			int serviceOrdinal = Integer.parseInt(args[0]);
			String logdir = null;
			if (args.length > 1) {
				logdir = args[1];
			}
			
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
				default: {
					throw new RuntimeException("unrecognized batch scheduling option :"+batchSystemType);
				}
			}
			
			VCMongoMessage.serviceStartup(ServiceName.dispatch, new Integer(serviceOrdinal), args);
			ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), 
					ServiceType.DISPATCH, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);	
			//		initLog(logdir);

			final SessionLog log = new StdoutSessionLog(serviceInstanceStatus.getID());

			KeyFactory keyFactory = new OracleKeyFactory();
			DbDriver.setKeyFactory(keyFactory);
			ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory, log);
			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory, log);
			ResultSetCrawler resultSetCrawler = new ResultSetCrawler(conFactory, adminDbTopLevel, log);
			SimulationDatabase simulationDatabase = new SimulationDatabase(resultSetCrawler, adminDbTopLevel, databaseServerImpl,log);

			VCMessagingService vcMessagingService = VCMessagingService.createInstance();

			SimulationDispatcher simulationDispatcher = new SimulationDispatcher(htcProxy, vcMessagingService, serviceInstanceStatus, simulationDatabase, log);
			simulationDispatcher.init();
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		}
	}


}
