package org.vcell.restq.handlers;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.NoCache;
import org.vcell.restq.db.AdminRestDB;
import org.vcell.restq.db.UserRestDB;
import org.vcell.restq.models.admin.UsageSummary;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import java.sql.SQLException;

@Path("/api/v1/admin")
@RequestScoped
public class AdminResource {

    // get Quarkus logger
    private static final Logger lg = LogManager.getLogger(AdminResource.class);

    @Inject
    SecurityIdentity securityIdentity;

    private final AdminRestDB adminRestDB;
    private final UserRestDB userRestDB;

    @Inject
    public AdminResource(AdminRestDB adminRestDB, UserRestDB userRestDB) {
        this.adminRestDB = adminRestDB;
        this.userRestDB = userRestDB;
    }

    @GET
    @Path("/usage")
    @RolesAllowed("admin")
    @Operation(operationId = "getUsage", summary = "Get usage summary")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public UsageSummary getUsage() throws DataAccessException {
        if (securityIdentity.isAnonymous()){
            throw new WebApplicationException("not authenticated", 401);
        }
        User vcellUser = userRestDB.getUserFromIdentity(securityIdentity);
        if (vcellUser == null) {
            throw new WebApplicationException("vcell user not specified", 401);
        }
        try {
            return adminRestDB.getUsageSummary(vcellUser);
        } catch (PermissionException e) {
            throw new WebApplicationException("not authorized", 403);
        } catch (SQLException e) {
            lg.error("database error", e);
            throw new DataAccessException("database error", e);
        }
    }

}
