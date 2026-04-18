package org.vcell.restq.handlers;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.optimization.OptRequestMessage;
import org.vcell.optimization.jtd.OptProblem;
import org.vcell.restq.activemq.OptimizationMQ;
import org.vcell.restq.errors.exceptions.DataAccessWebException;
import org.vcell.restq.errors.exceptions.NotAuthenticatedWebException;
import org.vcell.restq.errors.exceptions.NotFoundWebException;
import org.vcell.restq.errors.exceptions.RuntimeWebException;
import org.vcell.optimization.OptJobStatus;
import org.vcell.restq.models.OptimizationJobStatus;
import org.vcell.restq.services.OptimizationRestService;
import org.vcell.restq.services.UserRestService;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@Path("/api/v1/optimization")
@Produces(MediaType.APPLICATION_JSON)
public class OptimizationResource {

    private final OptimizationRestService optimizationRestService;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    UserRestService userRestService;

    @Inject
    OptimizationMQ optimizationMQ;

    @org.eclipse.microprofile.config.inject.ConfigProperty(name = "vcell.optimization.parest-data-dir", defaultValue = "/simdata/parest_data")
    String parestDataDir;

    @Inject
    public OptimizationResource(OptimizationRestService optimizationRestService) {
        this.optimizationRestService = optimizationRestService;
    }

    @GET
    @RolesAllowed("user")
    @Operation(operationId = "listOptimizationJobs", summary = "List optimization jobs for the authenticated user")
    public OptimizationJobStatus[] list() throws NotAuthenticatedWebException, DataAccessWebException {
        try {
            User vcellUser = userRestService.getUserFromIdentity(securityIdentity);
            return optimizationRestService.listOptimizationJobs(vcellUser);
        } catch (SQLException e) {
            throw new RuntimeWebException("Failed to list optimization jobs: " + e.getMessage(), e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    @Operation(operationId = "submitOptimization", summary = "Submit a new parameter estimation optimization job")
    public OptimizationJobStatus submit(OptProblem optProblem)
            throws NotAuthenticatedWebException, DataAccessWebException {
        try {
            User vcellUser = userRestService.getUserFromIdentity(securityIdentity);
            OptimizationJobStatus status = optimizationRestService.submitOptimization(
                    optProblem, new File(parestDataDir), vcellUser);

            optimizationMQ.sendSubmitRequest(new OptRequestMessage(
                    status.id().toString(),
                    "submit",
                    new File(parestDataDir, "CopasiParest_" + status.id() + "_optProblem.json").getAbsolutePath(),
                    new File(parestDataDir, "CopasiParest_" + status.id() + "_optRun.json").getAbsolutePath(),
                    new File(parestDataDir, "CopasiParest_" + status.id() + "_optReport.txt").getAbsolutePath(),
                    null
            ));

            return status;
        } catch (SQLException e) {
            throw new RuntimeWebException("Failed to submit optimization job: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeWebException("Failed to write optimization problem to filesystem: " + e.getMessage(), e);
        }
    }

    @GET
    @Path("{optId}")
    @RolesAllowed("user")
    @Operation(operationId = "getOptimizationStatus", summary = "Get status, progress, or results of an optimization job")
    public OptimizationJobStatus getStatus(@PathParam("optId") Long optId)
            throws NotAuthenticatedWebException, NotFoundWebException, DataAccessWebException {
        try {
            User vcellUser = userRestService.getUserFromIdentity(securityIdentity);
            return optimizationRestService.getOptimizationStatus(new KeyValue(optId.toString()), vcellUser);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("Failed to query optimization status: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeWebException("Failed to read optimization data: " + e.getMessage(), e);
        }
    }

    @POST
    @Path("{optId}/stop")
    @RolesAllowed("user")
    @Operation(operationId = "stopOptimization", summary = "Stop a running optimization job")
    public OptimizationJobStatus stop(@PathParam("optId") Long optId)
            throws NotAuthenticatedWebException, NotFoundWebException, DataAccessWebException {
        try {
            User vcellUser = userRestService.getUserFromIdentity(securityIdentity);
            KeyValue jobKey = new KeyValue(optId.toString());
            OptimizationJobStatus currentStatus = optimizationRestService.getOptimizationStatus(jobKey, vcellUser);
            if (currentStatus.status() != OptJobStatus.RUNNING && currentStatus.status() != OptJobStatus.QUEUED) {
                throw new DataAccessWebException("Cannot stop optimization job in state: " + currentStatus.status());
            }

            String htcJobId = optimizationRestService.getHtcJobId(jobKey);
            optimizationMQ.sendStopRequest(new OptRequestMessage(
                    jobKey.toString(),
                    "stop",
                    null, null, null,
                    htcJobId
            ));

            optimizationRestService.updateOptJobStatus(jobKey, OptJobStatus.STOPPED, "Stopped by user");
            return optimizationRestService.getOptimizationStatus(jobKey, vcellUser);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("Failed to stop optimization job: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeWebException("Failed to read optimization data: " + e.getMessage(), e);
        }
    }
}
