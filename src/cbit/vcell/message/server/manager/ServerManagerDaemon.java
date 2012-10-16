/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.manager;
import static cbit.vcell.message.server.ManageConstants.INTERVAL_PING_RESPONSE;
import static cbit.vcell.message.server.ManageConstants.INTERVAL_PING_SERVICE;
import static cbit.vcell.message.server.ManageConstants.MESSAGE_TYPE_ASKPERFORMANCESTATUS_VALUE;
import static cbit.vcell.message.server.ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE;
import static cbit.vcell.message.server.ManageConstants.MESSAGE_TYPE_ISSERVICEALIVE_VALUE;
import static cbit.vcell.message.server.ManageConstants.MESSAGE_TYPE_PROPERTY;
import static cbit.vcell.message.server.ManageConstants.MESSAGE_TYPE_REFRESHSERVERMANAGER_VALUE;
import static cbit.vcell.message.server.ManageConstants.MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE;
import static cbit.vcell.message.server.ManageConstants.MESSAGE_TYPE_STOPSERVICE_VALUE;
import static cbit.vcell.message.server.ManageConstants.SERVICE_ID_PROPERTY;
import static cbit.vcell.message.server.ManageConstants.SERVICE_STARTUPTYPE_AUTOMATIC;
import static cbit.vcell.message.server.ManageConstants.SERVICE_STATUS_FAILED;
import static cbit.vcell.message.server.ManageConstants.SERVICE_STATUS_RUNNING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.vcell.util.DataAccessException;
import org.vcell.util.ExecutableException;
import org.vcell.util.MessageConstants;
import org.vcell.util.MessageConstants.ServiceType;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.VCellServerID;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCTopicConsumer;
import cbit.vcell.message.VCTopicConsumer.TopicListener;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceStatus;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobID.BatchSystemType;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.pbs.PbsProxy;
import cbit.vcell.message.server.htc.sge.SgeProxy;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;

/**
 * Insert the type's description here.
 * Creation date: (8/7/2003 1:53:27 PM)
 * @author: Fei Gao
 */
public class ServerManagerDaemon {
	private org.vcell.util.SessionLog log = null;
	private HtcProxy htcProxy = null;
	private VCMessagingService vcMessagingService = null;
	private VCMessageSession topicProducerSession = null;
	private VCTopicConsumer daemonControlTopicConsumer = null;
	
	private List<ServiceInstanceStatus> serviceAliveList = Collections.synchronizedList(new ArrayList<ServiceInstanceStatus>());
	private List<ServiceStatus> serviceList = Collections.synchronizedList(new ArrayList<ServiceStatus>());
	
	private ServiceInstanceStatus serviceInstanceStatus = null;
	
