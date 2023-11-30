package org.vcell.restq.handlers;

import cbit.vcell.modeldb.DatabaseServerImpl;
import io.quarkus.logging.Log;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.auth.AuthUtils;
import org.vcell.restq.db.PublicationService;
import org.vcell.restq.models.Publication;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;

@Path("/api/publications")
public class PublicationResource {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance publications(Publication[] publications);
        public static native TemplateInstance publication(Publication publication);
    }

    private final PublicationService publicationService;

    @Inject
    public PublicationResource(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @GET
    @Path("{id}")
    @Operation(hidden=true)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get_by_id_html(@PathParam("id") Long publicationID) throws SQLException, DataAccessException {
        return Templates.publication(publicationService.getPublication(new KeyValue(publicationID.toString()), AuthUtils.PUBLICATION_USER));
    }

    @GET
    @Path("{id}")
    @Operation(operationId = "getPublication", summary = "Get publication by ID")
    @Produces(MediaType.APPLICATION_JSON)
    public Publication get_by_id(@PathParam("id") Long publicationID) throws SQLException, DataAccessException {
        return publicationService.getPublication(new KeyValue(publicationID.toString()), AuthUtils.PUBLICATION_USER);
    }

    @GET
    @Operation(operationId = "getPublications", summary = "Get all publications")
    @Produces(MediaType.APPLICATION_JSON)
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

    @GET
    @Operation(hidden=true)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get_list_html() {
        // look into using Oso for Authorization (future).
        User vcellUser = AuthUtils.PUBLICATION_USER;
        try {
            Publication[] publications = publicationService.getPublications(DatabaseServerImpl.OrderBy.year_desc, vcellUser);
            return Templates.publications(publications);
        } catch (DataAccessException | SQLException e) {
            Log.error(e);
            throw new RuntimeException("failed to retrieve publications from VCell Database : " + e.getMessage(), e);
        }
    }

    @Produces("application/json")
    @Consumes("application/json")
    @RolesAllowed("admin")
    @Operation(operationId = "addPublication", summary = "Add publication")
    @POST
    public Long add(Publication publication) {
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


    @Consumes("application/json")
    @RolesAllowed("admin")
    @Path("{id}")
    @Operation(operationId = "deletePublication", summary = "Delete publication")
    @DELETE
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