package org.vcell.restq.services.Exports;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.*;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.Identifier;
import io.smallrye.mutiny.Multi;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.vcell.restq.QuarkusStartUpTasks;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.User;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import java.io.*;

@ApplicationScoped
public class ExportService {
    private final DatabaseServerImpl databaseServer;
    private final DataServerImpl dataServer;

//    @Inject
//    @Identifier(QuarkusStartUpTasks.internalMQBeanIdentifier)
//    QueueConnection internalQueueConnection;

    @Inject
    ExportStatusCreator exportStatusCreator;

    @Inject
    ObjectMapper jsonMapper;

    @Inject
    @Channel("export-request")
    Emitter<String> exportJobEmitter;

    private final static Logger lg = LogManager.getLogger(ExportService.class);

    @Inject
    public ExportService(AgroalConnectionFactory connectionFactory, ExportStatusCreator exportStatusCreator) throws DataAccessException, FileNotFoundException {
        this.databaseServer = new DatabaseServerImpl(connectionFactory, connectionFactory.getKeyFactory());
        this.exportStatusCreator = exportStatusCreator;
        String primarySimDataDir = PropertyLoader.getProperty(PropertyLoader.primarySimDataDirInternalProperty, "/simdata");
        String secondarySimDataDir = PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirInternalProperty, "/simdata");
        this.dataServer = new DataServerImpl(new DataSetControllerImpl(null, new File(primarySimDataDir), new File(secondarySimDataDir)),
                new ExportServiceImpl());
    }

    public ExportResource.ExportHistory getExportHistory(User user) throws DataAccessException {
        return new ExportResource.ExportHistory("Hello");
    }

    public void addExportHistory(User user, ExportResource.ExportHistory history) throws DataAccessException {
        databaseServer.addExportHistory(user, history.exportHistory());
    }

    public void addExportJobToQueue(ExportResource.ExportJob exportJob) throws JMSException, JsonProcessingException {
        exportStatusCreator.addServerExportListener(exportJob.user(), exportJob.id());
        exportJobEmitter.send(jsonMapper.writeValueAsString(exportJob));
    }

    public Multi<ExportEvent> getExportStatuses(User user, long jobID) throws ObjectNotFoundException {
        return exportStatusCreator.getUsersExportStatus(user, jobID);
    }

    public void startExportJob(User user, OutputContext outputContext, ExportSpecs exportSpecs, long jobID) throws JMSException, JsonProcessingException {
        try {
            if (Thread.currentThread().getName().contains("event-loop")){
                throw new RuntimeException("EXPORTS ARE USING THE EVENT LOOP.");
            }
            ExportServiceImpl.makeRemoteFile(outputContext, user, dataServer, exportSpecs, exportStatusCreator, jobID);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
