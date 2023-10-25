package cbit.vcell.export.server.events;

import cbit.rmi.event.*;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import org.vcell.util.document.*;

public class ExportEventCommander {
    private final javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();

    public synchronized void addExportListener(ExportListener listener) {
        this.listenerList.add(ExportListener.class, listener);
    }

    public synchronized void removeExportListener(ExportListener listener) {
        listenerList.remove(ExportListener.class, listener);
    }

    protected ExportEvent fireExportEvent(ExportEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Reset the source to allow proper wiring
        event.setSource(this);

        // Process the listeners last to first, notifying those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] != ExportListener.class) continue;
            ((ExportListener)listeners[i+1]).exportMessage(event);
        }
        return event;
    }

    public ExportEvent fireExportCompleted(long jobID, User user, VCDataIdentifier vcdID, String format,
                                              String location, ExportSpecs exportSpecs) {
        if (!(vcdID instanceof SimResampleInfoProvider infoId))  // Create pattern variable (yay java 17!)
            throw new RuntimeException("unexpected VCDataIdentifier");

        ExportEvent event = new ExportCompleteEvent(this, jobID, user,
                infoId.getID(), infoId.getSimulationKey(), format, location,
                (exportSpecs != null ? exportSpecs.getTimeSpecs() : null),
                (exportSpecs != null ? exportSpecs.getVariableSpecs() : null));
        return this.fireExportEvent(event);
    }

    public void fireExportAssembling(long jobID, User user, VCDataIdentifier vcdID, String format) {
        ExportEvent event = new ExportAssemblingEvent(this, jobID, user, vcdID, format);
        this.fireExportEvent(event);
    }

    public void fireExportFailed(long jobID, User user, VCDataIdentifier vcdID, String format, String message) {
        ExportEvent event = new ExportFailureEvent(this, jobID, user, vcdID, format, message);
        this.fireExportEvent(event);
    }

    public void fireExportProgress(long jobID, User user, VCDataIdentifier vcdID, String format, double progress) {
        ExportEvent event = new ExportProgressEvent(this, jobID, user, vcdID, format, progress);
        this.fireExportEvent(event);
    }


    /**
     * Insert the method's description here.
     * Creation date: (4/1/2001 11:20:45 AM)
     * @deprecated
     */
    public void fireExportStarted(long jobID, User user, VCDataIdentifier vcdID, String format) {
        ExportEvent event = new ExportStartEvent(this, jobID, user, vcdID, format);
       this.fireExportEvent(event);
    }
}
