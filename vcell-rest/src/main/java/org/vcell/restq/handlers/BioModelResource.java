package org.vcell.restq.handlers;

import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.vcell.restq.db.BioModelRestDB;
import org.vcell.restq.db.UserRestDB;
import org.vcell.restq.models.BioModel;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;

@Path("/api/v1/bioModel")
public class BioModelResource {
    @Inject
    SecurityIdentity securityIdentity;

    private final BioModelRestDB bioModelRestDB;
    private final UserRestDB userRestDB;

    @Inject
    public BioModelResource(BioModelRestDB bioModelRestDB, UserRestDB userRestDB) {
        this.bioModelRestDB = bioModelRestDB;
        this.userRestDB = userRestDB;
    }

    @GET
    @Path("{bioModelID}")
    @Operation(operationId = "getBiomodelById", summary = "Get BioModel information in JSON format by ID.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "return BioModel information in JSON format"),
            @APIResponse(responseCode = "404", description = "BioModel not found")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public BioModel getBioModelInfo(@PathParam("bioModelID") String bioModelID) throws SQLException, DataAccessException, ExpressionException {
        User vcellUser = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.ALLOW_ANONYMOUS);
        try {
            BioModelRep bioModelRep = bioModelRestDB.getBioModelRep(new KeyValue(bioModelID), vcellUser);
            return BioModel.fromBioModelRep(bioModelRep);
        }catch (ObjectNotFoundException e) {
            throw new WebApplicationException("BioModel not found", 404);
        }
    }

    // TODO: Specify the media type instead of leaving it as wildcard
    @GET
    @Path("{bioModelID}/vcml_download")
    @Operation(operationId = "getBioModelVCML", summary = "Get the BioModel in VCML format.", hidden = true)
    @Produces(MediaType.APPLICATION_XML)
    public void getBioModelVCML(@PathParam("bioModelID") String bioModelID){
        throw new WebApplicationException("Not implemented", 501);
    }

    @GET
    @Path("{bioModelID}/sbml_download")
    @Operation(operationId = "getBioModelSBML", summary = "Get the BioModel in SBML format.", hidden = true)
    @Produces(MediaType.APPLICATION_XML)
    public void getBioModelSBML(@PathParam("bioModelID") String bioModelID){
        throw new WebApplicationException("Not implemented", 501);
    }

    @GET
    @Path("{bioModelID}/omex_download")
    @Operation(operationId = "getBioModelOMEX", summary = "Get the BioModel in OMEX format.", hidden = true)
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public void getBioModelOMEX(@PathParam("bioModelID") String bioModelID){
        throw new WebApplicationException("Not implemented", 501);
    }

    @GET
    @Path("{bioModelID}/bngl_download")
    @Operation(operationId = "getBioModelBNGL", summary = "Get the BioModel in BNGL format.", hidden = true)
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public void getBioModelBNGL(@PathParam("bioModelID") String bioModelID){
        throw new WebApplicationException("Not implemented", 501);
    }

    @GET
    @Path("{bioModelID}/diagram_download")
    @Operation(operationId = "getBioModelDIAGRAM", summary = "Get the BioModels diagram.", hidden = true)
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public void getBioModelDiagram(@PathParam("bioModelID") String bioModelID){
        throw new WebApplicationException("Not implemented", 501);
    }

    @POST
    @Path("upload_bioModel")
    @Operation(operationId = "uploadBioModel", summary = "Upload the BioModel to VCell database. Returns BioModel ID.")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("user")
    public String uploadBioModel(String bioModelXML) throws DataAccessException, XmlParseException {
        User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
        KeyValue keyValue = bioModelRestDB.saveBioModel(user, bioModelXML);
        return keyValue.toString();
    }

    @DELETE
    @Path("{bioModelID}")
    @Operation(operationId = "deleteBioModel", summary = "Delete the BioModel from VCell's database.")
    public void deleteBioModel(@PathParam("bioModelID") String bioModelID) throws DataAccessException {
        User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);;
        bioModelRestDB.deleteBioModel(user, new KeyValue(bioModelID));
    }
}
