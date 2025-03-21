package org.vcell.restq.handlers.FieldData;

import cbit.image.ImageException;
import cbit.vcell.field.io.FieldDataFileOperationResults;
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
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.RestForm;
import org.vcell.restq.db.UserRestDB;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
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

    @Inject
    public FieldDataResource(FieldDataDB fieldDataDB, UserRestDB userRestDB){
        this.fieldDataDB = fieldDataDB;
        this.userRestDB = userRestDB;
    }


    @GET
    @Path("IDs")
    @RolesAllowed("user")
    @Operation(operationId = "getAllFieldDataIDs", summary = "Get all of the ids used to identify, and retrieve field data.")
    public ArrayList<FieldDataReference> getAllFieldDataIDs(){
        try {
            return fieldDataDB.getAllFieldDataIDs(userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER));
        } catch (SQLException e) {
            throw new WebApplicationException("Can't retrieve field data ID's.", 500);
        } catch (DataAccessException e) {
            throw new WebApplicationException(e.getMessage(), 500);
        }
    }

    @GET
    @Path("/fieldDataShape/{fieldDataID}")
    @RolesAllowed("user")
    @Operation(operationId = "getFieldDataShapeFromID", summary = "Get the shape of the field data. That is it's size, origin, extent, and data identifiers.")
    public FieldDataShape getFieldDataShapeFromID(@PathParam("fieldDataID") String fieldDataID){
        try {
            FieldDataFileOperationResults results = fieldDataDB.getFieldDataFromID(userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER), fieldDataID, 0);
            return new FieldDataShape(results.extent, results.origin, results.iSize, results.dataIdentifierArr,results.times);
        } catch (DataAccessException e) {
            if (e.getCause() instanceof FileNotFoundException){
                throw new WebApplicationException("Field data not found.", 404);
            }
            throw new WebApplicationException("Problem retrieving file.", 500);
        }
    }

    @POST
    @RolesAllowed("user")
    @Path("/createFieldDataFromSimulation")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Operation(operationId = "createNewFieldDataFromSimulation", summary = "Create new field data from a simulation.")
    public void createNewFieldDataFromSimulation(@RestForm String simKeyReference, @RestForm int jobIndex, @RestForm String newFieldDataName){
        try {
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            fieldDataDB.saveFieldDataFromSimulation(user, new KeyValue(simKeyReference), jobIndex, newFieldDataName);
        } catch (DataAccessException e) {
            throw new WebApplicationException(e.getMessage(), 500);
        }
    }

    @POST
    @Path("/analyzeFieldDataFile")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(operationId = "analyzeFieldDataFile", summary = "Analyze the field data from the uploaded file. Filenames must be lowercase alphanumeric, and can contain underscores.")
    public AnalyzedResultsFromFieldData analyzeFieldData(@RestForm File file, @RestForm String fileName){
        try{
            userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            if (!Pattern.matches("^[a-z0-9_]*$", fileName) || fileName.length() > 100 || fileName.isEmpty()){
                throw new WebApplicationException("Invalid file name.", 400);
            }
            return fieldDataDB.generateFieldDataFromFile(file, fileName);
        } catch (ImageException | DataFormatException | DataAccessException e) {
            throw new WebApplicationException("Can't create new field data file", 500);
        }
    }

    @POST
    @Path("/createFieldDataFromAnalyzedFile")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "createFieldDataFromAnalyzedFile", summary = "Take the analyzed results of the field data, modify it to your liking, then save it on the server.")
    public FieldDataSaveResults createNewFieldDataFromFile(AnalyzedResultsFromFieldData saveFieldData){
        FieldDataSaveResults fieldDataSaveResults;
        try{
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            FieldDataFileOperationResults fileResults = fieldDataDB.saveNewFieldDataFromFile(saveFieldData, user);
            fieldDataSaveResults = new FieldDataSaveResults(fileResults.externalDataIdentifier.getName(), fileResults.externalDataIdentifier.getKey().toString());
        } catch (ImageException | DataFormatException | DataAccessException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return fieldDataSaveResults;
    }

    @POST
    @Path("/copyModelsFieldData")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "copyModelsFieldData", summary = "Copy all existing field data from a BioModel/MathModel if not already owned.")
    public Hashtable<String, ExternalDataIdentifier> copyFieldData(CopyFieldData copyFieldData){
        try {
            User user = userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER);
            return fieldDataDB.copyNoConflict(user, new KeyValue(copyFieldData.modelID()), copyFieldData.modelType.getName());
        } catch (DataAccessException | MathException | XmlParseException | ExpressionException e) {
            throw new WebApplicationException(e.getMessage(), 500);
        }
    }

    @DELETE
    @Path("/delete/{fieldDataID}")
    @RolesAllowed("user")
    @Operation(operationId = "deleteFieldData", summary = "Delete the selected field data.")
    public void deleteFieldData(@PathParam("fieldDataID") String fieldDataID){
        try{
            fieldDataDB.deleteFieldData(userRestDB.getUserFromIdentity(securityIdentity, UserRestDB.UserRequirement.REQUIRE_USER), fieldDataID);
        } catch (DataAccessException e) {
            throw new WebApplicationException(e.getMessage(), 500);
        }
    }

    public record FieldDataShape(
            Extent extent,
            Origin origin,
            ISize isize,
            DataIdentifier[] dataIdentifier,
            double[] times
    ){ }

//    public record DataID(
//            String name,
//            String displayName,
//            VariableType variableType,
//            String domainName,
//            boolean bFunction
//    ){ }

//    public record FieldDataNoCopyConflict(
//            Hashtable<String, ExternalDataIdentifier> oldNameNewIDHash,
//            Hashtable<String, KeyValue> oldNameOldExtDataIDKeyHash
//    ) { }

    public record CopyFieldData(
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
            ExternalDataIdentifier externalDataIdentifier,
            String externalDataAnnotation,
            Vector<KeyValue> externalDataIDSimRefs
    ) { }

    public record AnalyzedResultsFromFieldData(
            short[][][] shortSpecData,  //[time][var][data]
            String[] varNames,
            double[] times,
            Origin origin,
            Extent extent,
            ISize isize,
            String annotation,
            String name
    ){ }

    public record FieldDataSaveResults(
            String fieldDataName,
            String fieldDataID
    ){ }

    public static class FieldDataFile {
        @RestForm("file")
        public File file;

        @RestForm("fileName")
        public String fileName;
    }

}
