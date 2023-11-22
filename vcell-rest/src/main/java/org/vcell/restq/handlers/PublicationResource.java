package org.vcell.restq.handlers;

import cbit.vcell.modeldb.DatabaseServerImpl;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.vcell.restq.auth.AuthUtils;
import org.vcell.restq.db.PublicationService;
import org.vcell.restq.models.Publication;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;

@Path("/publications")
public class PublicationResource {

    private final PublicationService publicationService;

    @Inject
    public PublicationResource(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @Produces("application/json")
    @Consumes("application/json")
    @GET
    public Publication[] list() {
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

    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public String add(Publication publication) {
        User vcellUser = AuthUtils.PUBLICATION_USER;
        try {
            return publicationService.savePublication(publication, vcellUser).toString();
        } catch (PermissionException ee) {
            Log.error(ee);
            throw new RuntimeException("not authorized", ee);
        } catch (DataAccessException | SQLException e) {
            Log.error(e);
            throw new RuntimeException("failed to add publication to VCell Database : " + e.getMessage(), e);
        }
    }


    @Consumes("application/json")
    @DELETE
    public void delete(Long publicationID) {
        User vcellUser = AuthUtils.PUBLICATION_USER;
        try {
            int numRecordsDeleted = publicationService.deletePublication(new KeyValue(publicationID.toString()), vcellUser);
            if (numRecordsDeleted != 1) {
                throw new ObjectNotFoundException("failed to delete publication, record not found");
            }
            return;
        } catch (PermissionException ee) {
            Log.error(ee);
            throw new RuntimeException("not authorized", ee);
        } catch (DataAccessException | SQLException e) {
            Log.error(e);
            throw new RuntimeException("failed to delete biomodels from VCell Database : " + e.getMessage(), e);
        }
    }
}