package org.vcell.restq.handlers;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.*;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.solver.AnnotatedFunction;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Multi;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.RestStreamElementType;
import org.vcell.restq.activemq.ExportMQInterface;
import org.vcell.restq.activemq.ExportRequestListenerMQ;
import org.vcell.restq.errors.exceptions.*;
import org.vcell.restq.services.Exports.ExportService;
import org.vcell.restq.services.SimulationRestService;
import org.vcell.restq.services.UserRestService;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Path("/api/v1/export")
public class ExportResource {

    @Inject
    SecurityIdentity securityIdentity;
    @Inject
    UserRestService userRestService;
    @Inject
    ExportService exportService;
    @Inject
    Instance<ExportMQInterface> exportRequestListenerMQ;
    @Inject
    SimulationRestService simulationRestService;

    @Path("/history")
    @GET
    @RolesAllowed("user")
    @Operation(operationId = "getExportHistory", hidden = true)
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
    @Operation(operationId = "deleteExportHistory", hidden = true)
    public ExportHistory deleteExportHistoryEntry() throws DataAccessWebException, NotAuthenticatedWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try {
            return exportService.getExportHistory(user);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @Path("/status")
    @GET
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "exportStatus", description = "Get the status of your most recent export jobs.")
    public Set<ExportEvent> pollExportStatus() throws DataAccessWebException, NotAuthenticatedWebException, NotFoundWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            return exportService.getMostRecentExportStatus(user);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundWebException(e.getMessage(), e);
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


    @Path("/N5")
    @POST
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "exportN5", description = "Create an N5 (ImageJ compatible) export. The request must contain the standard export information, exportable data type, dataset name, and sub-volume specifications.")
    public long createN5Export(N5ExportRequest er) throws DataAccessWebException, NotAuthenticatedWebException, UnprocessableContentWebException, BadRequestWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            N5Specs n5Specs = new N5Specs(er.exportableDataType(), ExportFormat.N5, er.datasetName, er.subVolume);
            ExportJob exportJob = exportService.createExportJobFromRequest(user, er.standardExportInformation, n5Specs, ExportFormat.N5);
            exportRequestListenerMQ.get().addExportJobToQueue(exportJob);
            return exportJob.id();
        } catch ( JsonProcessingException e) {
            throw new UnprocessableContentWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e){
            throw new BadRequestWebException(e.getMessage(), e);
        }
    }

    public record N5ExportRequest(
            StandardExportInfo standardExportInformation,
            Map<Integer, String> subVolume,
            ExportSpecss.ExportableDataType exportableDataType,
            String datasetName
    ){ }

    public record StandardExportInfo(
            ArrayList<AnnotatedFunctionDTO> outputContext, String contextName,
            String simulationName, String simulationKey, int simulationJob,
            GeometrySpecDTO geometrySpecs,
            TimeSpecs timeSpecs, VariableSpecs variableSpecs
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
            String simulationName,String contextName
    ){ }

    public record GeometrySpecDTO(
            SpatialSelection[] selections, int axis, int sliceNumber, ExportSpecss.GeometryMode geometryMode
    ){ }
}
