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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;
import org.vcell.util.exe.ExecutableException;

import com.google.gson.Gson;

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
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.bootstrap.ServiceType;
import cbit.vcell.message.server.dispatcher.BatchScheduler.ActiveJob;
import cbit.vcell.message.server.dispatcher.BatchScheduler.SchedulerDecisions;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;
import cbit.vcell.message.server.htc.HtcProxy.PartitionStatistics;
import cbit.vcell.message.server.htc.slurm.SlurmProxy;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.server.SimulationService;
import cbit.vcell.server.SimulationStatus;
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
	public static final int ZOMBIE_MINUTES = 1; 
	/**
	 * minutes between queue flushing
	 */
	public static final int FLUSH_QUEUE_MINUTES = 4; 
	/**
	 * queue flush wait time
	 */
	public static final long QUEUE_FLUSH_WAITIME = MessageConstants.MINUTE_IN_MS*5;

	private SimulationDatabase simulationDatabase = null;
	private VCQueueConsumer workerEventConsumer_sim = null;
	private VCQueueConsumer simRequestConsumer_int = null;
	private VCRpcMessageHandler rpcMessageHandler_int = null;

	private SimulationDispatcherEngine simDispatcherEngine = new SimulationDispatcherEngine();

	private DispatchThread dispatchThread = null;
	private SimulationMonitor simMonitor = null;
	private VCMessageSession dispatcherQueueSession_int = null;
	private VCMessageSession clientStatusTopicSession_int = null;
	private VCMessageSession simMonitorThreadSession_sim = null;

	private HtcProxy htcProxy = null;
	/**
	 * format for logging. Lazily created on {@link #getDateFormat()}
	 */
	private DateFormat dateFormat = null;
	public static Logger lg = LogManager.getLogger(SimulationDispatcher.class);

	public class SimulationServiceImpl implements SimulationService {

		@Override
		public SimulationStatus stopSimulation(User user, VCSimulationIdentifier vcSimulationIdentifier) throws DataAccessException {
			if (lg.isDebugEnabled()) {
				lg.debug("stop simulation requested for "+vcSimulationIdentifier);
			}
			try {
				simDispatcherEngine.onStopRequest(vcSimulationIdentifier, user, simulationDatabase, dispatcherQueueSession_int);
			} catch (VCMessagingException | SQLException e) {
				lg.error("failed to stop simulation "+vcSimulationIdentifier, e);
				throw new DataAccessException(e.getMessage(),e);
			}
			SimulationStatus simulationStatus = simulationDatabase.getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
			if (lg.isDebugEnabled()) {
				lg.debug("stopped simulation processed for "+vcSimulationIdentifier+", status="+simulationStatus);
			}
			
			try {
				String htcJobNameSubstring = "_"+vcSimulationIdentifier.getSimulationKey()+"_";
				if (lg.isTraceEnabled()) lg.trace("killing batch jobs with jobName substring of "+htcJobNameSubstring);
				htcProxy.killJobs(htcJobNameSubstring);
				if (lg.isTraceEnabled()) lg.trace("killed batch jobs with jobName substring of "+htcJobNameSubstring);
			}catch (Exception e) {
				lg.error("failed to kill one or more simulation jobs for Simid "+vcSimulationIdentifier.getSimulationKey(), e);
			}
			return simulationStatus;
		}

		@Override
		public SimulationStatus startSimulation(User user, VCSimulationIdentifier vcSimulationIdentifier,int numSimulationScanJobs) throws DataAccessException {
			if (lg.isDebugEnabled()) {
				lg.debug("start simulation requested for "+vcSimulationIdentifier+" with "+numSimulationScanJobs+" jobs");
			}
			try {
				simDispatcherEngine.onStartRequest(vcSimulationIdentifier, user, numSimulationScanJobs, simulationDatabase, dispatcherQueueSession_int, dispatcherQueueSession_int);
			} catch (VCMessagingException | SQLException e1) {
				lg.error("failed to start simulation "+vcSimulationIdentifier, e1);
				throw new DataAccessException(e1.getMessage(),e1);
			}

			// wake up dispatcher thread
			if (dispatchThread!=null){
				try {
					synchronized (dispatchThread.notifyObject){
						dispatchThread.notifyObject.notify();
					}
				}catch (IllegalMonitorStateException e){
					lg.error("failed to notify dispatchThread",e);
				}
			}
			SimulationStatus simulationStatus = simulationDatabase.getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
			if (lg.isDebugEnabled()) {
				lg.debug("start simulation processed for "+vcSimulationIdentifier+", status="+simulationStatus);
			}
			return simulationStatus;
		}

		@Override
		public SimulationStatus getSimulationStatus(User user, KeyValue simulationKey) throws DataAccessException {
			if (lg.isDebugEnabled()) {
				lg.debug("getting simulation status for sim "+simulationKey+" for user "+user);
			}
			SimulationStatus simStatus = simulationDatabase.getSimulationStatus(simulationKey);
			if (simStatus.getVCSimulationIdentifier().getOwner().equals(user) || user.getName().equals(PropertyLoader.ADMINISTRATOR_ACCOUNT)){
				if (lg.isDebugEnabled()) {
					lg.debug("simulation status for sim "+simulationKey+" is '"+simStatus+"'");
				}
				return simStatus;
			}else{
				lg.error("User "+user.getName()+" doesn't have access to simulation "+simulationKey);
				throw new PermissionException("User "+user.getName()+" doesn't have access to simulation "+simulationKey);
			}
		}

		@Override
		public SimulationStatus[] getSimulationStatus(User user, KeyValue[] simKeys) throws DataAccessException {
			if (lg.isDebugEnabled()) {
				lg.debug("getting simulation status for sims "+Arrays.asList(simKeys)+" for user "+user);
			}
			SimulationStatus[] simStatusArray = simulationDatabase.getSimulationStatus(simKeys);
			for (SimulationStatus simStatus : simStatusArray){
				if (simStatus!=null){
					if (simStatus.getVCSimulationIdentifier().getOwner().equals(user) || user.getName().equals(PropertyLoader.ADMINISTRATOR_ACCOUNT)){
						continue;
					}
					lg.error("User "+user.getName()+" doesn't have access to simulation "+simStatus.getVCSimulationIdentifier().getSimulationKey());
					//throw new PermissionException("User "+user.getName()+" doesn't have access to simulation "+simStatus.getVCSimulationIdentifier().getSimulationKey());
				}
			}
			return simStatusArray;
		}

		@Override
		public SimpleJobStatus[] getSimpleJobStatus(User user, SimpleJobStatusQuerySpec simJobStatusQuerySpec) throws DataAccessException {
			if (lg.isDebugEnabled()) {
				Gson gson = new Gson();
				lg.debug("getting simpleJobStatus for query "+gson.toJson(simJobStatusQuerySpec)+" for user "+user);
			}
			SimpleJobStatus[] simpleJobStatusArray = simulationDatabase.getSimpleJobStatus(user,simJobStatusQuerySpec);
			for (SimpleJobStatus simStatus : simpleJobStatusArray){
				if (simStatus!=null){
					if (simStatus.simulationMetadata.vcSimID.getOwner().equals(user) || user.getName().equals(PropertyLoader.ADMINISTRATOR_ACCOUNT)){
						continue;
					}
					lg.error("User "+user.getName()+" doesn't have access to simulation "+simStatus.simulationMetadata.vcSimID.getSimulationKey());
					//throw new PermissionException("User "+user.getName()+" doesn't have access to simulation "+simStatus.simulationMetadata.vcSimID.getSimulationKey());
				}
			}
			return simpleJobStatusArray;
		}
	};

	private User[] quotaExemptUsers;//TreeMap<User.SPECIALS,TreeMap<User,String>> specialUsers;
	private long lastSpecialUserCheck;
	private void reloadSpecialUsers() {
		try {
			ArrayList<User> adminUserList = new ArrayList<User>();
			TreeMap<User.SPECIAL_CLAIM,TreeMap<User,String>> specialUsers = simulationDatabase.getSpecialUsers();
			final Iterator<User.SPECIAL_CLAIM> iterator = specialUsers.keySet().iterator();
			while(iterator.hasNext()) {
				final User.SPECIAL_CLAIM next = iterator.next();
				if(next == User.SPECIAL_CLAIM.admins) {//Admin Users
					final Iterator<User> iter = specialUsers.get(next).keySet().iterator();
					while(iter.hasNext()) {
						adminUserList.add(iter.next());
					}
					break;
				}
			}
			quotaExemptUsers = adminUserList.toArray(new User[0]);
		} catch (Exception e1) {
			lg.error(e1.getMessage(), e1);
		}
		lastSpecialUserCheck = System.currentTimeMillis();
	}
	public class DispatchThread extends Thread {

		Object notifyObject = new Object();

		public DispatchThread() {
			super();
			setDaemon(true);
			setName("Simulation Dispatch Thread");
		}

		public void run() {
			reloadSpecialUsers();
			while (true) {
				if((System.currentTimeMillis()-lastSpecialUserCheck) > (5*60*1000)) {
					reloadSpecialUsers();
				}
				boolean bDispatchedAnyJobs = false;

				try {
					final SimulationJobStatus[] allActiveJobsAllSites = simulationDatabase.getActiveJobs(null);
					Set<KeyValue> simKeys = new LinkedHashSet<KeyValue>(); //Linked hash set maintains insertion order
					for (SimulationJobStatus simJobStatus : allActiveJobsAllSites){
						KeyValue simKey = simJobStatus.getVCSimulationIdentifier().getSimulationKey();
						if (!simKeys.contains(simKey)){
							simKeys.add(simKey);
						}
					}
					if (lg.isDebugEnabled()) {
						int numRunning = 0;
						int numWaiting = 0;
						int numDispatched = 0;
						int numQueued = 0;
						for (SimulationJobStatus simJobStatus : allActiveJobsAllSites) {
							if (simJobStatus.getSchedulerStatus().isRunning()) numRunning++;
							if (simJobStatus.getSchedulerStatus().isWaiting()) numWaiting++;
							if (simJobStatus.getSchedulerStatus().isDispatched()) numDispatched++;
							if (simJobStatus.getSchedulerStatus().isQueued()) numQueued++;
						}
						if (numWaiting>0 || numDispatched>0 || numQueued>0){
							lg.debug("Dispatcher starting, database shows "+
										allActiveJobsAllSites.length+" active sim jobs ("+numWaiting+" waiting,"+numDispatched+" dispatched,"+numQueued+" queued,"+numRunning+" running)" +
										" with "+simKeys.size()+" unique sims for this site");
						}
					}
					if (allActiveJobsAllSites != null && allActiveJobsAllSites.length > 0) {
						PartitionStatistics partitionStatistics = htcProxy.getPartitionStatistics();
						int maxOdePerUser = BatchScheduler.getMaxOdeJobsPerUser();
						int maxPdePerUser = BatchScheduler.getMaxPdeJobsPerUser();
						VCellServerID serverID = VCellServerID.getSystemServerID();

						final Map<KeyValue,SimulationRequirements> simulationRequirementsMap = simulationDatabase.getSimulationRequirements(simKeys);
						ArrayList<BatchScheduler.ActiveJob> activeJobs = new ArrayList<BatchScheduler.ActiveJob>();
						for (SimulationJobStatus simJobStatus : allActiveJobsAllSites) {
							SimulationRequirements simulationRequirements = simulationRequirementsMap.get(simJobStatus.getVCSimulationIdentifier().getSimulationKey());
							boolean isPDE = (simulationRequirements!=null)?(simulationRequirements.isPDE()):(false);
							BatchScheduler.ActiveJob activeJob = new BatchScheduler.ActiveJob(simJobStatus, isPDE);
							activeJobs.add(activeJob);
						}
						SchedulerDecisions schedulerDecisions = BatchScheduler.schedule(activeJobs, partitionStatistics, maxOdePerUser, maxPdePerUser, serverID,quotaExemptUsers);
						if (lg.isTraceEnabled()) {
							lg.trace("Dispatcher limits: partitionStatistics="+partitionStatistics+", maxOdePerUser="+maxOdePerUser+", maxPdePerUser="+maxPdePerUser);
							lg.trace(schedulerDecisions.getRunnableThisSite().size() + " runnable jobs to process");
						}

						//
						// temporarily save simulations during this dispatch iteration (to expedite dispatching multiple simulation jobs for same simulation).
						// cache is discarded after use.
						//
						HashMap<KeyValue,Simulation> tempSimulationMap = new HashMap<KeyValue,Simulation>();
						for (ActiveJob jobToRun : schedulerDecisions.getRunnableThisSite()){
							SimulationJobStatus jobStatus = (SimulationJobStatus)jobToRun.jobObject;
							VCSimulationIdentifier vcSimID = jobStatus.getVCSimulationIdentifier();
							try {
								final KeyValue simKey = vcSimID.getSimulationKey();
								Simulation sim = tempSimulationMap.get(simKey);
								if (sim==null){
									sim = simulationDatabase.getSimulation(vcSimID.getOwner(), simKey);
									tempSimulationMap.put(simKey, sim);
								}
								if (lg.isDebugEnabled()) {
									lg.debug("dispatching simKey="+vcSimID+", jobId="+jobStatus.getJobIndex()+", taskId="+jobStatus.getTaskID());
								}
								simDispatcherEngine.onDispatch(sim, jobStatus, simulationDatabase, dispatcherQueueSession_int);
								bDispatchedAnyJobs = true;
							} catch (Exception e) {
								lg.error("failed to dispatch simKey="+vcSimID+", jobId="+jobStatus.getJobIndex()+", taskId="+jobStatus.getTaskID(), e);
								final String failureMessage = FAILED_LOAD_MESSAGE + e.getMessage();
								simDispatcherEngine.onSystemAbort(jobStatus, failureMessage, simulationDatabase, clientStatusTopicSession_int);
							}
							Thread.yield();
						}
					}
				} catch (Exception ex) {
					lg.error(ex.getMessage(), ex);
				}

				// if there are no messages or no qualified jobs or exceptions, sleep for a few seconds while
				// this will be interrupted if there is a start request.
				if (!bDispatchedAnyJobs){
					synchronized (notifyObject) {
						try {
							long waitTime = 5 * MessageConstants.SECOND_IN_MS;
							notifyObject.wait(waitTime);
						} catch (InterruptedException ex) {
							lg.debug("Dispatch thread wait interrupted", ex);
						}
					}
				}else {
					try { 
						Thread.sleep(1000);
					}catch (InterruptedException e) {
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

					Map<HtcJobInfo, HtcJobStatus> runningJobs = htcProxy.getRunningJobs();
					for (HtcJobInfo htcJobInfo : runningJobs.keySet()){
						try {
							String simJobName = htcJobInfo.getJobName();
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
							if (killJob && HtcProxy.isMyJob(htcJobInfo)){
								if (lg.isWarnEnabled()) {
									lg.warn("killing " + htcJobInfo + ", " + failureMessage);
								}
								VCMongoMessage.sendZombieJob(simJobStatus,failureMessage,htcJobInfo.getHtcJobID());
								htcProxy.killJobSafe(htcJobInfo);
							}
						}catch (Exception e){
							lg.error(e.getMessage(), e);
						}
					}
				}
				catch (Exception e) {
					//in case of transient status failure, reset timestamps to avoid premature termination of 
					//jobs
					simDispatcherEngine.resetTimeStamps();
					lg.error(e.getMessage(), e);
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
					lg.error(e1.getMessage(), e1);
				}
			}
			
			private void flushWorkerEventQueue() throws VCMessagingException{
				VCMessage message = simMonitorThreadSession_sim.createObjectMessage(new Long(VCMongoMessage.getServiceStartupTime()));
				message.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY,MessageConstants.MESSAGE_TYPE_FLUSH_VALUE);
				synchronized (notifyObject) {
					simMonitorThreadSession_sim.sendQueueMessage(VCellQueue.WorkerEventQueue, message, false, MessageConstants.MINUTE_IN_MS*5L);
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
				SimulationJobStatus[] activeJobStatusArray = simulationDatabase.getActiveJobs(VCellServerID.getSystemServerID());
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
						lg.info("obsolete job detected at timestampMS="+currentTimeMS+", status=(" + activeJobStatus + ")");
						//SimulationStateMachine simStateMachine = simDispatcherEngine.getSimulationStateMachine(activeJobStatus.getVCSimulationIdentifier().getSimulationKey(), activeJobStatus.getJobIndex());
						//					lg.debug(simStateMachine.show());
						VCMongoMessage.sendObsoleteJob(activeJobStatus,failureMessage);
						simDispatcherEngine.onSystemAbort(activeJobStatus, failureMessage, simulationDatabase, clientStatusTopicSession_int);
						if (activeJobStatus.getSimulationExecutionStatus()!=null && activeJobStatus.getSimulationExecutionStatus().getHtcJobID()!=null){
							HtcJobID htcJobId = activeJobStatus.getSimulationExecutionStatus().getHtcJobID();
							try {
								htcProxy.killJobUnsafe(htcJobId);
							} catch (ExecutableException | HtcException e) {
								lg.error(e);
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
	public SimulationDispatcher(HtcProxy htcProxy, VCMessagingService vcMessagingService_int, VCMessagingService vcMessagingService_sim, ServiceInstanceStatus serviceInstanceStatus, SimulationDatabase simulationDatabase, boolean bSlaveMode) throws Exception {
		super(vcMessagingService_int, vcMessagingService_sim,serviceInstanceStatus,bSlaveMode);
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
		workerEventConsumer_sim = new VCQueueConsumer(VCellQueue.WorkerEventQueue, workerEventListener, workerEventSelector, threadName, MessageConstants.PREFETCH_LIMIT_WORKER_EVENT);
		vcMessagingService_sim.addMessageConsumer(workerEventConsumer_sim);

		//
		// set up consumer for Simulation Request (non-blocking RPC) messages
		//
		SimulationService simServiceImpl = new SimulationServiceImpl();

		VCMessageSelector simRequestSelector = null;
		threadName = "Sim Request Consumer";
		this.rpcMessageHandler_int = new VCRpcMessageHandler(simServiceImpl, VCellQueue.SimReqQueue);

		simRequestConsumer_int = new VCQueueConsumer(VCellQueue.SimReqQueue, rpcMessageHandler_int, simRequestSelector, threadName, MessageConstants.PREFETCH_LIMIT_SIM_REQUEST);
		vcMessagingService_int.addMessageConsumer(simRequestConsumer_int);

		this.dispatcherQueueSession_int = vcMessagingService_int.createProducerSession();
		this.clientStatusTopicSession_int = vcMessagingService_int.createProducerSession();

		this.dispatchThread = new DispatchThread();
		this.dispatchThread.start();

		this.simMonitorThreadSession_sim = vcMessagingService_sim.createProducerSession();
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
						lg.warn(e);
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
			simDispatcherEngine.onWorkerEvent(workerEvent, simulationDatabase, clientStatusTopicSession_int);

		} catch (Exception ex) {
			lg.error(ex.getMessage(), ex);
		}
	}
	
	/**
	 * log time and thread id if {@link #lg} {@link Logger#isTraceEnabled()} is true
	 * @param source used to print class name
	 */
	private void traceThread(Object source) {
		if (lg.isDebugEnabled()) {
			lg.debug(source.getClass( ).getName() + " thread id "  + Thread.currentThread().getId( ) + 
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


	public void stopService() {
	}
	
	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 */
	public static void main(java.lang.String[] args) {
		OperatingSystemInfo.getInstance();

		if (args.length != 0) {
			System.out.println("No arguments expected: " + SimulationDispatcher.class.getName());
			System.exit(1);
		}

		try {
			PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);
			HtcProxy htcProxy = SlurmProxy.createRemoteProxy();

			int serviceOrdinal = 99;
			VCMongoMessage.serviceStartup(ServiceName.dispatch, new Integer(serviceOrdinal), args);

			ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),
					ServiceType.DISPATCH, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);

			ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory();
			KeyFactory keyFactory = conFactory.getKeyFactory();
			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory);
			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory);
			SimulationDatabase simulationDatabase = new SimulationDatabaseDirect(adminDbTopLevel, databaseServerImpl, true);

			VCMessagingService vcMessagingService_int = new VCMessagingServiceActiveMQ();
    		String jmshost_int = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
    		int jmsport_int = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
			vcMessagingService_int.setConfiguration(new ServerMessagingDelegate(), jmshost_int, jmsport_int);
			
			VCMessagingService vcMessagingService_sim = new VCMessagingServiceActiveMQ();
			String jmshost_sim = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostInternal);
    		int jmsport_sim = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortInternal));
			vcMessagingService_sim.setConfiguration(new ServerMessagingDelegate(), jmshost_sim, jmsport_sim);

			SimulationDispatcher simulationDispatcher = new SimulationDispatcher(htcProxy, vcMessagingService_int, vcMessagingService_sim, serviceInstanceStatus, simulationDatabase, false);

			simulationDispatcher.init();
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
			PropertyLoader.slurm_partition
		};

}
