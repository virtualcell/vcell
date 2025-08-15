package org.vcell.restq.services.Exports;

import cbit.rmi.event.ExportEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.properties.IfBuildProperty;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.vcell.util.document.User;

import java.time.Instant;
import java.util.List;


@ApplicationScoped
@IfBuildProperty(name = "vcell.exporter", stringValue = "true")
public class ExportStatusListener {
    private final ExportEventQueue exportEventQueue = new ExportEventQueue();
    private final Logger logger = LogManager.getLogger(ExportStatusListener.class);


    @Inject
    ObjectMapper objectMapper;


    @Incoming("subscriber-client-status")
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

    public synchronized List<ExportEvent> getMostRecentExportStatus (User user, Instant timestamp) {
        return exportEventQueue.getExportEventsPastSpecificTime(user, timestamp).stream().map(ExportEventQueue.MessageExportEvent::exportEvent).toList();
    }
}
