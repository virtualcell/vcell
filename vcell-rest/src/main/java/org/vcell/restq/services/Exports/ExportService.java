package org.vcell.restq.services.Exports;

import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportEventController;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.FormatSpecificSpecs;
import cbit.vcell.export.server.JobRequest;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationIdentifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.vcell.restq.activemq.ExportRequestListenerMQ;
import org.vcell.restq.errors.exceptions.RuntimeWebException;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.restq.services.SimulationRestService;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ExportService {
    @Inject
    Instance<ExportEventController> exportStatusCreator;

    @Inject
    SimulationRestService simulationRestService;

    public ExportResource.ExportHistory getExportHistory(User user) throws DataAccessException {
        return new ExportResource.ExportHistory("Hello");
    }

    public Multi<ExportEvent> getExportStatuses(User user, long jobID) throws ObjectNotFoundException {
        return ((ServerExportEventController) exportStatusCreator.get()).getSSEUsersExportStatus(user, jobID);
    }

    public List<ExportEvent> getMostRecentExportStatus(User user, Instant instant) {
        return ((ServerExportEventController) exportStatusCreator.get()).getMostRecentExportStatus(user, instant);
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
