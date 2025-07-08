package org.vcell.restq.handlers;

import cbit.vcell.modeldb.ExportHistoryDBDriver;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.errors.exceptions.DataAccessWebException;
import org.vcell.restq.errors.exceptions.NotAuthenticatedWebException;
import org.vcell.restq.services.ExportService;
import org.vcell.restq.services.UserRestService;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

@Path("/api/v1/exports")
public class ExportResource {

    @Inject
    SecurityIdentity securityIdentity;
    @Inject
    UserRestService userRestService;
    @Inject
    ExportService exportService;

    @Path("/history")
    @POST
    @RolesAllowed("user")
    @Operation(operationId = "addExportHistory", description = "Adds provided export information to a users export history.")
    public void addExportHistory(ExportHistoryDBDriver.ExportHistory history) throws DataAccessWebException, NotAuthenticatedWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try{
            exportService.addExportHistory(user, history);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @Path("/history")
    @GET
    @RolesAllowed("user")
    @Operation(operationId = "getExportHistory")
    public ExportHistoryDBDriver.ExportHistory getExportHistory() throws DataAccessWebException, NotAuthenticatedWebException {
        User user = userRestService.getUserFromIdentity(securityIdentity);
        try {
            return exportService.getExportHistory(user);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }
}
