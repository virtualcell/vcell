package cbit.vcell.anonymizer;
import cbit.util.DataAccessException;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.vcell.server.RMIVCellConnectionFactory;
import cbit.vcell.server.URLFinder;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.server.VCellConnection;
import cbit.vcell.server.SimulationController;
import cbit.vcell.server.UserMetaDbServer;
import cbit.gui.PropertyLoader;
import cbit.rmi.event.SimpleMessageService;
import cbit.vcell.server.DataSetController;
import java.rmi.*;
import java.rmi.server.*;
/**
 * The user's connection to the Virtual Cell.  It is obtained from the VCellServer
 * after the user has been authenticated.
 * Creation date: (Unknown)
 * @author: Jim Schaff.
 */
public class AnonymizerVCellConnection extends UnicastRemoteObject implements VCellConnection {

	private User user = null;
	private SessionLog sessionLog = null;
	private VCellConnection remoteVCellConnection = null;
	private RMIVCellConnectionFactory rmiVCellConnectionFactory = null;
	private SimpleMessageService messageService = new SimpleMessageService();

	private AnonymizerSimulationController simulationController = null;
	private SimulationController remoteSimulationController = null;
	private AnonymizerDataSetController dataSetController = null;
	private DataSetController remoteDataSetController = null;
	private AnonymizerUserMetaDbServer userMetaDbServer = null;
	private UserMetaDbServer remoteUserMetaDbServer = null;
	private AnonymizerBNGService bngService = null;
	private cbit.vcell.server.bionetgen.BNGService remoteBNGService = null;
	
/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public AnonymizerVCellConnection(RMIVCellConnectionFactory arg_rmiVCellConnectionFactory, SessionLog arg_sessionLog) throws cbit.vcell.server.AuthenticationException, cbit.vcell.server.ConnectionException, RemoteException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortVCellConnection,0));
	this.rmiVCellConnectionFactory = arg_rmiVCellConnectionFactory;
	sessionLog = arg_sessionLog;

	connect();
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2006 9:50:08 AM)
 */
private void connect() throws cbit.vcell.server.AuthenticationException, cbit.vcell.server.ConnectionException, RemoteException {
	remoteDataSetController = null;
	remoteSimulationController = null;
	remoteUserMetaDbServer = null;
	remoteVCellConnection = null;

	remoteVCellConnection = rmiVCellConnectionFactory.createVCellConnection();
	cbit.rmi.event.RemoteMessageHandler remoteMessageHandler = remoteVCellConnection.getRemoteMessageHandler();
	remoteMessageHandler.addRemoteMessageListener(getRemoteMessageHandler(),getRemoteMessageHandler().getRemoteMesssageListenerID());
	getRemoteMessageHandler().addRemoteMessageListener(remoteMessageHandler,remoteMessageHandler.getRemoteMesssageListenerID());
	((cbit.rmi.event.SimpleMessageHandler)messageService.getMessageHandler()).enablePolling(10);
	
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 3:22:07 PM)
 * @return cbit.vcell.server.DataSetController
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.server.bionetgen.BNGService getBNGService() throws cbit.util.DataAccessException, java.rmi.RemoteException {
	if (bngService == null) {
		bngService = new AnonymizerBNGService(this, sessionLog);
	}

	return bngService;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.server.DataSetController
 * @exception java.lang.Exception The exception description.
 */
public DataSetController getDataSetController() throws RemoteException, DataAccessException {
	if (dataSetController == null) {
		dataSetController = new AnonymizerDataSetController(this, sessionLog);
	}

	return dataSetController;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.server.DataSetController
 * @exception java.lang.Exception The exception description.
 */
cbit.vcell.server.bionetgen.BNGService getRemoteBNGService() throws RemoteException, DataAccessException {
	if (remoteBNGService == null) {
		remoteBNGService = getRemoteVCellConnection().getBNGService();
	}

	return remoteBNGService;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.server.DataSetController
 * @exception java.lang.Exception The exception description.
 */
DataSetController getRemoteDataSetController() throws RemoteException, DataAccessException {
	if (remoteDataSetController == null) {
		remoteDataSetController = getRemoteVCellConnection().getDataSetController();
	}

	return remoteDataSetController;
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
 * This method was created by a SmartGuide.
 * @return cbit.vcell.solvers.MathController
 * @param mathDesc cbit.vcell.math.MathDescription
 * @exception java.rmi.RemoteException The exception description.
 */
SimulationController getRemoteSimulationController() throws RemoteException {
	if (remoteSimulationController == null) {
		remoteSimulationController = getRemoteVCellConnection().getSimulationController();
	}

	return remoteSimulationController;
}


/**
 * This method was created by a SmartGuide.
 * @return DBManager
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
UserMetaDbServer getRemoteUserMetaDbServer() throws RemoteException, DataAccessException {
	if (remoteUserMetaDbServer == null) {
		remoteUserMetaDbServer = getRemoteVCellConnection().getUserMetaDbServer();
	}
	return remoteUserMetaDbServer;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2006 9:50:08 AM)
 */
private VCellConnection getRemoteVCellConnection() throws RemoteException {
	if (remoteVCellConnection == null) {
		reconnect();
	}

	return remoteVCellConnection;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.solvers.MathController
 * @param mathDesc cbit.vcell.math.MathDescription
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationController getSimulationController() throws RemoteException {
	if (simulationController == null){
		simulationController = new AnonymizerSimulationController(this, sessionLog);
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
	return getRemoteVCellConnection().getURLFinder();
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public User getUser() throws RemoteException {
	if (user == null) {
		user = getRemoteVCellConnection().getUser();
	}

	return user;
}


/**
 * This method was created by a SmartGuide.
 * @return DBManager
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public UserMetaDbServer getUserMetaDbServer() throws RemoteException, DataAccessException {
	if (userMetaDbServer == null) {
		userMetaDbServer = new AnonymizerUserMetaDbServer(this, sessionLog);
	}
	return userMetaDbServer;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2006 9:50:08 AM)
 */
public void reconnect() throws RemoteException {
	try {
		sessionLog.alert("trying to reconnect to remote server.");
		connect();
		sessionLog.alert("succeeded to reconnect to remote server.");
	} catch (cbit.vcell.server.AuthenticationException ex) {
		sessionLog.exception(ex);
		throw new RuntimeException(ex.getMessage());
	} catch (cbit.vcell.server.ConnectionException ex) {
		sessionLog.exception(ex);
		throw new RuntimeException(ex.getMessage());
	}	
}
}