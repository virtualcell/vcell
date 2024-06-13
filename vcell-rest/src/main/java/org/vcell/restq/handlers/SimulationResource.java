package org.vcell.restq.handlers;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.db.SimulationRestDB;
import org.vcell.restq.db.UserRestDB;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.sql.SQLException;

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
    public void startSimulation(@PathParam("simID") String simID) {
        try {
            User user = userRestDB.getUserFromIdentity(securityIdentity);
            simulationRestDB.startSimulation(simID, user);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
