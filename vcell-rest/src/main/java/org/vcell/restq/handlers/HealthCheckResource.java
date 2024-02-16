package org.vcell.restq.handlers;

import io.quarkus.logging.Log;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.auth.AuthUtils;
import org.vcell.restq.db.HealthService;
import org.vcell.restq.models.HealthCheck;
import org.vcell.restq.models.Publication;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;
import java.util.ArrayList;

@Path("/api/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthCheckResource {

    private final HealthService healthService;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    public HealthCheckResource(HealthService healthService) {
        this.healthService = healthService;
    }

//    @GET
//    @Path("login/{statusTime}")
//    @Operation(operationId = "getLoginStatus", summary = "Get login status")
//    public HealthCheck get_by_id(@PathParam("statusTime") long statusTime) throws SQLException, DataAccessException {
//        return healthService.getLoginStatus(statusTime);
//    }

    @GET
    @Operation(operationId = "checkServices", summary = "Check the health status of all services")
    public HealthCheck[] get_list(@QueryParam("startTime") Long startTime, @QueryParam("endTime") Long endTime) {
        // look into using Oso for Authorization (future).
        User vcellUser = AuthUtils.PUBLICATION_USER;
        try {
            long start_timestamp = startTime == null ? System.currentTimeMillis() - (2*60*60*1000): startTime; //2 hours before now
            long end_timestamp = endTime == null ? System.currentTimeMillis(): endTime;
            HealthService.HealthEvent[] healthEvents = healthService.query(start_timestamp, end_timestamp);
            ArrayList<HealthCheck> healthChecks = new ArrayList<HealthCheck>();
            for(HealthService.HealthEvent healthEvent: healthEvents){
                healthChecks.add(
                        new HealthCheck(healthEvent.eventType.name(), 0, healthEvent.message, healthEvent.timestamp_MS)
                );
            }
            return healthChecks.toArray(new HealthCheck[0]);
        } catch (PermissionException ee) {
            Log.error(ee);
            throw new RuntimeException("not authorized", ee);
        }
    }

//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed("curator")
//    @Operation(operationId = "createPublication", summary = "Create publication")
//    public Long add(Publication publication) {
//        Log.debug(securityIdentity.getPrincipal().getName()+" with roles " + securityIdentity.getRoles() + " is adding publication "+publication.title());
//        User vcellUser = AuthUtils.PUBLICATION_USER;
//        try {
//            KeyValue key = publicationService.savePublication(publication, vcellUser);
//            return Long.parseLong(key.toString());
//        } catch (PermissionException ee) {
//            Log.error(ee);
//            throw new RuntimeException("not authorized", ee);
//        } catch (DataAccessException | SQLException e) {
//            Log.error(e);
//            throw new RuntimeException("failed to add publication to VCell Database : " + e.getMessage(), e);
//        }
//    }


//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed("curator")
//    @Operation(operationId = "updatePublication", summary = "Create publication")
//    public Publication update(Publication publication) {
//        Log.debug(securityIdentity.getPrincipal().getName()+" with roles " + securityIdentity.getRoles() + " is adding publication "+publication.title());
//        User vcellUser = AuthUtils.PUBLICATION_USER;
//        try {
//            Publication pub = publicationService.updatePublication(publication, vcellUser);
//            return pub;
//        } catch (PermissionException ee) {
//            Log.error(ee);
//            throw new RuntimeException("not authorized", ee);
//        } catch (DataAccessException | SQLException e) {
//            Log.error(e);
//            throw new RuntimeException("failed to add publication to VCell Database : " + e.getMessage(), e);
//        }
//    }


//    @DELETE
//    @Path("{id}")
//    @RolesAllowed("curator")
//    @Operation(operationId = "deletePublication", summary = "Delete publication")
//    public void delete(@PathParam("id") Long publicationID) {
//        User vcellUser = AuthUtils.PUBLICATION_USER;
//        try {
//            int numRecordsDeleted = publicationService.deletePublication(new KeyValue(publicationID.toString()), vcellUser);
//            if (numRecordsDeleted != 1) {
//                throw new ObjectNotFoundException("failed to delete publication, record not found");
//            }
//        } catch (PermissionException ee) {
//            Log.error(ee);
//            throw new RuntimeException("not authorized", ee);
//        } catch (DataAccessException | SQLException e) {
//            Log.error(e);
//            throw new RuntimeException("failed to delete publication from VCell Database : " + e.getMessage(), e);
//        }
//    }
}