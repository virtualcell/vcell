package org.vcell.restq.handlers;

import cbit.vcell.message.VCMessagingException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.Simulations.SimulationStatusPersistentRecord;
import org.vcell.restq.Simulations.StatusMessage;
import org.vcell.restq.services.SimulationRestService;
import org.vcell.restq.services.UserRestService;
import org.vcell.restq.errors.exceptions.DataAccessWebException;
import org.vcell.restq.errors.exceptions.NotAuthenticatedWebException;
import org.vcell.restq.errors.exceptions.PermissionWebException;
import org.vcell.restq.errors.exceptions.RuntimeWebException;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.sql.SQLException;
import java.util.ArrayList;

@Path("/api/v1/Simulation")
public class SimulationResource {

    @Inject
    SecurityIdentity securityIdentity;

    private final SimulationRestService simulationRestService;
    private final UserRestService userRestService;

    @Inject
    public SimulationResource(SimulationRestService simulationRestService, UserRestService userRestService) {
        this.simulationRestService = simulationRestService;
        this.userRestService = userRestService;
    }


    @POST
    @Path("/{simID}/startSimulation")
    @RolesAllowed("user")
    @Operation(operationId = "startSimulation", summary = "Start a simulation.")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<StatusMessage> startSimulation(@PathParam("simID") String simID) throws PermissionWebException, NotAuthenticatedWebException, DataAccessWebException {
        try {
            User user = userRestService.getUserFromIdentity(securityIdentity, UserRestService.UserRequirement.REQUIRE_USER);
            return simulationRestService.startSimulation(simID, user);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("Database Exception", e);
        }
    }

    @POST
    @Path("/{simID}/stopSimulation")
    @RolesAllowed("user")
    @Operation(operationId = "stopSimulation", summary = "Stop a simulation.")
    public ArrayList<StatusMessage> stopSimulation(@PathParam("simID") String simID) throws PermissionWebException, NotAuthenticatedWebException, DataAccessWebException {
        try {
            User user = userRestService.getUserFromIdentity(securityIdentity, UserRestService.UserRequirement.REQUIRE_USER);
            return simulationRestService.stopSimulation(simID, user);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException | VCMessagingException e) {
            throw new RuntimeWebException(e.getMessage(), e);
        }
    }

    @GET
    @Path("/{simID}/simulationStatus")
    @RolesAllowed("user")
    @Operation(operationId = "getSimulationStatus", summary = "Get the status of simulation running")
    public SimulationStatusPersistentRecord getSimulationStatus(@PathParam("simID") String simID,
                                    @QueryParam("bioModelID") String bioModelID, @QueryParam("mathModelID") String mathModelID) throws PermissionWebException, NotAuthenticatedWebException, DataAccessWebException {
        try {
            User user = userRestService.getUserFromIdentity(securityIdentity, UserRestService.UserRequirement.REQUIRE_USER);
            return simulationRestService.getBioModelSimulationStatus(simID, bioModelID, user);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException(e.getMessage(), e);
        }
    }

}
