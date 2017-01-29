/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.server;
import java.rmi.RemoteException;

import org.vcell.client.logicalwindow.LWContainerHandle;
import org.vcell.client.logicalwindow.LWModelessWarning;
import org.vcell.client.logicalwindow.LWNamespace;
import org.vcell.util.AuthenticationException;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.PerformanceMonitorEvent;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.controls.SessionManager;
import cbit.vcell.export.server.ExportController;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.LocalVCellConnectionFactory;
import cbit.vcell.server.RMIVCellConnectionFactory;
import cbit.vcell.server.SimulationController;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.server.VCellConnectionFactory;
import cbit.vcell.simdata.VCDataManager;
/**
 * Insert the type's description here.
 * Creation date: (5/12/2004 4:31:18 PM)
 * @author: Ion Moraru
 */
public class ClientServerManager implements SessionManager,DataSetControllerProvider {


	public static final String PROPERTY_NAME_CONNECTION_STATUS = "connectionStatus";
	private class ClientConnectionStatus implements ConnectionStatus {
		// actual status info
		private String serverHost = null;
		private String userName = null;
		private int status = NOT_CONNECTED;

		/**
		 * @param userName java.lang.String
		 * @param serverHost java.lang.String
		 * @param status int
		 */
		private ClientConnectionStatus(String userName, String serverHost, int status) {
			switch (status) {
				case NOT_CONNECTED: {
					setUserName(null);
					setServerHost(null);
					break;
				}
				case INITIALIZING:
				case CONNECTED:
				case DISCONNECTED: {
					if (userName == null || serverHost == null) throw new RuntimeException("userName and serverHost should be non-null unless NOT_CONNECTED");
					setUserName(userName);
					setServerHost(serverHost);
					break;
				}
				default: {
					throw new RuntimeException("unknown connection status: " + status);
				}
			}
			setStatus(status);
		}


		public java.lang.String getServerHost() {
			return serverHost;
		}

		public int getStatus() {
			return status;
		}

		public java.lang.String getUserName() {
			return userName;
		}


		private void setServerHost(java.lang.String newServerHost) {
			serverHost = newServerHost;
		}


		private void setStatus(int newStatus) {
			status = newStatus;
		}

		private void setUserName(java.lang.String newUserName) {
			userName = newUserName;
		}

		public String toString() {
			return "status " + getStatus() + " server " + getServerHost() + " user " + getUserName();
		}



		@Override
		public Reconnector getReconnector() {
			return ClientServerManager.this.getReconnector();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((serverHost == null) ? 0 : serverHost.hashCode());
			result = prime * result + status;
			result = prime * result + ((userName == null) ? 0 : userName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ClientConnectionStatus other = (ClientConnectionStatus) obj;
			if (serverHost == null) {
				if (other.serverHost != null)
					return false;
			} else if (!serverHost.equals(other.serverHost))
				return false;
			if (status != other.status)
				return false;
			if (userName == null) {
				if (other.userName != null)
					return false;
			} else if (!userName.equals(other.userName))
				return false;
			return true;
		}

	}
	private enum ReconnectStatus {
		NOT("Not reconnecting"),
		FIRST("First reconnection attempt"),
		SUBSEQUENT("Subsequent reconnect attempt");

		private String label;

		private ReconnectStatus(String label) {
			this.label = label;
		}
		@Override
		public String toString( ) {
			return label;
		}
	}
	private ClientServerInfo clientServerInfo = null;
	//
	// remote references
	//
	private VCellConnection vcellConnection = null;
	private SimulationController simulationController = null;
	private UserMetaDbServer userMetaDbServer = null;
	private DataSetController dataSetController = null;
	// gotten from call to vcellConnection
	private User user = null;

