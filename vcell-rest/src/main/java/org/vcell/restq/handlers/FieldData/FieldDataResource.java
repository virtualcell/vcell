package org.vcell.restq.handlers.FieldData;

import cbit.image.ImageException;
import cbit.vcell.field.io.FieldDataSpec;
import cbit.vcell.math.MathException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.vcell.restq.db.UserRestDB;
import org.vcell.restq.errors.exceptions.*;
import org.vcell.restq.errors.exceptions.NotFoundWebException;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionableType;
import org.w3c.www.http.HTTP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

@Path("/api/v1/fieldData")
@RequestScoped
public class FieldDataResource {

    private static final Logger lg = LogManager.getLogger(FieldDataResource.class);

    @Inject
    SecurityIdentity securityIdentity;

    private final FieldDataDB fieldDataDB;
    private final UserRestDB userRestDB;
    private final String allowedFieldDataNamesRegex = "^[a-zA-Z0-9_]*$";

    @Inject
    public FieldDataResource(FieldDataDB fieldDataDB, UserRestDB userRestDB){
        this.fieldDataDB = fieldDataDB;
        this.userRestDB = userRestDB;
    }


    @GET
    @Path("IDs")
    @RolesAllowed("user")
    @Operation(operationId = "getAllIDs", summary = "Get all of the ids used to identify, and retrieve field data.")
    public ArrayList<FieldDataReference> getAllFieldDataIDs() throws DataAccessWebException, PermissionWebException, NotAuthenticatedWebException {
        try {
            return fieldDataDB.getAllFieldDataIDs(userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER));
        } catch (DataAccessException e) {
            throw new DataAccessWebException("Can't get field data ID's: " + e.getMessage(), e);
        }
    }

    @GET
    @Path("/shape/{fieldDataID}")
    @RolesAllowed("user")
    @Operation(operationId = "getShapeFromID", summary = "Get the shape of the field data. That is it's size, origin, extent, times, and data identifiers.")
    public FieldDataShape getFieldDataShapeFromID(@PathParam("fieldDataID") String fieldDataID) throws DataAccessWebException, NotFoundWebException, PermissionWebException, NotAuthenticatedWebException {
        try {
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            KeyValue keyValue = new KeyValue(fieldDataID);
            cbit.vcell.field.io.FieldDataShape results = fieldDataDB.getFieldDataShapeFromID(user, keyValue, FieldDataSpec.JOBINDEX_DEFAULT);
            return new FieldDataShape(results.extent, results.origin, results.iSize, results.variableInformation,results.times);
        } catch (DataAccessException e) {
            if (e.getCause() instanceof FileNotFoundException){
                throw new NotFoundWebException("Field data not found.", e);
            }
            throw new DataAccessWebException("Problem retrieving file.", e);
        }
    }

    @POST
    @RolesAllowed("user")
    @Path("/createFromSimulation")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Operation(operationId = "createFromSimulation", summary = "Create new field data from existing simulation results.")
    public void createNewFieldDataFromSimulation(@RestForm String simKeyReference, @RestForm int jobIndex, @RestForm String newFieldDataName) throws DataAccessWebException, PermissionWebException, NotAuthenticatedWebException {
        try {
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            fieldDataDB.saveFieldDataFromSimulation(user, new KeyValue(simKeyReference), jobIndex, newFieldDataName);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @POST
    @Path("/advancedCreate")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(operationId = "advancedCreate", summary = "Create Field Data with granular detail in one request." +
            "The following files are accepted: .tif and .zip.")
    public FieldDataSavedResults analyzeAndCreateFieldData(@RestForm @PartType(MediaType.APPLICATION_OCTET_STREAM) File file,
                                                  @RestForm @PartType(MediaType.TEXT_PLAIN) String fileName,
                                                  @RestForm("extent") @PartType(MediaType.APPLICATION_JSON) Extent extent,
                                                  @RestForm("iSize") @PartType(MediaType.APPLICATION_JSON) ISize iSize,
                                                  @RestForm @PartType(MediaType.TEXT_PLAIN) String[] channelNames,
                                                  @RestForm("times") @PartType(MediaType.TEXT_PLAIN) double[] times,
                                                  @RestForm("annotation") @PartType(MediaType.TEXT_PLAIN) String annotation,
                                                  @RestForm("origin") @PartType(MediaType.APPLICATION_JSON) Origin origin) throws UnprocessableContentWebException, DataAccessWebException, PermissionWebException, NotAuthenticatedWebException {
        try{
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            if (!Pattern.matches(allowedFieldDataNamesRegex, fileName) || fileName.length() > 100 || fileName.isEmpty()){
                throw new UnprocessableContentWebException("Invalid file name.");
            }
            FieldData fieldData = fieldDataDB.analyzeFieldDataFromFile(file, fileName);
            ExternalDataIdentifier edi = fieldDataDB.saveNewFieldDataFromFile(fileName,
                    channelNames, fieldData.shortSpecData, annotation,
                    user, times, origin, extent, iSize);
            return new FieldDataSavedResults(edi.getName(), edi.getKey().toString());
        } catch (DataFormatException e){
            throw new UnprocessableContentWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (ImageException | IOException e) {
            throw new WebApplicationException("Can't create new field data: " + e.getMessage(), e, HTTP.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/createFromFile")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(operationId = "createFromFile", summary = "Submit a .zip or .tif file that converts into field data, with all defaults derived from the file submitted.")
    public FieldDataSavedResults createFromFileWithDefaults(@RestForm @PartType(MediaType.APPLICATION_OCTET_STREAM) File file,
                                                            @RestForm String fieldDataName) throws UnprocessableContentWebException, DataAccessWebException, PermissionWebException, NotAuthenticatedWebException {
        try{
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            if (!Pattern.matches(allowedFieldDataNamesRegex, fieldDataName) || fieldDataName.length() > 100 || fieldDataName.isEmpty()){
                throw new UnprocessableContentWebException("Invalid file name.");
            }
            FieldData fieldData = fieldDataDB.analyzeFieldDataFromFile(file, fieldDataName);
            ExternalDataIdentifier edi = fieldDataDB.saveNewFieldDataFromFile(fieldDataName, fieldData.varNames, fieldData.shortSpecData,
                    fieldData.annotation, user, fieldData.times, fieldData.origin, fieldData.extent, fieldData.isize);
            return new FieldDataSavedResults(edi.getName(), edi.getKey().toString());
        } catch (DataFormatException e){
            throw new UnprocessableContentWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (ImageException | IOException e) {
            throw new WebApplicationException("Can't create new field data: " + e.getMessage(), e, HTTP.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/analyzeFile")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(operationId = "analyzeFile", summary = "Analyze uploaded image file (Tiff, Zip, and Non-GPL BioFormats) and return field data. Color mapped images not supported (the colors in those images will be interpreted as separate channels). " +
            "Filenames must be lowercase alphanumeric, and can contain underscores.")
    public FieldData analyzeFile(@RestForm File file, @RestForm String fileName) throws UnprocessableContentWebException, DataAccessWebException, PermissionWebException, NotAuthenticatedWebException {
        try{
            userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            if (!Pattern.matches(allowedFieldDataNamesRegex, fileName) || fileName.length() > 100 || fileName.isEmpty()){
                throw new UnprocessableContentWebException("Invalid file name.");
            }
            return fieldDataDB.analyzeFieldDataFromFile(file, fileName);
        } catch (DataFormatException e){
            throw new UnprocessableContentWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (ImageException e) {
            throw new WebApplicationException("Can't create new field data file", e, HTTP.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/save")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "save", summary = "Take the generated field data, and save it to the server. " +
            "User may adjust the analyzed file before uploading to edit defaults.")
    public FieldDataSavedResults createNewFieldDataFromSpecification(FieldData saveFieldData) throws UnprocessableContentWebException, DataAccessWebException, PermissionWebException, NotAuthenticatedWebException {
        FieldDataSavedResults fieldDataSavedResults;
        try{
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            ExternalDataIdentifier edi = fieldDataDB.saveNewFieldDataFromFile(saveFieldData.name,
                   saveFieldData.varNames, saveFieldData.shortSpecData, saveFieldData.doubleSpecData, saveFieldData.annotation,
                    user, saveFieldData.times, saveFieldData.origin, saveFieldData.extent, saveFieldData.isize);
            fieldDataSavedResults = new FieldDataSavedResults(edi.getName(), edi.getKey().toString());
        } catch (DataFormatException e){
            throw new UnprocessableContentWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (ImageException | IOException e) {
            throw new WebApplicationException(e.getMessage(), e, HTTP.INTERNAL_SERVER_ERROR);
        }
        return fieldDataSavedResults;
    }

    @POST
    @Path("/copyModelsFieldData")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    @Operation(operationId = "copyModelsFieldData", summary = "Copy all existing field data from a BioModel/MathModel that you have access to, but don't own.")
    public Hashtable<String, ExternalDataIdentifier> copyFieldData(SourceModel sourceModel) throws UnprocessableContentWebException, DataAccessWebException, PermissionWebException, NotAuthenticatedWebException {
        try {
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            return fieldDataDB.copyModelsFieldData(user, new KeyValue(sourceModel.modelID()), sourceModel.modelType.getName());
        } catch (MathException | XmlParseException | ExpressionException e){
            throw new UnprocessableContentWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    @DELETE
    @Path("/delete/{fieldDataID}")
    @RolesAllowed("user")
    @Operation(operationId = "delete", summary = "Delete the selected field data.")
    public void deleteFieldData(@PathParam("fieldDataID") String fieldDataID) throws DataAccessWebException, PermissionWebException, NotAuthenticatedWebException {
        try{
            ExternalDataIdentifier edi = new ExternalDataIdentifier(new KeyValue(fieldDataID), userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER),
                    null);
            fieldDataDB.deleteFieldData(edi);
        } catch (DataAccessException e) {
            throw new DataAccessWebException(e.getMessage(), e);
        }
    }

    public record FieldDataShape(
            Extent extent,
            Origin origin,
            ISize isize,
            DataIdentifier[] dataIdentifier,
            double[] times
    ){ }

    public record SourceModel(
            String modelID,
            ModelType modelType
    ) { }

    public enum ModelType{
        BIOMODEL(VersionableType.BioModelMetaData.getTypeName()),
        MATHMODEL(VersionableType.MathModelMetaData.getTypeName());

        private final String name;
        public String getName(){return name;}
        ModelType(String name){
            this.name = name;
        }
    }

    public record FieldDataReference(
            ExternalDataIdentifier fieldDataID,
            String annotation,
            Vector<KeyValue> simulationsReferencingThisID
    ) { }

    public record FieldData(
            short[][][] shortSpecData,  //[time][var][data]
            double[][][] doubleSpecData,
            String[] varNames,
            double[] times,
            Origin origin,
            Extent extent,
            ISize isize,
            String annotation,
            String name
    ){ }

    public record FieldDataSavedResults(
            String fieldDataName,
            String fieldDataKey
    ){ }

    public static class FieldDataFile {
        @RestForm("file")
        public File file;

        @RestForm("fileName")
        public String fileName;
    }

}
