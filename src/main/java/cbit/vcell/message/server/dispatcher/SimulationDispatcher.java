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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.ExecutableException;
import org.vcell.util.PermissionException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.VCRpcMessageHandler;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.bootstrap.SimulationService;
import cbit.vcell.message.server.dispatcher.BatchScheduler.WaitingJob;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;
import cbit.vcell.messaging.db.SimpleJobStatus;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.modeldb.SimpleJobStatusQuerySpec;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class SimulationDispatcher extends ServiceProvider {
	public static final String METHOD_NAME_STARTSIMULATION = "startSimulation";
	public static final String METHOD_NAME_STOPSIMULATION = "stopSimulation";
	/**
	 * message prepended to fail to load simluation exception message
	 */
	public static final String FAILED_LOAD_MESSAGE = "Unable to load simulation ";
	/**
	 * minutes between zombie kill runs
	 */
	public static final int ZOMBIE_MINUTES = 2; 
	/**
	 * minutes between queue flushing
	 */
	public static final int FLUSH_QUEUE_MINUTES = 4; 
	/**
	 * queue flush wait time
	 */
	public static final long QUEUE_FLUSH_WAITIME = MessageConstants.MINUTE_IN_MS*5;

	private SimulationDatabase simulationDatabase = null;
	private VCQueueConsumer workerEventConsumer = null;
	private VCQueueConsumer simRequestConsumer = null;
	private VCRpcMessageHandler rpcMessageHandler = null;

	private SimulationDispatcherEngine simDispatcherEngine = new SimulationDispatcherEngine();

	private DispatchThread dispatchThread = null;
	private SimulationMonitor simMonitor = null;
	private VCMessageSession dispatcherQueueSession = null;
	private VCMessageSession simMonitorThreadSession = null;

	private HtcProxy htcProxy = null;
	/**
	 * format for logging. Lazily created on {@link #getDateFormat()}
	 */
	private DateFormat dateFormat = null;
	private static Logger lg = Logger.getLogger(SimulationDispatcher.class);

	public class SimulationServiceImpl implements SimulationService {

		@Override
		public SimulationStatus stopSimulation(User user, VCSimulationIdentifier vcSimulationIdentifier) throws DataAccessException {
			try {
				simDispatcherEngine.onStopRequest(vcSimulationIdentifier, user, simulationDatabase, dispatcherQueueSession, log);
			} catch (VCMessagingException | SQLException e) {
				e.printStackTrace();
				throw new DataAccessException(e.getMessage(),e);
			}
			return simulationDatabase.getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
		}

		@Override
		public SimulationStatus startSimulation(User user, VCSimulationIdentifier vcSimulationIdentifier,int numSimulationScanJobs) throws DataAccessException {
			try {
				simDispatcherEngine.onStartRequest(vcSimulationIdentifier, user, numSimulationScanJobs, simulationDatabase, dispatcherQueueSession, dispatcherQueueSession, log);
			} catch (VCMessagingException | SQLException e1) {
				e1.printStackTrace();
				throw new DataAccessException(e1.getMessage(),e1);
			}

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
			return simulationDatabase.getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
		}

		@Override
		public SimulationStatus getSimulationStatus(User user, KeyValue simulationKey) throws DataAccessException {
			SimulationStatus simStatus = simulationDatabase.getSimulationStatus(simulationKey);
			if (simStatus.getVCSimulationIdentifier().getOwner().equals(user) || user.getName().equals(PropertyLoader.ADMINISTRATOR_ACCOUNT)){
				return simStatus;
			}else{
				throw new PermissionException("User "+user.getName()+" doesn't have access to simulation "+simulationKey);
			}
		}

		@Override
		public SimulationStatus[] getSimulationStatus(User user, KeyValue[] simKeys) throws DataAccessException {
			SimulationStatus[] simStatusArray = simulationDatabase.getSimulationStatus(simKeys);
			for (SimulationStatus simStatus : simStatusArray){
				if (simStatus!=null){
					if (simStatus.getVCSimulationIdentifier().getOwner().equals(user) || user.getName().equals(PropertyLoader.ADMINISTRATOR_ACCOUNT)){
						continue;
					}
					//throw new PermissionException("User "+user.getName()+" doesn't have access to simulation "+simStatus.getVCSimulationIdentifier().getSimulationKey());
				}
			}
			return simStatusArray;
		}

		@Override
		public SimpleJobStatus[] getSimpleJobStatus(User user, SimpleJobStatusQuerySpec simJobStatusQuerySpec) throws DataAccessException {
			SimpleJobStatus[] simpleJobStatusArray = simulationDatabase.getSimpleJobStatus(user,simJobStatusQuerySpec);
			for (SimpleJobStatus simStatus : simpleJobStatusArray){
				if (simStatus!=null){
					if (simStatus.simulationMetadata.vcSimID.getOwner().equals(user) || user.getName().equals(PropertyLoader.ADMINISTRATOR_ACCOUNT)){
						continue;
					}
					throw new PermissionException("User "+user.getName()+" doesn't have access to simulation "+simStatus.simulationMetadata.vcSimID.getSimulationKey());
				}
			}
			return simpleJobStatusArray;
		}
	};


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
					//Set<KeyValue> simKeys = new LinkedHashSet<KeyValue>(); //Linked hash set maintains insertion order
					ArrayList<KeyValue> simKeys = new ArrayList<>();
					for (SimulationJobStatus simJobStatus : allActiveJobs){
						KeyValue simKey = simJobStatus.getVCSimulationIdentifier().getSimulationKey();
						if (!simKeys.contains(simKey)){
							simKeys.add(simKey);
						}
					}
					if (allActiveJobs != null && allActiveJobs.length > 0) {
						int maxJobsPerSite = BatchScheduler.getMaxJobsPerSite();
						int maxOdePerUser = BatchScheduler.getMaxOdeJobsPerUser();
						int maxPdePerUser = BatchScheduler.getMaxPdeJobsPerUser();
						VCellServerID serverID = VCellServerID.getSystemServerID();

						final Map<KeyValue,SimulationRequirements> simulationRequirementsMap = simulationDatabase.getSimulationRequirements(simKeys);
						WaitingJob[] waitingJobs = BatchScheduler.schedule(allActiveJobs, simulationRequirementsMap, maxJobsPerSite, maxOdePerUser, maxPdePerUser, serverID, log);

						//
						// temporarily save simulations during this dispatch iteration (to expedite dispatching multiple simulation jobs for same simulation).
						// cache is discarded after use.
						//
						HashMap<KeyValue,Simulation> tempSimulationMap = new HashMap<KeyValue,Simulation>();
						if (lg.isTraceEnabled()) {
							lg.trace(waitingJobs.length + " jobs to process");
						}
						for (WaitingJob waitingJob : waitingJobs){
							SimulationJobStatus jobStatus = waitingJob.simJobStatus;
							VCSimulationIdentifier vcSimID = jobStatus.getVCSimulationIdentifier();
							try {
								final KeyValue simKey = vcSimID.getSimulationKey();
								Simulation sim = tempSimulationMap.get(simKey);
								if (sim==null){
									sim = simulationDatabase.getSimulation(vcSimID.getOwner(), simKey);
									tempSimulationMap.put(simKey, sim);
								}
								if (lg.isTraceEnabled()) {
									lg.trace("dispatching  " +  vcSimID);
								}
								simDispatcherEngine.onDispatch(sim, jobStatus, simulationDatabase, dispatcherQueueSession, log);
								bDispatchedAnyJobs = true;
							} catch (Exception e) {
								if (lg.isEnabledFor(Level.WARN)) {
									lg.warn("failed to dispatch " + vcSimID);
								}
								final String failureMessage = FAILED_LOAD_MESSAGE + e.getMessage();
								simDispatcherEngine.onSystemAbort(jobStatus, failureMessage, simulationDatabase, simMonitorThreadSession, log);
							}
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
							lg.warn("Dispatch thread wait interrupted", ex);
						}
					}
				}
			} // while(true)
		}
	}

	class SimulationMonitor implements ThreadFactory, RejectedExecutionHandler {
		private ScheduledThreadPoolExecutor executor; 
		private int threadCount;
		/**
		 * synchronizes {@link SimulationDispatcher#onWorkerEventMessage(VCMessage, VCMessageSession)} and
		 * {@link QueueFlusher#flushWorkerEventQueue()}
		 */
		Object notifyObject = new Object();

		public SimulationMonitor( ) {
			threadCount = 1;
			executor =  new ScheduledThreadPoolExecutor(2,this,this);
			executor.scheduleAtFixedRate(new ZombieKiller( ), 0, ZOMBIE_MINUTES, TimeUnit.MINUTES); 
			executor.scheduleAtFixedRate(new QueueFlusher( ), 1,FLUSH_QUEUE_MINUTES,TimeUnit.MINUTES);
		}

		/**
		 * find and kill zombie processes
		 */
		class ZombieKiller implements Runnable {
			@Override
			public void run() {
				try {
					traceThread(this);

					List<HtcJobID> runningSimulations = htcProxy.getRunningSimulationJobIDs();
					Map<HtcJobID,HtcJobInfo> jobInfos = htcProxy.getJobInfos(runningSimulations);
					for (HtcJobID htcJobID : runningSimulations){
						HtcJobInfo jobInfo = jobInfos.get(htcJobID);
						if (jobInfo!=null && jobInfo.isFound()){
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
										SimulationStateMachine ssm = simDispatcherEngine.getSimulationStateMachine(simTaskInfo.simId, simTaskInfo.jobIndex);
										long elapsedTimeMS = System.currentTimeMillis() - ssm.getSolverProcessTimestamp(); 
										if (elapsedTimeMS > MessageConstants.INTERVAL_HTCJOBKILL_DONE_TIMEOUT_MS){
											killJob = true;
										}
									}
								}
								if (killJob){
									if (lg.isEnabledFor(Level.WARN)) {
										lg.warn("killing " + jobInfo + ", " + failureMessage);
									}
									if (simJobStatus == null) { 
										simJobStatus = SimulationJobStatus.noSuchJob();
									}
									VCMongoMessage.sendZombieJob(simJobStatus,failureMessage,htcJobID);
									htcProxy.killJob(htcJobID);
								}
							}catch (Exception e){
								log.exception(e);
							}
						}
					}
				}
				catch (Exception e) {
					//in case of transient status failure, reset timestamps to avoid premature termination of 
					//jobs
					simDispatcherEngine.resetTimeStamps();
					log.exception(e);
				}
			}
		}

		/**
		 * flush message queue
		 */
		class QueueFlusher implements Runnable {
			public void run() {
				try {
					traceThread(this);
					//
					// flush the message queue and measure processing time.
					//
					long startFlushTimeMS = System.currentTimeMillis();
					flushWorkerEventQueue();
					long endFlushTimeMS = System.currentTimeMillis();
					long messageFlushTimeMS = endFlushTimeMS - startFlushTimeMS;

					//
					// abort unresponsive or unreferenced jobs
					//
					abortStalledOrUnreferencedSimulationTasks(messageFlushTimeMS);
				} catch (Exception e1) {
					log.exception(e1);
				}
			}
			
			private void flushWorkerEventQueue() throws VCMessagingException{
				VCMessage message = simMonitorThreadSession.createObjectMessage(new Long(VCMongoMessage.getServiceStartupTime()));
				message.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY,MessageConstants.MESSAGE_TYPE_FLUSH_VALUE);
				synchronized (notifyObject) {
					simMonitorThreadSession.sendQueueMessage(VCellQueue.WorkerEventQueue, message, false, MessageConstants.MINUTE_IN_MS*5L);
					try {
						long startWaitTime = System.currentTimeMillis();
						notifyObject.wait(QUEUE_FLUSH_WAITIME);
						long endWaitTime = System.currentTimeMillis();
						long elapsedFlushTime = endWaitTime-startWaitTime;
						VCMongoMessage.sendInfo("flushed worker event queue: elapsedTime="+(elapsedFlushTime/1000.0)+" s");
						if (elapsedFlushTime >= QUEUE_FLUSH_WAITIME){
							throw new VCMessagingException("worker event queue flush timed out (>"+QUEUE_FLUSH_WAITIME+" s), considerable message backlog?");
						}
					} catch (InterruptedException e) {
						lg.warn("flush worker queue interrupted",e);
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

					KeyValue simId = activeJobStatus.getVCSimulationIdentifier().getSimulationKey();
					int jobIndex = activeJobStatus.getJobIndex();
					SimulationStateMachine ssm = simDispatcherEngine.getSimulationStateMachine(simId, jobIndex);
					long timeSinceLastUpdateMS = currentTimeMS - ssm.getSolverProcessTimestamp(); 

					//
					// fail any active jobs
					//
					boolean bTimedOutSimulation = (schedulerStatus.isRunning() || schedulerStatus.isDispatched()) && (timeSinceLastUpdateMS > (MessageConstants.INTERVAL_SIMULATIONJOBSTATUS_DISPATCHED_RUNNING_TIMEOUT_MS + messageFlushTimeMS));

					//
					// fail any queued jobs
					//
					if (schedulerStatus.isQueued() && (timeSinceLastUpdateMS > MessageConstants.INTERVAL_SIMULATIONJOBSTATUS_QUEUED_TIMEOUT_MS + messageFlushTimeMS)){
						bTimedOutSimulation = true;
					}

					boolean bUnreferencedSimulation = unreferencedSimKeys.contains(activeJobStatus.getVCSimulationIdentifier().getSimulationKey());

					if (bTimedOutSimulation || bUnreferencedSimulation){
						String failureMessage = (bTimedOutSimulation) ? ("failed: timed out") : ("failed: unreferenced simulation");
						System.out.println("obsolete job detected at timestampMS="+currentTimeMS+", status=(" + activeJobStatus + ")\n\n");
						//SimulationStateMachine simStateMachine = simDispatcherEngine.getSimulationStateMachine(activeJobStatus.getVCSimulationIdentifier().getSimulationKey(), activeJobStatus.getJobIndex());
						//					System.out.println(simStateMachine.show());
						VCMongoMessage.sendObsoleteJob(activeJobStatus,failureMessage);
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

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor arg1) {
			lg.warn("Failed to execute " + r.getClass( ).getName());
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r,"SimulationMonitor " + threadCount++);
			t.setDaemon(true);
			t.setPriority(Thread.MIN_PRIORITY + 1);
			return t;
		}
	}

	/**
	 * Scheduler constructor comment.
	 */
	public SimulationDispatcher(HtcProxy htcProxy, VCMessagingService vcMessagingService, ServiceInstanceStatus serviceInstanceStatus, SimulationDatabase simulationDatabase, SessionLog log, boolean bSlaveMode) throws Exception {
		super(vcMessagingService,serviceInstanceStatus,log,bSlaveMode);
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
		workerEventConsumer = new VCQueueConsumer(VCellQueue.WorkerEventQueue, workerEventListener, workerEventSelector, threadName, MessageConstants.PREFETCH_LIMIT_WORKER_EVENT);
		vcMessagingService.addMessageConsumer(workerEventConsumer);

		//
		// set up consumer for Simulation Request (non-blocking RPC) messages
		//
		SimulationService simServiceImpl = new SimulationServiceImpl();

		VCMessageSelector simRequestSelector = null;
		threadName = "Sim Request Consumer";
		this.rpcMessageHandler = new VCRpcMessageHandler(simServiceImpl, VCellQueue.SimReqQueue, log);

		simRequestConsumer = new VCQueueConsumer(VCellQueue.SimReqQueue, rpcMessageHandler, simRequestSelector, threadName, MessageConstants.PREFETCH_LIMIT_SIM_REQUEST);
		vcMessagingService.addMessageConsumer(simRequestConsumer);

		this.dispatcherQueueSession = vcMessagingService.createProducerSession();

		this.dispatchThread = new DispatchThread();
		this.dispatchThread.start();

		initControlTopicListener();

		this.simMonitorThreadSession = vcMessagingService.createProducerSession();
		this.simMonitor = new SimulationMonitor();
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
			if (vcMessage.propertyExists(VCMessagingConstants.MESSAGE_TYPE_PROPERTY) && vcMessage.getStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY).equals(MessageConstants.MESSAGE_TYPE_FLUSH_VALUE)){
				if (simMonitor!=null){
					try {
						synchronized (simMonitor.notifyObject){
							simMonitor.notifyObject.notify();
						}
					}catch (IllegalMonitorStateException e){
						e.printStackTrace();
					}
				}
				return;
			}

			WorkerEventMessage.UserResolver userResolver = new WorkerEventMessage.UserResolver() {
				@Override
				public User getUser(String username) {
					try {
						return simulationDatabase.getUser(username);
					}catch (SQLException | DataAccessException e){
						throw new RuntimeException("cannot resolve user from userid "+username,e);
					}
				}
			};
			
			WorkerEventMessage workerEventMessage = new WorkerEventMessage(userResolver, vcMessage);
			WorkerEvent workerEvent = workerEventMessage.getWorkerEvent();
			simDispatcherEngine.onWorkerEvent(workerEvent, simulationDatabase, session, log);

		} catch (Exception ex) {
			log.exception(ex);
		}
	}
	
	/**
	 * log time and thread id if {@link #lg} {@link Logger#isTraceEnabled()} is true
	 * @param source used to print class name
	 */
	private void traceThread(Object source) {
		if (lg.isTraceEnabled()) {
			lg.trace(source.getClass( ).getName() + " thread id "  + Thread.currentThread().getId( ) + 
				" commencing run cycle at " +  getDateFormat( ).format(new Date( )) );
		}
	}
	
	/**
	 * @return new or existing date format
	 */
	private DateFormat getDateFormat( ) {
		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat("k:m:s");
		}
		return dateFormat;
	}


}
