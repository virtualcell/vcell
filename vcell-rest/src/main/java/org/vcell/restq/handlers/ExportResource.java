package org.vcell.restq.handlers;

import cbit.image.VCImage;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.*;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.Compressed;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.RestStreamElementType;
import org.vcell.restq.errors.exceptions.DataAccessWebException;
import org.vcell.restq.errors.exceptions.NotAuthenticatedWebException;
import org.vcell.restq.errors.exceptions.NotFoundWebException;
import org.vcell.restq.errors.exceptions.RuntimeWebException;
import org.vcell.restq.services.Exports.ExportService;
import org.vcell.restq.services.SimulationRestService;
import org.vcell.restq.services.UserRestService;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import javax.jms.JMSException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Path("/api/v1/exports")
public class ExportResource {

    @Inject
    SecurityIdentity securityIdentity;
    @Inject
    UserRestService userRestService;
    @Inject
    ExportService exportService;
    @Inject
    SimulationRestService simulationRestService;

    @Context
    HttpServerRequest request;

    @Path("/history")
    @GET
    @RolesAllowed("user")
    @Operation(operationId = "getExportHistory")
    public ExportHistory getExportHistory() throws DataAccessWebException, NotAuthenticatedWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try {
            return exportService.getExportHistory(user);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @Path("/history")
    @DELETE
    @RolesAllowed("user")
    @Operation(operationId = "deleteExportHistory")
    public ExportHistory deleteExportHistoryEntry() throws DataAccessWebException, NotAuthenticatedWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try {
            return exportService.getExportHistory(user);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @Path("/{exportJobID}/status")
    @GET
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "exportStatus")
    public ExportEvent pollExportStatus(@PathParam("exportJobID") long exportJobID) throws DataAccessWebException, NotAuthenticatedWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            return exportService.getMostRecentExportStatus(user, exportJobID);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @Path("/{exportJobID}/status-sse")
    @GET
    @RolesAllowed("user")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    @Operation(hidden = true)
    public Multi<ExportEvent> exportStatus(@PathParam("exportJobID") long exportJobID) throws DataAccessWebException, NotAuthenticatedWebException, NotFoundWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try {
            return exportService.getExportStatuses(user, exportJobID);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundWebException(e.getMessage(), e);
        }
    }

    @Path("")
    @POST
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "export")
    public long createExport(ExportRequest er) throws DataAccessWebException, NotAuthenticatedWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            ExportJob exportJob = exportService.createExportJobFromRequest(user, er);
            exportService.addExportJobToQueue(exportJob);
            return exportJob.id();
        } catch (JMSException | JsonProcessingException | DataAccessException | SQLException e) {
            throw new RuntimeWebException(e.getMessage(), e);
        }
    }


    public record ExportRequest(
            ArrayList<AnnotatedFunctionDTO> outputContext, ExportFormat exportFormat,
            String simulationID, int simulationJob,
            FormatSpecificSpecs formatSpecificSpecs,
            GeometrySpecDTO geometrySpecs,
            TimeSpecs timeSpecs, VariableSpecs variableSpecs,
            HashMap<Integer, String> subVolume,
            String simulationName, String contextName
    ){ }

    public record AnnotatedFunctionDTO(
            String functionName,
            String functionExpression,
            String error,
            Variable.Domain domain,
            VariableType functionType,
            AnnotatedFunction.FunctionCategory category
    ) { }

    public record ExportHistory(
            String exportHistory
    ){ }

    public record ExportJob(
            long id,
            User user,
            AnnotatedFunction[] outputContext,
            VCDataIdentifier vcdID,
            ExportFormat format,
            VariableSpecs variableSpecs, TimeSpecs timeSpecs,
            GeometrySpecDTO geometrySpecs, FormatSpecificSpecs formatSpecificSpecs,
            HashMap<Integer, String> subVolume,
            String simulationName,String contextName
    ){ }

    public record GeometrySpecDTO(
            SpatialSelection[] selections, int axis, int sliceNumber, ExportSpecss.GeometryMode geometryMode
    ){ }
}
