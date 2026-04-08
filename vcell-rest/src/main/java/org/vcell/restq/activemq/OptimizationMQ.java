package org.vcell.restq.activemq;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.vcell.optimization.OptRequestMessage;
import org.vcell.optimization.OptStatusMessage;
import org.vcell.restq.services.OptimizationRestService;
import org.vcell.util.document.KeyValue;

/**
 * ActiveMQ messaging for optimization job dispatch and status updates.
 *
 * vcell-rest produces messages on "opt-request" (submit/stop commands to vcell-submit)
 * and consumes messages on "opt-status" (status updates from vcell-submit).
 */
@ApplicationScoped
public class OptimizationMQ {
    private static final Logger lg = LogManager.getLogger(OptimizationMQ.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    OptimizationRestService optimizationRestService;

    @Inject
    @Channel("publisher-opt-request")
    Emitter<String> optRequestEmitter;

    /**
     * Send a submit command to vcell-submit.
     */
    public void sendSubmitRequest(OptRequestMessage request) {
        try {
            lg.info("Sending optimization submit request for job {}", request.jobId);
            optRequestEmitter.send(mapper.writeValueAsString(request));
        } catch (Exception e) {
            lg.error("Failed to send optimization submit request for job {}: {}", request.jobId, e.getMessage(), e);
        }
    }

    /**
     * Send a stop command to vcell-submit.
     */
    public void sendStopRequest(OptRequestMessage request) {
        try {
            lg.info("Sending optimization stop request for job {}", request.jobId);
            optRequestEmitter.send(mapper.writeValueAsString(request));
        } catch (Exception e) {
            lg.error("Failed to send optimization stop request for job {}: {}", request.jobId, e.getMessage(), e);
        }
    }

    /**
     * Consume status updates from vcell-submit (e.g. QUEUED with htcJobId, FAILED with error).
     */
    @Incoming("subscriber-opt-status")
    public Uni<Void> consumeOptStatus(Message<String> message) {
        try {
            OptStatusMessage statusMsg = mapper.readValue(message.getPayload(), OptStatusMessage.class);
            lg.info("Received optimization status update: job={}, status={}", statusMsg.jobId, statusMsg.status);

            KeyValue jobKey = new KeyValue(statusMsg.jobId);

            if (statusMsg.htcJobId != null) {
                optimizationRestService.updateHtcJobId(jobKey, statusMsg.htcJobId);
            }
            if (statusMsg.status != null) {
                optimizationRestService.updateOptJobStatus(jobKey, statusMsg.status, statusMsg.statusMessage);
            }

            return Uni.createFrom().completionStage(message.ack());
        } catch (Exception e) {
            lg.error("Failed to process optimization status message: {}", e.getMessage(), e);
            return Uni.createFrom().completionStage(message.nack(e));
        }
    }
}
