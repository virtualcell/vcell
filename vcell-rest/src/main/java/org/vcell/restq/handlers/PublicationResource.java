package org.vcell.restq.handlers;

import cbit.vcell.modeldb.DatabaseServerImpl;
import io.quarkus.logging.Log;
import io.quarkus.security.PermissionsAllowed;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.vcell.restq.auth.AuthUtils;
import org.vcell.restq.db.PublicationService;
import org.vcell.restq.models.Publication;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.security.Permission;
import java.sql.SQLException;

@Path("/api/publications")
@Produces(MediaType.APPLICATION_JSON)
public class PublicationResource {

    private final PublicationService publicationService;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    public PublicationResource(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @GET
    @Path("{id}")
    @Operation(operationId = "getPublicationById", summary = "Get publication by ID")
    public Publication get_by_id(@PathParam("id") Long publicationID) throws SQLException, DataAccessException {
        return publicationService.getPublication(new KeyValue(publicationID.toString()), AuthUtils.PUBLICATION_USER);
    }

    @GET
    @Operation(operationId = "getPublications", summary = "Get all publications")
    public Publication[] get_list() {
        // look into using Oso for Authorization (future).
        User vcellUser = AuthUtils.PUBLICATION_USER;
        try {
            return publicationService.getPublications(DatabaseServerImpl.OrderBy.year_desc, vcellUser);
        } catch (PermissionException ee) {
            Log.error(ee);
            throw new RuntimeException("not authorized", ee);
        } catch (DataAccessException | SQLException e) {
            Log.error(e);
            throw new RuntimeException("failed to retrieve publications from VCell Database : " + e.getMessage(), e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("curator")
    @SecurityRequirement(name = "openId", scopes = {"roles"})
    @Operation(operationId = "createPublication", summary = "Create publication")
    public Long add(Publication publication) {
        Log.debug(securityIdentity.getPrincipal().getName()+" with roles " + securityIdentity.getRoles() + " is adding publication "+publication.title());
        User vcellUser = AuthUtils.PUBLICATION_USER;
        try {
            KeyValue key = publicationService.savePublication(publication, vcellUser);
            return Long.parseLong(key.toString());
        } catch (PermissionException ee) {
            Log.error(ee);
            throw new RuntimeException("not authorized", ee);
        } catch (DataAccessException | SQLException e) {
            Log.error(e);
            throw new RuntimeException("failed to add publication to VCell Database : " + e.getMessage(), e);
        }
    }


    @DELETE
    @Path("{id}")
    @RolesAllowed("curator")
    @SecurityRequirement(name = "openId", scopes = {"roles"})
    @Operation(operationId = "deletePublication", summary = "Delete publication")
    public void delete(@PathParam("id") Long publicationID) {
        User vcellUser = AuthUtils.PUBLICATION_USER;
        try {
            int numRecordsDeleted = publicationService.deletePublication(new KeyValue(publicationID.toString()), vcellUser);
            if (numRecordsDeleted != 1) {
                throw new ObjectNotFoundException("failed to delete publication, record not found");
            }
        } catch (PermissionException ee) {
            Log.error(ee);
            throw new RuntimeException("not authorized", ee);
        } catch (DataAccessException | SQLException e) {
            Log.error(e);
            throw new RuntimeException("failed to delete publication from VCell Database : " + e.getMessage(), e);
        }
    }
}