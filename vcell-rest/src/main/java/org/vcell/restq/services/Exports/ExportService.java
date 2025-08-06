package org.vcell.restq.services.Exports;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.FormatSpecificSpecs;
import cbit.vcell.export.server.JobRequest;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.activemq.ExportRequestListenerMQ;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.errors.exceptions.RuntimeWebException;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.restq.services.SimulationRestService;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Set;

@ApplicationScoped
public class ExportService {
    @Inject
    ExportStatusCreator exportStatusCreator;

    @Inject
    SimulationRestService simulationRestService;

    @Inject
    ObjectMapper jsonMapper;

    @Inject
    public ExportService(ExportStatusCreator exportStatusCreator) throws DataAccessException, FileNotFoundException {
        this.exportStatusCreator = exportStatusCreator;
    }

    public ExportResource.ExportHistory getExportHistory(User user) throws DataAccessException {
        return new ExportResource.ExportHistory("Hello");
    }

    public Multi<ExportEvent> getExportStatuses(User user, long jobID) throws ObjectNotFoundException {
        return exportStatusCreator.getUsersExportStatus(user, jobID);
    }

    public Set<ExportEvent> getMostRecentExportStatus(User user) throws ObjectNotFoundException {
        return exportStatusCreator.getMostRecentExportStatus(user);
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
                request.variableSpecs(), request.timeSpecs(), request.geometrySpecs(), formatSpecificSpecs, request.simulationName(), request.contextName());
    }

}
