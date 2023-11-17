package org.vcell.rest;

import io.agroal.api.AgroalDataSource;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/publications")
@Produces("application/json")
@Consumes("application/json")
public class PublicationResource {

    @Inject
    AgroalDataSource ds;

    private final Set<Publication> publications = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    public PublicationResource() {
        publications.add(new Publication("publication 1", "first publication"));
        publications.add(new Publication("publication 2", "second publication"));
    }

    @GET
    public Set<Publication> list() {
        Set<Publication> db_publications = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT ID, CITATION FROM VC_PUBLICATION")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BigDecimal key = rs.getBigDecimal("ID");
                    String value = rs.getString("CITATION");
                    db_publications.add(new Publication(key.toString(), value));
                }
            }
        } catch (SQLException e) {
            Log.error("database call failed", e);
        }
        Log.info("publications = " + db_publications.stream().map(p -> p.description).toList());
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