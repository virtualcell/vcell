package org.vcell.restq.handlers.FieldData;

import cbit.image.ImageException;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
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

import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
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
    @Operation(operationId = "getAllFieldData", summary = "Get all of the field data for that user.")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public FieldDataExternalDataIDs getAllFieldData(FieldDataDBOperationSpec fieldDataDBOperationSpec){
        FieldDataDBOperationResults results = null;
        try {
            results = fieldDataDB.getAllFieldData(userRestDB.getUserFromIdentity(securityIdentity), fieldDataDBOperationSpec);
        } catch (SQLException e) {
            throw new WebApplicationException("Can't retrieve field data.", 500);
        } catch (DataAccessException e) {
            throw new WebApplicationException(e.getMessage(), 500);
        }
        return new FieldDataResource.FieldDataExternalDataIDs(results.extDataIDArr, results.extDataAnnotArr, results.extdataIDAndSimRefH);
    }

    @POST
    @Path("/createFieldDataFromSimulation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "createNewFieldDataFromSimulation", summary = "Create new field data from a simulation.")
    public ExternalDataIdentifier submitNewFieldDataToDB(FieldDataDBOperationSpec fieldDataDBOperationSpec){
        FieldDataDBOperationResults results = null;
        try {
            results = fieldDataDB.saveNewFieldDataIntoDB(userRestDB.getUserFromIdentity(securityIdentity), fieldDataDBOperationSpec);
        } catch (DataAccessException e) {
            throw new WebApplicationException(e.getMessage(), 500);
        }
        return results.extDataID;
    }

    @POST
    @Path("/createFieldDataFromFile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(operationId = "generateFieldDataEstimate")
    public FieldDataFileOperationSpec generateFieldDataEstimate(FieldDataFile fieldDataFile){
        try{
            return fieldDataDB.generateFieldDataFromFile(fieldDataFile.file, fieldDataFile.fileName);
        } catch (ImageException | DataFormatException | DataAccessException e) {
            throw new WebApplicationException("Can't create new field data file", 500);
        }
    }

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

    @POST
    @Path("/copy")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "copyFieldData", summary = "Copy an existing field data entry.")
    public FieldDataNoCopyConflict copyFieldData(FieldDataDBOperationSpec fieldDataDBOperationSpec){
        FieldDataDBOperationResults results = null;
        try {
            results = fieldDataDB.copyNoConflict(userRestDB.getUserFromIdentity(securityIdentity), fieldDataDBOperationSpec);
        } catch (DataAccessException e) {
            throw new WebApplicationException(e.getMessage(), 500);
        }
        return new FieldDataNoCopyConflict(results.oldNameNewIDHash, results.oldNameOldExtDataIDKeyHash);
    }

    @DELETE
    @Operation(operationId = "deleteFieldData", summary = "Delete the selected field data.")
    public void deleteFieldData(String fieldDataID){
        try{
            fieldDataDB.deleteFieldData(userRestDB.getUserFromIdentity(securityIdentity), fieldDataID);
        } catch (DataAccessException e) {
            throw new WebApplicationException(e.getMessage(), 500);
        }
    }


    public record FieldDataNoCopyConflict(
            Hashtable<String, ExternalDataIdentifier> oldNameNewIDHash,
            Hashtable<String, KeyValue> oldNameOldExtDataIDKeyHash
    ) { }

    public record FieldDataExternalDataIDs(
            ExternalDataIdentifier[] externalDataIdentifiers,
            String[] externalDataAnnotations,
            HashMap<ExternalDataIdentifier, Vector<KeyValue>> externalDataIDSimRefs
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
