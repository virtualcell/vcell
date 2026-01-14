package org.vcell.restq.activemq;

import cbit.vcell.export.server.*;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.properties.IfBuildProperty;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.restq.services.Exports.ServerExportEventController;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.*;



@ApplicationScoped
@IfBuildProperty(name = "vcell.exporter", stringValue = "false")
class DummyExportRequestListenerMQ implements ExportMQInterface {

    @Override
    public void addExportJobToQueue(ExportRequestListenerMQ.ExportJob exportJob) throws JsonProcessingException {
        throw new IllegalCallerException("ExportRequestListenerMQ is not enabled, cannot consume export request");
    }

    @Override
    public Uni<Void> consumeExportRequest(Message<String> message) {
        throw new IllegalCallerException("ExportRequestListenerMQ is not enabled, cannot consume export request");
    }

    @Override
    public CompletableFuture<Void> startJob(Message<String> exportJob) {
        throw new IllegalCallerException("ExportRequestListenerMQ is not enabled, cannot consume export request");
    }
}

@ApplicationScoped
@IfBuildProperty(name = "vcell.exporter", stringValue = "true")
public class ExportRequestListenerMQ implements ExportMQInterface {
    private static final Logger logger = LogManager.getLogger(ExportRequestListenerMQ.class);
    private final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);
    private DataServerImpl dataServer;
    private TimeUnit waitUnit = TimeUnit.MINUTES;

    @Inject
    ServerExportEventController exportStatusCreator;
    @Inject
    ObjectMapper mapper;

    @Inject
    @Channel("publisher-export-request")
    Emitter<String> exportJobEmitter;

    @PostConstruct
    void init() throws FileNotFoundException {
        String primarySimDataDir = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty);
        String secondarySimDataDir = PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirInternalProperty);
        this.dataServer = new DataServerImpl(new DataSetControllerImpl(null, new File(primarySimDataDir), new File(secondarySimDataDir)),
                new ExportServiceImpl());
    }

    public void addExportJobToQueue(ExportJob exportJob) {
        logger.debug("Export job added to queue: {} for user: {}", exportJob.id(), exportJob.user().getName());
        logger.debug("Export job details: {}", exportJob);
        try{
            exportStatusCreator.addServerExportListener(exportJob);
            exportJobEmitter.send(mapper.writeValueAsString(exportJob));
        } catch (Exception e){
            logger.error("Can't add job to remote queue: ", e);
            exportStatusCreator.fireExportFailed(exportJob.id(), null, null, e.getMessage());
        }
    }

    @Incoming("subscriber-export-request")
    public Uni<Void> consumeExportRequest(Message<String> message) {
        try {
            logger.debug("Received export request: {}", message.getPayload());
            startJob(message);
            return Uni.createFrom().completionStage(message.ack());
        } catch (Exception e) {
            logger.error(e);
            return Uni.createFrom().completionStage(message.nack(e));
        }
    }

    public CompletableFuture<Void> startJob(Message<String> message) throws JsonProcessingException {
        return startJob(message, true);
    }

    public CompletableFuture<Void> startJob(Message<String> message, boolean handleFailure) throws JsonProcessingException {
        String exportJobJSON = message.getPayload();
        ExportJob exportJob = mapper.readValue(exportJobJSON, ExportJob.class);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        ExportSpecs exportSpecs = getExportSpecs(exportJob);
                        ExportServiceImpl.makeRemoteFile(new OutputContext(exportJob.outputContext()), exportJob.user(), dataServer, exportSpecs, exportStatusCreator, exportJob.id());
                    } catch (SQLException | DataAccessException e) {
                        throw new RuntimeException(e);
                    }
                }, threadPoolExecutor)
                .orTimeout(15, waitUnit);

        if (handleFailure){
            future = future.exceptionally(ex -> {
                logger.error("Error thrown when trying to start export job", ex);
                exportStatusCreator.fireExportFailed(exportJob.id(),  new VCSimulationDataIdentifier(exportJob.simulationIdentifier(), exportJob.simulationJob()),
                        exportJob.format() == null ? null : exportJob.format().toString(), ex.getMessage());
                return null;
            });
        }
        return future;
    }

    private ExportSpecs getExportSpecs(ExportJob exportJob) throws SQLException, DataAccessException {
        logger.debug("Geometry specs: {}", exportJob.geometrySpecs());
        GeometrySpecs geometrySpecs = null;
        if (exportJob.geometrySpecs() != null) {
            geometrySpecs = new GeometrySpecs(exportJob.geometrySpecs().selections(), exportJob.geometrySpecs().axis(),
                    exportJob.geometrySpecs().sliceNumber(), exportJob.geometrySpecs().geometryMode());
        }
        
        Map<Integer, String> subVolume = exportJob.formatSpecificSpecs() instanceof N5Specs n5ExportRequest ?
                n5ExportRequest.getSubVolumeMapping() : null;
        HumanReadableExportData humanReadableExportData = new HumanReadableExportData(null,
                null, null, null, null, null, false, subVolume);
        return new ExportSpecs(new VCSimulationDataIdentifier(exportJob.simulationIdentifier(), exportJob.simulationJob()), exportJob.format(), exportJob.variableSpecs(), exportJob.timeSpecs(),
                geometrySpecs, exportJob.formatSpecificSpecs(), exportJob.simulationName(), exportJob.contextName(), humanReadableExportData);
    }

    public void setThreadWaitTimeUnit(TimeUnit timeUnit) {
        waitUnit = timeUnit;
    }

    public record ExportJob(
            long id,
            User user,
            AnnotatedFunction[] outputContext,
            VCSimulationIdentifier simulationIdentifier,
            int simulationJob,
            ExportFormat format,
            VariableSpecs variableSpecs, TimeSpecs timeSpecs,
            ExportResource.GeometrySpecDTO geometrySpecs, FormatSpecificSpecs formatSpecificSpecs,
            String simulationName, String contextName
    ){ }
}
