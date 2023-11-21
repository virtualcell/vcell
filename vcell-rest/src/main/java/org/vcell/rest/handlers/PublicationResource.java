package org.vcell.rest.handlers;

import cbit.vcell.modeldb.DatabaseServerImpl;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.vcell.rest.auth.AuthUtils;
import org.vcell.rest.db.PublicationService;
import org.vcell.rest.models.Publication;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import java.sql.SQLException;

@Path("/publications")
@Produces("application/json")
@Consumes("application/json")
public class PublicationResource {

    private final PublicationService publicationService;

    @Inject
    public PublicationResource(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @GET
    public Publication[] list() {
        // look into using Oso for Authorization (future).
        User vcellUser = AuthUtils.DUMMY_USER;
        try {
            return publicationService.getPublications(DatabaseServerImpl.OrderBy.year_desc, vcellUser);
        } catch (PermissionException ee) {
            Log.error(ee);
            throw new RuntimeException("not authorized");
        } catch (DataAccessException | SQLException e) {
            Log.error(e);
            throw new RuntimeException("failed to retrieve biomodels from VCell Database : " + e.getMessage());
        }
    }

//    @POST
//    public Set<Publication> add(Publication publication) {
//        publications.add(publication);
//        return publications;
//    }
//
//    @DELETE
//    public Set<Publication> delete(Publication publication) {
//        publications.removeIf(existingPublication -> existingPublication.name.contentEquals(publication.name));
//        return publications;
//    }
}