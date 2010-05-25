package cbit.vcell.server;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.FileNotFoundException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.SimpleMessageService;
import cbit.sql.KeyFactory;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.simdata.LocalDataSetController;
import cbit.vcell.simdata.LocalDataSetControllerProxy;
/**
 * The user's connection to the Virtual Cell.  It is obtained from the VCellServer
 * after the user has been authenticated.
 * Creation date: (Unknown)
 * @author: Jim Schaff.
 */
public class LocalVCellConnection extends UnicastRemoteObject implements VCellConnection, ExportListener ,DataJobListener{	
	private DataSetController dataSetControllerProxy = null;
	private SimulationController simulationController = null;
	private UserMetaDbServer userMetaDbServer = null;
	private SimpleMessageService messageService = new SimpleMessageService();
	//
	private User fieldUser = null;
	private String fieldPassword = null;

	//
	// database resources
	//
	private static cbit.sql.ConnectionFactory conFactory = null;
	private static cbit.sql.KeyFactory keyFactory = null;

	
	private SessionLog fieldSessionLog = null;
	private LocalVCellServer fieldLocalVCellServer = null;
	private String fieldHost = null;

/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public LocalVCellConnection(User user, String password, String host, SessionLog sessionLog, LocalVCellServer aLocalVCellServer) throws RemoteException, java.sql.SQLException, FileNotFoundException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortVCellConnection,0));
	this.fieldUser = user;
	this.fieldPassword = password;
	this.fieldHost = host;
	this.fieldSessionLog = sessionLog;
	this.fieldLocalVCellServer = aLocalVCellServer;
	sessionLog.print("new LocalVCellConnection(" + user.getName() + ")");
	
	getLocalVCellServer().getExportServiceImpl().addExportListener(this);
	getLocalVCellServer().getDataSetControllerImpl().addDataJobListener(this);

	PerformanceMonitoringFacility pmf = new PerformanceMonitoringFacility(user, sessionLog);
	getMessageService().getMessageDispatcher().addPerformanceMonitorListener(pmf);
	
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
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataSetControllerImpl
 */