	private DocumentManager documentManager = new ClientDocumentManager(this, 10000000L);;
	private JobManager jobManager = null;
	private ExportController exportController = null;
	private AsynchMessageManager asynchMessageManager = null;
	private VCDataManager vcDataManager = null;
	private UserPreferences userPreferences = new UserPreferences(this);
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private ClientConnectionStatus fieldConnectionStatus = new ClientConnectionStatus(null, null, ConnectionStatus.NOT_CONNECTED);
	private ReconnectStatus reconnectStat = ReconnectStatus.NOT;
	/**
	 * modeless window to warn about lost connection
	 */
	private LWModelessWarning cantConnectWarning = null;
	private Reconnector reconnector = null;

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

public ClientServerManager(ClientServerInfo clientServerInfo) {
	this.clientServerInfo = clientServerInfo;
}

/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:48:13 PM)
 */
private void changeConnection(TopLevelWindowManager requester, VCellConnection newVCellConnection) {
	VCellThreadChecker.checkRemoteInvocation();

	VCellConnection lastVCellConnection = getVcellConnection();
	setVcellConnection(newVCellConnection);
	if (getVcellConnection() != null) {
		try {
			/* new credentials; need full init */
			// throw it away; doesn't properly support full reinits
			// preload the document manager cache
			((ClientDocumentManager)getDocumentManager()).initAllDatabaseInfos();

			// load user preferences
			getUserPreferences().resetFromSaved(getDocumentManager().getPreferences());

			setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), getClientServerInfo().getActiveHost(), ConnectionStatus.CONNECTED));
		} catch (DataAccessException exc) {
			// unlikely, since we just connected, but it looks like we did loose the connection...
			lastVCellConnection = getVcellConnection();
			setVcellConnection(null);
			setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), getClientServerInfo().getActiveHost(), ConnectionStatus.DISCONNECTED));
			exc.printStackTrace(System.out);
			PopupGenerator.showErrorDialog(requester, "Server connection failed:\n\n" + exc.getMessage());
		}
	} else if(lastVCellConnection != null) {
		setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), getClientServerInfo().getActiveHost(), ConnectionStatus.DISCONNECTED));
	} else {
		setConnectionStatus(new ClientConnectionStatus(null, null, ConnectionStatus.NOT_CONNECTED));
	}
}


