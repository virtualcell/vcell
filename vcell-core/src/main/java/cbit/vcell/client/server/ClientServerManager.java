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

import cbit.rmi.event.MessageEvent;
import cbit.vcell.client.server.ClientServerInfo.ServerType;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.resource.ErrorUtils;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.*;
import cbit.vcell.simdata.VCDataManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.service.registration.RegistrationService;
import org.vcell.util.*;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.VCellSoftwareVersion.VCellSite;

import java.io.IOException;
import java.net.URL;

/**
 * Insert the type's description here.
 * Creation date: (5/12/2004 4:31:18 PM)
 * @author: Ion Moraru
 */
public class ClientServerManager implements SessionManager,DataSetControllerProvider {
	private final static Logger lg = LogManager.getLogger(ClientServerManager.class);
	private final VCellConnectionFactory vcellConnectionFactory;

	public interface InteractiveContext {
		void showErrorDialog(String errorMessage);
		
		void showWarningDialog(String warningMessage);
		
		void clearConnectWarning();
		
		void showConnectWarning(String message);
	}
	
	public interface InteractiveContextDefaultProvider {
		InteractiveContext getInteractiveContext();
	}
	
	public static final String PROPERTY_NAME_CONNECTION_STATUS = "connectionStatus";
	private class ClientConnectionStatus implements ConnectionStatus {
		// actual status info
		private String apihost = null;
		private Integer apiport = null;
		private String userName = null;
		private int status = NOT_CONNECTED;

		private ClientConnectionStatus(String userName, String apihost, Integer apiport, int status) {
			switch (status) {
				case NOT_CONNECTED: {
					setUserName(null);
					setApihost(null);
					setApiport(null);
					break;
				}
				case INITIALIZING:
				case CONNECTED:
				case DISCONNECTED: {
					if (userName == null) throw new RuntimeException("userName should be non-null unless NOT_CONNECTED");
					setUserName(userName);
					setApihost(apihost);
					setApiport(apiport);
					break;
				}
				default: {
					throw new RuntimeException("unknown connection status: " + status);
				}
			}
			setStatus(status);
		}


		public String getApihost() {
			return apihost;
		}

		public Integer getApiport() {
			return apiport;
		}

		public int getStatus() {
			return status;
		}

		public java.lang.String getUserName() {
			return userName;
		}


		private void setApihost(java.lang.String newApihost) {
			this.apihost = newApihost;
		}

		private void setApiport(Integer newApiport) {
			this.apiport = newApiport;
		}


		private void setStatus(int newStatus) {
			status = newStatus;
		}

		private void setUserName(java.lang.String newUserName) {
			userName = newUserName;
		}

		public String toString() {
			return "status " + getStatus() + " apihost " + getApihost() + " apiport " + getApiport() + " user " + getUserName();
		}



