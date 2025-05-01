package org.vcell.restq.handlers;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.RestForm;
import org.vcell.restq.Main;
import org.vcell.restq.config.WebExceptionErrorHandler;
import org.vcell.restq.db.BioModelRestDB;
import org.vcell.restq.db.UserRestDB;
import org.vcell.restq.models.BioModel;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.w3c.www.http.HTTP;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Optional;

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
    @Operation(operationId = "getBioModel", summary = "Get BioModel.")
    @Produces(MediaType.APPLICATION_JSON)
    public BioModel getBioModelInfo(@PathParam("bioModelID") String bioModelID) throws SQLException, DataAccessException, ExpressionException {
        User vcellUser = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.ALLOW_ANONYMOUS);
        if (vcellUser == null) {
            vcellUser = Main.DUMMY_USER;
        }
        try {
            BioModelRep bioModelRep = bioModelRestDB.getBioModelRep(new KeyValue(bioModelID), vcellUser);
            return BioModel.fromBioModelRep(bioModelRep);
        }catch (ObjectNotFoundException e) {
            throw new WebApplicationException("BioModel not found", e, HTTP.NOT_FOUND);
        }
    }

    @GET
    @Path("{bioModelID}/vcml_download")
    @Operation(operationId = "getBioModelVCML", summary = "Get the BioModel in VCML format.")
    @Produces(MediaType.TEXT_XML)
    public String getBioModelVCML(@PathParam("bioModelID") String bioModelID){
        User vcellUser = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.ALLOW_ANONYMOUS);
        if (vcellUser == null) {
            vcellUser = Main.DUMMY_USER;
        }
        try {
            return bioModelRestDB.getBioModel(vcellUser, new KeyValue(bioModelID));
        }catch (ObjectNotFoundException e) {
            throw new WebApplicationException("BioModel not found", e, HTTP.NOT_FOUND);
        } catch (DataAccessException e) {
            throw new WebApplicationException("Can't Access BioModel", e, WebExceptionErrorHandler.DATA_ACCESS_EXCEPTION_HTTP_CODE);
        }
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

    @DELETE
    @Path("{bioModelID}")
    @Operation(operationId = "deleteBioModel", summary = "Delete the BioModel from VCell's database.")
    public void deleteBioModel(@PathParam("bioModelID") String bioModelID) throws DataAccessException {
        User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);;
        bioModelRestDB.deleteBioModel(user, new KeyValue(bioModelID));
    }

    @POST
    @Path("/save")
    @RolesAllowed("user")
    @Consumes(MediaType.TEXT_XML)
    @Operation(operationId = "saveBioModel", summary = "Save the BioModel, returning saved BioModel as VCML.")
    public String saveBioModel(String bioModelXML){
        User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
        try {
            cbit.vcell.biomodel.BioModel savedBioModel = bioModelRestDB.saveBioModel(user, bioModelXML);
            return XmlHelper.bioModelToXML(savedBioModel);
        } catch (DataAccessException e) {
            throw new WebApplicationException("Data Access Exception: " + e.getMessage(), e, WebExceptionErrorHandler.DATA_ACCESS_EXCEPTION_HTTP_CODE);
        } catch (MappingException | PropertyVetoException | SQLException e){
            throw new WebApplicationException("Mapping, SQL, or PropertyVeto Exception.", e, HTTP.INTERNAL_SERVER_ERROR);
        } catch (XmlParseException e) {
            throw new WebApplicationException("Couldn't parse the BioModel.", e, WebExceptionErrorHandler.UNPROCESSABLE_HTTP_CODE);
        }
    }

    @POST
    @Path("/saveAs")
    @RolesAllowed("user")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(operationId = "saveBioModelAs", summary = "Save as a new BioModel under the name given. Returns saved BioModel as VCML.")
    public String saveAsBioModel(@RestForm String bioModelXML, @RestForm String name){
        User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
        try {
            cbit.vcell.biomodel.BioModel savedBioModel = bioModelRestDB.saveAs(user, bioModelXML, name, null);
            return XmlHelper.bioModelToXML(savedBioModel);
        } catch (DataAccessException e) {
            throw new WebApplicationException("Data Access Exception: " + e.getMessage(), e, WebExceptionErrorHandler.DATA_ACCESS_EXCEPTION_HTTP_CODE);
        } catch (XmlParseException e){
            throw new WebApplicationException("Couldn't parse the BioModel.", e, WebExceptionErrorHandler.UNPROCESSABLE_HTTP_CODE);
        }
    }

    @POST
    @Path("/advancedSave")
    @RolesAllowed("user")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(operationId = "advancedSaveBioModel", summary = "Save the BioModel while also " +
            "specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.")
    public String advancedSave(@RestForm String bioModelXML, @RestForm String[] simsRequiringUpdates){
        User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
        try {
            cbit.vcell.biomodel.BioModel savedBioModel = bioModelRestDB.saveAs(user, null, bioModelXML, simsRequiringUpdates);
            return XmlHelper.bioModelToXML(savedBioModel);
        } catch (DataAccessException e) {
            throw new WebApplicationException("Data Access Exception: " + e.getMessage(), e, WebExceptionErrorHandler.DATA_ACCESS_EXCEPTION_HTTP_CODE);
        } catch (XmlParseException e){
            throw new WebApplicationException("Couldn't parse the BioModel.", e, WebExceptionErrorHandler.UNPROCESSABLE_HTTP_CODE);
        }
    }

    @POST
    @Path("/advancedSaveAs")
    @RolesAllowed("user")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(operationId = "advancedSaveAsBioModel", summary = "Save the BioModel while also " +
            "specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.")
    public String advancedSaveAs(@RestForm String bioModelXML, @RestForm String name, @RestForm String[] simsRequiringUpdates){
        User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
        try {
            cbit.vcell.biomodel.BioModel savedBioModel = bioModelRestDB.saveAs(user, name, bioModelXML, simsRequiringUpdates);
            return XmlHelper.bioModelToXML(savedBioModel);
        } catch (DataAccessException e) {
            throw new WebApplicationException("Data Access Exception: " + e.getMessage(), e, WebExceptionErrorHandler.DATA_ACCESS_EXCEPTION_HTTP_CODE);
        } catch (XmlParseException e){
            throw new WebApplicationException("Couldn't parse the BioModel.", e, WebExceptionErrorHandler.UNPROCESSABLE_HTTP_CODE);
        }
    }
}
