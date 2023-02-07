package org.vcell.cli.run.hdf5;

import cbit.vcell.resource.NativeLib;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import org.vcell.cli.run.hdf5.rewrite.Hdf5DataPreparer;
import org.vcell.cli.run.hdf5.rewrite.Hdf5File;
import org.vcell.cli.run.hdf5.rewrite.Hdf5DataPreparer.Hdf5PreparedData;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hdf5Writer {

    private final static Logger logger = LogManager.getLogger(Hdf5Writer.class);

    private Hdf5Writer(){
        
    }

    public static Map<String, Integer> generateGroupsMap(String sedmlLocation){
        Map<String, Integer> pathToGroupIDTranslator = new HashMap<>();
        Path pathRelativeToCombineArchive = Paths.get(sedmlLocation);
        logger.info("Processing: " + pathRelativeToCombineArchive.toString());
        
        do {
            pathToGroupIDTranslator.put(pathRelativeToCombineArchive.toString(), null);
        } while ((pathRelativeToCombineArchive = pathRelativeToCombineArchive.getParent()) != null);

        return pathToGroupIDTranslator;
    }

    public static void writeHdf5(Hdf5DataWrapper hdf5FileWrapper, File outDirForCurrentSedml) throws HDF5Exception, IOException {
        Hdf5File masterHdf5 = null;

        // Boot Hdf5 Library
        NativeLib.HDF5.load();

        // Create and open the Hdf5 file
        logger.info("Creating hdf5 file `reports.h5` in" + outDirForCurrentSedml.getAbsolutePath());
        masterHdf5 = new Hdf5File(outDirForCurrentSedml);
        masterHdf5.open();

        
        // Attempt to fill the Hdf5
        try {
            for (String rawPath : hdf5FileWrapper.datasetWrapperMap.keySet()){
                // Process Parent Groups
                String path = "";
                for (String group : rawPath.split("/")){ 
                    path += ("/" + group); // Start from first group, then travel down the path.
                    if (masterHdf5.containsGroup(path)) continue;
                    int groupId = masterHdf5.addGroup(path);
                    String relativePath = path.substring(1);
                    masterHdf5.insertAttribute_classic(groupId, "combineArchiveLocation", relativePath);
                    masterHdf5.insertAttribute_classic(groupId, "uri", relativePath);
                    // We leave them open because there may be other datasets that share (some of) the same parent groups
                }
    
                // Process the Dataset
                for (Hdf5DatasetWrapper data : hdf5FileWrapper.datasetWrapperMap.get(rawPath)){
                    Hdf5PreparedData preparedData;
                    if (data.dataSource instanceof Hdf5DataSourceNonspatial) preparedData = Hdf5DataPreparer.prepareNonspacialData(data);
                    else if (data.dataSource instanceof Hdf5DataSourceSpatial) preparedData = Hdf5DataPreparer.prepareSpacialData(data);
                    else continue;
    
                    int currentDatasetId = masterHdf5.insertSedmlData(path, preparedData);
                    
                    if (data.dataSource instanceof Hdf5DataSourceSpatial){
                        masterHdf5.insertAttributes(currentDatasetId, "times", Hdf5DataPreparer.getSpacialHdf5Attribute_Times(data));
                    }  
                    masterHdf5.insertAttribute_classic(currentDatasetId, "_type", data.datasetMetadata._type);
                    masterHdf5.insertAttributes_classic(currentDatasetId, "scanParameterNames", Arrays.asList(data.dataSource.scanParameterNames));
                    masterHdf5.insertAttributes_classic(currentDatasetId, "sedmlDataSetDataTypes", data.datasetMetadata.sedmlDataSetDataTypes);
                    masterHdf5.insertAttributes_classic(currentDatasetId, "sedmlDataSetIds", data.datasetMetadata.sedmlDataSetIds);
                    
                    if (data.datasetMetadata.sedmlDataSetNames.get(0) != null) {
                        masterHdf5.insertAttributes_classic(currentDatasetId, "sedmlDataSetNames", data.datasetMetadata.sedmlDataSetNames);
                    }
                    masterHdf5.insertAttributes_classic(currentDatasetId, "sedmlDataSetLabels", data.datasetMetadata.sedmlDataSetLabels);
                    masterHdf5.insertAttributes_classic(currentDatasetId, "sedmlDataSetShapes", data.datasetMetadata.sedmlDataSetShapes);
                    masterHdf5.insertAttribute_classic(currentDatasetId, "sedmlId", data.datasetMetadata.sedmlId);
                    masterHdf5.insertAttribute_classic(currentDatasetId, "sedmlName", data.datasetMetadata.sedmlName);
                    masterHdf5.insertAttribute_classic(currentDatasetId, "uri", path.substring(1) + "/" + data.datasetMetadata.sedmlId);
    
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
