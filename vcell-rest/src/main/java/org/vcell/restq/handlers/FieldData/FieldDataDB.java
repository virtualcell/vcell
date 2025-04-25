package org.vcell.restq.handlers.FieldData;

import cbit.image.ImageException;
import cbit.image.VCImageUncompressed;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.*;
import cbit.vcell.field.io.CopyFieldDataResult;
import cbit.vcell.field.io.FieldData;
import cbit.vcell.field.io.FieldDataShape;
import cbit.vcell.field.io.FieldDataSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.*;
import org.vcell.util.document.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.DataFormatException;

@ApplicationScoped
public class FieldDataDB {
    private static final Logger logger = LogManager.getLogger(FieldDataDB.class);

    private final DatabaseServerImpl databaseServer;
    private final DataSetControllerImpl dataSetController;

    @Inject
    public FieldDataDB(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException, FileNotFoundException {
        databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        String primarySimDataDir = PropertyLoader.getProperty(PropertyLoader.primarySimDataDirInternalProperty, "/simdata");
        String secondarySimDataDir = PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirInternalProperty, "/simdata");
        dataSetController = new DataSetControllerImpl(null,
                new File(primarySimDataDir),
                new File(secondarySimDataDir));
    }

    /**
     * Copy all the field data for a specific model that is not already owned by the user.
     * It the client is copying a Model, and call this to get all of its field data, then it
     * is the clients responsibility to update the Models references to the newly copied field data.
     */
    public Hashtable<String, ExternalDataIdentifier> copyModelsFieldData(User requester, KeyValue documentKey, String documentType) throws DataAccessException, XmlParseException, MathException, ExpressionException {
        VCDocument vcDocument;

        //Get Objects from Document that might need to have FieldFuncs replaced
        // If the requester does not have access to the Model an exception should be thrown to stop this function
        ArrayList<SimulationOwner> modelsListOfContext = new ArrayList<>();
        if(documentType.equalsIgnoreCase(VersionableType.MathModelMetaData.getTypeName())){
            BigString mathModelXML = databaseServer.getMathModelXML(requester, documentKey);
            vcDocument = XmlHelper.XMLToMathModel(new XMLSource(mathModelXML.toString()));
            modelsListOfContext.add(((MathModel)vcDocument));
        }else if(documentType.equalsIgnoreCase(VersionableType.BioModelMetaData.getTypeName())){
            BigString bioModelXML = databaseServer.getBioModelXML(requester, documentKey);
            vcDocument = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
            SimulationContext[] contexts = ((BioModel)vcDocument).getSimulationContexts();
            modelsListOfContext.addAll(Arrays.asList(contexts));
        } else {
            UnsupportedOperationException notSupported = new UnsupportedOperationException("Document type (" + documentType + ") is not supported.");
            logger.error(notSupported);
            throw notSupported;
        }
        User ogOwner = vcDocument.getVersion().getOwner();

		//Get Field names
        Set<String> fieldNames = new HashSet<>();
        for(SimulationOwner fieldFunctionContainer : modelsListOfContext){
            FieldFunctionArguments[] fieldFuncArgsArr = {};
            if (fieldFunctionContainer instanceof MathDescription mathDescription){
                fieldFuncArgsArr = FieldUtilities.getFieldFunctionArguments(mathDescription);
            }else if (fieldFunctionContainer instanceof SimulationContext simulationContext){
                fieldFuncArgsArr = simulationContext.getFieldFunctionArguments();
            }
            for (FieldFunctionArguments fieldFunctionArguments : fieldFuncArgsArr) {
                fieldNames.add(fieldFunctionArguments.getFieldName());
            }
        }
        if (fieldNames.isEmpty()){
            return new Hashtable<>();
        }

        // Find which ID's are appropriate
        FieldDataAllDBEntries allIDs = databaseServer.getFieldDataIDs(ogOwner);
        ArrayList<ExternalDataIdentifier> edis = new ArrayList<>();
        ArrayList<String> annotations = new ArrayList<>();
        for (int i = 0; i < allIDs.ids.length; i++){
            ExternalDataIdentifier edi = allIDs.ids[i];
            if (fieldNames.contains(edi.getName())){
                edis.add(edi);
                annotations.add(allIDs.annotationsForIds[i]);
            }
        }

        if (edis.isEmpty()){
            throw new DataAccessException("Can't find required source External Data Identifiers for copying.");
        }


        ///////////////////////////////////////////////////////////////////////
        // Copy the Field Data (First create entries in DB, then copy files) //
        ///////////////////////////////////////////////////////////////////////

        Hashtable<String, ExternalDataIdentifier> oldNameNewID = new Hashtable<>();

        try{
            // Tie those DB entries to newly created copies of the original field data that the requester owns
            for (int i = 0; i < edis.size(); i++){
                // Create DB entries where new field data names are created
                CopyFieldDataResult dbResults = databaseServer.copyFieldData(requester,
                        edis.get(i), annotations.get(i), documentType, vcDocument.getVersion().getName());

                dataSetController.fieldDataCopySim(dbResults.oldID.getDataKey() ,ogOwner, dbResults.newID, FieldDataSpec.JOBINDEX_DEFAULT, requester);

                oldNameNewID.put(edis.get(i).getName(), dbResults.newID);
            }
        } catch (Exception e){
            for (ExternalDataIdentifier newID : oldNameNewID.values()){
                try{
                    deleteFieldData(newID);
                } catch (Exception t){
                    logger.error("Problem removing field data with id {} that couldn't be added.", newID, t);
                    continue;
                }
            }
            throw new RuntimeException(e);
        }

        return oldNameNewID;
    }

