package org.vcell.rest.handlers;

import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.PublicationRep;
import cbit.vcell.parser.ExpressionException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.vcell.rest.auth.AuthUtils;
import org.vcell.rest.db.PublicationService;
import org.vcell.rest.models.PublicationRepresentation;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import java.sql.SQLException;
import java.util.ArrayList;

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
    public PublicationRepresentation[] list() {
        // look into using Oso for Authorization (future).
        User vcellUser = AuthUtils.DUMMY_USER;
        ArrayList<PublicationRepresentation> publicationRepresentations = new ArrayList<PublicationRepresentation>();
        try {
            PublicationRep[] publicationReps = publicationService.getPublicationReps(DatabaseServerImpl.OrderBy.year_desc, vcellUser);
            for (PublicationRep publicationRep : publicationReps) {
                PublicationRepresentation publicationRepresentation = new PublicationRepresentation(publicationRep);
                publicationRepresentations.add(publicationRepresentation);
            }
        } catch (PermissionException ee){
            Log.error(ee);
            throw new RuntimeException("not authorized");
        } catch (DataAccessException | SQLException | ExpressionException e) {
            Log.error(e);
            throw new RuntimeException("failed to retrieve biomodels from VCell Database : "+e.getMessage());
        }
        return publicationRepresentations.toArray(new PublicationRepresentation[0]);
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