package org.vcell.restq.activemq;

import cbit.vcell.export.server.*;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.restq.services.Exports.ExportService;
import org.vcell.restq.services.Exports.ExportStatusCreator;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@ApplicationScoped
public class ExportRequestListenerMQ {
    private static final Logger logger = LogManager.getLogger(ExportRequestListenerMQ.class);
    private final ExportResource.ExportJob[] acceptedJobs = new ExportResource.ExportJob[100];
    private final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);
    private final ScheduledExecutorService canceller = Executors.newSingleThreadScheduledExecutor();
    private DataServerImpl dataServer;
    private TimeUnit waitUnit = TimeUnit.MINUTES;

    @Inject
    ExportService exportService;
    @Inject
    ExportStatusCreator exportStatusCreator;
    @Inject
    ObjectMapper mapper;
    @Inject
    AgroalConnectionFactory connectionFactory;

    @PostConstruct
    void init() throws FileNotFoundException {
        String primarySimDataDir = PropertyLoader.getProperty(PropertyLoader.primarySimDataDirInternalProperty, "/simdata");
        String secondarySimDataDir = PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirInternalProperty, "/simdata");
        this.dataServer = new DataServerImpl(new DataSetControllerImpl(null, new File(primarySimDataDir), new File(secondarySimDataDir)),
                new ExportServiceImpl());
    }


    @Incoming("processed-export-request")
    public CompletionStage<Void> consumeExportRequest(Message<String> message) {
        try {
            String exportJobJSON = message.getPayload();
            ExportResource.ExportJob exportJob = mapper.readValue(exportJobJSON, ExportResource.ExportJob.class);
            startJob(exportJob);
            return message.ack();
        } catch (Exception e) {
            logger.error(e);
            return message.nack(e);
        }
    }

    public CompletableFuture<Void> startJob(ExportResource.ExportJob exportJob) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    ExportSpecs exportSpecs = getExportSpecs(exportJob);
                    ExportServiceImpl.makeRemoteFile(new OutputContext(exportJob.outputContext()), exportJob.user(), dataServer, exportSpecs, exportStatusCreator, exportJob.id());
                } catch (SQLException | DataAccessException e) {
                    throw new RuntimeException(e);
                }
        }, threadPoolExecutor)
                .orTimeout(15, waitUnit)
                .exceptionally(ex -> {
                    exportStatusCreator.fireExportFailed(exportJob.id(), exportJob.vcdID(),
                            exportJob.format() == null ? null : exportJob.format().toString(), ex.getMessage());
                    logger.error("Error thrown when trying to start export job", ex);
                    throw new RuntimeException(ex);
                });
        return future;
    }

    private ExportSpecs getExportSpecs(ExportResource.ExportJob exportJob) throws SQLException, DataAccessException {
        GeometrySpecs geometrySpecs = new GeometrySpecs(exportJob.geometrySpecs().selections(), exportJob.geometrySpecs().axis(),
                exportJob.geometrySpecs().sliceNumber(), exportJob.geometrySpecs().geometryMode());
        Map<Integer, String> subVolume = exportJob.formatSpecificSpecs() instanceof N5Specs n5ExportRequest ?
                n5ExportRequest.getSubVolumeMapping() : null;
        HumanReadableExportData humanReadableExportData = new HumanReadableExportData(null,
                null, null, null, null, null, false, subVolume);
        return new ExportSpecs(exportJob.vcdID(), exportJob.format(), exportJob.variableSpecs(), exportJob.timeSpecs(),
                geometrySpecs, exportJob.formatSpecificSpecs(), exportJob.simulationName(), exportJob.contextName(), humanReadableExportData);
    }

    public void setThreadWaitTimeUnit(TimeUnit timeUnit) {
        waitUnit = timeUnit;
    }

}
