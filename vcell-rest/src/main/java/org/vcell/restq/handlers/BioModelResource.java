package org.vcell.restq.handlers;

import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.xml.XmlParseException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.vcell.restq.auth.AuthUtils;
import org.vcell.restq.db.BioModelRestDB;
import org.vcell.restq.db.OracleAgroalConnectionFactory;
import org.vcell.restq.models.BioModel;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;

@Path("/api/biomodel")
public class BioModelResource {
    private static final User dummyUser = AuthUtils.DUMMY_USER; // replace when OAuth is implemented
    private final BioModelRestDB bioModelRestDB;
    private final DatabaseServerImpl databaseServer;

    @Inject
    public BioModelResource(OracleAgroalConnectionFactory oracleAgroalConnectionFactory) throws DataAccessException {
        bioModelRestDB = new BioModelRestDB(oracleAgroalConnectionFactory);
        databaseServer = new DatabaseServerImpl(oracleAgroalConnectionFactory, oracleAgroalConnectionFactory.getKeyFactory());
    }

    @GET
    @Path("{bioModelID}")
    @Operation(operationId = "getBiomodelById", summary = "Get BioModel information in JSON format by ID.")
    @Produces(MediaType.APPLICATION_JSON)
    public BioModel getBioModelInfo(@PathParam("bioModelID") String bioModelID) throws SQLException, DataAccessException, ExpressionException {
        User vcellUser = dummyUser;
        BioModelRep bioModelRep = bioModelRestDB.getBioModelRep(new KeyValue(bioModelID), vcellUser);
        return BioModel.fromBioModelRep(bioModelRep);
    }

    // TODO: Specify the media type instead of leaving it as wildcard
    @GET
    @Path("{biomodelID}/vcml_download")
    @Operation(operationId = "getBioModelVCML", summary = "Get the BioModel in VCML format.")
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public void getBioModelVCML(){

    }

    @GET
    @Path("{biomodelID}/sbml_download")
    @Operation(operationId = "getBioModelSBML", summary = "Get the BioModel in SBML format.")
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public void getBioModelSBML(){

    }

    @GET
    @Path("{biomodelID}/omex_download")
    @Operation(operationId = "getBioModelOMEX", summary = "Get the BioModel in OMEX format.")
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public void getBioModelOMEX(){

    }

    @GET
    @Path("{biomodelID}/bngl_download")
    @Operation(operationId = "getBioModelBNGL", summary = "Get the BioModel in BNGL format.")
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public void getBioModelBNGL(){

    }

    @GET
    @Path("{biomodelID}/diagram_download")
    @Operation(operationId = "getBioModelDIAGRAM", summary = "Get the BioModels diagram.")
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public void getBioModelDiagram(){

    }

    @POST
    @Path("upload_bioModel")
    @Operation(operationId = "uploadBioModel", summary = "Upload the BioModel to VCell database.")
    @Consumes(MediaType.TEXT_XML)
    public void uploadBioModel(String bioModelXML) throws DataAccessException {
        User user = dummyUser;
        databaseServer.saveBioModel(user, new BigString(bioModelXML), null);
    }

    @DELETE
    @Path("{bioModelID}/delete_bioModel")
    @Operation(operationId = "deleteBioModel", summary = "Delete the BioModel from VCell's database.")
    public void deleteBioModel(@PathParam("bioModelID") String bioModelID) throws DataAccessException {
        User user = dummyUser;
        databaseServer.deleteBioModel(user, new KeyValue(bioModelID));
    }


}
