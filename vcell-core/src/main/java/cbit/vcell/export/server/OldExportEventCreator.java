package cbit.vcell.export.server;

import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.ExportStatusEventCreator;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import java.util.Hashtable;

public class OldExportEventCreator implements ExportStatusEventCreator {
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
                this, jobID, user, vcdID.getID(), dataKey, ExportSpecss.ExportProgressType.EXPORT_COMPLETE,
                format, location, null, timeSpecs, varSpecs);
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
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportSpecss.ExportProgressType.EXPORT_ASSEMBLING, format, null, null, null, null);
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
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportSpecss.ExportProgressType.EXPORT_FAILURE, format, message, null, null, null);
        fireExportEvent(event);
    }

    public void fireExportProgress(long jobID, VCDataIdentifier vcdID, String format, double progress) {
        User user = null;
        Object object = jobRequestIDs.get(new Long(jobID));
        if (object != null) {
            user = (User)object;
        }
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportSpecss.ExportProgressType.EXPORT_PROGRESS, format, null, new Double(progress), null, null);
        fireExportEvent(event);
    }


    public void fireExportStarted(long jobID, VCDataIdentifier vcdID, String format) {
        User user = null;
        Object object = jobRequestIDs.get(new Long(jobID));
        if (object != null) {
            user = (User)object;
        }
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportSpecss.ExportProgressType.EXPORT_START, format, null, null, null, null);
        fireExportEvent(event);
    }

    public synchronized void removeExportListener(ExportListener listener) {
        listenerList.remove(ExportListener.class, listener);
    }
}
