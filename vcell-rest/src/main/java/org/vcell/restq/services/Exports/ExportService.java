package org.vcell.restq.services.Exports;

import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportEventController;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.FormatSpecificSpecs;
import cbit.vcell.export.server.JobRequest;
import cbit.vcell.exports.ExportHistory;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.exports.ExportHistoryDBRep;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationIdentifier;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.vcell.restq.activemq.ExportRequestListenerMQ;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.errors.exceptions.RuntimeWebException;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.restq.services.SimulationRestService;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class ExportService {
    @Inject
    Instance<ExportEventController> exportStatusCreator;
    @Inject
    Instance<ExportStatusListener> exportStatusListener;

    @Inject
    SimulationRestService simulationRestService;

    DatabaseServerImpl databaseServer;
    @Inject
    public ExportService(AgroalConnectionFactory connectionFactory) throws DataAccessException {
        databaseServer = new DatabaseServerImpl(connectionFactory, connectionFactory.getKeyFactory());
    }

    public List<ExportHistory> getExportHistory(User user, int pageNumber) throws DataAccessException {
        return databaseServer.getUsersExportHistory(user, pageNumber);
    }

    public Multi<ExportEvent> getExportStatuses(User user, long jobID) throws ObjectNotFoundException {
        return ((ServerExportEventController) exportStatusCreator.get()).getSSEUsersExportStatus(user, jobID);
    }

    public List<ExportEvent> getMostRecentExportStatus(User user, long timestamp) {
        return exportStatusListener.get().getMostRecentExportStatus(user, Instant.ofEpochSecond(timestamp));
    }

    public ExportRequestListenerMQ.ExportJob createExportJobFromRequest(User user, ExportResource.StandardExportInfo request, FormatSpecificSpecs formatSpecificSpecs, ExportFormat format) throws DataAccessException, SQLException {
        SimulationRep simulationRep = simulationRestService.getSimulationRep(new KeyValue(request.simulationKey()));
        VCSimulationIdentifier simulationIdentifier = new VCSimulationIdentifier(simulationRep.getKey(), simulationRep.getOwner());
        JobRequest newExportJob = JobRequest.createExportJobRequest(user);
        long exportID = newExportJob.getExportJobID();
        AnnotatedFunction[] annotatedFunctions = {};
        if (request.outputContext() != null){
            annotatedFunctions = request.outputContext().stream().map(
                    dto -> {
                        try {
                            return new AnnotatedFunction(dto.functionName(), new Expression(dto.functionExpression()), dto.domain(), dto.error(),
                                    dto.functionType(), dto.category());
                        } catch (ExpressionException e) {
                            throw new RuntimeWebException(e.getMessage(), e);
                        }
                    }
            ).toArray(AnnotatedFunction[]::new);
        }
        return new ExportRequestListenerMQ.ExportJob(exportID, user, annotatedFunctions, simulationIdentifier, request.simulationJob(), format,
                request.variableSpecs(), request.timeSpecs(), request.geometrySpecs(), formatSpecificSpecs, request.simulationName(), request.contextName(),
                request.bioModelKey(), request.mathModelKey(), request.mathDescriptionKey());
    }

}
