package cbit.vcell.messaging.server;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.FileNotFoundException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import cbit.gui.PropertyLoader;
import cbit.rmi.event.SimpleMessageHandler;
import cbit.util.DataAccessException;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.vcell.export.ExportEvent;
import cbit.vcell.export.ExportListener;
import cbit.vcell.messaging.JmsClientMessaging;
import cbit.vcell.messaging.JmsConnectionFactory;
import cbit.vcell.messaging.VCellQueueConnection;
import cbit.vcell.messaging.VCellTopicConnection;
import cbit.vcell.messaging.event.SimpleMessageServiceMessaging;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.LocalVCellServer;
import cbit.vcell.server.PerformanceMonitoringFacility;
import cbit.vcell.server.SimulationController;
import cbit.vcell.server.URLFinder;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.simdata.DataJobListener;

/**
 * The user's connection to the Virtual Cell.  It is obtained from the VCellServer
 * after the user has been authenticated.
 * Creation date: (Unknown)
 * @author: Jim Schaff.
 */
public class LocalVCellConnectionMessaging extends UnicastRemoteObject implements VCellConnection, ExportListener ,DataJobListener{
	
	private LocalDataSetControllerMessaging dataSetControllerMessaging = null;
	private LocalSimulationControllerMessaging simulationControllerMessaging = null;
	private LocalUserMetaDbServerMessaging userMetaDbServerMessaging = null;
	private cbit.vcell.messaging.event.SimpleMessageServiceMessaging messageService = null;
	private LocalBNGServiceMessaging bngServiceMessaging = null;

	private VCellTopicConnection topicConn = null;
	private VCellQueueConnection queueConn = null;
	
	private User fieldUser = null;
	private String fieldPassword = null;
	
	//
	// database resources
	//
	private static cbit.sql.ConnectionFactory conFactory = null;
	private static cbit.sql.KeyFactory keyFactory = null;
	private static cbit.sql.DBCacheTable dbCacheTable = null;

	
	private SessionLog fieldSessionLog = null;
	private LocalVCellServer fieldLocalVCellServer = null;
	private String fieldHost = null;

	private JmsClientMessaging dbClientMessaging = null;
	private JmsClientMessaging dataClientMessaging = null;
	private JmsClientMessaging simClientMessaging = null;
	private JmsClientMessaging bngClientMessaging = null;

/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public LocalVCellConnectionMessaging(User user, String password, String host, 
		SessionLog sessionLog, JmsConnectionFactory jmsConnFactory, LocalVCellServer aLocalVCellServer) 
		throws RemoteException, java.sql.SQLException, FileNotFoundException, javax.jms.JMSException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortVCellConnection,0));
	this.fieldUser = user;
	this.fieldPassword = password;
	this.fieldHost = host;
	this.fieldSessionLog = sessionLog;
	this.fieldLocalVCellServer = aLocalVCellServer;
	queueConn = jmsConnFactory.createQueueConnection();
	topicConn = jmsConnFactory.createTopicConnection();
	
	messageService = new SimpleMessageServiceMessaging(topicConn, user, sessionLog);	
	sessionLog.print("new LocalVCellConnection(" + user.getName() + ")");	
	fieldLocalVCellServer.getExportServiceImpl().addExportListener(this);

	PerformanceMonitoringFacility pmf = new PerformanceMonitoringFacility(user, sessionLog);
	getMessageService().getMessageDispatcher().addPerformanceMonitorListener(pmf);
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/16/2004 10:42:29 AM)
 */
