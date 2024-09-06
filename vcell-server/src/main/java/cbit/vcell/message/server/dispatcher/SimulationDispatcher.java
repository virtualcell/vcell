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

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.*;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ServerMessagingDelegate;
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
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.*;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
import com.google.gson.Gson;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;
import org.vcell.util.exe.ExecutableException;

import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class SimulationDispatcher {
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
	public final static long QUEUE_FLUSH_WAITIME = MessageConstants.MINUTE_IN_MS*5;

	private final VCMessagingService vcMessagingService_int;
	private final VCMessagingService vcMessagingService_sim;

	private final SimulationDatabase simulationDatabase;
	private final VCQueueConsumer workerEventConsumer_sim;
	private final VCQueueConsumer simRequestConsumer_int;
	private final VCRpcMessageHandler rpcMessageHandler_int;

	protected final SimulationDispatcherEngine simDispatcherEngine = new SimulationDispatcherEngine();

	protected final DispatchThread dispatchThread;
	protected final SimulationMonitor simMonitor;
	private final VCMessageSession dispatcherQueueSession_int;
	private final VCMessageSession clientStatusTopicSession_int;
	private final VCMessageSession simMonitorThreadSession_sim;

	private final HtcProxy htcProxy;
	public static Logger lg = LogManager.getLogger(SimulationDispatcher.class);
	public final SimulationService simServiceImpl;

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
					synchronized (dispatchThread.dispatcherNotifyObject){
						dispatchThread.dispatcherNotifyObject.notify();
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
			if (simStatus.getVCSimulationIdentifier().getOwner().equals(user) || user.getName().equals(PropertyLoader.ADMINISTRATOR_USERID)){
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
					if (simStatus.getVCSimulationIdentifier().getOwner().equals(user) || user.getName().equals(PropertyLoader.ADMINISTRATOR_USERID)){
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
					if (simStatus.simulationMetadata.vcSimID.getOwner().equals(user) || user.getName().equals(PropertyLoader.ADMINISTRATOR_USERID)){
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

		final Object dispatcherNotifyObject = new Object();
		final Object finishListener = new Object(); //used for tests

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
                                    lg.debug("dispatching simKey={}, jobId={}, taskId={}", vcSimID, jobStatus.getJobIndex(), jobStatus.getTaskID());
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
				finally {
					synchronized (finishListener){
						finishListener.notify();
					}
				}

				// if there are no messages or no qualified jobs or exceptions, sleep for a few seconds while
				// this will be interrupted if there is a start request.
				if (!bDispatchedAnyJobs){
					synchronized (dispatcherNotifyObject) {
						try {
							long waitTime = 5 * MessageConstants.SECOND_IN_MS;
							dispatcherNotifyObject.wait(waitTime);
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
		protected final ScheduledThreadPoolExecutor executor;
		private int threadCount;
		ZombieKiller initialZombieKiller = new ZombieKiller();
		QueueFlusher initialQueueFlusher = new QueueFlusher();
		/**
		 * synchronizes {@link SimulationDispatcher#onWorkerEventMessage(VCMessage, VCMessageSession)} and
		 * {@link QueueFlusher#flushWorkerEventQueue()}
		 */
		final Object monitorNotifyObject = new Object();

		public SimulationMonitor( ) {
			threadCount = 1;
			executor =  new ScheduledThreadPoolExecutor(2,this,this);
			executor.scheduleAtFixedRate(initialZombieKiller, 0, ZOMBIE_MINUTES, TimeUnit.MINUTES);
			executor.scheduleAtFixedRate(initialQueueFlusher, 1,FLUSH_QUEUE_MINUTES,TimeUnit.MINUTES);
		}

		/**
		 * find and kill zombie processes
		 */
		class ZombieKiller implements Runnable {
			public static final String noJob = "no jobStatus found in database for running htc job";
			public static final String newJobFound = "newer task found in database for running htc job";
			public static final String jobIsAlreadyDone = "jobStatus Done in database for running htc job";
			@Override
			public void run() {
				try {
					traceThread(this);
					Map<HtcJobInfo, HtcJobStatus> runningJobs = htcProxy.getRunningJobs();
					for (HtcJobInfo htcJobInfo : runningJobs.keySet()){
						try {
							String simJobName = htcJobInfo.getJobName();
							if (!simJobName.startsWith(HtcProxy.JOB_NAME_PREFIX_SIMULATION)){
								continue;
							}
							HtcProxy.SimTaskInfo simTaskInfo = HtcProxy.getSimTaskInfoFromSimJobName(simJobName);
							SimulationJobStatus simJobStatus = simulationDatabase.getLatestSimulationJobStatus(simTaskInfo.simId, simTaskInfo.jobIndex);
							String failureMessage = null;
							boolean killJob = false;
							if (simJobStatus==null){
								failureMessage = noJob;
								killJob = true;
							}else if (simTaskInfo.taskId < simJobStatus.getTaskID()){
								failureMessage = newJobFound;
								killJob = true;
							}else if (simJobStatus.getSchedulerStatus().isDone()){
								failureMessage = jobIsAlreadyDone;
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
							if (killJob && HtcProxy.isMySimulationJob(htcJobInfo)){
								if (lg.isWarnEnabled()) {
                                    lg.warn("killing {}; {}; {}", htcJobInfo, failureMessage, simJobStatus);
								}
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
			protected final static String timeOutFailure = "failed: timed out";
			protected final static String unreferencedFailure = "failed: unreferenced simulation";
			protected final Object finishListener = new Object(); //used for tests
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
				} finally {
					synchronized (finishListener){
						finishListener.notify();
					}
				}
			}
			
			private void flushWorkerEventQueue() throws VCMessagingException{
				VCMessage message = simMonitorThreadSession_sim.createObjectMessage(VCMongoMessage.getServiceStartupTime());
				message.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY,MessageConstants.MESSAGE_TYPE_FLUSH_VALUE);
				synchronized (monitorNotifyObject) {
					simMonitorThreadSession_sim.sendQueueMessage(VCellQueue.WorkerEventQueue, message, false, MessageConstants.MINUTE_IN_MS*5L);
					try {
						long startWaitTime = System.currentTimeMillis();
						monitorNotifyObject.wait(QUEUE_FLUSH_WAITIME);
						long endWaitTime = System.currentTimeMillis();
						long elapsedFlushTime = endWaitTime-startWaitTime;
                        lg.info("flushed worker event queue: elapsedTime={} s", elapsedFlushTime / 1000.0);
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
						String failureMessage = (bTimedOutSimulation) ? timeOutFailure : unreferencedFailure;
                        lg.info("obsolete job detected at timestampMS={}, status={}", currentTimeMS, activeJobStatus);
						//SimulationStateMachine simStateMachine = simDispatcherEngine.getSimulationStateMachine(activeJobStatus.getVCSimulationIdentifier().getSimulationKey(), activeJobStatus.getJobIndex());
						//					lg.debug(simStateMachine.show());
						lg.warn("{} {}", activeJobStatus, failureMessage);
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

	public static SimulationDispatcher simulationDispatcherCreator(SimulationDatabase simulationDatabase, VCMessagingService messagingServiceInternal,
																   VCMessagingService messagingServiceSim, HtcProxy htcProxy, boolean startDispatcher){
		return new SimulationDispatcher(simulationDatabase, messagingServiceInternal, messagingServiceSim, htcProxy, startDispatcher);
	}

	public static SimulationDispatcher simulationDispatcherCreator() throws SQLException, DataAccessException {
		ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory();
		KeyFactory keyFactory = conFactory.getKeyFactory();
		DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory);
		AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory);
		SimulationDatabase simulationDatabase = new SimulationDatabaseDirect(adminDbTopLevel, databaseServerImpl, true);

		VCMessagingService vcMessagingServiceInternal = new VCMessagingServiceActiveMQ();
		String jmshost_int = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
		int jmsport_int = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
		vcMessagingServiceInternal.setConfiguration(new ServerMessagingDelegate(), jmshost_int, jmsport_int);

		VCMessagingService vcMessagingServiceSim = new VCMessagingServiceActiveMQ();
		String jmshost_sim = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostInternal);
		int jmsport_sim = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortInternal));
		vcMessagingServiceSim.setConfiguration(new ServerMessagingDelegate(), jmshost_sim, jmsport_sim);

		return SimulationDispatcher.simulationDispatcherCreator(simulationDatabase,
				vcMessagingServiceInternal, vcMessagingServiceSim, SlurmProxy.createRemoteProxy(), true);
	}

	private SimulationDispatcher(SimulationDatabase simulationDatabase, VCMessagingService messagingServiceInternal,
								 VCMessagingService messagingServiceSim, HtcProxy htcProxy, boolean startDispatcher){
		this.simulationDatabase = simulationDatabase;
		this.vcMessagingService_int = messagingServiceInternal;
		this.vcMessagingService_sim = messagingServiceSim;

		QueueListener workerEventListener = new QueueListener() {
			public void onQueueMessage(VCMessage vcMessage, VCMessageSession session) throws RollbackException {
				onWorkerEventMessage(vcMessage, session);
			}
		};
		VCMessageSelector workerEventSelector = null;
		String threadName = "Worker Event Consumer";
		workerEventConsumer_sim = new VCQueueConsumer(VCellQueue.WorkerEventQueue, workerEventListener, workerEventSelector, threadName, MessageConstants.PREFETCH_LIMIT_WORKER_EVENT);
		this.vcMessagingService_sim.addMessageConsumer(workerEventConsumer_sim);

		//
		// set up consumer for Simulation Request (non-blocking RPC) messages
		//
		simServiceImpl = new SimulationServiceImpl();

		VCMessageSelector simRequestSelector = null;
		threadName = "Sim Request Consumer";
		this.rpcMessageHandler_int = new VCRpcMessageHandler(simServiceImpl, VCellQueue.SimReqQueue);

		simRequestConsumer_int = new VCQueueConsumer(VCellQueue.SimReqQueue, rpcMessageHandler_int, simRequestSelector, threadName, MessageConstants.PREFETCH_LIMIT_SIM_REQUEST);
		this.vcMessagingService_int.addMessageConsumer(simRequestConsumer_int);

		this.dispatcherQueueSession_int = this.vcMessagingService_int.createProducerSession();
		this.clientStatusTopicSession_int = this.vcMessagingService_int.createProducerSession();


		this.simMonitorThreadSession_sim = this.vcMessagingService_sim.createProducerSession();
		this.htcProxy = htcProxy;

		// Wait until all resources are created to start separate threads

		this.simMonitor = new SimulationMonitor();
		this.dispatchThread = new DispatchThread();
		if (startDispatcher){
			this.dispatchThread.start();
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
			if (vcMessage.propertyExists(VCMessagingConstants.MESSAGE_TYPE_PROPERTY) && vcMessage.getStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY).equals(MessageConstants.MESSAGE_TYPE_FLUSH_VALUE)){
				if (simMonitor!=null){
					try {
						synchronized (simMonitor.monitorNotifyObject){
							simMonitor.monitorNotifyObject.notify();
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
				" commencing run cycle at " +  new SimpleDateFormat("k:m:s").format(new Date( )) );
		}
	}

}
