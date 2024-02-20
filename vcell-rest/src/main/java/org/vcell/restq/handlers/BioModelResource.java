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

    @Location("biomodel.html")
    @Inject
    Template biomodelTemplate;


    @Inject
    public BioModelResource(OracleAgroalConnectionFactory oracleAgroalConnectionFactory) throws DataAccessException {
        bioModelRestDB = new BioModelRestDB(oracleAgroalConnectionFactory);
    }

    @GET
    @Path("{biomodelID}/html")
    @Operation(operationId = "getBiomodelInfoHTMLById", summary = "Get biomodel HTML information page by ID")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getBioModelInfoHTML(@PathParam("biomodelID") String bioModelID, @Context UriInfo uriInfo) throws SQLException, DataAccessException, MalformedURLException, ExpressionException {
        User vcellUser = dummyUser;
        BioModelRep bioModelRep = bioModelRestDB.getBioModelRep(new KeyValue(bioModelID), vcellUser);
        BioModel bioModel = BioModel.fromBioModelRep(bioModelRep);

        if (bioModel==null){
            throw new RuntimeException("biomodel not found");
        }
        Map<String, Object> links = new HashMap<>();

        String loginPath = "/"+ Main.LOGINFORM;  // +"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+getRequest().getResourceRef().toUrl());
        String logoutPath = "/"+Main.LOGOUT+"?"+Main.REDIRECTURL_FORMNAME+"="+ URLEncoder.encode(uriInfo.getBaseUri().toString(), Charset.defaultCharset());
        links.put("loginurl", loginPath);
        links.put("logouturl", logoutPath);
        links.put("vcmllink", "/api/" + Main.BIOMODEL + "/" + bioModelID + "/" + Main.VCML_DOWNLOAD);
        links.put("omexlink", "/api/" + Main.BIOMODEL + "/" + bioModelID + "/" + Main.OMEX_DOWNLOAD);
        links.put("sbmllink", "/api/" + Main.BIOMODEL + "/" + bioModelID + "/" + Main.SBML_DOWNLOAD);
        links.put("simstatuslink", "");
        links.put("simtasklink", "");
        links.put("diagramlink", "");

        Gson gson = new Gson();

        return biomodelTemplate.data("links", links,"user", vcellUser.getName(), "biomodel", bioModel, "jsonResponse", gson.toJson(bioModel));
    }

    @GET
    @Path("{biomodelID}/json")
    @Operation(operationId = "getBiomodelInfoJSONById", summary = "Get biomodel information in JSON format by ID")
    @Produces(MediaType.APPLICATION_JSON)
    public void getBioModelInfoJSON(){

    }
}