public void close() throws java.rmi.RemoteException {
	messageService.close();
	try {
		queueConn.close();
		topicConn.close();
	} catch (javax.jms.JMSException ex) {
		fieldSessionLog.exception(ex);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2006 3:32:25 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void dataJobMessage(cbit.vcell.simdata.DataJobEvent event) {
	// if it's from one of our jobs, pass it along so it will reach the client
	if (getUser().equals(event.getUser())) {
		messageService.getMessageCollector().dataJobMessage(event);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 2:59:05 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void exportMessage(ExportEvent event) {
	// if it's from one of our jobs, pass it along so it will reach the client
	if (getUser().equals(event.getUser())) {
		messageService.getMessageCollector().exportMessage(event);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 12:32:06 PM)
 * @return cbit.vcell.server.DataSetController
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.server.bionetgen.BNGService getBNGService() throws java.rmi.RemoteException, DataAccessException {
	fieldSessionLog.print("LocalVCellConnectionMessaging.getBNGService()");
	if (bngServiceMessaging == null) {
		try {
			bngClientMessaging = new JmsClientMessaging(queueConn, fieldSessionLog);
			bngServiceMessaging = new LocalBNGServiceMessaging(fieldSessionLog, getUser(), bngClientMessaging);
			fieldSessionLog.print("new bngClientMessaging=" + bngClientMessaging);
		} catch (javax.jms.JMSException ex) {
			fieldSessionLog.exception(ex);
			throw new DataAccessException(ex.getMessage());
		}
	}
	
	return bngServiceMessaging;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.server.DataSetController
 * @exception java.lang.Exception The exception description.
 */
public DataSetController getDataSetController() throws RemoteException, DataAccessException {
	fieldSessionLog.print("LocalVCellConnectionMessaging.getDataSetController()");
	if (dataSetControllerMessaging == null) {
		try {
			dataClientMessaging = new JmsClientMessaging(queueConn, fieldSessionLog);
			dataSetControllerMessaging = new LocalDataSetControllerMessaging(fieldSessionLog, getUser(), dataClientMessaging);
			fieldSessionLog.print("new dataClientMessaging=" + dataClientMessaging);
		} catch (javax.jms.JMSException ex) {
			fieldSessionLog.exception(ex);
			throw new DataAccessException(ex.getMessage());
		}
	}
	
	return dataSetControllerMessaging;
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 4:34:02 PM)
 * @return cbit.vcell.messaging.event.SimpleMessageServiceMessaging
 */
cbit.vcell.messaging.event.SimpleMessageServiceMessaging getMessageService() {
	return messageService;
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/01 1:27:23 PM)
 * @return cbit.rmi.event.RemoteMessageHandler
 */
public cbit.rmi.event.RemoteMessageHandler getRemoteMessageHandler() {
	return messageService.getMessageHandler();
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.solvers.MathController
 * @param mathDesc cbit.vcell.math.MathDescription
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationController getSimulationController() throws RemoteException {
	if (simulationControllerMessaging == null){
		try {
			simClientMessaging = new JmsClientMessaging(queueConn, fieldSessionLog);
			simulationControllerMessaging = new LocalSimulationControllerMessaging(getUser(), simClientMessaging, fieldSessionLog);
			fieldSessionLog.print("new simClientMessaging=" + simClientMessaging);
		} catch (DataAccessException ex) {
			fieldSessionLog.exception(ex);
			throw new RuntimeException(ex.getMessage());
		} catch (javax.jms.JMSException ex) {
			fieldSessionLog.exception(ex);
			throw new RuntimeException(ex.getMessage());
		}
	}

	return simulationControllerMessaging;
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/01 11:15:49 PM)
 * @return cbit.vcell.server.URLFinder
 * @exception java.rmi.RemoteException The exception description.
 */
public URLFinder getURLFinder() {
	try {
		return new URLFinder(	new URL(PropertyLoader.getRequiredProperty(PropertyLoader.tutorialURLProperty)),
								new URL(PropertyLoader.getRequiredProperty(PropertyLoader.userGuideURLProperty)));
	} catch (java.net.MalformedURLException e){
		fieldSessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public User getUser() {
	return (fieldUser);
}


/**
 * This method was created by a SmartGuide.
 * @return DBManager
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public UserMetaDbServer getUserMetaDbServer() throws RemoteException, DataAccessException {
	fieldSessionLog.print("LocalVCellConnectionMessaging.getUserMetaDbServer(" + getUser() + ")");
	if (userMetaDbServerMessaging == null) {
		try {
			dbClientMessaging = new JmsClientMessaging(queueConn, fieldSessionLog);
			userMetaDbServerMessaging = new LocalUserMetaDbServerMessaging(dbClientMessaging, getUser(), fieldSessionLog);
			fieldSessionLog.print("new dbClientMessaging=" + dbClientMessaging);
		} catch (javax.jms.JMSException ex) {
			fieldSessionLog.exception(ex);
			throw new DataAccessException(ex.getMessage());
		}			
	}
	return userMetaDbServerMessaging;
}


/**
 * Insert the method's description here.
 * Creation date: (4/16/2004 11:22:31 AM)
 */
public boolean isTimeout() throws java.rmi.RemoteException {
	SimpleMessageHandler messageHander = (SimpleMessageHandler)messageService.getMessageHandler();
	if (messageHander.isTimeout()) {
		return true;
	}

	//long TIMEOUT_INTERVAL = 3600 * 1000; // a hour
	
	//long t = System.currentTimeMillis();
	//if ((dbClientMessaging == null || t - dbClientMessaging.getTimeSinceLastMessage() >= TIMEOUT_INTERVAL) 
		//&& (dataClientMessaging == null || t - dataClientMessaging.getTimeSinceLastMessage() >= TIMEOUT_INTERVAL)
		//&& (simClientMessaging == null || t - simClientMessaging.getTimeSinceLastMessage() >= TIMEOUT_INTERVAL)
		//&& t - messageService.getJmsMessageCollector().getTimeSinceLastMessage() >= TIMEOUT_INTERVAL) {
		//return true;
	//}	

	return false;
}
}