    public ArrayList<FieldDataResource.FieldDataReference> getAllFieldDataIDs(User user) throws SQLException, DataAccessException {
        FieldDataAllDBEntries results = databaseServer.getFieldDataIDs(user);
        ArrayList<FieldDataResource.FieldDataReference> fieldDataReferenceList = new ArrayList<>();
        for (int i =0; i < results.ids.length; i++){
            Vector<KeyValue> simRefs = new Vector<>();
            if (results.edisToSimRefs != null){
                simRefs = results.edisToSimRefs.get(results.ids[i]);
            }
            FieldDataResource.FieldDataReference fieldDataReference = new FieldDataResource.FieldDataReference(results.ids[i], results.annotationsForIds[i], simRefs);
            fieldDataReferenceList.add(fieldDataReference);
        }
        return fieldDataReferenceList;
    }

    public FieldDataShape getFieldDataShapeFromID(User user, KeyValue keyValue, int jobParameter) throws ObjectNotFoundException {
        return dataSetController.fieldDataInfo(keyValue, user, jobParameter);
    }

    public FieldDataResource.FieldData analyzeFieldDataFromFile(File imageFile, String fileName) throws DataAccessException, ImageException, DataFormatException {
        if (imageFile == null) {
            throw new DataAccessException("No file present");
        }
        if (!fileName.contains(".vfrap")) {
            try {
                FieldData spec = FieldDataFileConversion.createFDOSFromImageFile(imageFile, false, null);
                return new FieldDataResource.FieldData(
                        spec.data, new double[][][]{}, spec.channelNames.toArray(new String[0]), spec.times.stream().mapToDouble(Double::doubleValue).toArray(),
                        spec.origin, spec.extent, spec.iSize, spec.annotation, fileName
                );
            } catch (DataFormatException  ex) {
                throw new RuntimeException("Cannot read image " + fileName + "\n" + ex.getMessage());
            }
        } else {
            throw new DataFormatException("Can't use .vfrap files");
        }
    }

    public ExternalDataIdentifier saveNewFieldDataFromFile(String fieldDataName, String[] varNames,
                                                                  short[][][] imageData, String annotation,
                                                                  User user, double[] times, Origin origin,
                                                                  Extent extent, ISize iSize) throws DataAccessException, ImageException, DataFormatException{
        return saveNewFieldDataFromFile(fieldDataName, varNames, imageData, new double[][][]{}, annotation, user, times, origin, extent, iSize);
    }

    public ExternalDataIdentifier saveNewFieldDataFromFile(String fieldDataName, String[] varNames,
                                                                  short[][][] imageData, double[][][] doubleImageData, String annotation,
                                                                  User user, double[] times, Origin origin,
                                                                  Extent extent, ISize iSize) throws DataAccessException, ImageException, DataFormatException {
        // Ensure name is unique for user
        FieldDataAllDBEntries usersFieldData = databaseServer.getFieldDataIDs(user);
        Set<String> namesUsed = new HashSet<>();
        for (ExternalDataIdentifier edi : usersFieldData.ids){
            namesUsed.add(edi.getName());
        }
        while (namesUsed.contains(fieldDataName)){
            fieldDataName = TokenMangler.getNextEnumeratedToken(fieldDataName);
        }


        VariableType[] varTypes = new VariableType[varNames.length];
        for (int j = 0; j < varNames.length; j++){
            varTypes[j] = VariableType.VOLUME;
        }
        FieldDataExternalDataIDEntry entry = new FieldDataExternalDataIDEntry(user, fieldDataName, annotation);
        ExternalDataIdentifier edi = databaseServer.saveFieldDataEDI(entry);

        FieldData fieldData = new FieldData();
        if (doubleImageData == null || doubleImageData.length == 0){
            fieldData.data = imageData;
        } else {
            fieldData.doubleData = doubleImageData;
        }
        fieldData.channelNames = Arrays.stream(varNames).toList(); fieldData.variableTypes = varTypes;
        fieldData.times = Arrays.stream(times).boxed().toList(); fieldData.owner = user; fieldData.origin = origin;
        fieldData.extent = extent; fieldData.iSize = iSize; fieldData.annotation = annotation;

        try {
            fieldData.cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent, iSize,
                    new RegionImage(new VCImageUncompressed(null, new byte[iSize.getXYZ()],//empty regions
                            extent, iSize.getX(), iSize.getY(), iSize.getZ()),
                            0, null, null, RegionImage.NO_SMOOTHING));
            dataSetController.fieldDataAdd(fieldData, edi);
            return edi;
        } catch (Exception e) {
            // Remove DB entry if file creation fails
            databaseServer.deleteFieldDataID(user, edi);
            throw new RuntimeException(e);
        }
    }

    public void saveFieldDataFromSimulation(User user, KeyValue simKeyValue, int jobIndex, String newFieldDataName) throws DataAccessException {
        // Create DB entry
        SimulationInfo simInfo = databaseServer.getSimulationInfo(user, simKeyValue);
        FieldDataExternalDataIDEntry entry = new FieldDataExternalDataIDEntry(user, newFieldDataName, "");
        ExternalDataIdentifier edi = databaseServer.saveFieldDataEDI(entry);

        // Save new file with reference to DB entry
        dataSetController.fieldDataCopySim(simKeyValue, simInfo.getOwner(), edi, jobIndex, user);
    }

    public void deleteFieldData(ExternalDataIdentifier edi) throws DataAccessException {
        databaseServer.deleteFieldDataID(edi.getOwner(), edi); // remove from DB
        dataSetController.fieldDataDelete(edi); // remove from File System
    }

}



















