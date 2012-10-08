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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;

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
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatusInfo;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.modeldb.ResultSetCrawler;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.solver.VCSimulationIdentifier;

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
	private VCMessageSession dispatcherQueueSession = null;

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

//	class SimulationMonitorThread extends Thread {
//
//		public SimulationMonitorThread() {
//			super();
//			setDaemon(true);
//			setName("Simulation Monitor Thread");
//		}
//		
//		public void run() {
//
//			while (true){
//				
//				//
//				// for first 10 minutes of dispatcher uptime, don't check for obsolete messages.
//				// as a startup transient, let the dispatchers catch up with worker messages before passing
//				// judgement on the health of jobs.
//				//
//				// a better way of doing it is to wait until the worker messages have caught-up (message.date > startup.date).
//				//
//				// also, now that things are in memory, we can check memory for those jobs that are not well behaved.
//				//
//				long uptime = System.currentTimeMillis() - VCMongoMessage.getServiceStartupTime();
//				final int UPTIME_WAIT = 1000*60*10;
//				if (uptime < UPTIME_WAIT){
//					try {
//						Thread.sleep(UPTIME_WAIT - uptime);
//					}catch (Exception e){
//					}
//					continue;  // for first 10 minutes of uptime, don't obsolete any jobs
//				}
//				
//				SimulationJobStatus jobStatus = simulationDatabase.getNextObsoleteSimulation(MessageConstants.INTERVAL_DATABASE_SERVER_FAIL);								
//				PbsJobID pbsJobID = jobStatus.getSimulationExecutionStatus().getPbsJobID();
//				if (pbsJobID!=null){
//					PBSUtils.killJob(pbsJobID);
//				}
//
//				// too many retries
//				if ((jobStatus.getTaskID() & MessageConstants.TASKID_RETRYCOUNTER_MASK) >= MessageConstants.TASKID_MAX_RETRIES) {							
//					log.print("##MT too many retries " + jobStatus);
//
//					// new job status is failed.
//					SimulationJobStatus	newJobStatus = new SimulationJobStatus(VCellServerID.getSystemServerID(), jobStatus.getVCSimulationIdentifier(), jobStatus.getJobIndex(), jobStatus.getSubmitDate(),
//						SchedulerStatus.FAILED, jobStatus.getTaskID(),
//						SimulationMessage.MESSAGE_JOB_FAILED_TOOMANYRETRIES,
//						jobStatus.getSimulationQueueEntryStatus(), jobStatus.getSimulationExecutionStatus());
//					//update the database
//					this.simulationDispatcherMessaging.jobAdminXA.updateSimulationJobStatus(obsoleteJobDbConnection.getConnection(), jobStatus, newJobStatus);
//					// tell client
//					StatusMessage statusMsg = new StatusMessage(newJobStatus, jobStatus.getVCSimulationIdentifier().getOwner().getName(), null, null);
//					statusMsg.sendToClient(obsoleteJobDispatcher);
//					
//				} else {
//					SimulationTask simTask = this.simulationDispatcherMessaging.simDispatcher.getSimulationTask(jobStatus);
//					
//					log.print("##MT requeued " + simTask);
//
//					// increment taskid, new job status is queued
//					SimulationJobStatus newJobStatus = new SimulationJobStatus(VCellServerID.getSystemServerID(), jobStatus.getVCSimulationIdentifier(), jobStatus.getJobIndex(), jobStatus.getSubmitDate(), 
//						SchedulerStatus.QUEUED, jobStatus.getTaskID() + 1, 
//						SimulationMessage.MESSAGE_JOB_QUEUED_RETRY, jobStatus.getSimulationQueueEntryStatus(), null);
//					
//					//update the database
//					this.simulationDispatcherMessaging.jobAdminXA.updateSimulationJobStatus(obsoleteJobDbConnection.getConnection(), jobStatus, newJobStatus);
//					// send to simulation queue
//					Simulation sim = simTask.getSimulationJob().getSimulation();
//					SimulationTask newSimTask = new SimulationTask(new SimulationJob(sim, newJobStatus.getJobIndex(), this.simulationDispatcherMessaging.simDispatcher.getFieldDataIdentifierSpecs(sim)), newJobStatus.getTaskID());
//					SimulationTaskMessage taskMsg = new SimulationTaskMessage(newSimTask);
//					taskMsg.sendSimulationTask(obsoleteJobDispatcher);
//					// tell client
//					StatusMessage statusMsg = new StatusMessage(newJobStatus, newSimTask.getUserName(), null, null);
//					statusMsg.sendToClient(obsoleteJobDispatcher);
//				}
//				// start next check after some time
//				try {
//					sleep(MessageConstants.INTERVAL_PING_SERVER);
//				} catch (InterruptedException ex) {
//					log.exception(ex);
//				}
//				
//			} // first while (true);
//		}
//	}

	/**
	 * Scheduler constructor comment.
	 */
	public SimulationDispatcher(VCMessagingService vcMessagingService, ServiceInstanceStatus serviceInstanceStatus, SimulationDatabase simulationDatabase, SessionLog log) throws Exception {
		super(vcMessagingService,serviceInstanceStatus,log);
		this.simulationDatabase = simulationDatabase;
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
		if (args.length < 1) {
			System.out.println("Missing arguments: " + SimulationDispatcher.class.getName() + " serviceOrdinal [logdir]");
			System.exit(1);
		}

		try {
			PropertyLoader.loadProperties();		

			int serviceOrdinal = Integer.parseInt(args[0]);
			String logdir = null;
			if (args.length > 1) {
				logdir = args[1];
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

			SimulationDispatcher simulationDispatcher = new SimulationDispatcher(vcMessagingService, serviceInstanceStatus, simulationDatabase, log);
			simulationDispatcher.init();
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		}
	}


}
