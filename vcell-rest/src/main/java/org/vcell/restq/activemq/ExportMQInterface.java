package org.vcell.restq.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.concurrent.CompletableFuture;

public interface ExportMQInterface {
    void addExportJobToQueue(ExportRequestListenerMQ.ExportJob exportJob) throws JsonProcessingException;
    Uni<Void> consumeExportRequest(Message<String> message);
    CompletableFuture<Void> startJob(Message<String> exportJob) throws JsonProcessingException;
}
