package org.vcell.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

@Path("/publications")
public class PublicationResource {

    private Set<Publication> publications = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    public PublicationResource() {
        publications.add(new Publication("publication 1", "first publication"));
        publications.add(new Publication("publication 2", "second publication"));
    }

    @GET
    public Set<Publication> list() {
        return publications;
    }

    @POST
    public Set<Publication> add(Publication publication) {
        publications.add(publication);
        return publications;
    }

    @DELETE
    public Set<Publication> delete(Publication publication) {
        publications.removeIf(existingPublication -> existingPublication.name.contentEquals(publication.name));
        return publications;
    }
}