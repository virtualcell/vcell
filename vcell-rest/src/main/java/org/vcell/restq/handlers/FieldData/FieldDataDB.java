package org.vcell.restq.handlers.FieldData;

import cbit.image.ImageException;
import cbit.image.VCImageUncompressed;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.FieldDataFileConversion;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.math.VariableType;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solvers.CartesianMesh;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.*;
import java.sql.SQLException;
import java.util.zip.DataFormatException;

@ApplicationScoped
public class FieldDataDB {

    private final DatabaseServerImpl databaseServer;
    private final DataSetControllerImpl dataSetController;

    @Inject
    public FieldDataDB(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException, FileNotFoundException {
        databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        dataSetController = new DataSetControllerImpl(null, new File("/simdata"), new File("/simdata"));
    }

    public FieldDataDBOperationResults copyNoConflict(User user, FieldDataDBOperationSpec spec) throws DataAccessException {
        return databaseServer.fieldDataDBOperation(user, spec);
    }

    public FieldDataDBOperationResults getAllFieldData(User user, FieldDataDBOperationSpec spec) throws SQLException, DataAccessException {
        return databaseServer.fieldDataDBOperation(user, spec);
    }

    public FieldDataFileOperationSpec generateFieldDataFromFile(InputStream imageFile, String fileName) throws DataAccessException, ImageException, DataFormatException {
        if (imageFile == null) {
            throw new DataAccessException("No file present");
        }
        if (!fileName.contains(".vfrap")) {
            try {
                File tmpFile = File.createTempFile("tmp", "file");
                tmpFile.deleteOnExit();
                FileOutputStream out = new FileOutputStream(tmpFile);
                IOUtils.copy(imageFile, out);
                out.close();
                return FieldDataFileConversion.createFDOSFromImageFile(tmpFile, false, null);
            } catch (DataFormatException | IOException ex) {
                throw new RuntimeException("Cannot read image " + fileName + "\n" + ex.getMessage());
            }
        } else {
            throw new DataFormatException("Can't use .vfrap files");
        }
    }


    public FieldDataFileOperationResults saveNewFieldDataFromFile(FieldDataResource.AnalyzedResultsFromFieldData saveFieldData, User user) throws DataAccessException, ImageException, DataFormatException {


        VariableType[] varTypes = new VariableType[saveFieldData.varNames().length];
        for (int j = 0; j < saveFieldData.varNames().length; j++){
            varTypes[j] = VariableType.VOLUME;
        }
        FieldDataDBOperationSpec fieldDataDBOperationSpec = FieldDataDBOperationSpec.createSaveNewExtDataIDSpec(user, saveFieldData.name(), saveFieldData.annotation());
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

    public FieldDataDBOperationResults saveNewFieldDataIntoDB(User user, FieldDataDBOperationSpec spec) throws DataAccessException {
        return databaseServer.fieldDataDBOperation(user, spec);
    }

    public void deleteFieldData(User user, String fieldDataID) throws DataAccessException {
        ExternalDataIdentifier edi = new ExternalDataIdentifier(new KeyValue(fieldDataID), user, null);
        databaseServer.fieldDataDBOperation(user, FieldDataDBOperationSpec.createDeleteExtDataIDSpec(edi)); // remove from DB
        dataSetController.fieldDataFileOperation(FieldDataFileOperationSpec.createDeleteFieldDataFileOperationSpec(edi)); // remove from File System
    }

}



