	private AdminDBTopLevel adminDbTop = null;	
	private boolean bStopped = false;

/**
 * ServerManagerMessaging constructor comment.
 * @param log 
 * @param adminDbTop
 * @param vcMessagingService
 * @param serviceInstanceStatus
 */
public ServerManagerDaemon(HtcProxy htcProxy, ServiceInstanceStatus serviceInstanceStatus, VCMessagingService vcMessagingService, AdminDBTopLevel adminDbTop, SessionLog log) {
	this.serviceInstanceStatus = serviceInstanceStatus;
	this.vcMessagingService = vcMessagingService;
	this.adminDbTop = adminDbTop;
	this.log = log;
	this.htcProxy = htcProxy;
}

private void init() {
	TopicListener listener = new TopicListener(){

		public void onTopicMessage(VCMessage vcMessage, VCMessageSession session) {
			try {		
				log.print("onMessage [" + vcMessage.show() + "]");		

				String msgType = vcMessage.getStringProperty(MESSAGE_TYPE_PROPERTY);
				
				if (msgType.equals(MESSAGE_TYPE_ASKPERFORMANCESTATUS_VALUE)) {
					VCMessage reply = session.createObjectMessage(serviceInstanceStatus);
					reply.setStringProperty(MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE);
					reply.setStringProperty(SERVICE_ID_PROPERTY, serviceInstanceStatus.getID());
					session.sendTopicMessage(VCellTopic.DaemonControlTopic, reply);
					log.print("sending reply [" + reply.show() + "]");			
				} else if (msgType.equals(MESSAGE_TYPE_IAMALIVE_VALUE)) {
					on_iamalive(vcMessage);			
				} else if (msgType.equals(MESSAGE_TYPE_STOPSERVICE_VALUE)) {
					on_stopservice(vcMessage);
				} else if (msgType.equals(MESSAGE_TYPE_REFRESHSERVERMANAGER_VALUE)) {
					synchronized (this) {
						notify();
					}						
				}
			} catch (Exception ex) {
				log.exception(ex);
			}
		}
		
	};
	VCMessageSelector selector = vcMessagingService.createSelector(getMessageFilter());
	String threadName = "Daemon Control Topic Consumer";
	daemonControlTopicConsumer = new VCTopicConsumer(VCellTopic.DaemonControlTopic, listener, selector, threadName);
	vcMessagingService.addMessageConsumer(daemonControlTopicConsumer);
	
	topicProducerSession = vcMessagingService.createProducerSession();
}

private void startAllServices() throws SQLException, DataAccessException, VCMessagingException {
	log.print("Starting all the services");
	serviceList = Collections.synchronizedList(adminDbTop.getAllServiceStatus(true));	
	
	pingAll();	

	Iterator<ServiceStatus> iter = serviceList.iterator();
	while (iter.hasNext()) {
		ServiceStatus service = iter.next();		
		if (service.getServiceSpec().getStartupType() == SERVICE_STARTUPTYPE_AUTOMATIC) {			
			boolean alive = false;
			ServiceInstanceStatus foundSis = null; 
			Iterator<ServiceInstanceStatus> aliveIter = serviceAliveList.iterator();
			while (aliveIter.hasNext()) {
				ServiceInstanceStatus sis = aliveIter.next();
				if (sis.getSpecID().equals(service.getServiceSpec().getID())) {
					if (alive) { // there are more than instances for the same service
						if (foundSis.getStartDate().compareTo(sis.getStartDate()) > 0) { // kill the service with earlier start date
							stopService(sis);
						} else {
							stopService(foundSis);
							foundSis = sis;
						}
					} else {
						alive = true;
						foundSis = sis;
					}
				}
			}
			
			if (!alive) {
				try {
					startAService(service);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}	
}

private void stopService(ServiceInstanceStatus sis) {
	try {
		VCMessage msg = topicProducerSession.createMessage();
			
		msg.setStringProperty(MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_STOPSERVICE_VALUE);
		msg.setStringProperty(SERVICE_ID_PROPERTY, sis.getID());
		
		log.print("sending stop service message [" + msg.show() + "]");		
		topicProducerSession.sendTopicMessage(VCellTopic.DaemonControlTopic, msg);
		
	} catch (Exception ex) {
		ex.printStackTrace();
	}	
}

private void startAService(ServiceStatus service) throws UpdateSynchronizationException, SQLException {
	log.print("starting service " + service);
	AdminDBTopLevel.TransactionalServiceOperation tso = new AdminDBTopLevel.TransactionalServiceOperation() {
		public ServiceStatus doOperation(ServiceStatus oldStatus) throws Exception {
			HtcJobID jobid = submit2PBS(oldStatus);
			ServiceStatus newServiceStatus = null;
			if (jobid == null) {
				newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_FAILED, "unknown pbs exception",	jobid);
			} else {
				long t = System.currentTimeMillis();
				HtcJobStatus status;
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
					}
					
					status = htcProxy.getJobStatus(jobid);
					if (status!=null && status.isExiting()){
						// should never happen
						newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_FAILED, "exit immediately after submit", jobid);	
						break;
					} else if (status!=null && status.isRunning()) {						
						newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_RUNNING, "running", jobid);	
						break;
					} else if (System.currentTimeMillis() - t > 30 * MessageConstants.SECOND_IN_MS) {
						String pendingReason = htcProxy.getPendingReason(jobid);
						htcProxy.killJob(jobid); // kill the job if it takes too long to dispatch the job.
						newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_FAILED, 
								"PBS Job scheduler timed out. Please try again later. (Job [" + jobid + "]: " + pendingReason + ")",
								jobid);						
						break;
					}
				}
			} 			
			return newServiceStatus;
		}
	};
	
	adminDbTop.updateServiceStatus(service, tso, true);
}	

