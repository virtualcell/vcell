package org.vcell.restq.services.Exports;

import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.ExportStatusEventCreator;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.MultiEmitterProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ExportStatusCreator implements ExportStatusEventCreator {
    private final ConcurrentHashMap<Long, User> jobRequestToUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MultiEmitterProcessor<ExportEvent>> listeners = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ExportEvent> mostRecentExportEvents = new ConcurrentHashMap<>();

    protected ExportSpecs exportExists(ExportSpecs exportSpecs) {
        // Call DB for export history and check if it exists. Store in cache if it exists for future requests.
        return exportSpecs;
    }

    public ExportEvent fireExportCompleted(long jobID, VCDataIdentifier vcdID, String format, String location, ExportSpecs exportSpecs) {
        User user = jobRequestToUser.get(jobID);
        String key = entryKey(user, jobID);

        if (!listeners.containsKey(key)) {
            throw new RuntimeException("Did not find entry for user " + user.getName() + " and job id " + jobID);
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
                this, jobID, user, vcdID.getID(), dataKey, ExportEvent.EXPORT_COMPLETE,
                format, location, null, timeSpecs, varSpecs);
        event.setHumanReadableExportData(exportSpecs != null ? exportSpecs.getHumanReadableExportData() : null);

        event.setHumanReadableExportData(exportSpecs.getHumanReadableExportData());
        listeners.get(key).onNext(event);
        listeners.get(key).complete();
        mostRecentExportEvents.put(key, event);
        removeServerExportListener(user, jobID);
        return event;
    }

    public synchronized void addExportListener(ExportListener listener) {
        throw new IllegalCallerException("addExportListener not implemented for the server");
    }

    public synchronized Multi<ExportEvent> getUsersExportStatus(User user, long exportJobID) throws ObjectNotFoundException {
        String key = entryKey(user, exportJobID);
        if (!listeners.containsKey(key)) {
            throw new ObjectNotFoundException("Did not find entry for user " + user.getName() + " and job id " + exportJobID);
        }
        return listeners.get(key).toMulti();
    }

    public synchronized ExportEvent getMostRecentExportStatus (User user, long exportJobID) throws ObjectNotFoundException {
        String key = entryKey(user, exportJobID);
        if (!mostRecentExportEvents.containsKey(key)) {
            throw new ObjectNotFoundException("Did not find entry for user " + user.getName() + " and job id " + exportJobID);
        }
        return mostRecentExportEvents.get(key);
    }

    public synchronized void addServerExportListener(User user, long exportJobID){
        MultiEmitterProcessor<ExportEvent> emitter = MultiEmitterProcessor.create();
        String key = entryKey(user, exportJobID);
        listeners.put(key, emitter);
        jobRequestToUser.put(exportJobID, user);
        ExportEvent event = new ExportEvent(
                this, exportJobID, user, "", null, ExportEvent.EXPORT_ASSEMBLING,
                "", "", 0.0, null, null);
        mostRecentExportEvents.put(key, event);
    }

    public void fireExportAssembling(long jobID, VCDataIdentifier vcdID, String format) {
        User user = jobRequestToUser.get(jobID);
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEvent.EXPORT_ASSEMBLING, format, null, null, null, null);
        fireExportEvent(event);
    }

    public void fireExportEvent(ExportEvent event) {
        String key = entryKey(event.getUser(), event.getJobID());
        if (!listeners.containsKey(key)){
            throw new RuntimeException("Did not find entry for user " + event.getUser().getName() + " and job id " + event.getJobID());
        }
        MultiEmitterProcessor<ExportEvent> listener = listeners.get(event.getUser().getName() + event.getJobID());
        listener.onNext(event);
        mostRecentExportEvents.put(key, event);
    }

    public void fireExportFailed(long jobID, VCDataIdentifier vcdID, String format, String message) {
        User user = jobRequestToUser.get(jobID);
        String key = entryKey(user, jobID);
        if (!listeners.containsKey(key)) {
            throw new RuntimeException("Did not find entry for user " + user.getName() + " and job id " + jobID);
        }
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEvent.EXPORT_FAILURE, format, message, null, null, null);
        listeners.get(key).onNext(event);
        listeners.get(key).complete();
        mostRecentExportEvents.put(key, event);
        removeServerExportListener(user, jobID);
    }

    public void fireExportProgress(long jobID, VCDataIdentifier vcdID, String format, double progress) {
        User user = jobRequestToUser.get(jobID);

        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEvent.EXPORT_PROGRESS, format, null, progress, null, null);
        fireExportEvent(event);
    }

    public void fireExportStarted(long jobID, VCDataIdentifier vcdID, String format) {
        User user = jobRequestToUser.get(jobID);

        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEvent.EXPORT_START, format, null, null, null, null);
        fireExportEvent(event);
    }

    public synchronized void removeServerExportListener(User user, long exportJobID){
        listeners.remove(user.getName() + exportJobID);
    }

    private String entryKey(User user, long jobID){
        return user.getName() + jobID;
    }
}
