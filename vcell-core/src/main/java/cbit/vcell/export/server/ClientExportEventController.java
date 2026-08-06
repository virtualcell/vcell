package cbit.vcell.export.server;

import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.ExportEventController;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import java.util.Hashtable;

// Old implementation of ExportServiceImpl's event controller.
public class ClientExportEventController implements ExportEventController {
    private final static Logger lg = LogManager.getLogger(ClientExportEventController.class);
    private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    private Hashtable<Long, User> jobRequestIDs = new Hashtable<Long, User>();


    public void putJobRequest(Long jobRequestID, User user){
        jobRequestIDs.put(jobRequestID, user);
    }

    public synchronized void addExportListener(ExportListener listener) {
        listenerList.add(ExportListener.class, listener);
    }


    public ExportEvent fireExportCompleted(long jobID, VCDataIdentifier vcdID, String format, String location, ExportSpecs exportSpecs) {
        User user = null;
        Object object = jobRequestIDs.get(new Long(jobID));
        if (object != null) {
            user = (User)object;
        }
        TimeSpecs timeSpecs = (exportSpecs!=null)?exportSpecs.getTimeSpecs():(null);
        VariableSpecs varSpecs = (exportSpecs!=null)?exportSpecs.getVariableSpecs():(null);
        final KeyValue dataKey;
        if (vcdID instanceof VCSimulationDataIdentifier) {
            dataKey = ((VCSimulationDataIdentifier)vcdID).getSimulationKey();
        }else if (vcdID instanceof ExternalDataIdentifier) {
            dataKey = ((ExternalDataIdentifier)vcdID).getSimulationKey();
        }else {
            throw new RuntimeException("unexpected VCDataIdentifier");
        }
        ExportEvent event = new ExportEvent(
                this, jobID, user, vcdID.getID(), dataKey, ExportEnums.ExportProgressType.EXPORT_COMPLETE,
                format, location, null);
        event.setHumanReadableExportData(exportSpecs != null ? exportSpecs.getHumanReadableExportData() : null);
        fireExportEvent(event);
        return event;
    }


    public void fireExportAssembling(long jobID, VCDataIdentifier vcdID, String format) {
        User user = null;
        Object object = jobRequestIDs.get(new Long(jobID));
        if (object != null) {
            user = (User)object;
        }
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEnums.ExportProgressType.EXPORT_ASSEMBLING, format, null, null);
        fireExportEvent(event);
    }

    public void fireExportEvent(ExportEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Reset the source to allow proper wiring
        event.setSource(this);
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ExportListener.class) {
                ((ExportListener)listeners[i+1]).exportMessage(event);
            }
        }
    }

    public void fireExportFailed(long jobID, VCDataIdentifier vcdID, String format, String message) {
        User user = null;
        Object object = jobRequestIDs.get(new Long(jobID));
        if (object != null) {
            user = (User)object;
        }
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEnums.ExportProgressType.EXPORT_FAILURE, format, message, null);
        fireExportEvent(event);
    }

    public void fireExportProgress(long jobID, VCDataIdentifier vcdID, String format, double progress) {
        User user = null;
        Object object = jobRequestIDs.get(new Long(jobID));
        if (object != null) {
            user = (User)object;
        }
        // Backstop for every EXPORT_PROGRESS event: this is the one place they are built,
        // so a non-finite value cannot escape here regardless of which exporter computed
        // it. JSON has no representation for Infinity or NaN, and such a value used to
        // make the event permanently undeliverable. Individual exporters guard their own
        // divisions (see ASCIIExporter.fraction); this catches anything they miss.
        if (!Double.isFinite(progress)) {
            lg.warn("export job {} ({}) reported non-finite progress {}; reporting 0 instead",
                    jobID, format, progress);
            progress = 0.0;
        }
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEnums.ExportProgressType.EXPORT_PROGRESS, format, null, new Double(progress));
        fireExportEvent(event);
    }


    public void fireExportStarted(long jobID, VCDataIdentifier vcdID, String format) {
        User user = null;
        Object object = jobRequestIDs.get(new Long(jobID));
        if (object != null) {
            user = (User)object;
        }
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEnums.ExportProgressType.EXPORT_START, format, null, null);
        fireExportEvent(event);
    }

    public synchronized void removeExportListener(ExportListener listener) {
        listenerList.remove(ExportListener.class, listener);
    }
}