private HtcJobID submit2PBS(ServiceStatus service) throws IOException, ExecutableException, HtcException {
	killService(service);
	
	String executable = PropertyLoader.getRequiredProperty(PropertyLoader.serviceSubmitScript);
	
	ServiceType type = service.getServiceSpec().getType();
	int ordinal = service.getServiceSpec().getOrdinal();
	// site, type, ordinal, memory
	String[] command = new String[] { 
			executable, 
//			VCellServerID.getSystemServerID().toString().toLowerCase(), 
			type.getName(), 
			String.valueOf(ordinal), 
			String.valueOf(service.getServiceSpec().getMemoryMB()) };
	
	File sub_file = File.createTempFile("service", ".pbs.sub");
	log.print("PBS sub file  for service " + service.getServiceSpec() + " is " + sub_file.getName());
	return htcProxy.submitServiceJob(service.getServiceSpec().getID(), sub_file.getName(), command, 1, service.getServiceSpec().getMemoryMB());
}
/**
 * This method was created in VisualAge.
 * @return int
 */
private java.lang.String getMessageFilter() {
	return MESSAGE_TYPE_PROPERTY + " NOT IN (" 
		+ "'" + MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE + "'" 
		+ ")";
//		+ " OR (" + ManageConstants.MESSAGE_TYPE_PROPERTY + "='" + ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE + "'"
//		+ " AND " + ManageConstants.SERVER_NAME_PROPERTY + " IS NOT NULL"
//		+ ")";
}

/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:47:40 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {	
	try {
		if (args.length != 1 && args.length != 4) {
			System.out.println("Missing arguments: " + ServerManagerDaemon.class.getName() + " (logdir|-) [pbsServer pbsUser pbsPswd]");
			System.exit(1);
		}
		
		if (args.length == 1) {
			File logdir = new File(args[0]);
			if (!logdir.exists()) {
				throw new RuntimeException("Log directory doesn't exist");
			}
				
			// log file name:
			// hostname_A_Data_0.log : alpha first data on hostname
			// hostname_B_Db_0.log : beta first database on hostname
			// hostname_R_Export_0.log : rel first export on hostname
			File logfile = new File(logdir, "ServerManager_" + ManageUtils.getHostName() + ".log");
			java.io.PrintStream ps = new PrintStream(new FileOutputStream(logfile), true); // don't append
			System.setOut(ps);
			System.setErr(ps);			
		}

		org.vcell.util.PropertyLoader.loadProperties();
		CommandService commandService = null;
		if (args.length==4){
			String htcHost = args[1];
			String htcUser = args[2];
			String htcPswd = args[3];
			commandService = new CommandServiceSsh(htcHost,htcUser,htcPswd);
		}else{
			commandService = new CommandServiceLocal();
		}
		BatchSystemType batchSystemType = BatchSystemType.valueOf(PropertyLoader.getRequiredProperty(PropertyLoader.htcBatchSystemType));
		HtcProxy htcProxy = null;
		if (batchSystemType == BatchSystemType.PBS){
			htcProxy = new PbsProxy(commandService);
		}else if (batchSystemType == BatchSystemType.SGE){
			htcProxy = new SgeProxy(commandService);
		}else{
			throw new RuntimeException("unsupported batch system "+batchSystemType);
		}
		ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.SERVERMANAGER, 0, ManageUtils.getHostName(), new Date(), true); 
		SessionLog log = new StdoutSessionLog(serviceInstanceStatus.getID());
		ConnectionFactory conFactory = null;
		try {
			conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
		} catch (ClassNotFoundException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		} catch (InstantiationException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		KeyFactory keyFactory = new cbit.sql.OracleKeyFactory();	
		DbDriver.setKeyFactory(keyFactory);
		AdminDBTopLevel adminDbTop = new AdminDBTopLevel(conFactory,log);
		VCMessagingService vcMessagingService = VCMessagingService.createInstance();
		VCMongoMessage.serviceStartup(ServiceName.serverManager, new Integer(0), args);
		ServerManagerDaemon serverManagerDaemon = new ServerManagerDaemon(htcProxy, serviceInstanceStatus, vcMessagingService, adminDbTop, log);
		serverManagerDaemon.init();
		serverManagerDaemon.serviceMonitorLoop();
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		VCMongoMessage.sendException(exc);
		VCMongoMessage.flush();
		System.exit(1);
	}
}