public void reportPerformanceMonitorEvent(PerformanceMonitorEvent pme) throws RemoteException {
	// just pass it to the the messaging service
	if (isStatusConnected()) {
		vcellConnection.reportPerformanceMonitorEvent(pme);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/25/2004 2:03:47 AM)
 * @return int
 */
public void cleanup() {
	setVcellConnection(null);
}

public MessageEvent[] getMessageEvents() throws RemoteException{
	if (vcellConnection!=null && isStatusConnected()){
		return vcellConnection.getMessageEvents();
	} else {
		return null;
	}
}

public static void checkClientServerSoftwareVersion(TopLevelWindowManager requester, ClientServerInfo clientServerInfo) {
	String clientSoftwareVersion = System.getProperty(PropertyLoader.vcellSoftwareVersion);
	if (clientSoftwareVersion != null &&  clientSoftwareVersion.toLowerCase().contains("devel") ) {
		return;
	}
	if (clientServerInfo.getServerType() == ClientServerInfo.SERVER_REMOTE) {
		String[] hosts = clientServerInfo.getHosts();
		for (int i = 0; i < hosts.length; i ++) {
			String serverSoftwareVersion = RMIVCellConnectionFactory.getVCellSoftwareVersion((requester!=null?requester.getComponent():null),hosts[i]);
			if (serverSoftwareVersion != null && !serverSoftwareVersion.equals(clientSoftwareVersion)) {
					PopupGenerator.showWarningDialog(requester.getComponent(), "A new VCell client is available:\n"
						+ "current version : " + clientSoftwareVersion + "\n"
						+ "new version : " + serverSoftwareVersion + "\n"
						+ "\nPlease exit VCell and download the latest client from VCell Software page (http://vcell.org).");
				break;
			}
		}
	}
}


public void connectNewServer(TopLevelWindowManager requester, ClientServerInfo csi) {
	clientServerInfo = csi;
	connect(requester);
}

/**
 * Insert the method's description here.
 * Creation date: (5/17/2004 6:26:14 PM)
 */
public void connect(TopLevelWindowManager requester) {
	asynchMessageManager.stopPolling();
	reconnectStat = ReconnectStatus.NOT;
	checkClientServerSoftwareVersion(requester,clientServerInfo);

	// get new server connection
	VCellConnection newVCellConnection = connectToServer(requester);
	// update managers, status, etc.
	changeConnection(requester, newVCellConnection);
	if (fieldConnectionStatus.getStatus() == ConnectionStatus.CONNECTED) {
		//start polling if haven't already
		asynchMessageManager.startPolling();
	}
}

/**
 * same as {@link #connect(TopLevelWindowManager)} but pause {@link Reconnector}
 * @param requester
 */
public void reconnect(TopLevelWindowManager requester) {
	Reconnector rc = getReconnector();
	try {
		rc.start();
		rc.notificationPause(true);
		connect(requester);
		if (fieldConnectionStatus.getStatus() == ConnectionStatus.CONNECTED) {
			rc.stop( );
			asynchMessageManager.startPolling();
		}
	}
	finally {
		rc.notificationPause(false);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/17/2004 6:26:14 PM)
 * @param clientServerInfo cbit.vcell.client.server.ClientServerInfo
 */
public void connectAs(TopLevelWindowManager requester, String user, DigestedPassword digestedPassword) {
	reconnectStat = ReconnectStatus.NOT;
	switch (getClientServerInfo().getServerType()) {
		case ClientServerInfo.SERVER_LOCAL: {
			clientServerInfo = ClientServerInfo.createLocalServerInfo(user, digestedPassword);
			break;
		}
		case ClientServerInfo.SERVER_REMOTE: {
			clientServerInfo = ClientServerInfo.createRemoteServerInfo(getClientServerInfo().getHosts(), user, digestedPassword);
			break;
		}
	}
	connect(requester);
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:48:13 PM)
 */
private VCellConnection connectToServer(TopLevelWindowManager requester) {
	VCellThreadChecker.checkRemoteInvocation();

	VCellConnection newVCellConnection = null;
	String badConnStr = "";
	try {
		switch (getClientServerInfo().getServerType()) {
			case ClientServerInfo.SERVER_REMOTE: {
				String[] hosts = getClientServerInfo().getHosts();
				for (int i = 0; i < hosts.length; i ++) {
					try {
						if (i == 0) {
							badConnStr += "(";
						}
						getClientServerInfo().setActiveHost(hosts[i]);

						badConnStr += hosts[i] + ";";
						RMIVCellConnectionFactory vcConnFactory = new RMIVCellConnectionFactory(hosts[i], getClientServerInfo().getUserLoginInfo());
						setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), hosts[i], ConnectionStatus.INITIALIZING));
						newVCellConnection = vcConnFactory.createVCellConnectionAskProxy((requester!=null?requester.getComponent():null));
						break;
					} catch (AuthenticationException ex) {
						throw ex;
					} catch (Exception ex) {
						if (i == hosts.length - 1) {
							badConnStr += ")";
							throw ex;
						}
					}
				}
				break;
			}
			case ClientServerInfo.SERVER_LOCAL: {
				new PropertyLoader();
				getClientServerInfo().setActiveHost(ClientServerInfo.LOCAL_SERVER);
				SessionLog log = new StdoutSessionLog(getClientServerInfo().getUsername());
				LocalVCellConnectionFactory vcConnFactory = new LocalVCellConnectionFactory(getClientServerInfo().getUserLoginInfo(), log);
				setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), ClientServerInfo.LOCAL_SERVER, ConnectionStatus.INITIALIZING));
				newVCellConnection = vcConnFactory.createVCellConnection();
				break;
			}
		}
		if (cantConnectWarning != null) { //clear warning message if it is up
			cantConnectWarning.dispose();
			cantConnectWarning = null;
		}
		reconnectStat = ReconnectStatus.NOT;
	} catch (AuthenticationException aexc) {
		aexc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(requester, aexc.getMessage());
	} catch (ConnectionException cexc) {
		String msg = badConnectMessage(badConnStr) + "\n\n" + cexc.getMessage();
		cexc.printStackTrace(System.out);
		BeanUtils.sendRemoteLogMessage(getClientServerInfo().getUserLoginInfo(),msg);
		if (reconnectStat != ReconnectStatus.SUBSEQUENT) {
			LWContainerHandle lwParent = LWNamespace.findLWOwner(requester.getComponent());
			if (cantConnectWarning == null) {
				cantConnectWarning = new LWModelessWarning(lwParent,msg);
			}
			else {
				cantConnectWarning.setMessage(msg);
			}
			cantConnectWarning.setVisible(true);
		}
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		String msg = badConnectMessage(badConnStr) + "\nException:\n" + exc.getMessage();
		BeanUtils.sendRemoteLogMessage(getClientServerInfo().getUserLoginInfo(),msg);
		PopupGenerator.showErrorDialog(requester, msg);
	}

	return newVCellConnection;
}

private String badConnectMessage(String badConnStr) {
	String ctype = reconnectStat == ReconnectStatus.NOT ? "connect" : "reconnect";
	return VCellErrorMessages.getErrorMessage(VCellErrorMessages.BAD_CONNECTION_MESSAGE, ctype, badConnStr);
}


public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFielOperationSpec) throws DataAccessException{
	return getVCDataManager().fieldDataFileOperation(fieldDataFielOperationSpec);
}

