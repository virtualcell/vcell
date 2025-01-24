package cbit.vcell.client.server;

import cbit.rmi.event.*;
import org.vcell.util.DataAccessException;

public interface AsyncMessageManagerInterface extends SimStatusListener, DataAccessException.Listener,DataJobListenerHolder {
    void addVCellMessageEventListener(VCellMessageEventListener listener);
    void removeDataJobListener(DataJobListener listener);
    void removeExportListener(ExportListener listener);
    void removeSimStatusListener(SimStatusListener listener);
    void removeSimulationJobStatusListener(SimulationJobStatusListener listener);
    void removeVCellMessageEventListener(VCellMessageEventListener listener);
    void simStatusChanged(SimStatusEvent simStatusEvent);
}
