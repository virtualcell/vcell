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
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.VCellSoftwareVersion.VCellSite;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.*;

/**
 * Insert the type's description here.
 * Creation date: (5/12/2004 4:31:18 PM)
 *
 * @author: Ion Moraru
 */
public class ClientServerManager implements SessionManager, DataSetControllerProvider {
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
                    this.setUserName(null);
                    this.setApihost(null);
                    this.setApiport(null);
                    break;
                }
                case INITIALIZING:
                case CONNECTED:
                case DISCONNECTED: {
                    if (userName == null)
                        throw new RuntimeException("userName should be non-null unless NOT_CONNECTED");
                    this.setUserName(userName);
                    this.setApihost(apihost);
                    this.setApiport(apiport);
                    break;
                }
                default: {
                    throw new RuntimeException("unknown connection status: " + status);
                }
            }
            this.setStatus(status);
        }


        public String getApihost() {
            return this.apihost;
        }

        public Integer getApiport() {
            return this.apiport;
        }

        public int getStatus() {
            return this.status;
        }

        public java.lang.String getUserName() {
            return this.userName;
        }


        private void setApihost(java.lang.String newApihost) {
            this.apihost = newApihost;
        }

        private void setApiport(Integer newApiport) {
            this.apiport = newApiport;
        }


        private void setStatus(int newStatus) {
            this.status = newStatus;
        }

        private void setUserName(java.lang.String newUserName) {
            this.userName = newUserName;
        }

        public String toString() {
            return "status " + this.getStatus() + " apihost " + this.getApihost() + " apiport " + this.getApiport() + " user " + this.getUserName();
        }


        @Override
        public Reconnector getReconnector() {
            return ClientServerManager.this.getReconnector();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.apihost == null) ? 0 : this.apihost.hashCode());
            result = prime * result + ((this.apiport == null) ? 0 : this.apiport.hashCode());
            result = prime * result + this.status;
            result = prime * result + ((this.userName == null) ? 0 : this.userName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ClientConnectionStatus) {
                return false;
            }
            ClientConnectionStatus other = (ClientConnectionStatus) obj;
            if (!Compare.isEqualOrNull(this.apihost, other.apihost)) {
                return false;
            }
            if (!Compare.isEqualOrNull(this.apiport, other.apiport)) {
                return false;
            }
            if (this.status != other.status) {
                return false;
            }
            return Compare.isEqualOrNull(this.userName, other.userName);
        }

    }

    private enum ReconnectStatus {
        NOT("Not reconnecting"),
        FIRST("First reconnection attempt"),
        SUBSEQUENT("Subsequent reconnect attempt");

        private final String label;

        ReconnectStatus(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return this.label;
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

    private final DocumentManager documentManager = new ClientDocumentManager(this, 10000000L);
    private JobManager jobManager = null;
    private ExportController exportController = null;
    private AsynchMessageManager asynchMessageManager = null;
    private VCDataManager vcDataManager = null;
    private final UserPreferences userPreferences = new UserPreferences(this);
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
        this.getPropertyChange().addPropertyChangeListener(listener);
    }

    public ClientServerManager(VCellConnectionFactory vcellConnectionFactory, ClientServerInfo clientServerInfo, InteractiveContextDefaultProvider defaultInteractiveContextProvider) {
        this.vcellConnectionFactory = vcellConnectionFactory;
        this.clientServerInfo = clientServerInfo;
        this.defaultInteractiveContextProvider = defaultInteractiveContextProvider;
    }

    public Auth0ConnectionUtils getAuth0ConnectionUtils() {
        return this.vcellConnectionFactory.getAuth0ConnectionUtils();
    }

    /**
     * Insert the method's description here.
     * Creation date: (5/12/2004 4:48:13 PM)
     */
    private void changeConnection(InteractiveContext requester, VCellConnection newVCellConnection) {
        VCellThreadChecker.checkRemoteInvocation();

        VCellConnection lastVCellConnection = this.getVcellConnection();
        this.setVcellConnection(newVCellConnection);
        if (this.getVcellConnection() != null) {
            try {
                /* new credentials; need full init */
                // throw it away; doesn't properly support full reinits
                // preload the document manager cache
                ((ClientDocumentManager) this.getDocumentManager()).initAllDatabaseInfos();

                // load user preferences
                this.getUserPreferences().resetFromSaved(this.getDocumentManager().getPreferences());

                this.setConnectionStatus(new ClientConnectionStatus(this.getClientServerInfo().getUsername(), this.getClientServerInfo().getApihost(), this.getClientServerInfo().getApiport(), ConnectionStatus.CONNECTED));
            } catch (DataAccessException exc) {
                // unlikely, since we just connected, but it looks like we did loose the connection...
                lastVCellConnection = this.getVcellConnection();
                this.setVcellConnection(null);
                this.setConnectionStatus(new ClientConnectionStatus(this.getClientServerInfo().getUsername(), this.getClientServerInfo().getApihost(), this.getClientServerInfo().getApiport(), ConnectionStatus.DISCONNECTED));
                lg.error("Server connection failed", exc);
                requester.showErrorDialog("Server connection failed:\n\n" + exc.getMessage());
            }
        } else if (lastVCellConnection != null) {
            this.setConnectionStatus(new ClientConnectionStatus(this.getClientServerInfo().getUsername(), this.getClientServerInfo().getApihost(), this.getClientServerInfo().getApiport(), ConnectionStatus.DISCONNECTED));
        } else {
            this.setConnectionStatus(new ClientConnectionStatus(null, null, null, ConnectionStatus.NOT_CONNECTED));
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (5/25/2004 2:03:47 AM)
     *
     * @return int
     */
    public void cleanup() {
        this.setVcellConnection(null);
        this.setConnectionStatus(new ClientConnectionStatus(null, null, null, ConnectionStatus.NOT_CONNECTED));
    }

    public MessageEvent[] getMessageEvents() throws RemoteProxyException, IOException {
        if (this.vcellConnection != null && this.isStatusConnected()) {
            return this.vcellConnection.getMessageEvents();
        } else {
            return null;
        }
    }

    public MessageEvent[] getMessageEvents(long timeoutMs) throws RemoteProxyException, IOException, TimeoutException {
        Callable<MessageEvent[]> getMessageEventsCallable = this::getMessageEvents;
        FutureTask<MessageEvent[]> futureTask = new FutureTask<>(getMessageEventsCallable);
        Thread messageFetchThread = new Thread(futureTask);
        messageFetchThread.start();
        try {
            return futureTask.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error Attempting to get message events:", e);
        }
    }

    private void checkClientServerSoftwareVersion(InteractiveContext requester, ClientServerInfo clientServerInfo) {
        String clientSoftwareVersion = System.getProperty(PropertyLoader.vcellSoftwareVersion);
        if (clientSoftwareVersion != null && clientSoftwareVersion.toLowerCase().contains("devel")) {
            return;
        }
        if (clientServerInfo.getServerType() == ServerType.SERVER_REMOTE) {
            String apihost = clientServerInfo.getApihost();
            Integer apiport = clientServerInfo.getApiport();
            String pathPrefixV0 = clientServerInfo.getPathPrefix_v0();
            String serverSoftwareVersion = RemoteProxyVCellConnectionFactory.getVCellSoftwareVersion(apihost, apiport, pathPrefixV0);
            if (serverSoftwareVersion != null && !serverSoftwareVersion.equals(clientSoftwareVersion)) {
                VCellSoftwareVersion clientVersion = VCellSoftwareVersion.fromString(clientSoftwareVersion);
                VCellSoftwareVersion serverVersion = VCellSoftwareVersion.fromString(serverSoftwareVersion);
                if (clientVersion.getSite() == VCellSite.beta) {
                    requester.showWarningDialog("VCell Public Beta program has been discontinued - please use our Release VCell version\n\n"
                            + "We have adopted a Release Early, Release Often software development approach\n\n"
                            + "\nPlease exit VCell and download the latest client from VCell Software page (http://vcell.org).");
                }
                if (clientVersion.getMajorVersion() != serverVersion.getMajorVersion() ||
                        clientVersion.getMinorVersion() != serverVersion.getMinorVersion() ||
                        clientVersion.getPatchVersion() != serverVersion.getPatchVersion()) {
                    requester.showWarningDialog("software version mismatch between client and server:\n"
                            + "client VCell version : " + clientSoftwareVersion + "\n"
                            + "server VCell version : " + serverSoftwareVersion + "\n"
                            + "\nPlease exit VCell and download the latest client from VCell Software page (http://vcell.org).");
                }
            }
        }
    }


    public void connectNewServer(InteractiveContext requester, ClientServerInfo csi) {
        this.clientServerInfo = csi;
        this.connect(requester);
    }

    /**
     * Insert the method's description here.
     * Creation date: (5/17/2004 6:26:14 PM)
     */
    public void connect(InteractiveContext requester) {
        this.asynchMessageManager.stopPolling();
        this.reconnectStat = ReconnectStatus.NOT;
        this.checkClientServerSoftwareVersion(requester, this.clientServerInfo);

        // get new server connection
        VCellConnection newVCellConnection = this.connectToServer(requester, true);
        // update managers, status, etc.
        this.changeConnection(requester, newVCellConnection);
        if (this.fieldConnectionStatus.getStatus() == ConnectionStatus.CONNECTED) {
            //start polling if haven't already
            this.asynchMessageManager.startPolling();
        }
    }

    /**
     * same as {@link #connect(InteractiveContext)} but pause {@link Reconnector}
     *
     * @param requester
     */
    public void reconnect(InteractiveContext requester) {
        Reconnector rc = this.getReconnector();
        try {
            rc.start();
            rc.notificationPause(true);
            this.connect(requester);
            if (this.fieldConnectionStatus.getStatus() == ConnectionStatus.CONNECTED) {
                rc.stop();
                this.asynchMessageManager.startPolling();
            }
        } finally {
            rc.notificationPause(false);
        }
    }

//public void connectAs(InteractiveContext requester, String user) {
//	reconnectStat = ReconnectStatus.NOT;
//	switch (getClientServerInfo().getServerType()) {
//		case SERVER_LOCAL: {
//			clientServerInfo = ClientServerInfo.createLocalServerInfo(user);
//			break;
//		}
//		case SERVER_REMOTE: {
//			clientServerInfo = ClientServerInfo.createRemoteServerInfo(
//					getClientServerInfo().getApihost(), getClientServerInfo().getApiport(), getClientServerInfo().getPathPrefix_v0(),
//					user);
//			break;
//		}
//	}
//	connect(requester);
//}


    /**
     * Insert the method's description here.
     * Creation date: (5/12/2004 4:48:13 PM)
     */
    private VCellConnection connectToServer(InteractiveContext requester, boolean bShowErrors) {
        try {
            // see static-files-config ConfigMap for definitions of dynamic properties as deployed
            String url_path = PropertyLoader.getProperty(PropertyLoader.DYNAMIC_PROPERTIES_URL_PATH, "/vcell_dynamic_properties.csv");
            boolean isHTTP = PropertyLoader.getBooleanProperty(PropertyLoader.isHTTP, false);
            String webapp_base_url = isHTTP ? "http://" : "https://" + this.getClientServerInfo().getApihost() + ":" + this.getClientServerInfo().getApiport();
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
                String apihost = this.getClientServerInfo().getApihost();
                Integer apiport = this.getClientServerInfo().getApiport();
                Auth0ConnectionUtils auth0ConnectionUtils = this.vcellConnectionFactory.getAuth0ConnectionUtils();
                String username = User.isGuest(this.getClientServerInfo().getUsername()) ? this.getClientServerInfo().getUsername() : auth0ConnectionUtils.getAuth0MappedUser();
                this.setConnectionStatus(new ClientConnectionStatus(username, apihost, apiport, ConnectionStatus.INITIALIZING));
                newVCellConnection = this.vcellConnectionFactory.createVCellConnection(this.getClientServerInfo().getUserLoginInfo());
                requester.clearConnectWarning();
                this.reconnectStat = ReconnectStatus.NOT;
            } catch (Exception e) {
                lg.error(e.getMessage(), e);
                if (bShowErrors) {
                    throw e;
                }
            }
        } catch (ConnectionException cexc) {
            String msg = this.badConnectMessage(badConnStr) + "\n" + cexc.getMessage();
            lg.error(cexc);
            ErrorUtils.sendRemoteLogMessage(this.getClientServerInfo().getUserLoginInfo(), msg);
            if (this.reconnectStat != ReconnectStatus.SUBSEQUENT) {
                requester.showConnectWarning(msg);
            }
        } catch (Exception exc) {
            lg.error(exc.getMessage(), exc);
            String msg = "Exception: " + exc.getMessage() + "\n\n" + this.badConnectMessage(badConnStr);
            ErrorUtils.sendRemoteLogMessage(this.getClientServerInfo().getUserLoginInfo(), msg);
            requester.showErrorDialog(msg);
        }

        return newVCellConnection;
    }

    private String badConnectMessage(String badConnStr) {
        String ctype = this.reconnectStat == ReconnectStatus.NOT ? "connect" : "reconnect";
        return VCellErrorMessages.getErrorMessage(VCellErrorMessages.BAD_CONNECTION_MESSAGE, ctype, badConnStr);
    }


    public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFielOperationSpec) throws DataAccessException {
        return this.getVCDataManager().fieldDataFileOperation(fieldDataFielOperationSpec);
    }

    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
        this.getPropertyChange().firePropertyChange(evt);
    }


    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
        this.getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
        this.getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
        this.getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * Insert the method's description here.
     * Creation date: (6/9/2004 3:21:46 PM)
     *
     * @return cbit.vcell.client.AsynchMessageManager
     */
    public AsynchMessageManager getAsynchMessageManager() {
        if (this.asynchMessageManager == null) {
            this.asynchMessageManager = new AsynchMessageManager(this);
        }
        return this.asynchMessageManager;
    }


    /**
     * Insert the method's description here.
     * Creation date: (5/12/2004 4:45:19 PM)
     *
     * @return cbit.vcell.client.server.ClientServerInfo
     */
    public ClientServerInfo getClientServerInfo() {
        return this.clientServerInfo;
    }


    /**
     * Gets the connectionStatus property (cbit.vcell.client.server.ConnectionStatus) value.
     *
     * @return The connectionStatus property value.
     * @see #setConnectionStatus
     */
    public ConnectionStatus getConnectionStatus() {
        return this.fieldConnectionStatus;
    }

    public boolean isStatusConnected() {
        return (this.fieldConnectionStatus.getStatus() == ConnectionStatus.CONNECTED);
    }

    /**
     * Insert the method's description here.
     * Creation date: (5/13/2004 1:54:04 PM)
     *
     * @return UserMetaDbServer
     */
    public synchronized DataSetController getDataSetController() throws DataAccessException {
        VCellThreadChecker.checkRemoteInvocation();
        if (this.dataSetController != null) {
            return this.dataSetController;
        } else if (this.getVcellConnection() == null) {
            throw new RuntimeException("cannot get Simulation Data Server, no VCell Connection\ntry Server->Reconnect");
        } else {
            try {
                this.dataSetController = this.getVcellConnection().getDataSetController();
                return this.dataSetController;
            } catch (RemoteProxyException rexc) {
                lg.error(rexc);
                try {
                    // one more time before we fail../
                    this.dataSetController = this.getVcellConnection().getDataSetController();
                    return this.dataSetController;
                } catch (RemoteProxyException rexc2) {
                    throw new DataAccessException("RemoteProxyException: " + rexc2.getMessage(), rexc2);
                }
            }
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (5/13/2004 1:54:04 PM)
     *
     * @return cbit.vcell.clientdb.DocumentManager
     */
    public DocumentManager getDocumentManager() {
        return this.documentManager;
    }


    /**
     * Insert the method's description here.
     * Creation date: (5/13/2004 1:54:04 PM)
     */
    public ExportController getExportController() {
        if (this.exportController == null) {
            this.exportController = new ClientExportController(this);
        }
        return this.exportController;
    }


    /**
     * Insert the method's description here.
     * Creation date: (5/13/2004 1:54:04 PM)
     *
     * @return cbit.vcell.clientdb.JobManager
     */
    public JobManager getJobManager() {
        if (this.jobManager == null) {
            this.jobManager = new ClientJobManager(this);
            this.getAsynchMessageManager().addSimulationJobStatusListener(this.jobManager);
            this.jobManager.addSimStatusListener(this.getAsynchMessageManager());
        }
        return this.jobManager;
    }


    /**
     * Accessor for the propertyChange field.
     */
    protected java.beans.PropertyChangeSupport getPropertyChange() {
        if (this.propertyChange == null) {
            this.propertyChange = new java.beans.PropertyChangeSupport(this);
        }
        return this.propertyChange;
    }

    /**
     * Insert the method's description here.
     * Creation date: (5/13/2004 1:54:04 PM)
     *
     * @return UserMetaDbServer
     */
    public synchronized SimulationController getSimulationController() {
        VCellThreadChecker.checkRemoteInvocation();
        if (this.simulationController != null) {
            return this.simulationController;
        } else if (this.getVcellConnection() == null) {
            throw new RuntimeException("cannot get Simulation Server, no VCell Connection\ntry Server->Reconnect");
        } else {
            try {
                this.simulationController = this.getVcellConnection().getSimulationController();
                return this.simulationController;
            } catch (RemoteProxyException rexc) {
                lg.error(rexc);
                try {
                    // one more time before we fail../
                    this.simulationController = this.getVcellConnection().getSimulationController();
                    return this.simulationController;
                } catch (RemoteProxyException rexc2) {
                    throw new RuntimeException("RemoteProxyException: " + rexc2.getMessage(), rexc2);
                }
            }
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (5/13/2004 1:54:04 PM)
     *
     * @return cbit.vcell.server.URLFinder
     */
    public synchronized User getUser() {
        if (this.user != null) {
            return this.user;
        } else if (this.getVcellConnection() == null) {
            return null;
        } else {
            VCellThreadChecker.checkRemoteInvocation();
            try {
                this.user = this.getVcellConnection().getUserLoginInfo().getUser();
                return this.user;
            } catch (RemoteProxyException rexc) {
                throw new RuntimeException("RemoteProxyException: " + rexc.getMessage(), rexc);
            }
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (5/13/2004 1:54:04 PM)
     *
     * @return UserMetaDbServer
     */
    public synchronized UserMetaDbServer getUserMetaDbServer() throws DataAccessException {
        VCellThreadChecker.checkRemoteInvocation();
        if (this.userMetaDbServer != null) return this.userMetaDbServer;

        if (this.getVcellConnection() == null) {
            throw new RuntimeException("cannot get Database Server, no VCell Connection\ntry Server->Reconnect");
        } else {
            try {
                this.userMetaDbServer = this.getVcellConnection().getUserMetaDbServer();
                return this.userMetaDbServer;
            } catch (RemoteProxyException rexc) {
                lg.error(rexc);
                try {
                    // one more time before we fail../
                    this.userMetaDbServer = this.getVcellConnection().getUserMetaDbServer();
                    return this.userMetaDbServer;
                } catch (RemoteProxyException rexc2) {
                    throw new DataAccessException("RemoteProxyException: " + rexc2.getMessage(), rexc2);
                }
            }
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (5/24/2004 8:19:36 PM)
     *
     * @return cbit.vcell.client.UserPreferences
     */
    public UserPreferences getUserPreferences() {
        return this.userPreferences;
    }


    /**
     * Insert the method's description here.
     * Creation date: (6/9/2004 3:21:46 PM)
     *
     * @return cbit.vcell.client.VCDataManager
     */
    public VCDataManager getVCDataManager() {
        if (this.vcDataManager == null) {
            this.vcDataManager = new VCDataManager(this);
        }
        return this.vcDataManager;
    }


    /**
     * Insert the method's description here.
     * Creation date: (5/13/2004 12:26:57 PM)
     *
     * @return VCellConnection
     */
    private VCellConnection getVcellConnection() {
        return this.vcellConnection;
    }


    /**
     * The hasListeners method was generated to support the propertyChange field.
     */
    public synchronized boolean hasListeners(java.lang.String propertyName) {
        return this.getPropertyChange().hasListeners(propertyName);
    }

    /**
     * @return lazily created {@link Reconnector }
     */
    private Reconnector getReconnector() {
        if (this.reconnector == null) {
            this.reconnector = new Reconnector(this);
        }
        return this.reconnector;
    }

    /**
     * commence automatic reconnect attempts
     */
    void attemptReconnect() {
        this.getReconnector().start();
    }

    /**
     * attempt reconnect now
     */
    void reconnect() {
        Reconnector rc = this.getReconnector();
        rc.notificationPause(true);
        try {
            switch (this.reconnectStat) {
                case NOT:
                    this.reconnectStat = ReconnectStatus.FIRST;
                    break;
                case FIRST:
                    this.reconnectStat = ReconnectStatus.SUBSEQUENT;
                    break;
                default:
            }
            InteractiveContext requester = this.defaultInteractiveContextProvider.getInteractiveContext();
            VCellConnection connection = this.connectToServer(requester, false);
            if (connection != null) { //success
                this.changeConnection(requester, connection);
                rc.stop();
                this.asynchMessageManager.startPolling();
                return;
            }
            this.setConnectionStatus(new ClientConnectionStatus(this.getClientServerInfo().getUsername(), this.getClientServerInfo().getApihost(), this.getClientServerInfo().getApiport(), ConnectionStatus.DISCONNECTED));
        } finally {
            rc.notificationPause(false);
        }
    }


    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        this.getPropertyChange().removePropertyChangeListener(listener);
    }


    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
        this.getPropertyChange().removePropertyChangeListener(propertyName, listener);
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
     *
     * @param connectionStatus The new value for the property.
     * @see #getConnectionStatus
     */
    private void setConnectionStatus(ClientConnectionStatus connectionStatus) {
        ConnectionStatus oldValue = this.fieldConnectionStatus;
        this.fieldConnectionStatus = connectionStatus;
        this.firePropertyChange(PROPERTY_NAME_CONNECTION_STATUS, oldValue, connectionStatus);
    }

    /**
     * Insert the method's description here.
     * Creation date: (5/13/2004 12:26:57 PM)
     *
     * @param newVcellConnection VCellConnection
     * @throws DataAccessException
     */
    private void setVcellConnection(VCellConnection newVcellConnection) {
        this.vcellConnection = newVcellConnection;
        this.user = null;
        this.simulationController = null;
        this.dataSetController = null;
        this.userMetaDbServer = null;

        if (this.vcellConnection == null) {
            return;
        }
        try {
            this.getUser();
            this.getSimulationController();
            this.getDataSetController();
            this.getUserMetaDbServer();
        } catch (DataAccessException ex) {
            throw new RuntimeException("DataAccessException: " + ex.getMessage(), ex);
        }
    }


    /**
     * calls {@link #sendErrorReport(Throwable, cbit.vcell.server.VCellConnection.ExtraContext)} with null ExtraContext
     *
     * @param exception
     */
    public void sendErrorReport(Throwable exception) {
        this.sendErrorReport(exception, null);
    }

    /**
     * @param exception
     * @param extraContext may be null
     */
    public void sendErrorReport(Throwable exception, VCellConnection.ExtraContext extraContext) {
        try {
            this.getVcellConnection().sendErrorReport(exception, extraContext);
        } catch (Exception ex) {
            lg.error(ex.getMessage(), ex);
        }
    }

    /**
     * start {@link Reconnector}, set status to {@link ConnectionStatus#DISCONNECTED}
     */
    void setDisconnected() {
        this.getReconnector().start();
        this.setConnectionStatus(new ClientConnectionStatus(this.getClientServerInfo().getUsername(), this.getClientServerInfo().getApihost(), this.getClientServerInfo().getApiport(), ConnectionStatus.DISCONNECTED));
    }

    public RegistrationService getRegistrationProvider() {
        return new VCellConnectionRegistrationProvider(this.vcellConnection);
    }
}
