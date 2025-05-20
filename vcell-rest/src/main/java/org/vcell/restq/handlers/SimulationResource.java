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
import org.vcell.restq.db.SimulationRestDB;
import org.vcell.restq.db.UserRestDB;
import org.vcell.restq.errors.exceptions.NotAuthenticatedWebException;
import org.vcell.restq.errors.exceptions.PermissionWebException;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.sql.SQLException;
import java.util.ArrayList;

@Path("/api/v1/Simulation")
public class SimulationResource {

    @Inject
    SecurityIdentity securityIdentity;

    private final SimulationRestDB simulationRestDB;
    private final UserRestDB userRestDB;

    @Inject
    public SimulationResource(SimulationRestDB simulationRestDB, UserRestDB userRestDB) {
        this.simulationRestDB = simulationRestDB;
        this.userRestDB = userRestDB;
    }


    @POST
    @Path("/{simID}/startSimulation")
    @RolesAllowed("user")
    @Operation(operationId = "startSimulation", summary = "Start a simulation.")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<StatusMessage> startSimulation(@PathParam("simID") String simID) throws PermissionWebException, NotAuthenticatedWebException {
        try {
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            return simulationRestDB.startSimulation(simID, user);
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/{simID}/stopSimulation")
    @RolesAllowed("user")
    @Operation(operationId = "stopSimulation", summary = "Stop a simulation.")
    public ArrayList<StatusMessage> stopSimulation(@PathParam("simID") String simID) throws PermissionWebException, NotAuthenticatedWebException {
        try {
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            return simulationRestDB.stopSimulation(simID, user);
        } catch (DataAccessException | SQLException | VCMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/{simID}/simulationStatus")
    @RolesAllowed("user")
    @Operation(operationId = "getSimulationStatus", summary = "Get the status of simulation running")
    public SimulationStatusPersistentRecord getSimulationStatus(@PathParam("simID") String simID,
                                    @QueryParam("bioModelID") String bioModelID, @QueryParam("mathModelID") String mathModelID) throws PermissionWebException, NotAuthenticatedWebException {
        try {
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            return simulationRestDB.getBioModelSimulationStatus(simID, bioModelID, user);
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
