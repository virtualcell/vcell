package org.vcell.cli.run.hdf5;

import cbit.vcell.resource.NativeLib;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import org.vcell.cli.run.hdf5.Hdf5DataPreparer.Hdf5PreparedData;
import java.util.*;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Static class for writing out Hdf5 formatted files
 */
public class Hdf5Writer {

    private final static Logger logger = LogManager.getLogger(Hdf5Writer.class);

    private Hdf5Writer(){} // Static class = no instances allowed

    /**
     * Writes an Hdf5 formatted file given a hdf5FileWrapper and a destination to write the file to.
     * 
     * @param hdf5DataWrapper the wrapper of hdf5 relevant data
     * @param outDirForCurrentSedml the directory to place the report file into, NOT the report file itself.
     * @throws HDF5Exception if there is an expection thrown from hdf5 while using the library.
     * @throws IOException if the computer encounteres an unexepcted system IO problem
     */
    public static void writeHdf5(Hdf5DataContainer hdf5DataWrapper, File outDirForCurrentSedml) throws HDF5Exception, IOException {
        Hdf5File masterHdf5 = null;

        // Boot Hdf5 Library
        NativeLib.HDF5.load();

        // Create and open the Hdf5 file
        logger.info("Creating hdf5 file `reports.h5` in" + outDirForCurrentSedml.getAbsolutePath());
        masterHdf5 = new Hdf5File(outDirForCurrentSedml);
        masterHdf5.open();

        
        // Attempt to fill the Hdf5
        try {
            for (String rawPath : hdf5DataWrapper.uriToResultsMap.keySet()){
                // Process Parent Groups
                String path = "";
                for (String group : rawPath.split("/")){ 
                    path += ("/" + group); // Start from first group, then travel down the path.
                    if (masterHdf5.containsGroup(path)) continue;
                    int groupId = masterHdf5.addGroup(path);
                    String relativePath = path.substring(1);
                    masterHdf5.insertFixedStringAttribute(groupId, "combineArchiveLocation", relativePath);
                    masterHdf5.insertFixedStringAttribute(groupId, "uri", relativePath);
                    // We leave them open because there may be other datasets that share (some of) the same parent groups
                }
    
                // Process the Dataset
                for (Hdf5SedmlResults data : hdf5DataWrapper.uriToResultsMap.get(rawPath)){
                    Hdf5PreparedData preparedData;
                    if (data.dataSource instanceof Hdf5SedmlResultsNonspatial) preparedData = Hdf5DataPreparer.prepareNonspacialData(data);
                    else if (data.dataSource instanceof Hdf5SedmlResultsSpatial) preparedData = Hdf5DataPreparer.prepareSpacialData(data);
                    else continue;
    
                    int currentDatasetId = masterHdf5.insertSedmlData(path, preparedData);
                    
                    if (data.dataSource instanceof Hdf5SedmlResultsSpatial){
                        masterHdf5.insertNumericAttributes(currentDatasetId, "times", Hdf5DataPreparer.getSpacialHdf5Attribute_Times(data));
                    }  
                    masterHdf5.insertFixedStringAttribute(currentDatasetId, "_type", data.datasetMetadata._type);
                    masterHdf5.insertFixedStringAttributes(currentDatasetId, "scanParameterNames", Arrays.asList(data.dataSource.scanParameterNames));
                    masterHdf5.insertFixedStringAttributes(currentDatasetId, "sedmlDataSetDataTypes", data.datasetMetadata.sedmlDataSetDataTypes);
                    masterHdf5.insertFixedStringAttributes(currentDatasetId, "sedmlDataSetIds", data.datasetMetadata.sedmlDataSetIds);
                    
                    if (data.datasetMetadata.sedmlDataSetNames.get(0) != null) {
                        masterHdf5.insertFixedStringAttributes(currentDatasetId, "sedmlDataSetNames", data.datasetMetadata.sedmlDataSetNames);
                    }
                    masterHdf5.insertFixedStringAttributes(currentDatasetId, "sedmlDataSetLabels", data.datasetMetadata.sedmlDataSetLabels);
                    masterHdf5.insertFixedStringAttributes(currentDatasetId, "sedmlDataSetShapes", data.datasetMetadata.sedmlDataSetShapes);
                    masterHdf5.insertFixedStringAttribute(currentDatasetId, "sedmlId", data.datasetMetadata.sedmlId);
                    masterHdf5.insertFixedStringAttribute(currentDatasetId, "sedmlName", data.datasetMetadata.sedmlName);
                    masterHdf5.insertFixedStringAttribute(currentDatasetId, "uri", path.substring(1) + "/" + data.datasetMetadata.sedmlId);
    
                    masterHdf5.closeDataset(currentDatasetId);
                }
            }
        } finally {
            try {
                masterHdf5.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