/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:21:46 PM)
 * @return cbit.vcell.client.AsynchMessageManager
 */
public AsynchMessageManager getAsynchMessageManager() {
	if (asynchMessageManager == null) {
		asynchMessageManager = new AsynchMessageManager(this);
	}
	return asynchMessageManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:45:19 PM)
 * @return cbit.vcell.client.server.ClientServerInfo
 */
public ClientServerInfo getClientServerInfo() {
	return clientServerInfo;
}


/**
 * Gets the connectionStatus property (cbit.vcell.client.server.ConnectionStatus) value.
 * @return The connectionStatus property value.
 * @see #setConnectionStatus
 */
public ConnectionStatus getConnectionStatus() {
	return fieldConnectionStatus;
}

public boolean isStatusConnected(){
	return (fieldConnectionStatus.getStatus() == ConnectionStatus.CONNECTED);
}

/**
 * Insert the method's description here.
 * Creation date: (5/13/2004 1:54:04 PM)
 * @return UserMetaDbServer
 */
public synchronized DataSetController getDataSetController() throws DataAccessException {
	VCellThreadChecker.checkRemoteInvocation();
	if (dataSetController!=null){
		return dataSetController;
	}else if (getVcellConnection()==null){
		throw new RuntimeException("cannot get Simulation Data Server, no VCell Connection\ntry Server->Reconnect");
	}else{
		try {
			dataSetController = getVcellConnection().getDataSetController();
			return dataSetController;
		} catch (java.rmi.RemoteException rexc) {
			rexc.printStackTrace(System.out);
			try {
				// one more time before we fail../
				dataSetController = getVcellConnection().getDataSetController();
				return dataSetController;
			} catch (java.rmi.RemoteException rexc2) {
				rexc.printStackTrace(System.out);
				throw new DataAccessException("RemoteException: "+rexc2.getMessage());
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/13/2004 1:54:04 PM)
 * @return cbit.vcell.clientdb.DocumentManager
 */
public DocumentManager getDocumentManager() {
	return documentManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/13/2004 1:54:04 PM)
 */
public ExportController getExportController() {
	if (exportController == null) {
		exportController = new ClientExportController(this);
	}
	return exportController;
}


/**
 * Insert the method's description here.
 * Creation date: (5/13/2004 1:54:04 PM)
 * @return cbit.vcell.clientdb.JobManager
 */
public JobManager getJobManager() {
	if (jobManager == null) {
		jobManager = new ClientJobManager(this);
		getAsynchMessageManager().addSimulationJobStatusListener(jobManager);
		jobManager.addSimStatusListener(getAsynchMessageManager());
	}
	return jobManager;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}

/**
 * Insert the method's description here.
 * Creation date: (5/13/2004 1:54:04 PM)
 * @return UserMetaDbServer
 */
public synchronized SimulationController getSimulationController() {
	VCellThreadChecker.checkRemoteInvocation();
	if (simulationController!=null){
		return simulationController;
	}else if (getVcellConnection()==null){
		throw new RuntimeException("cannot get Simulation Server, no VCell Connection\ntry Server->Reconnect");
	}else{
		try {
			simulationController = getVcellConnection().getSimulationController();
			return simulationController;
		} catch (java.rmi.RemoteException rexc) {
			rexc.printStackTrace(System.out);
			try {
				// one more time before we fail../
				simulationController = getVcellConnection().getSimulationController();
				return simulationController;
			} catch (java.rmi.RemoteException rexc2) {
				rexc.printStackTrace(System.out);
				throw new RuntimeException("RemoteException: "+rexc2.getMessage());
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/13/2004 1:54:04 PM)
 * @return cbit.vcell.server.URLFinder
 */
public synchronized User getUser() {
	if (user!=null){
		return user;
	}else if (getVcellConnection()==null){
		return null;
	}else{
		VCellThreadChecker.checkRemoteInvocation();
		try {
			user = getVcellConnection().getUserLoginInfo().getUser();
			return user;
		} catch (java.rmi.RemoteException rexc) {
			rexc.printStackTrace(System.out);
			throw new RuntimeException("RemoteException: "+rexc.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/13/2004 1:54:04 PM)
 * @return UserMetaDbServer
 */
public synchronized UserMetaDbServer getUserMetaDbServer() throws DataAccessException {
	VCellThreadChecker.checkRemoteInvocation();
	if (userMetaDbServer!=null){
		return userMetaDbServer;
	}else if (getVcellConnection()==null){
		throw new RuntimeException("cannot get Database Server, no VCell Connection\ntry Server->Reconnect");
	}else{
		try {
			userMetaDbServer = getVcellConnection().getUserMetaDbServer();
			return userMetaDbServer;
		} catch (java.rmi.RemoteException rexc) {
			rexc.printStackTrace(System.out);
			try {
				// one more time before we fail../
				userMetaDbServer = getVcellConnection().getUserMetaDbServer();
				return userMetaDbServer;
			} catch (java.rmi.RemoteException rexc2) {
				rexc.printStackTrace(System.out);
				throw new DataAccessException("RemoteException: "+rexc2.getMessage());
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 8:19:36 PM)
 * @return cbit.vcell.client.UserPreferences
 */
public UserPreferences getUserPreferences() {
	return userPreferences;
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:21:46 PM)
 * @return cbit.vcell.client.VCDataManager
 */
public VCDataManager getVCDataManager() {
	if (vcDataManager == null) {
		vcDataManager = new VCDataManager(this);
	}
	return vcDataManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/13/2004 12:26:57 PM)
 * @return VCellConnection
 */
private VCellConnection getVcellConnection() {
	return vcellConnection;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * @return lazily created {@link Reconnector }
 */
private Reconnector getReconnector( ) {
	if (reconnector == null) {
		reconnector = new Reconnector(this);
	}
	return reconnector;
}

/**
 * commence automatic reconnect attempts
 */
void attemptReconnect( ) {
	getReconnector( ).start();
}

/**
 * attempt reconnect now
 */
void reconnect() {
	Reconnector rc = getReconnector();
	rc.notificationPause(true);
	try {
		switch (reconnectStat) {
		case NOT:
			reconnectStat = ReconnectStatus.FIRST;
			break;
		case FIRST:
			reconnectStat = ReconnectStatus.SUBSEQUENT;
			break;
		default:
		}
		TopLevelWindowManager am = TopLevelWindowManager.activeManager();
		VCellConnection connection = connectToServer(am);
		if (connection != null) { //success
			changeConnection(am,connection);
			rc.stop();
			asynchMessageManager.startPolling();
			return;
		}
		setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), getClientServerInfo().getActiveHost(), ConnectionStatus.DISCONNECTED));
	} finally {
		rc.notificationPause(false);
	}
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:45:19 PM)
 * @param newClientServerInfo cbit.vcell.client.server.ClientServerInfo
 */
//public void setClientServerInfo(ClientServerInfo newClientServerInfo) {
//	clientServerInfo = newClientServerInfo;
//}


/**
 * Sets the connectionStatus property (cbit.vcell.client.server.ConnectionStatus) value.
 * @param connectionStatus The new value for the property.
 * @see #getConnectionStatus
 */
private void setConnectionStatus(ClientConnectionStatus connectionStatus) {
	ConnectionStatus oldValue = fieldConnectionStatus;
	fieldConnectionStatus = connectionStatus;
	firePropertyChange(PROPERTY_NAME_CONNECTION_STATUS, oldValue, connectionStatus);
}

/**
 * Insert the method's description here.
 * Creation date: (5/13/2004 12:26:57 PM)
 * @param newVcellConnection VCellConnection
 * @throws DataAccessException
 */
private void setVcellConnection(VCellConnection newVcellConnection) {
	vcellConnection = newVcellConnection;
	user = null;
	simulationController = null;
	dataSetController = null;
	userMetaDbServer = null;

	if (vcellConnection == null) {
		return;
	}
	try {
		getUser();
		getSimulationController();
		getDataSetController();
		getUserMetaDbServer();
	} catch (DataAccessException ex) {
		ex.printStackTrace(System.out);
		throw new RuntimeException("DataAccessException: "+ex.getMessage());
	}
}


/**
 * calls {@link #sendErrorReport(Throwable, cbit.vcell.server.VCellConnection.ExtraContext)} with null ExtraContext
 * @param exception
 */
public void sendErrorReport(Throwable exception ) {
	sendErrorReport(exception, null);
}
/**
 * @param exception
 * @param extraContext may be null
 */
public void sendErrorReport(Throwable exception, VCellConnection.ExtraContext extraContext) {
	try {
		getVcellConnection().sendErrorReport(exception, extraContext);
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
}

/**
 * start {@link Reconnector}, set status to {@link ConnectionStatus#DISCONNECTED}
 */
void setDisconnected() {
	getReconnector().start();
	setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), getClientServerInfo().getActiveHost(), ConnectionStatus.DISCONNECTED));
}
}
