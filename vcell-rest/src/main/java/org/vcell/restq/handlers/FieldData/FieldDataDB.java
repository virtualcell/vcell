package org.vcell.restq.handlers.FieldData;

import cbit.image.ImageException;
import cbit.image.VCImageUncompressed;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.*;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.ServerDocumentManager;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.*;

import java.io.*;
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

    public Hashtable<String, ExternalDataIdentifier> copyNoConflict(User requester, KeyValue documentKey, String documentType) throws DataAccessException, XmlParseException, MathException, ExpressionException {
        VCDocument vcDocument;

        //Get Objects from Document that might need to have FieldFuncs replaced
        Vector<Object> modelsListOfContext = new Vector<Object>();
        if(documentType.equalsIgnoreCase(VersionableType.MathModelMetaData.getTypeName())){
            BigString mathModelXML = databaseServer.getMathModelXML(requester, documentKey);
            vcDocument = XmlHelper.XMLToMathModel(new XMLSource(mathModelXML.toString()));
            modelsListOfContext.add(((MathModel)vcDocument).getMathDescription());
        }else if(documentType.equalsIgnoreCase(VersionableType.BioModelMetaData.getTypeName())){
            BigString bioModelXML = databaseServer.getBioModelXML(requester, documentKey);
            vcDocument = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
            modelsListOfContext.add(((BioModel)vcDocument).getSimulationContexts());
        } else {
            UnsupportedOperationException notSupported = new UnsupportedOperationException("Document type (" + documentType + ") is not supported.");
            logger.error(notSupported);
            throw notSupported;
        }
        User ogOwner = vcDocument.getVersion().getOwner();

		//Get Field names
        Vector<String> fieldNames = new Vector<>();
        for(Object fieldFunctionContainer : modelsListOfContext){
            FieldFunctionArguments[] fieldFuncArgsArr = {};
            if (fieldFunctionContainer instanceof MathDescription){
                fieldFuncArgsArr = FieldUtilities.getFieldFunctionArguments((MathDescription)fieldFunctionContainer);
            }else if (fieldFunctionContainer instanceof SimulationContext){
                fieldFuncArgsArr = ((SimulationContext)fieldFunctionContainer).getFieldFunctionArguments();
            }
            for (FieldFunctionArguments fieldFunctionArguments : fieldFuncArgsArr) {
                if (!fieldNames.contains(fieldFunctionArguments.getFieldName())) {
                    fieldNames.add(fieldFunctionArguments.getFieldName());
                }
            }
        }
        if (fieldNames.isEmpty()){
            return new Hashtable<>();
        }

        ///////////////////////////////////////////////////////////////////////
        // Copy the Field Data (First create entries in DB, then copy files) //
        ///////////////////////////////////////////////////////////////////////

        // Create DB entries where new field data names are created
        FieldDataDBOperationResults dbResults = databaseServer.copyFieldData(requester, ogOwner,
                fieldNames.toArray(new String[0]), documentType, vcDocument.getVersion().getName());

        // Tie those DB entries to newly created copies of the original field data that the requester owns
        for (String oldName: fieldNames){
            FieldDataFileOperationSpec fileOperationSpec = FieldDataFileOperationSpec.createCopySimFieldDataFileOperationSpec(dbResults.extDataID,
                    dbResults.oldNameOldExtDataIDKeyHash.get(oldName), ogOwner, FieldDataFileOperationSpec.JOBINDEX_DEFAULT, requester);
            dataSetController.fieldDataFileOperation(fileOperationSpec);
        }
        return dbResults.oldNameNewIDHash;
    }

    public ArrayList<FieldDataResource.FieldDataReference> getAllFieldDataIDs(User user) throws SQLException, DataAccessException {
        FieldDataDBOperationResults results = databaseServer.fieldDataDBOperation(user, FieldDataDBOperationSpec.createGetExtDataIDsSpec(user));
        ArrayList<FieldDataResource.FieldDataReference> fieldDataReferenceList = new ArrayList<>();
        for (int i =0; i < results.extDataIDArr.length; i++){
            Vector<KeyValue> simRefs = new Vector<>();
            if (results.extdataIDAndSimRefH != null){
                simRefs = results.extdataIDAndSimRefH.get(results.extDataIDArr[i]);
            }
            FieldDataResource.FieldDataReference fieldDataReference = new FieldDataResource.FieldDataReference(results.extDataIDArr[i], results.extDataAnnotArr[i], simRefs);
            fieldDataReferenceList.add(fieldDataReference);
        }
        return fieldDataReferenceList;
    }

    public FieldDataFileOperationResults getFieldDataFromID(User user, String id, int jobParameter) throws ObjectNotFoundException {
        return dataSetController.fieldDataFileOperation(FieldDataFileOperationSpec.createInfoFieldDataFileOperationSpec(new KeyValue(id), user, jobParameter));
    }

    public FieldDataResource.AnalyzedResultsFromFieldData generateFieldDataFromFile(File imageFile, String fileName) throws DataAccessException, ImageException, DataFormatException {
        if (imageFile == null) {
            throw new DataAccessException("No file present");
        }
        if (!fileName.contains(".vfrap")) {
            try {
                FieldDataFileOperationSpec spec = FieldDataFileConversion.createFDOSFromImageFile(imageFile, false, null);
                return new FieldDataResource.AnalyzedResultsFromFieldData(
                        spec.shortSpecData, spec.varNames, spec.times, spec.origin, spec.extent, spec.isize, spec.annotation, fileName
                );
            } catch (DataFormatException  ex) {
                throw new RuntimeException("Cannot read image " + fileName + "\n" + ex.getMessage());
            }
        } else {
            throw new DataFormatException("Can't use .vfrap files");
        }
    }


    public FieldDataFileOperationResults saveNewFieldDataFromFile(FieldDataResource.AnalyzedResultsFromFieldData saveFieldData, User user) throws DataAccessException, ImageException, DataFormatException {
        // Ensure name is unique for user
        String fieldDataName = saveFieldData.name();
        FieldDataDBOperationResults usersFieldData = databaseServer.fieldDataDBOperation(user, FieldDataDBOperationSpec.createGetExtDataIDsSpecWithSimRefs(user));
        for (ExternalDataIdentifier edi : usersFieldData.extDataIDArr){
            if (edi.getName().equals(fieldDataName)){
                fieldDataName = TokenMangler.getNextEnumeratedToken(fieldDataName);
            }
        }


        VariableType[] varTypes = new VariableType[saveFieldData.varNames().length];
        for (int j = 0; j < saveFieldData.varNames().length; j++){
            varTypes[j] = VariableType.VOLUME;
        }
        FieldDataDBOperationSpec fieldDataDBOperationSpec = FieldDataDBOperationSpec.createSaveNewExtDataIDSpec(user, fieldDataName, saveFieldData.annotation());
        FieldDataDBOperationResults results = databaseServer.fieldDataDBOperation(user, fieldDataDBOperationSpec);
        FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec(saveFieldData.shortSpecData(), null, null,
                results.extDataID, saveFieldData.varNames(), varTypes, saveFieldData.times(), user,
                saveFieldData.origin(), saveFieldData.extent(), saveFieldData.isize(), saveFieldData.annotation(),
                -1, null, user);
        CartesianMesh cartesianMesh;
        fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
        try {
            cartesianMesh = CartesianMesh.createSimpleCartesianMesh(fdos.origin, fdos.extent, fdos.isize,
                    new RegionImage(new VCImageUncompressed(null, new byte[fdos.isize.getXYZ()],//empty regions
                            fdos.extent, fdos.isize.getX(), fdos.isize.getY(), fdos.isize.getZ()),
                            0, null, null, RegionImage.NO_SMOOTHING));
            fdos.cartesianMesh = cartesianMesh;
            FieldDataFileOperationResults fileSaveResults = dataSetController.fieldDataFileOperation(fdos);
            fileSaveResults.externalDataIdentifier = results.extDataID;
            return fileSaveResults;
        } catch (Exception e) {
            // Remove DB entry if file creation fails
            databaseServer.fieldDataDBOperation(user, FieldDataDBOperationSpec.createDeleteExtDataIDSpec(results.extDataID));
            throw new RuntimeException(e);
        }
    }

    public void saveFieldDataFromSimulation(User user, KeyValue simKeyValue, int jobIndex, String newFieldDataName) throws DataAccessException {
        // Create DB entry
        SimulationInfo simInfo = databaseServer.getSimulationInfo(user, simKeyValue);
        FieldDataDBOperationSpec fieldDataDBOperationSpec = FieldDataDBOperationSpec.createSaveNewExtDataIDSpec(user, newFieldDataName, "");
        FieldDataDBOperationResults results = databaseServer.fieldDataDBOperation(user, fieldDataDBOperationSpec);

        // Save new file with reference to DB entry
        dataSetController.fieldDataCopySim(simKeyValue, simInfo.getOwner(), results.extDataID, jobIndex, user);
    }

    public void deleteFieldData(User user, String fieldDataID) throws DataAccessException {
        ExternalDataIdentifier edi = new ExternalDataIdentifier(new KeyValue(fieldDataID), user, null);
        databaseServer.fieldDataDBOperation(user, FieldDataDBOperationSpec.createDeleteExtDataIDSpec(edi)); // remove from DB
        dataSetController.fieldDataFileOperation(FieldDataFileOperationSpec.createDeleteFieldDataFileOperationSpec(edi)); // remove from File System
    }

}



















