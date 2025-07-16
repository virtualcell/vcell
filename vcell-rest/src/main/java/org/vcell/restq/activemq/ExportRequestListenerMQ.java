package org.vcell.restq.activemq;

import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.GeometrySpecs;
import cbit.vcell.export.server.HumanReadableExportData;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
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
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.restq.services.Exports.ExportService;
import org.vcell.restq.services.Exports.ExportStatusCreator;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import javax.jms.JMSException;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class ExportRequestListenerMQ {
    private static final Logger logger = LogManager.getLogger(ExportRequestListenerMQ.class);
    private final ArrayList<ExportResource.ExportJob> acceptedJobs = new ArrayList<>();
    private WorkerExecutor workerExecutor;
    private final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);
    private DataServerImpl dataServer;


    @Inject
    ExportService exportService;
    @Inject
    ExportStatusCreator exportStatusCreator;
    @Inject
    ObjectMapper mapper;
    @Inject
    AgroalConnectionFactory connectionFactory;
    @Inject
    Vertx vertx;

    @PostConstruct
    void init() throws FileNotFoundException {
        workerExecutor = vertx.createSharedWorkerExecutor("export-pool");
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
                ExportSpecs exportSpecs = getExportSpecs(exportJob);
                startExportJob(exportJob.user(), new OutputContext(exportJob.outputContext()), exportSpecs, exportJob.id());
            } catch (Exception e){
                logger.error(e);
                exportStatusCreator.fireExportFailed(exportJob.id(), exportJob.vcdID(),
                        exportJob.format() == null ? null : exportJob.format().toString(), e.getMessage());
            }
        });
    }

    private ExportSpecs getExportSpecs(ExportResource.ExportJob exportJob) throws SQLException, DataAccessException {
        GeometrySpecs geometrySpecs = new GeometrySpecs(exportJob.geometrySpecs().selections(), exportJob.geometrySpecs().axis(),
                exportJob.geometrySpecs().sliceNumber(), exportJob.geometrySpecs().geometryMode());
        HumanReadableExportData humanReadableExportData = new HumanReadableExportData(null,
                null, null, null, null, null, false, exportJob.subVolume());
        return new ExportSpecs(exportJob.vcdID(), exportJob.format(), exportJob.variableSpecs(), exportJob.timeSpecs(),
                geometrySpecs, exportJob.formatSpecificSpecs(), exportJob.simulationName(), exportJob.contextName(), humanReadableExportData);
    }

    private void startExportJob(User user, OutputContext outputContext, ExportSpecs exportSpecs, long jobID) throws JMSException, JsonProcessingException {
        try {
            if (Thread.currentThread().getName().contains("event-loop")){
                throw new RuntimeException("EXPORTS ARE USING THE EVENT LOOP.");
            }
            ExportServiceImpl.makeRemoteFile(outputContext, user, dataServer, exportSpecs, exportStatusCreator, jobID);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<ExportResource.ExportJob> getAcceptedJobs(){
        return acceptedJobs;
    }
}
