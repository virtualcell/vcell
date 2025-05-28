package org.vcell.restq.handlers;

import cbit.vcell.modeldb.DatabaseServerImpl;
import io.quarkus.logging.Log;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.services.PublicationService;
import org.vcell.restq.services.UserRestService;
import org.vcell.restq.errors.exceptions.*;
import org.vcell.restq.models.Publication;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;

@Path("/api/v1/publications")
@Produces(MediaType.APPLICATION_JSON)
public class PublicationResource {

    private final PublicationService publicationService;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    UserRestService userRestService;

    @Inject
    public PublicationResource(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @GET
    @Path("{id}")
    @Operation(operationId = "getPublicationById", summary = "Get publication by ID")
    public Publication get_by_id(@PathParam("id") Long publicationID) throws DataAccessWebException {
        try {
            User vcellUser = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
            return publicationService.getPublication(new KeyValue(publicationID.toString()), vcellUser);
        } catch (SQLException e){
            throw new RuntimeWebException(e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new DataAccessWebException("failed to retrieve publications from VCell Database : " + e.getMessage(), e);
        }

    }

    @GET
    @Operation(operationId = "getPublications", summary = "Get all publications")
    public Publication[] get_list() throws DataAccessWebException {
        try {
            User vcellUser = userRestService.getUserOrAnonymousFromIdentity(securityIdentity);
            return publicationService.getPublications(DatabaseServerImpl.OrderBy.year_desc, vcellUser);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("failed to retrieve publications from VCell Database : " + e.getMessage(), e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed("curator")
    @RolesAllowed("user")
    @Operation(operationId = "createPublication", summary = "Create publication")
    public Long add(Publication publication) throws PermissionWebException, NotAuthenticatedWebException, DataAccessWebException {
        Log.debug(securityIdentity.getPrincipal().getName()+" with roles " + securityIdentity.getRoles() + " is adding publication "+publication.title());
        try {
            User vcellUser = userRestService.getUserFromIdentity(securityIdentity);
            KeyValue key = publicationService.savePublication(publication, vcellUser);
            return Long.parseLong(key.toString());
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("failed to add publication to VCell Database : " + e.getMessage(), e);
        }
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed("curator")
    @RolesAllowed("user")
    @Operation(operationId = "updatePublication", summary = "Update publication")
    public Publication update(Publication publication) throws PermissionWebException, NotAuthenticatedWebException, DataAccessWebException {
        Log.debug(securityIdentity.getPrincipal().getName()+" with roles " + securityIdentity.getRoles() + " is adding publication "+publication.title());
        try {
            User vcellUser = userRestService.getUserFromIdentity(securityIdentity);
            Publication pub = publicationService.updatePublication(publication, vcellUser);
            return pub;
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("failed to add publication to VCell Database : " + e.getMessage(), e);
        }
    }


    @DELETE
    @Path("{id}")
//    @RolesAllowed("curator")
    @RolesAllowed("user")
    @Operation(operationId = "deletePublication", summary = "Delete publication")
    public void delete(@PathParam("id") Long publicationID) throws PermissionWebException, NotAuthenticatedWebException, NotFoundWebException, DataAccessWebException {
        try {
            User vcellUser = userRestService.getUserFromIdentity(securityIdentity);
            int numRecordsDeleted = publicationService.deletePublication(new KeyValue(publicationID.toString()), vcellUser);
            if (numRecordsDeleted != 1) {
                throw new NotFoundWebException("failed to delete publication, record not found");
            }
        } catch (PermissionException e){
            throw new PermissionWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("failed to delete publication from VCell Database : " + e.getMessage(), e);
        }
    }
}