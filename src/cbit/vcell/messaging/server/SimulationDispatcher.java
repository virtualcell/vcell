package cbit.vcell.messaging.server;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.rmi.event.WorkerEvent;
import javax.jms.*;

import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.MessageConstants;
import org.vcell.util.PropertyLoader;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.MessageConstants.ServiceType;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.solver.Simulation;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Vector;

import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.modeldb.LocalAdminDbServer;
import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.DBCacheTable;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.messaging.JmsClientMessaging;
import cbit.vcell.messaging.SimulationDispatcherMessaging;
import cbit.vcell.messaging.JmsSession;
import cbit.vcell.messaging.admin.ManageUtils;
import cbit.vcell.messaging.admin.ServiceInstanceStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.server.AdminDatabaseServerXA;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.messaging.WorkerEventMessage;
import cbit.vcell.messaging.StatusMessage;
import java.util.HashSet;
import cbit.vcell.modeldb.ResultSetCrawler;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class SimulationDispatcher extends AbstractJmsServiceProvider {
	private Map<User, RpcDbServerProxy> userDbServerMap = null;
	private ConnectionFactory conFactory = null;
	private KeyFactory keyFactory = null;
	private JmsClientMessaging clientMessaging = null;
	private LocalAdminDbServer adminDbServer = null;
	
	private boolean bStop = false;		
	private SimulationDispatcherMessaging dispatcherMessaging = null;
	private DBCacheTable simulationMap = null;
	private Map<KeyValue, User> simUserMap = Collections.synchronizedMap(new HashMap<KeyValue, User>());
	private Map<KeyValue, FieldDataIdentifierSpec[]> simFieldDataIDMap = Collections.synchronizedMap(new HashMap<KeyValue, FieldDataIdentifierSpec[]>());

	private MessagingDispatcherDbManager dispatcherDbManager = new JmsDispatcherDbManager();
	protected HashSet<VCSimulationDataIdentifier> resultSetSavedSet = new HashSet<VCSimulationDataIdentifier>();
	protected ResultSetCrawler rsCrawler = null;	

/**
 * Scheduler constructor comment.
 */
public SimulationDispatcher(int serviceOrdinal, String logdir) throws Exception {	
	serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID().toString(), 
			ServiceType.DISPATCH, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);	
	initLog(logdir);

	log = new StdoutSessionLog(serviceInstanceStatus.getID());
	
	conFactory = new OraclePoolingConnectionFactory(log);
	keyFactory = new OracleKeyFactory();		
	adminDbServer = new LocalAdminDbServer(conFactory,keyFactory,log);
	rsCrawler = new ResultSetCrawler(conFactory, adminDbServer, log, new DBCacheTable(1000*60*30));	

	dispatcherMessaging = new SimulationDispatcherMessaging(this, conFactory, keyFactory, log);	
}


private void dataMoved(VCSimulationDataIdentifier vcSimDataID, User user, double timepoint) {
	// called by data mover thread after successful move operations
	try {		
		if (!resultSetSavedSet.contains(vcSimDataID)){
			try {
				rsCrawler.updateSimResults(user,vcSimDataID);
				resultSetSavedSet.add(vcSimDataID);
			} catch (Throwable exc) {
				log.exception(exc);
			}
		}
	} catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/27/2003 3:15:57 PM)
 * @return cbit.vcell.server.UserMetaDbServer
 */