private void on_iamalive(VCMessage message)  {
	Object obj = message.getObjectContent();
	if (obj instanceof ServiceInstanceStatus) {
		ServiceInstanceStatus status = (ServiceInstanceStatus)obj;			
		serviceAliveList.add(status);			
	}
}

/**
* if the serviceInstanceStatus is "me" (the serverManagerDaemon) ... exit(0)
* 
* if the serviceInstanceStatus is in my "serviceList", start a thread to wait and kill
* the corresponding PBS job after waiting a short amount of time.
*/
private void on_stopservice(VCMessage message) throws Exception {
	String serviceID = message.getStringProperty(SERVICE_ID_PROPERTY);
	
	if (serviceID != null) {
		if (serviceID.equals(serviceInstanceStatus.getID())) { // stop myself
			System.exit(0);
		}
		Iterator<ServiceStatus> iter = serviceList.iterator();
		while (iter.hasNext()) {
			ServiceStatus service = iter.next();		
			if (service.getServiceSpec().getID().equals(serviceID)) {
				final HtcJobID htcJobId = service.getHtcJobId();
				if (htcJobId != null){
					
					String threadName = "Kill Thread for ServiceID "+serviceID+", htcID: "+htcJobId.toDatabase();
					Runnable serviceKillTask = new Runnable() {
						@Override
						public void run() {
							try {
								HtcProxy threadsafeHtcProxy = htcProxy.cloneThreadsafe();
								HtcJobStatus jobStatus = threadsafeHtcProxy.getJobStatus(htcJobId);
								if (jobStatus!=null && jobStatus.isRunning()){
									try {
										Thread.sleep(5 * MessageConstants.SECOND_IN_MS); // wait 5 seconds
									} catch (InterruptedException ex) {							
									}					
									// if the service is not stopped, kill it from PBS
									jobStatus = threadsafeHtcProxy.getJobStatus(htcJobId);
									if (jobStatus!=null && jobStatus.isRunning()) {
										threadsafeHtcProxy.killJob(htcJobId);
									}
								}
							} catch (Exception e) {
								log.exception(e);
							}
						}
					};
					Thread killThread = new Thread(serviceKillTask, threadName);
					killThread.start();
					
					
				}
				break;
			}
		}
	}
}

/**
* onMessage method comment.
*/
private void pingAll() throws VCMessagingException {
	
	serviceAliveList.clear();
	
	VCMessage msg = topicProducerSession.createMessage();
		
	msg.setStringProperty(MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_ISSERVICEALIVE_VALUE);

	log.print("sending ping message [" + msg.show() + "]");
	
	topicProducerSession.sendTopicMessage(VCellTopic.DaemonControlTopic, msg);

	try {
		Thread.sleep(INTERVAL_PING_RESPONSE);
	} catch (InterruptedException ex) {
		ex.printStackTrace();
	}
}

private void killService(ServiceStatus service) throws ExecutableException, HtcJobNotFoundException, HtcException {
	 TreeMap<HtcJobID, String>  jobIdMapJobName = htcProxy.getRunningServiceJobIDs(VCellServerID.getSystemServerID());
	 HtcJobID foundJobID = null;
	 for(HtcJobID jobID : jobIdMapJobName.keySet()){
		 if(jobIdMapJobName.get(jobID).equals(service.getServiceSpec().getID())){
			 foundJobID = jobID;
			 break;
		 }
	 }
	 if(foundJobID == null){
		 return;
	 }
	 htcProxy.killJob(foundJobID);
	 long TIMEOUT = 60000;
	 long startTime = System.currentTimeMillis();
	 while((System.currentTimeMillis()-startTime) < TIMEOUT){
		 HtcJobStatus htcJobStatus = htcProxy.getJobStatus(foundJobID);
		 if(htcJobStatus == null || !htcJobStatus.isRunning()){
			 return;
		 }
		 try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 throw new HtcException("Timeout Error: failed to kill service "+service.getServiceSpec().getID()+" jobid="+foundJobID);
}
/**
* Insert the method's description here.
* Creation date: (8/7/2003 5:12:22 PM)
*/
public void serviceMonitorLoop() {
	while (!bStopped) {			
		try {
			startAllServices();
		} catch (Throwable exc) {
			log.exception(exc);
		}		
		try {
			synchronized (this) {			
				wait(INTERVAL_PING_SERVICE);
			}
		} catch (InterruptedException exc) {
		}			
	}
}
}
