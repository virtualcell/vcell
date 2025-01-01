package org.vcell.restq.handlers.FieldData;

import cbit.image.ImageException;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.DataIdentifier;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.PartType;
import org.vcell.restq.db.UserRestDB;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
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
    @Operation(operationId = "getAllFieldDataIDs", summary = "Get all of the ids used to identify, and retrieve field data.")
    public ArrayList<FieldDataReference> getAllFieldDataIDs(){
        try {
            return fieldDataDB.getAllFieldDataIDs(userRestDB.getUserFromIdentity(securityIdentity));
        } catch (SQLException e) {
            throw new WebApplicationException("Can't retrieve field data ID's.", 500);
        } catch (DataAccessException e) {
            throw new WebApplicationException(e.getMessage(), 500);
        }
    }

    @GET
    @Operation(operationId = "getFieldDataFromID", summary = "Get the field data from the selected field data ID.")
    public FieldDataInfo getFieldDataFromID(String fieldDataID){
        try {
            FieldDataFileOperationResults results = fieldDataDB.getFieldDataFromID(userRestDB.getUserFromIdentity(securityIdentity), fieldDataID, 0);
            return new FieldDataInfo(results.extent, results.origin, results.iSize, results.dataIdentifierArr,results.times);
        } catch (DataAccessException e) {
            if (e.getCause() instanceof FileNotFoundException){
                throw new WebApplicationException("Field data not found.", 404);
            }
            throw new WebApplicationException("Problem retrieving file.", 500);
        }
    }

//    @POST
//    @Path("/createFieldDataFromSimulation")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Operation(operationId = "createNewFieldDataFromSimulation", summary = "Create new field data from a simulation.")
//    public ExternalDataIdentifier submitNewFieldDataToDB(FieldDataDBOperationSpec fieldDataDBOperationSpec){
//        FieldDataDBOperationResults results = null;
//        try {
//            results = fieldDataDB.saveNewFieldDataIntoDB(userRestDB.getUserFromIdentity(securityIdentity), fieldDataDBOperationSpec);
//        } catch (DataAccessException e) {
//            throw new WebApplicationException(e.getMessage(), 500);
//        }
//        return results.extDataID;
//    }

//    @POST
//    @Path("/analyzeFieldDataFromFile")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Operation(operationId = "generateFieldDataEstimate")
//    public FieldDataFileOperationSpec generateFieldDataEstimate(FieldDataFile fieldDataFile){
//        try{
//            return fieldDataDB.generateFieldDataFromFile(fieldDataFile.file, fieldDataFile.fileName);
//        } catch (ImageException | DataFormatException | DataAccessException e) {
//            throw new WebApplicationException("Can't create new field data file", 500);
//        }
//    }

    @POST
    @Path("/createFieldDataFromFileAlreadyAnalyzed")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "createNewFieldDataFromFileAlreadyAnalyzed")
    public FieldDataSaveResults createNewFieldDataFromFile(AnalyzedResultsFromFieldData saveFieldData){
        FieldDataSaveResults fieldDataSaveResults;
        try{
            User user = userRestDB.getUserFromIdentity(securityIdentity);
            FieldDataFileOperationResults fileResults = fieldDataDB.saveNewFieldDataFromFile(saveFieldData, user);
            fieldDataSaveResults = new FieldDataSaveResults(fileResults.externalDataIdentifier.getName(), fileResults.externalDataIdentifier.getKey().toString());
        } catch (ImageException | DataFormatException | DataAccessException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return fieldDataSaveResults;
    }

//    @POST
//    @Path("/copy")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Operation(operationId = "copyFieldData", summary = "Copy an existing field data entry.")
//    public FieldDataNoCopyConflict copyFieldData(FieldDataDBOperationSpec fieldDataDBOperationSpec){
//        FieldDataDBOperationResults results = null;
//        try {
//            results = fieldDataDB.copyNoConflict(userRestDB.getUserFromIdentity(securityIdentity), fieldDataDBOperationSpec);
//        } catch (DataAccessException e) {
//            throw new WebApplicationException(e.getMessage(), 500);
//        }
//        return new FieldDataNoCopyConflict(results.oldNameNewIDHash, results.oldNameOldExtDataIDKeyHash);
//    }

    @DELETE
    @Operation(operationId = "deleteFieldData", summary = "Delete the selected field data.")
    public void deleteFieldData(String fieldDataID){
        try{
            fieldDataDB.deleteFieldData(userRestDB.getUserFromIdentity(securityIdentity), fieldDataID);
        } catch (DataAccessException e) {
            throw new WebApplicationException(e.getMessage(), 500);
        }
    }

    public record FieldDataInfo(
            Extent extent,
            Origin origin,
            ISize isize,
            DataIdentifier[] dataIdentifier,
            double[] times
    ){ }

    public record DataID(
            String name,
            String displayName,
            VariableType variableType,
            String domainName,
            boolean bFunction
    ){ }

    public record FieldDataNoCopyConflict(
            Hashtable<String, ExternalDataIdentifier> oldNameNewIDHash,
            Hashtable<String, KeyValue> oldNameOldExtDataIDKeyHash
    ) { }

    public record FieldDataReference(
            ExternalDataIdentifier externalDataIdentifier,
            String externalDataAnnotation,
            Vector<KeyValue> externalDataIDSimRef
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
        @FormParam("file")
        @PartType(MediaType.APPLICATION_OCTET_STREAM)
        public InputStream file;

        @FormParam("fieldDataName")
        @PartType(MediaType.TEXT_PLAIN)
        public String fileName;
    }

}
