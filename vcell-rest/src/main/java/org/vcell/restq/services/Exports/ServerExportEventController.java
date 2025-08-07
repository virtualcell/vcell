package org.vcell.restq.services.Exports;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.ExportEnums;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.MultiEmitterProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ServerExportEventController implements cbit.rmi.event.ExportEventController {
    private final ConcurrentHashMap<Long, User> jobRequestToUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MultiEmitterProcessor<ExportEvent>> listeners = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<User, Set<ComparableExportEvent>> mostRecentExportEvents = new ConcurrentHashMap<>(); // TODO: Clean out this set

    static class ComparableExportEvent{
        private final ExportEvent exportEvent;

        public ComparableExportEvent(ExportEvent exportEvent) {
            this.exportEvent = exportEvent;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ComparableExportEvent cpe){
                return cpe.exportEvent.getJobID() == this.exportEvent.getJobID() &&
                        cpe.exportEvent.getUser().equals(this.exportEvent.getUser());
            }
            return false;
        }
        @Override
        public int hashCode() {
            int result = Long.hashCode(exportEvent.getJobID());
            result = 31 * result + exportEvent.getUser().hashCode();
            return result;
        }
    }

    protected ExportSpecs exportExists(ExportSpecs exportSpecs) {
        // Call DB for export history and check if it exists. Store in cache if it exists for future requests.
        return exportSpecs;
    }

    public synchronized ExportEvent fireExportCompleted(long jobID, VCDataIdentifier vcdID, String format, String location, ExportSpecs exportSpecs) {

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

        ExportEvent event = null;
        synchronized (this){
            User user = jobRequestToUser.get(jobID);
            String key = entryKey(user, jobID);

             event = new ExportEvent(
                    this, jobID, user, vcdID.getID(), dataKey, ExportEnums.ExportProgressType.EXPORT_COMPLETE,
                    format, location, null, timeSpecs, varSpecs);
            event.setHumanReadableExportData(exportSpecs != null ? exportSpecs.getHumanReadableExportData() : null);
            event.setHumanReadableExportData(exportSpecs.getHumanReadableExportData());

            if (!listeners.containsKey(key)) {
                throw new RuntimeException("Did not find entry for user " + user.getName() + " and job id " + jobID);
            }
            listeners.get(key).onNext(event);
            listeners.get(key).complete();
            replaceSetEntry(user, event);
            removeServerExportListener(user, jobID);
            jobRequestToUser.remove(jobID);
        }

        return event;
    }

    public void addExportListener(cbit.rmi.event.ExportListener listener) {
        throw new IllegalCallerException("addExportListener not implemented for the server");
    }

    public synchronized Multi<ExportEvent> getUsersExportStatus(User user, long exportJobID) throws ObjectNotFoundException {
        String key = entryKey(user, exportJobID);
        if (!listeners.containsKey(key)) {
            throw new ObjectNotFoundException("Did not find entry for user " + user.getName() + " and job id " + exportJobID);
        }
        return listeners.get(key).toMulti();
    }

    public synchronized Set<ExportEvent> getMostRecentExportStatus (User user) {
        if (!mostRecentExportEvents.containsKey(user)) {
            return new HashSet<>();
        }
        return mostRecentExportEvents.get(user).stream().map(cpe -> cpe.exportEvent).collect(java.util.stream.Collectors.toSet());
    }

    public synchronized void addServerExportListener(User user, long exportJobID){
        MultiEmitterProcessor<ExportEvent> emitter = MultiEmitterProcessor.create();
        String key = entryKey(user, exportJobID);
        listeners.put(key, emitter);
        jobRequestToUser.put(exportJobID, user);
        ExportEvent event = new ExportEvent(
                this, exportJobID, user, "", null, ExportEnums.ExportProgressType.EXPORT_ASSEMBLING,
                "", "", 0.0, null, null);
        if (!mostRecentExportEvents.containsKey(user)) {
            mostRecentExportEvents.put(user, new HashSet<>());
        }
        mostRecentExportEvents.get(user).add(new ComparableExportEvent(event));
    }

    public synchronized void fireExportAssembling(long jobID, VCDataIdentifier vcdID, String format) {
        User user = jobRequestToUser.get(jobID);
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEnums.ExportProgressType.EXPORT_ASSEMBLING, format, null, null, null, null);
        fireExportEvent(event);
    }

    public synchronized void fireExportEvent(ExportEvent event) {
        String key = entryKey(event.getUser(), event.getJobID());
        if (!listeners.containsKey(key)){
            throw new RuntimeException("Did not find entry for user " + event.getUser().getName() + " and job id " + event.getJobID());
        }
        MultiEmitterProcessor<ExportEvent> listener = listeners.get(event.getUser().getName() + event.getJobID());
        listener.onNext(event);
        replaceSetEntry(event.getUser(), event);
    }

    public synchronized void fireExportFailed(long jobID, VCDataIdentifier vcdID, String format, String message) {
        User user = jobRequestToUser.get(jobID);
        String key = entryKey(user, jobID);
        if (!listeners.containsKey(key)) {
            throw new RuntimeException("Did not find entry for user " + user.getName() + " and job id " + jobID);
        }
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEnums.ExportProgressType.EXPORT_FAILURE, format, message, null, null, null);
        listeners.get(key).onNext(event);
        listeners.get(key).complete();

        replaceSetEntry(user, event);
        removeServerExportListener(user, jobID);
        jobRequestToUser.remove(jobID);
    }

    public synchronized void fireExportProgress(long jobID, VCDataIdentifier vcdID, String format, double progress) {
        User user = jobRequestToUser.get(jobID);
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEnums.ExportProgressType.EXPORT_PROGRESS, format, null, progress, null, null);
        fireExportEvent(event);
    }

    public synchronized void fireExportStarted(long jobID, VCDataIdentifier vcdID, String format) {
        User user = jobRequestToUser.get(jobID);
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEnums.ExportProgressType.EXPORT_START, format, null, null, null, null);
        fireExportEvent(event);
    }

    public synchronized void removeServerExportListener(User user, long exportJobID){
        listeners.remove(user.getName() + exportJobID);
    }

    private String entryKey(User user, long jobID){
        return user.getName() + jobID;
    }

    private synchronized void replaceSetEntry(User user, ExportEvent newEvent){
        mostRecentExportEvents.get(user).remove(new ComparableExportEvent(newEvent));
        mostRecentExportEvents.get(user).add(new ComparableExportEvent(newEvent));
    }
}
