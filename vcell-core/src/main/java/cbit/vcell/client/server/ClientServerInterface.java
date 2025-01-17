package cbit.vcell.client.server;

import cbit.rmi.event.MessageEvent;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory;
import cbit.vcell.server.DataSetControllerProvider;
import cbit.vcell.server.ExportController;
import cbit.vcell.server.SessionManager;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.simdata.VCDataManager;
import org.vcell.service.registration.RegistrationService;
import org.vcell.util.DataAccessException;

import java.io.IOException;

public interface ClientServerInterface extends SessionManager, DataSetControllerProvider {
    void addPropertyChangeListener(java.beans.PropertyChangeListener listener);

    public void cleanup();
    public MessageEvent[] getMessageEvents() throws RemoteProxyVCellConnectionFactory.RemoteProxyException, IOException;

    public void connectNewServer(InteractiveClientServerContext requester, ClientServerInfo csi);
    public void connect(InteractiveClientServerContext requester);
    public void reconnect(InteractiveClientServerContext requester);
    public void connectAs(InteractiveClientServerContext requester, String user);
    public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFielOperationSpec) throws DataAccessException;

    public void firePropertyChange(java.beans.PropertyChangeEvent evt);
    public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue);
    public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue);
    public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue);
    public ClientServerInfo getClientServerInfo();
    public boolean isStatusConnected();
    public DocumentManager getDocumentManager();
    public ExportController getExportController();
    public JobManager getJobManager();
    public UserPreferences getUserPreferences();
    public VCDataManager getVCDataManager();
    public boolean hasListeners(java.lang.String propertyName);
    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener);
    public void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener);
    public void sendErrorReport(Throwable exception );
    public void sendErrorReport(Throwable exception, VCellConnection.ExtraContext extraContext);
    public RegistrationService getRegistrationProvider();
}
