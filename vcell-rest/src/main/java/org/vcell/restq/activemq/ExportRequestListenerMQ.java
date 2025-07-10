package org.vcell.restq.activemq;

import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.simdata.OutputContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.restq.services.Exports.ExportService;
import org.vcell.restq.services.Exports.ExportStatusCreator;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.concurrent.*;

@ApplicationScoped
public class ExportRequestListenerMQ {
    private static final Logger logger = LogManager.getLogger(ExportRequestListenerMQ.class);
    private final ArrayList<ExportResource.ExportJob> acceptedJobs = new ArrayList<>();
    private WorkerExecutor workerExecutor;
    private final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);

    @Inject
    ExportService exportService;
    @Inject
    ExportStatusCreator exportStatusCreator;
    @Inject
    ObjectMapper mapper;
    @Inject
    Vertx vertx;

    @PostConstruct
    void init(){
        workerExecutor = vertx.createSharedWorkerExecutor("export-pool");
    }


    @Incoming("processed-export-request")
    public CompletionStage<Void> consumeExportRequest(Message<String> message) {
        try {
            String exportJobJSON = message.getPayload();
            ExportResource.ExportJob exportJob = mapper.readValue(exportJobJSON, ExportResource.ExportJob.class);
            startJob(exportJob);

//            Uni.createFrom().future(Unchecked.supplier(() -> {
//                        try {
//                            exportService.startExportJob(exportJob.user(), new OutputContext(exportJob.outputContext()), exportJob.exportSpecs());
//                        } catch (JMSException | JsonProcessingException e) {
//                            throw new RuntimeException(e);
//                        }
//                        return null;
//                    }),
//                    Duration.of(15, ChronoUnit.MINUTES)
//            );
//            exportService.startExportJob(exportJob.user(), new OutputContext(exportJob.outputContext()), exportJob.exportSpecs());
            acceptedJobs.add(exportJob);
            return message.ack();
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException("Failed to setup JMS manually", e);
        }
    }

    public void startJob(ExportResource.ExportJob exportJob) {
        threadPoolExecutor.submit(() -> {
            try {
                exportService.startExportJob(exportJob.user(), new OutputContext(exportJob.outputContext()), exportJob.exportSpecs(), exportJob.id());
            } catch (Exception e){
                logger.error(e);
                VCDataIdentifier dataIdentifier = exportJob.exportSpecs() == null ? null : exportJob.exportSpecs().getVCDataIdentifier();
                ExportFormat format = exportJob.exportSpecs() == null ? null : exportJob.exportSpecs().getFormat();
                exportStatusCreator.fireExportFailed(exportJob.id(), dataIdentifier,
                        format == null ? null : format.toString(), e.getMessage());
            }
        });
    }

    public ArrayList<ExportResource.ExportJob> getAcceptedJobs(){
        return acceptedJobs;
    }
}
