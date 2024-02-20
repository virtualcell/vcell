package org.vcell.restq.handlers;

import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.parser.ExpressionException;
import com.google.gson.Gson;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.Main;
import org.vcell.restq.auth.AuthUtils;
import org.vcell.restq.db.BioModelRestDB;
import org.vcell.restq.db.OracleAgroalConnectionFactory;
import org.vcell.restq.models.BioModel;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Path("/api/biomodel")
public class BioModelResource {
    private static final User dummyUser = AuthUtils.DUMMY_USER; // replace when OAuth is implemented
    private final BioModelRestDB bioModelRestDB;

    @Inject
    public BioModelResource(OracleAgroalConnectionFactory oracleAgroalConnectionFactory) throws DataAccessException {
        bioModelRestDB = new BioModelRestDB(oracleAgroalConnectionFactory);
    }

    @GET
    @Path("{biomodelID}/json")
    @Operation(operationId = "getBiomodelInfoJSONById", summary = "Get biomodel information in JSON format by ID")
    @Produces(MediaType.APPLICATION_JSON)
    public BioModel getBioModelInfoJSON(@PathParam("biomodelID") String bioModelID) throws SQLException, DataAccessException, ExpressionException {
        User vcellUser = dummyUser;
        BioModelRep bioModelRep = bioModelRestDB.getBioModelRep(new KeyValue(bioModelID), vcellUser);
        return BioModel.fromBioModelRep(bioModelRep);
    }
}