private RpcDbServerProxy getDbServerProxy(User user) throws JMSException {
	if (clientMessaging == null) {
		clientMessaging = new JmsClientMessaging(dispatcherMessaging.getJmsConnection(), log);
	}

	if (userDbServerMap == null) {
		userDbServerMap = Collections.synchronizedMap(new HashMap<User, RpcDbServerProxy>());
	}
		
	synchronized (userDbServerMap) {
		RpcDbServerProxy dbServer = (RpcDbServerProxy)userDbServerMap.get(user);
		
		if (dbServer == null) {
			dbServer = new RpcDbServerProxy(user, clientMessaging, log);
			userDbServerMap.put(user, dbServer);
		}

		return dbServer;		
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2003 2:56:43 PM)
 * @return cbit.vcell.server.User
 * @param simKey cbit.sql.KeyValue
 */
public FieldDataIdentifierSpec[] getFieldDataIdentifierSpecs(Simulation sim) throws DataAccessException, JMSException {
	try {		
		KeyValue simKey = sim.getKey();
		log.print("Get FieldDataIdentifierSpec for [" + simKey + "]");	
		FieldDataIdentifierSpec[] fieldDataIDSs = (FieldDataIdentifierSpec[])simFieldDataIDMap.get(simKey);

		if (fieldDataIDSs != null) {
			return fieldDataIDSs;
		}

		FieldFunctionArguments[] fieldFuncArgs =  sim.getMathDescription().getFieldFunctionArguments();
		if (fieldFuncArgs == null || fieldFuncArgs.length == 0) {
			return null;
		}
		
		RpcDbServerProxy dbServerProxy = getDbServerProxy(sim.getVersion().getOwner());		
		ExternalDataIdentifier[] externalDataIDs =
			dbServerProxy.fieldDataDBOperation(
					FieldDataDBOperationSpec.createGetExtDataIDsSpec(dbServerProxy.user)
			).extDataIDArr;
		if (externalDataIDs != null && externalDataIDs.length != 0 &&
			fieldFuncArgs != null && fieldFuncArgs.length>0	) {
			Vector<FieldDataIdentifierSpec> fieldDataIdV = new Vector<FieldDataIdentifierSpec>();
			for(int j=0;fieldFuncArgs != null && j<fieldFuncArgs.length;j+= 1){
				for(int i=0;i<externalDataIDs.length;i+= 1){
					if(externalDataIDs[i].getName().equals(fieldFuncArgs[j].getFieldName())){
						fieldDataIdV.add(
								new FieldDataIdentifierSpec(fieldFuncArgs[j],externalDataIDs[i])
								);
						break;
					}
				}
			}
			if(fieldDataIdV.size() > 0){
				fieldDataIDSs = new FieldDataIdentifierSpec[fieldDataIdV.size()];
				fieldDataIdV.copyInto(fieldDataIDSs);
			}
		}

		if (fieldDataIDSs != null){
			simFieldDataIDMap.put(simKey, fieldDataIDSs);		
		}
		
		return fieldDataIDSs;
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
		throw new DataAccessException(ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/2003 1:34:06 PM)
 * @return cbit.vcell.solver.Simulation
 * @param simInfo cbit.vcell.solver.SimulationInfo
 */
public Simulation getSimulation(User user, KeyValue simKey) throws JMSException, DataAccessException {
	if (simulationMap == null) {
		log.print("Initializaing DBCacheTable!");
		simulationMap = new DBCacheTable(3600 * 1000);
	}

	log.print("Get simulation [" + simKey + ","  + user + "]");	
	Simulation sim = (Simulation)simulationMap.getCloned(simKey);

	if (sim != null) {
		return sim;
	}

	RpcDbServerProxy dbServerProxy = getDbServerProxy(user);			
	BigString simstr = dbServerProxy.getSimulationXML(simKey);	

	if (simstr != null){
		try {
			sim = XmlHelper.XMLToSim(simstr.toString());
		}catch (XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}
		if (sim != null) {
			simulationMap.putProtected(simKey, sim);
		}
	}
	
	return sim;
}


/**
 * Insert the method's description here.
 * Creation date: (2/3/2004 8:34:36 AM)
 * @return cbit.vcell.messaging.SimulationTask
 * @param simKey cbit.sql.KeyValue
 */
public SimulationTask getSimulationTask(SimulationJobStatus jobStatus) throws DataAccessException, JMSException {
	VCSimulationIdentifier vcSimID = jobStatus.getVCSimulationIdentifier();
	User user = getUser(vcSimID.getSimulationKey(), null);				
	Simulation sim = getSimulation(user, vcSimID.getSimulationKey());
	SimulationTask simTask = new SimulationTask(new SimulationJob(sim, getFieldDataIdentifierSpecs(sim), jobStatus.getJobIndex()), jobStatus.getTaskID());

	return simTask;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2003 2:56:43 PM)
 * @return cbit.vcell.server.User
 * @param simKey cbit.sql.KeyValue
 */
public User getUser(KeyValue simKey, String username) throws DataAccessException {
	User user = null;

	synchronized(simUserMap) {
		user = (User)simUserMap.get(simKey);

		if (user != null && username != null && !user.getName().equals(username)) {
			throw new DataAccessException("Wrong user [" + user.getName() + "," + username + "] for the simulation [" + simKey + "]");
		}
			
		if (user == null) {
			if (username != null) {
				user = adminDbServer.getUser(username);
			} else {
				user = adminDbServer.getUserFromSimulationKey(simKey);
			}
			if (user != null) {
				simUserMap.put(simKey, user);
			}			
		}
	}
		
	return user;
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
		
		SimulationDispatcher simulationDispatcher = new SimulationDispatcher(serviceOrdinal, logdir);
		simulationDispatcher.start();
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 11:34:15 AM)
 */
public void onWorkerEventMessage(AdminDatabaseServerXA adminDbXA, java.sql.Connection con, JmsSession statusPublisher, Message receivedMsg) throws JMSException, DataAccessException {
	WorkerEventMessage workerEventMessage = null;
	
	try {
		workerEventMessage = new WorkerEventMessage(this, receivedMsg);
	} catch (RuntimeException ex) {
		// parse error, wrong message
		log.exception(ex);
		return;
	}
		
	WorkerEvent workerEvent = workerEventMessage.getWorkerEvent();
	String hostName = workerEvent.getHostName();
	String userName = workerEvent.getUserName(); // as the filter of the client
	int taskID = workerEvent.getTaskID();
	int jobIndex = workerEvent.getJobIndex();
	
	log.print("onWorkerEventMessage[" + workerEvent.getEventTypeID() + "," + workerEvent.getSimulationMessage() + "][job=" + jobIndex + "]");

	VCSimulationDataIdentifier vcSimDataID = workerEvent.getVCSimulationDataIdentifier();
	if (vcSimDataID == null) {
		return;
	}		
	KeyValue simKey = vcSimDataID.getSimulationKey();
	SimulationJobStatus oldJobStatus = adminDbXA.getSimulationJobStatus(con, simKey, jobIndex);	
	
	if (oldJobStatus == null || taskID != oldJobStatus.getTaskID() || oldJobStatus.isDone()){
		log.print("Outdated message: taskID=" + taskID + "::" + oldJobStatus);
		return;
	}	

	SimulationJobStatus newJobStatus = null;
	
	if (workerEvent.isAcceptedEvent()) {
		if (!oldJobStatus.isRunning()) {			
			newJobStatus = updateDispatchedStatus(oldJobStatus, adminDbXA, con, hostName, vcSimDataID.getVcSimID(), jobIndex, workerEvent.getSimulationMessage());
		}
		
	} else if (workerEvent.isStartingEvent()) {
		// only update database when the job event changes from started to runinng. The later progress event will not be recorded.
		SimulationMessage startMsg = workerEvent.getSimulationMessage();
		if (oldJobStatus.isQueued() || oldJobStatus.isDispatched()) {
			newJobStatus = updateRunningStatus(oldJobStatus, adminDbXA, con, hostName, vcSimDataID.getVcSimID(), jobIndex, false, startMsg);
		} else if (oldJobStatus.isRunning()) {
			newJobStatus = new SimulationJobStatus(oldJobStatus.getServerID(), oldJobStatus.getVCSimulationIdentifier(), oldJobStatus.getJobIndex(), oldJobStatus.getSubmitDate(), 
				oldJobStatus.getSchedulerStatus(), oldJobStatus.getTaskID(), startMsg, oldJobStatus.getSimulationQueueEntryStatus(), oldJobStatus.getSimulationExecutionStatus());
		}
		
	} else if (workerEvent.isNewDataEvent()) {
		if (workerEvent.getTimePoint() != null) {
			dataMoved(vcSimDataID, workerEvent.getUser(), workerEvent.getTimePoint().doubleValue());
			newJobStatus = updateRunningStatus(oldJobStatus, adminDbXA, con, hostName, vcSimDataID.getVcSimID(), jobIndex, true, workerEvent.getSimulationMessage());
		}
			
	} else if (workerEvent.isProgressEvent()) {
		newJobStatus = oldJobStatus;
		if (oldJobStatus.isQueued() || oldJobStatus.isDispatched()) {
			newJobStatus = updateRunningStatus(oldJobStatus, adminDbXA, con, hostName, vcSimDataID.getVcSimID(), jobIndex, false, workerEvent.getSimulationMessage());
		} else {
			updateLatestUpdateDate(oldJobStatus, adminDbXA, con, vcSimDataID.getVcSimID(), jobIndex, workerEvent.getSimulationMessage());
		}
		
	} else if (workerEvent.isCompletedEvent()) {			
		newJobStatus = updateEndStatus(oldJobStatus, adminDbXA, con, vcSimDataID.getVcSimID(), jobIndex, hostName, SimulationJobStatus.SCHEDULERSTATUS_COMPLETED, workerEvent.getSimulationMessage());

	} else if (workerEvent.isFailedEvent()) {						
		SimulationMessage failMsg = workerEvent.getSimulationMessage();
		newJobStatus = updateEndStatus(oldJobStatus, adminDbXA, con, vcSimDataID.getVcSimID(), jobIndex, hostName, SimulationJobStatus.SCHEDULERSTATUS_FAILED, failMsg);	
			
	} else if (workerEvent.isWorkerAliveEvent()) {
		if (oldJobStatus.isRunning()) {
			updateLatestUpdateDate(oldJobStatus, adminDbXA, con, vcSimDataID.getVcSimID(), jobIndex, workerEvent.getSimulationMessage());
		}
	}

	if (workerEvent.isStartingEvent() && newJobStatus != null) {
		StatusMessage msgForClient = new StatusMessage(newJobStatus, userName, null, null);
		msgForClient.sendToClient(statusPublisher);
		log.print("Send status to client: " + msgForClient);
	} else if (newJobStatus != null && (!newJobStatus.compareEqual(oldJobStatus) || workerEvent.isProgressEvent() || workerEvent.isNewDataEvent())) {		
		Double progress = workerEvent.getProgress();
		Double timepoint = workerEvent.getTimePoint();
		StatusMessage msgForClient = new StatusMessage(newJobStatus, userName, progress, timepoint);
		msgForClient.sendToClient(statusPublisher);
		log.print("Send status to client: " + msgForClient);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2003 12:07:28 PM)
 */
public final void start() throws JMSException {	
	log.print("Start PropertyLoader thread...");
	new PropertyLoaderThread().start();

	while (!bStop) {
		try {
			VCSimulationIdentifier vcSimID = dispatcherMessaging.processNextRequest();
			if (vcSimID != null) {
				continue;
			}
		} catch (Exception ex) {
			log.exception(ex);
		}
		
		try {
			Thread.sleep(2 * MessageConstants.SECOND);
		} catch (Exception ex) {
			log.exception(ex);
		}			
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:28:05 PM)
 */
public final void stop() {
	log.print(this.getClass().getName() + " ending");
	bStop = true;
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
private SimulationJobStatus updateDispatchedStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, java.sql.Connection con, String computeHost, VCSimulationIdentifier vcSimID, int jobIndex, SimulationMessage startMsg) throws DataAccessException, UpdateSynchronizationException {
//	log.print("updateDispatchedStatus[" + vcSimID + "][" + jobIndex + "]");
	return dispatcherDbManager.updateDispatchedStatus(oldJobStatus, adminDbXA, con, computeHost, vcSimID, jobIndex, startMsg);
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus updateEndStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, java.sql.Connection con, VCSimulationIdentifier vcSimID, int jobIndex, String hostName, int status, SimulationMessage solverMsg) throws DataAccessException, UpdateSynchronizationException {
//	log.print("updateEndStatus[" + vcSimID + "][" + jobIndex + "]");
	return dispatcherDbManager.updateEndStatus(oldJobStatus, adminDbXA, con, vcSimID, jobIndex, hostName, status, solverMsg);
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
private void updateLatestUpdateDate(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, java.sql.Connection con, 
		VCSimulationIdentifier vcSimID, int jobIndex, SimulationMessage simulationMessage) throws DataAccessException, UpdateSynchronizationException {
//	log.print("updateLatestUpdateDate[" + vcSimID + "][" + jobIndex + "]");
	dispatcherDbManager.updateLatestUpdateDate(oldJobStatus, adminDbXA, con, vcSimID, jobIndex, simulationMessage);
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus updateQueueStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, java.sql.Connection con, VCSimulationIdentifier vcSimID, int jobIndex, int queueID, int taskID, boolean firstSubmit) throws DataAccessException, UpdateSynchronizationException {
//	log.print("updateQueueStatus[" + vcSimID + "][" + jobIndex + "]");
	return dispatcherDbManager.updateQueueStatus(oldJobStatus, adminDbXA, con, vcSimID, jobIndex, queueID, taskID, firstSubmit);		// update SimulationJobTable
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
private SimulationJobStatus updateRunningStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, java.sql.Connection con, String hostName, VCSimulationIdentifier vcSimID, int jobIndex, boolean hasData, SimulationMessage solverMsg)	throws DataAccessException, UpdateSynchronizationException {
//	log.print("updateExeRunningStatus[" + vcSimID + "][" + jobIndex + "]");
	return dispatcherDbManager.updateRunningStatus(oldJobStatus, adminDbXA, con, hostName, vcSimID, jobIndex, hasData, solverMsg);
}
}