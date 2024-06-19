package org.vcell.restq.handlers;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.vcell.restq.db.SimulationRestDB;
import org.vcell.restq.db.UserRestDB;
import org.vcell.restq.models.simulation.OverrideRepresentationGenerator;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.List;

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
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/{simID}/stopSimulation")
    @RolesAllowed("user")
    @Operation(operationId = "stopSimulation", summary = "Stop a simulation.")
    public void stopSimulation(@PathParam("simID") String simID) {
        try {
            User user = userRestDB.getUserFromIdentity(securityIdentity);
            simulationRestDB.stopSimulation(simID, user);
        } catch (DataAccessException | SQLException | VCMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/{simID}/simulationStatus")
    @RolesAllowed("user")
    @Operation(operationId = "getSimulationStatus", summary = "Get the status of simulation running")
    public SimulationRestDB.SimulationStatus getSimulationStatus(@PathParam("simID") String simID){
        try {
            User user = userRestDB.getUserFromIdentity(securityIdentity);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