public ConnectionPool getConnectionPool() {
	return (getLocalVCellServer().getConnectionPool());
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.server.DataSetController
 * @exception java.lang.Exception The exception description.
 */
public DataSetController getDataSetController() throws RemoteException, DataAccessException {
	getSessionLog().print("LocalVCellConnection.getDataSetController()");
	if (dataSetControllerProxy == null) {
		//
		// if this is a Primary Server, then create a factory for Remote Data Services
		//
		RemoteDataSetControllerFactory remoteDataSetControllerFactory = null;
		getSessionLog().print("getDataSetController(), this is a primary server, creating remote factory for DataSetControllerProxy");
		if (getLocalVCellServer().isPrimaryServer()) {
			remoteDataSetControllerFactory = new RemoteDataSetControllerFactory() {
				public DataSetController getRemoteDataSetController() throws RemoteException, DataAccessException {
					return getRemoteDataSetController0();
				}
			};
		}else{
			remoteDataSetControllerFactory = null;
		}

		getSessionLog().print("getDataSetController() creating LocalDataSetController(" + getUser().getName() + ")");
		LocalDataSetController localDataSetController = new LocalDataSetController(this, getSessionLog(), getLocalVCellServer().getDataSetControllerImpl(), getLocalVCellServer().getExportServiceImpl(), getUser());
		dataSetControllerProxy = new LocalDataSetControllerProxy(getSessionLog(), remoteDataSetControllerFactory, localDataSetController);
	}

	return dataSetControllerProxy;
}


/**
 * Insert the method's description here.
 * Creation date: (1/29/2003 5:07:46 PM)
 * @return java.lang.String
 */
public String getHost() {
	return fieldHost;
}


/**
 * Insert the method's description here.
 * Creation date: (8/29/2000 1:14:39 PM)
 * @author: John Wagner
 * @return: the <code>LocalVCellServer</code> associated with this connection.
 */
private LocalVCellServer getLocalVCellServer() {
	return (fieldLocalVCellServer);
}


/**
 * Insert the method's description here.
 * Creation date: (6/29/01 10:33:49 AM)
 * @return cbit.rmi.event.SimpleMessageService
 */
cbit.rmi.event.SimpleMessageService getMessageService() {
	return messageService;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @param simIdentifier java.lang.String
 */
String getPassword() {
	return (fieldPassword);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.DataSetController
 */
private DataSetController getRemoteDataSetController0() throws RemoteException, DataAccessException {
	VCellConnection vcConn = getSimDataServerRemoteConnection();
	if (vcConn != null && vcConn!=this){
		DataSetController remoteDataSetController = vcConn.getDataSetController();
		return remoteDataSetController;
	}else{
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/01 1:27:23 PM)
 * @return cbit.rmi.event.RemoteMessageHandler
 */
public cbit.rmi.event.RemoteMessageHandler getRemoteMessageHandler() throws java.rmi.RemoteException {
	return messageService.getMessageHandler();
}


/**
 * Insert the method's description here.
 * Creation date: (2/14/01 9:45:10 AM)
 * @return cbit.vcell.modeldb.ResultSetCrawler
 */
public cbit.vcell.modeldb.ResultSetCrawler getResultSetCrawler() {
	return this.fieldLocalVCellServer.getResultSetCrawler();
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @param simIdentifier java.lang.String
 */
private SessionLog getSessionLog() {
	return (fieldSessionLog);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.VCellConnection
 */
private VCellConnection getSimDataServerRemoteConnection() {
	ConnectionPool connectionPool = getLocalVCellServer().getConnectionPool();
	if (connectionPool != null) {
		try {
			VCellConnection vcConn = connectionPool.getSimDataServerVCellConnection(getUser().getName(), getPassword());
			//
			// connect() establishes a two way connection, so this is sufficient for receiving data events.
			//
			if (vcConn!=null && !getRemoteMessageHandler().isConnected(vcConn.getRemoteMessageHandler())){
				cbit.rmi.event.RemoteMessageHandler localMessageHandler = getRemoteMessageHandler();
				cbit.rmi.event.RemoteMessageHandler remoteMessageHandler = vcConn.getRemoteMessageHandler();
				localMessageHandler.addRemoteMessageListener(remoteMessageHandler, remoteMessageHandler.getRemoteMesssageListenerID());
				remoteMessageHandler.addRemoteMessageListener(localMessageHandler, localMessageHandler.getRemoteMesssageListenerID());
			}
			return vcConn;
		} catch (Throwable e) {
			getSessionLog().exception(e);
		}
	}
	return null;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.solvers.MathController
 * @param mathDesc cbit.vcell.math.MathDescription
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationController getSimulationController() throws RemoteException {
	if (simulationController == null){
		try {
			simulationController = new LocalSimulationController(getUser(),getSessionLog(),getLocalVCellServer().getAdminDatabaseServer(), getLocalVCellServer().getSimulationControllerImpl(),getUserMetaDbServer());
		}catch (DataAccessException e){
			throw new RuntimeException("DataAccessException creating LocalSimulationController, :"+e.getMessage());
		}
	}
	return simulationController;
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/01 11:15:49 PM)
 * @return cbit.vcell.server.URLFinder
 * @exception java.rmi.RemoteException The exception description.
 */
public URLFinder getURLFinder() throws java.rmi.RemoteException {
	try {
		return new URLFinder(	new URL(PropertyLoader.getRequiredProperty(PropertyLoader.tutorialURLProperty)),
								new URL(PropertyLoader.getRequiredProperty(PropertyLoader.userGuideURLProperty)));
	}catch (java.net.MalformedURLException e){
		getSessionLog().exception(e);
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
	getSessionLog().print("LocalVCellConnection.getUserMetaDbServer(" + getUser() + ")");
	if (userMetaDbServer == null) {
		userMetaDbServer = new cbit.vcell.modeldb.LocalUserMetaDbServer(conFactory, keyFactory, getUser(), getSessionLog());
	}
	return userMetaDbServer;
}


/**
 * This method was created in VisualAge.
 * @param conFactory cbit.sql.ConnectionFactory
 */
static void setDatabaseResources(cbit.sql.ConnectionFactory argConFactory, KeyFactory argKeyFactory) {
	conFactory = argConFactory;
	keyFactory = argKeyFactory;
}


public void dataJobMessage(DataJobEvent event) {
	if (getUser().equals(event.getUser())) {
		messageService.getMessageCollector().dataJobMessage(event);
	}
}


public void sendErrorReport(Throwable exception) throws RemoteException {
	BeanUtils.sendErrorReport(exception);
}
}