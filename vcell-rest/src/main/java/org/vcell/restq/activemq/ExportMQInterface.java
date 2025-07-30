package org.vcell.restq.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.vcell.restq.handlers.ExportResource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface ExportMQInterface {
    void addExportJobToQueue(ExportResource.ExportJob exportJob) throws JsonProcessingException;
    CompletionStage<Void> consumeExportRequest(Message<String> message);
    CompletableFuture<Void> startJob(ExportResource.ExportJob exportJob);
}
