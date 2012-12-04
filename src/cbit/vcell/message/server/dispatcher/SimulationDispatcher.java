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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.util.DataAccessException;
import org.vcell.util.ExecutableException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
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
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.ServiceSpec.ServiceType;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.dispatcher.BatchScheduler.WaitingJob;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobID.BatchSystemType;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;
import cbit.vcell.message.server.htc.pbs.PbsProxy;
import cbit.vcell.message.server.htc.sge.SgeProxy;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.modeldb.ResultSetDBTopLevel;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.solver.Simulation;
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

		Object notifyObject = new Object();
		
		public DispatchThread() {
			super();
			setDaemon(true);
			setName("Simulation Dispatch Thread");
		}

		public void run() {
			
			while (true) {
				
				boolean bDispatchedAnyJobs = false;

				try {
					final SimulationJobStatus[] allActiveJobs = simulationDatabase.getActiveJobs();
					ArrayList<KeyValue> simKeys = new ArrayList<KeyValue>();
					for (SimulationJobStatus simJobStatus : allActiveJobs){
						simKeys.add(simJobStatus.getVCSimulationIdentifier().getSimulationKey());
					}
					final Map<KeyValue,SimulationRequirements> simulationRequirementsMap = simulationDatabase.getSimulationRequirements(simKeys);
					if (allActiveJobs != null && allActiveJobs.length > 0) {
						int maxJobsPerSite = BatchScheduler.getMaxJobsPerSite();
						int maxOdePerUser = BatchScheduler.getMaxOdeJobsPerUser();
						int maxPdePerUser = BatchScheduler.getMaxPdeJobsPerUser();
						VCellServerID serverID = VCellServerID.getSystemServerID();
						
						WaitingJob[] waitingJobs = BatchScheduler.schedule(allActiveJobs, simulationRequirementsMap, maxJobsPerSite, maxOdePerUser, maxPdePerUser, serverID, log);
						
						//
						// temporarily save simulations during this dispatch iteration (to expedite dispatching multiple simulation jobs for same simulation).
						// cache is discarded after use.
						//
						HashMap<KeyValue,Simulation> tempSimulationMap = new HashMap<KeyValue,Simulation>();
						for (WaitingJob waitingJob : waitingJobs){
							SimulationJobStatus jobStatus = waitingJob.simJobStatus;
							VCSimulationIdentifier vcSimID = jobStatus.getVCSimulationIdentifier();
							KeyValue simKey = vcSimID.getSimulationKey();
							Simulation sim = tempSimulationMap.get(simKey);
							if (sim==null){
								sim = simulationDatabase.getSimulation(vcSimID.getOwner(), vcSimID.getSimulationKey());
								tempSimulationMap.put(simKey, sim);
							}
							simDispatcherEngine.onDispatch(sim, jobStatus, simulationDatabase, dispatcherQueueSession, log);
							bDispatchedAnyJobs = true;
							
							Thread.yield();
						}
					}
				} catch (Exception ex) {
					log.exception(ex);
				}

				// if there are no messages or no qualified jobs or exceptions, sleep for a while
				// this will be interrupted if there is a start request.
				if (!bDispatchedAnyJobs){
					synchronized (notifyObject) {
						try {
							long waitTime = 5 * MessageConstants.SECOND_IN_MS;
							notifyObject.wait(waitTime);
						} catch (InterruptedException ex) {
						}
					}
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
				// abort unresponsive or unreferenced jobs
				//
				try {
					abortStalledOrUnreferencedSimulationTasks(messageFlushTimeMS);
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
					long elapsedFlushTime = endWaitTime-startWaitTime;
					VCMongoMessage.sendInfo("flushed worker event queue: elapsedTime="+(elapsedFlushTime/1000.0)+" s");
					if (elapsedFlushTime >= waitTime){
						throw new VCMessagingException("worker event queue flush timed out (>"+waitTime+" s), considerable message backlog?");
					}
				} catch (InterruptedException e) {
				}
			}
		}
		
		private void killZombieProcesses() throws ExecutableException{
			List<HtcJobID> runningSimulations = htcProxy.getRunningSimulationJobIDs();
			Map<HtcJobID,HtcJobInfo> jobInfos = htcProxy.getJobInfos(runningSimulations);
			for (HtcJobID htcJobID : runningSimulations){
				HtcJobInfo jobInfo = jobInfos.get(htcJobID);
				if (jobInfo.isFound()){
					try {
						String simJobName = jobInfo.getJobName();
						HtcProxy.SimTaskInfo simTaskInfo = HtcProxy.getSimTaskInfoFromSimJobName(simJobName);
						SimulationJobStatus simJobStatus = simulationDatabase.getLatestSimulationJobStatus(simTaskInfo.simId, simTaskInfo.jobIndex);
						String failureMessage = null;
						boolean killJob = false;
						if (simJobStatus==null){
							failureMessage = "no jobStatus found in database for running htc job";
							killJob = true;
						}else if (simTaskInfo.taskId < simJobStatus.getTaskID()){
							failureMessage = "newer task found in database for running htc job";
							killJob = true;
						}else if (simJobStatus.getSchedulerStatus().isDone()){
							failureMessage = "jobStatus Done in database for running htc job";
							if (simJobStatus.getSimulationExecutionStatus()==null){
								killJob = true;
							}else{
								long elapsedTimeMS = System.currentTimeMillis() - simJobStatus.getSimulationExecutionStatus().getLatestUpdateDate().getTime();
								if (elapsedTimeMS > MessageConstants.INTERVAL_SIMULATIONJOBSTATUS_TIMEOUT_MS){
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
		}
		
		private void abortStalledOrUnreferencedSimulationTasks(long messageFlushTimeMS) throws SQLException, DataAccessException{
			
			//
			// message queue has already been flushed ... and the time it took was recorded in messageFlushTimeMS
			//
			// because of this, we don't have to worry about killing jobs prematurely.
			//
			// here we want to kill jobs that are:
			//
			//  1) "timed out" (same VCellServerID)  ("Running" or "Dispatched")   (last update older than 10 minutes + flush time)
			//
			// or
			//
			//  2) "unreferenced" (same VCellServerID)   ("Waiting" or "Queued" or "Dispatched" or "Running")   (not referenced by BioModel, MathModel, or Simulation parent reference)
			//
			//
			long currentTimeMS = System.currentTimeMillis();
			SimulationJobStatus[] activeJobStatusArray = simulationDatabase.getActiveJobs();
			Set<KeyValue> unreferencedSimKeys = simulationDatabase.getUnreferencedSimulations();
			for (SimulationJobStatus activeJobStatus : activeJobStatusArray){
				SchedulerStatus schedulerStatus = activeJobStatus.getSchedulerStatus();
				long timeSinceLastUpdateMS = currentTimeMS - activeJobStatus.getSimulationExecutionStatus().getLatestUpdateDate().getTime();
				
				boolean bTimedOutSimulation = (schedulerStatus.isRunning() || schedulerStatus.isDispatched()) && (timeSinceLastUpdateMS > (MessageConstants.INTERVAL_SIMULATIONJOBSTATUS_TIMEOUT_MS + messageFlushTimeMS));

				boolean bUnreferencedSimulation = unreferencedSimKeys.contains(activeJobStatus.getVCSimulationIdentifier().getSimulationKey());
				
				if (bTimedOutSimulation || bUnreferencedSimulation){
					String failureMessage = (bTimedOutSimulation) ? ("failed: timed out") : ("failed: unreferenced simulation");
					System.out.println("obsolete job detected at timestampMS="+currentTimeMS+", status=(" + activeJobStatus + ")\n\n");
					SimulationStateMachine simStateMachine = simDispatcherEngine.getSimulationStateMachine(activeJobStatus.getVCSimulationIdentifier().getSimulationKey(), activeJobStatus.getJobIndex());
					System.out.println(simStateMachine.show());
					VCMongoMessage.sendObsoleteJob(activeJobStatus,failureMessage,simStateMachine);
					simDispatcherEngine.onSystemAbort(activeJobStatus, failureMessage, simulationDatabase, simMonitorThreadSession, log);
					if (activeJobStatus.getSimulationExecutionStatus()!=null && activeJobStatus.getSimulationExecutionStatus().getHtcJobID()!=null){
						HtcJobID htcJobId = activeJobStatus.getSimulationExecutionStatus().getHtcJobID();
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

			User user = request.getUser();

			if (request.getMethodName().equals(METHOD_NAME_STARTSIMULATION)) {

				VCSimulationIdentifier vcSimID = (VCSimulationIdentifier)request.getArguments()[0];
				Integer numSimulationScanJobs = (Integer)request.getArguments()[1];
				
				simDispatcherEngine.onStartRequest(vcSimID, user, numSimulationScanJobs.intValue(), simulationDatabase, session, dispatcherQueueSession, log);

				// wake up dispatcher thread
				if (dispatchThread!=null){
					try {
						synchronized (dispatchThread.notifyObject){
							dispatchThread.notifyObject.notify();
						}
					}catch (IllegalMonitorStateException e){
						e.printStackTrace();
					}
				}

			} else if (request.getMethodName().equals(METHOD_NAME_STOPSIMULATION)) {

				VCSimulationIdentifier vcSimID = (VCSimulationIdentifier)request.getArguments()[0];
				
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
			ResultSetDBTopLevel resultSetDbTopLevel = new ResultSetDBTopLevel(conFactory, log);
			SimulationDatabase simulationDatabase = new SimulationDatabaseDirect(resultSetDbTopLevel, adminDbTopLevel, databaseServerImpl,log);

			VCMessagingService vcMessagingService = VCMessagingService.createInstance();

			SimulationDispatcher simulationDispatcher = new SimulationDispatcher(htcProxy, vcMessagingService, serviceInstanceStatus, simulationDatabase, log);
			simulationDispatcher.init();
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		}
	}


}