		@Override
		public Reconnector getReconnector() {
			return ClientServerManager.this.getReconnector();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((apihost == null) ? 0 : apihost.hashCode());
			result = prime * result + ((apiport == null) ? 0 : apiport.hashCode());
			result = prime * result + status;
			result = prime * result + ((userName == null) ? 0 : userName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ClientConnectionStatus) {
				return false;
			}
			ClientConnectionStatus other = (ClientConnectionStatus) obj;
			if (!Compare.isEqualOrNull(apihost, other.apihost)) {
				return false;
			}
			if (!Compare.isEqualOrNull(apiport, other.apiport)) {
				return false;
			}
			if (status != other.status) {
				return false;
			}
			if (!Compare.isEqualOrNull(userName, other.userName)) {
				return false;
			}
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
	private ClientConnectionStatus fieldConnectionStatus = new ClientConnectionStatus(null, null, null, ConnectionStatus.NOT_CONNECTED);
	private ReconnectStatus reconnectStat = ReconnectStatus.NOT;
	private final InteractiveContextDefaultProvider defaultInteractiveContextProvider;
	/**
	 * modeless window to warn about lost connection
	 */
	private Reconnector reconnector = null;

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

public ClientServerManager(VCellConnectionFactory vcellConnectionFactory, ClientServerInfo clientServerInfo, InteractiveContextDefaultProvider defaultInteractiveContextProvider) {
	this.vcellConnectionFactory = vcellConnectionFactory;
	this.clientServerInfo = clientServerInfo;
	this.defaultInteractiveContextProvider = defaultInteractiveContextProvider;
}

	public Auth0ConnectionUtils getAuth0ConnectionUtils() {
		return vcellConnectionFactory.getAuth0ConnectionUtils();
	}

/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:48:13 PM)
 */
private void changeConnection(InteractiveContext requester, VCellConnection newVCellConnection) {
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

			setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), getClientServerInfo().getApihost(), getClientServerInfo().getApiport(), ConnectionStatus.CONNECTED));
		} catch (DataAccessException exc) {
			// unlikely, since we just connected, but it looks like we did loose the connection...
			lastVCellConnection = getVcellConnection();
			setVcellConnection(null);
			setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), getClientServerInfo().getApihost(), getClientServerInfo().getApiport(), ConnectionStatus.DISCONNECTED));
			lg.error("Server connection failed", exc);
			requester.showErrorDialog("Server connection failed:\n\n" + exc.getMessage());
		}
	} else if(lastVCellConnection != null) {
		setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), getClientServerInfo().getApihost(), getClientServerInfo().getApiport(),  ConnectionStatus.DISCONNECTED));
	} else {
		setConnectionStatus(new ClientConnectionStatus(null, null, null, ConnectionStatus.NOT_CONNECTED));
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

public MessageEvent[] getMessageEvents() throws RemoteProxyException, IOException{
	if (vcellConnection!=null && isStatusConnected()){
		return vcellConnection.getMessageEvents();
	} else {
		return null;
	}
}

private void checkClientServerSoftwareVersion(InteractiveContext requester, ClientServerInfo clientServerInfo) {
	String clientSoftwareVersion = System.getProperty(PropertyLoader.vcellSoftwareVersion);
	if (clientSoftwareVersion != null &&  clientSoftwareVersion.toLowerCase().contains("devel") ) {
		return;
	}
	if (clientServerInfo.getServerType() == ServerType.SERVER_REMOTE) {
		String apihost = clientServerInfo.getApihost();
		Integer apiport = clientServerInfo.getApiport();
		String pathPrefixV0 = clientServerInfo.getPathPrefix_v0();
		String serverSoftwareVersion = RemoteProxyVCellConnectionFactory.getVCellSoftwareVersion(apihost,apiport,pathPrefixV0);
		if (serverSoftwareVersion != null && !serverSoftwareVersion.equals(clientSoftwareVersion)) {
			VCellSoftwareVersion clientVersion = VCellSoftwareVersion.fromString(clientSoftwareVersion);
			VCellSoftwareVersion serverVersion = VCellSoftwareVersion.fromString(serverSoftwareVersion);
			if (clientVersion.getSite()==VCellSite.beta) {
				requester.showWarningDialog("VCell Public Beta program has been discontinued - please use our Release VCell version\n\n"
					+ "We have adopted a Release Early, Release Often software development approach\n\n"
					+ "\nPlease exit VCell and download the latest client from VCell Software page (http://vcell.org).");
			}
			if (clientVersion.getMajorVersion()!=serverVersion.getMajorVersion() ||
				clientVersion.getMinorVersion()!=serverVersion.getMinorVersion() ||
				clientVersion.getPatchVersion()!=serverVersion.getPatchVersion()) {
				requester.showWarningDialog("software version mismatch between client and server:\n"
					+ "client VCell version : " + clientSoftwareVersion + "\n"
					+ "server VCell version : " + serverSoftwareVersion + "\n"
					+ "\nPlease exit VCell and download the latest client from VCell Software page (http://vcell.org).");
			}
		}
	}
}


public void connectNewServer(InteractiveContext requester, ClientServerInfo csi) {
	clientServerInfo = csi;
	connect(requester);
}

/**
 * Insert the method's description here.
 * Creation date: (5/17/2004 6:26:14 PM)
 */
public void connect(InteractiveContext requester) {
	asynchMessageManager.stopPolling();
	reconnectStat = ReconnectStatus.NOT;
	checkClientServerSoftwareVersion(requester,clientServerInfo);

	// get new server connection
	VCellConnection newVCellConnection = connectToServer(requester,true);
	// update managers, status, etc.
	changeConnection(requester, newVCellConnection);
	if (fieldConnectionStatus.getStatus() == ConnectionStatus.CONNECTED) {
		//start polling if haven't already
		asynchMessageManager.startPolling();
	}
}

/**
 * same as {@link #connect(InteractiveContext)} but pause {@link Reconnector}
 * @param requester
 */
public void reconnect(InteractiveContext requester) {
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

public void connectAs(InteractiveContext requester, String user, DigestedPassword digestedPassword) {
	reconnectStat = ReconnectStatus.NOT;
	switch (getClientServerInfo().getServerType()) {
		case SERVER_LOCAL: {
			clientServerInfo = ClientServerInfo.createLocalServerInfo(user, digestedPassword);
			break;
		}
		case SERVER_REMOTE: {
			clientServerInfo = ClientServerInfo.createRemoteServerInfo(
					getClientServerInfo().getApihost(), getClientServerInfo().getApiport(), getClientServerInfo().getPathPrefix_v0(),
					user, digestedPassword);
			break;
		}
	}
	connect(requester);
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:48:13 PM)
 */
private VCellConnection connectToServer(InteractiveContext requester,boolean bShowErrors) {
	try {
		// see static-files-config ConfigMap for definitions of dynamic properties as deployed
		String url_path = PropertyLoader.getProperty(PropertyLoader.DYNAMIC_PROPERTIES_URL_PATH, "/vcell_dynamic_properties.csv");
		boolean isHTTP = PropertyLoader.getBooleanProperty(PropertyLoader.isHTTP, false);
		String webapp_base_url = isHTTP ? "http://" : "https://" + getClientServerInfo().getApihost() + ":" + getClientServerInfo().getApiport();
		URL vcell_dynamic_client_properties_url = new URL(webapp_base_url + url_path);
		DynamicClientProperties.updateDynamicClientProperties(vcell_dynamic_client_properties_url);
	} catch (Exception e) {
		lg.error(e.getMessage(), e);
	}
	VCellThreadChecker.checkRemoteInvocation();

	VCellConnection newVCellConnection = null;
	String badConnStr = "";
	try {
		try {
			String apihost = getClientServerInfo().getApihost();
			Integer apiport = getClientServerInfo().getApiport();
			Auth0ConnectionUtils auth0ConnectionUtils = vcellConnectionFactory.getAuth0ConnectionUtils();
			String username = User.isGuest(getClientServerInfo().getUsername()) ? getClientServerInfo().getUsername() : auth0ConnectionUtils.getAuth0MappedUser();
			setConnectionStatus(new ClientConnectionStatus(username, apihost, apiport, ConnectionStatus.INITIALIZING));
			newVCellConnection = vcellConnectionFactory.createVCellConnectionAuth0(getClientServerInfo().getUserLoginInfo());
			requester.clearConnectWarning();
			reconnectStat = ReconnectStatus.NOT;
		}catch(Exception e) {
			lg.error(e.getMessage(), e);
			if(bShowErrors) {
				throw e;
			}
		}
	} catch (ConnectionException cexc) {
		String msg = badConnectMessage(badConnStr) + "\n" + cexc.getMessage();
		lg.error(cexc);
		ErrorUtils.sendRemoteLogMessage(getClientServerInfo().getUserLoginInfo(),msg);
		if (reconnectStat != ReconnectStatus.SUBSEQUENT) {
			requester.showConnectWarning(msg);
		}
	} catch (Exception exc) {
		lg.error(exc.getMessage(), exc);
		String msg = "Exception: "+exc.getMessage() + "\n\n" + badConnectMessage(badConnStr);
		ErrorUtils.sendRemoteLogMessage(getClientServerInfo().getUserLoginInfo(),msg);
		requester.showErrorDialog(msg);
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
		} catch (RemoteProxyException rexc) {
			lg.error(rexc);
			try {
				// one more time before we fail../
				dataSetController = getVcellConnection().getDataSetController();
				return dataSetController;
			} catch (RemoteProxyException rexc2) {
				throw new DataAccessException("RemoteProxyException: "+rexc2.getMessage(), rexc2);
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
		} catch (RemoteProxyException rexc) {
			lg.error(rexc);
			try {
				// one more time before we fail../
				simulationController = getVcellConnection().getSimulationController();
				return simulationController;
			} catch (RemoteProxyException rexc2) {
				throw new RuntimeException("RemoteProxyException: "+rexc2.getMessage(), rexc2);
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
		} catch (RemoteProxyException rexc) {
			throw new RuntimeException("RemoteProxyException: "+rexc.getMessage(), rexc);
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
		} catch (RemoteProxyException rexc) {
			lg.error(rexc);
			try {
				// one more time before we fail../
				userMetaDbServer = getVcellConnection().getUserMetaDbServer();
				return userMetaDbServer;
			} catch (RemoteProxyException rexc2) {
				throw new DataAccessException("RemoteProxyException: "+rexc2.getMessage(), rexc2);
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
		InteractiveContext requester = defaultInteractiveContextProvider.getInteractiveContext();
		VCellConnection connection = connectToServer(requester,false);
		if (connection != null) { //success
			changeConnection(requester,connection);
			rc.stop();
			asynchMessageManager.startPolling();
			return;
		}
		setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), getClientServerInfo().getApihost(), getClientServerInfo().getApiport(), ConnectionStatus.DISCONNECTED));
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
		throw new RuntimeException("DataAccessException: "+ex.getMessage(), ex);
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
		lg.error(ex.getMessage(), ex);
	}
}

/**
 * start {@link Reconnector}, set status to {@link ConnectionStatus#DISCONNECTED}
 */
void setDisconnected() {
	getReconnector().start();
	setConnectionStatus(new ClientConnectionStatus(getClientServerInfo().getUsername(), getClientServerInfo().getApihost(), getClientServerInfo().getApiport(), ConnectionStatus.DISCONNECTED));
}

public RegistrationService getRegistrationProvider() {
	return new VCellConnectionRegistrationProvider(this.vcellConnection);
}
}
