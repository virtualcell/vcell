package org.vcell.restq.services.Exports;

import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportEventController;
import cbit.rmi.event.ExportListener;
import cbit.vcell.export.server.ExportEnums;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.properties.IfBuildProperty;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.MultiEmitterProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.vcell.restq.activemq.ExportRequestListenerMQ;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@IfBuildProperty(name = "vcell.exporter", stringValue = "false")
class DummyExportEventController implements ExportEventController{

    @Override
    public ExportEvent fireExportCompleted(long jobID, VCDataIdentifier vcdID, String format, String location, ExportSpecs exportSpecs) {
        return null;
    }

    @Override
    public void addExportListener(ExportListener listener) { }

    @Override
    public void fireExportAssembling(long jobID, VCDataIdentifier vcdID, String format) { }

    @Override
    public void fireExportEvent(ExportEvent event) { }

    @Override
    public void fireExportFailed(long jobID, VCDataIdentifier vcdID, String format, String message) { }

    @Override
    public void fireExportProgress(long jobID, VCDataIdentifier vcdID, String format, double progress) { }

    @Override
    public void fireExportStarted(long jobID, VCDataIdentifier vcdID, String format) { }
}


@ApplicationScoped
@IfBuildProperty(name = "vcell.exporter", stringValue = "true")
public class ServerExportEventController implements cbit.rmi.event.ExportEventController {
    private final ConcurrentHashMap<Long, User> jobRequestToUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MultiEmitterProcessor<ExportEvent>> listeners = new ConcurrentHashMap<>();
    private final ExportEventQueue exportEventQueue = new ExportEventQueue();

    private final static Logger logger = LogManager.getLogger(ServerExportEventController.class);

    @Inject
    @Channel("outgoing-client-status")
    Emitter<String> clientStatusEmitter;

    @Inject
    ObjectMapper objectMapper;

    @Incoming("incoming-client-status")
    public Uni<Void> clientStatusTopicListener(Message<String> message) {
        try{
            JsonNode node = objectMapper.readTree(message.getPayload());
            if (node.has("eventType") && node.has("format")){
                ExportEvent exportEvent = objectMapper.treeToValue(node, ExportEvent.class);
                synchronized (this){
                    exportEventQueue.addExportEvent(exportEvent);
                }
                logger.debug("Received export status from topic: {}", exportEvent);
            }
            return Uni.createFrom().voidItem();
        } catch (JsonProcessingException jsonProcessingException){
            logger.error("Problem parsing export status message", jsonProcessingException);
            return Uni.createFrom().voidItem();
        } finally {
            message.ack();
        }
    }

    public synchronized Multi<ExportEvent> getSSEUsersExportStatus(User user, long exportJobID) throws ObjectNotFoundException {
        String key = sseEventListenerKey(user, exportJobID);
        if (!listeners.containsKey(key)) {
            throw new ObjectNotFoundException("Did not find entry for user " + user.getName() + " and job id " + exportJobID);
        }
        return listeners.get(key).toMulti();
    }

    public synchronized List<ExportEvent> getMostRecentExportStatus (User user, Instant timestamp) {
        return exportEventQueue.getExportEventsPastSpecificTime(user, timestamp).stream().map(ExportEventQueue.MessageExportEvent::exportEvent).toList();
    }


    public void addExportListener(cbit.rmi.event.ExportListener listener) {
        throw new IllegalCallerException("addExportListener not implemented for the server");
    }

    public synchronized void addServerExportListener(ExportRequestListenerMQ.ExportJob exportJob){
        jobRequestToUser.put(exportJob.id(), exportJob.user());
        MultiEmitterProcessor<ExportEvent> emitter = MultiEmitterProcessor.create();
        String key = sseEventListenerKey(exportJob.user(), exportJob.id());
        listeners.put(key, emitter);
        ExportEvent event = new ExportEvent(
                this, exportJob.id(), exportJob.user(), exportJob.simulationIdentifier().getID(), exportJob.simulationIdentifier().getSimulationKey(), ExportEnums.ExportProgressType.EXPORT_ASSEMBLING,
                exportJob.format().name(), "", null);
        fireExportEvent(event);
    }





    public synchronized void fireExportEvent(ExportEvent event) {
        String key = sseEventListenerKey(event.getUser(), event.getJobID());
        if (!listeners.containsKey(key)){
            throw new RuntimeException("Did not find entry for user " + event.getUser().getName() + " and job id " + event.getJobID());
        }
        MultiEmitterProcessor<ExportEvent> listener = listeners.get(event.getUser().getName() + event.getJobID());
        listener.onNext(event);
        if (event.getEventType() == ExportEnums.ExportProgressType.EXPORT_FAILURE || event.getEventType() == ExportEnums.ExportProgressType.EXPORT_COMPLETE){
            listener.complete();
            listeners.remove(key);
            jobRequestToUser.remove(event.getJobID());
        }
        try {
            clientStatusEmitter.send(objectMapper.writeValueAsString(event));
            logger.debug("Sent event to client status topic: {}", event);
        } catch (JsonProcessingException e) {
            logger.error(e);
        }
    }

    private synchronized void fireExportEvent(long jobID, VCDataIdentifier vcdID, String format, Double progress, ExportEnums.ExportProgressType exportProgressType, String message){
        User user = jobRequestToUser.getOrDefault(jobID, null);
        if (user == null){
            logger.error("For job (JobID: {}, VCDataID: {}, Format: {}) a user was not found.", jobID, vcdID, format);
            return;
        }
        ExportEvent event = new ExportEvent(this, jobID, user, vcdID, exportProgressType, format, message, progress);
        fireExportEvent(event);
    }

    public synchronized ExportEvent fireExportCompleted(long jobID, VCDataIdentifier vcdID, String format, String location, ExportSpecs exportSpecs) {
        final KeyValue dataKey;
        if (vcdID instanceof VCSimulationDataIdentifier) {
            dataKey = ((VCSimulationDataIdentifier)vcdID).getSimulationKey();
        }else if (vcdID instanceof ExternalDataIdentifier) {
            dataKey = ((ExternalDataIdentifier)vcdID).getSimulationKey();
        }else {
            throw new RuntimeException("unexpected VCDataIdentifier");
        }

        User user = jobRequestToUser.get(jobID);
        ExportEvent event = new ExportEvent(
                this, jobID, user, vcdID.getID(), dataKey, ExportEnums.ExportProgressType.EXPORT_COMPLETE,
                format, location, null);
        event.setHumanReadableExportData(exportSpecs != null ? exportSpecs.getHumanReadableExportData() : null);
        fireExportEvent(event);

        return event;
    }

    public void fireExportFailed(long jobID, VCDataIdentifier vcdID, String format, String message) {
        fireExportEvent(jobID, vcdID, format, null, ExportEnums.ExportProgressType.EXPORT_FAILURE, message);
    }

    public void fireExportProgress(long jobID, VCDataIdentifier vcdID, String format, double progress) {
        fireExportEvent(jobID, vcdID, format, progress, ExportEnums.ExportProgressType.EXPORT_PROGRESS, null);
    }

    public void fireExportStarted(long jobID, VCDataIdentifier vcdID, String format) {
        fireExportEvent(jobID, vcdID, format, null, ExportEnums.ExportProgressType.EXPORT_START, null);
    }

    public void fireExportAssembling(long jobID, VCDataIdentifier vcdID, String format) {
        fireExportEvent(jobID, vcdID, format, null, ExportEnums.ExportProgressType.EXPORT_ASSEMBLING, null);
    }

    private String sseEventListenerKey(User user, long jobID){
        return user.getName() + jobID;
    }

}
