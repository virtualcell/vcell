package org.vcell.cli.run.hdf5.rewrite;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.jlibsedml.Variable;

import org.vcell.cli.run.hdf5.Hdf5DatasetWrapper;
import org.vcell.cli.run.hdf5.Hdf5DataSourceSpatial;
import org.vcell.cli.run.hdf5.Hdf5DataSourceNonspatial;
import org.vcell.cli.run.hdf5.Hdf5DataSourceSpatialVarDataItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hdf5DataPreparer {
    private final static Logger logger = LogManager.getLogger(Hdf5File.class);

    public static class Hdf5PreparedData{
        public String sedmlId;
        public long[] dataDimensions;
        public double[] flattenedDataBuffer;
    }

    public static double[] getSpacialHdf5Attribute_Times(Hdf5DatasetWrapper datasetWrapper){
        return ((Hdf5DataSourceSpatial)datasetWrapper.dataSource).varDataItems.get(0).times;
    }

    public static Hdf5PreparedData prepareSpacialData (Hdf5DatasetWrapper datasetWrapper){
        Hdf5DataSourceSpatial dataSourceSpatial = (Hdf5DataSourceSpatial)datasetWrapper.dataSource; 
        logger.debug(dataSourceSpatial);
        Hdf5DataSourceSpatialVarDataItem firstVarDataItem = dataSourceSpatial.varDataItems.get(0);
        int[] spaceTimeDimensions = firstVarDataItem.spaceTimeDimensions;
        int[] spatialDimensions = Arrays.copyOf(spaceTimeDimensions, spaceTimeDimensions.length-1);
        //int numSpatialPoints = Arrays.stream(spatialDimensions).sum();
        //long numJobs = dataSourceSpatial.varDataItems.stream().map(varDataItem -> varDataItem.jobIndex).collect(Collectors.toSet()).size();
        List<Variable> sedmlVars = new ArrayList<>(dataSourceSpatial.varDataItems.stream().map(varDataItem -> varDataItem.sedmlVariable).collect(Collectors.toSet()));
        long numVars = sedmlVars.size();
        long numTimes = firstVarDataItem.times.length;
        List<Long> dataDimensionList = new ArrayList<>();
        int numJobs = 1;

        if (numTimes != spaceTimeDimensions[spaceTimeDimensions.length-1]){
            throw new RuntimeException("unexpected dimension "+spaceTimeDimensions+" for data, expected last dimension to be that of time: "+numTimes);
        }
        
        
        for (int scanBound : dataSourceSpatial.scanBounds){
            dataDimensionList.add((long)scanBound+1);
            numJobs *= (scanBound+1);
        }

        dataDimensionList.add(numVars);
        for (long dim : spatialDimensions){
            dataDimensionList.add(dim);
        }
        dataDimensionList.add(numTimes);

        
        long[] dataDimensions = dataDimensionList.stream().mapToLong(l -> l).toArray(); // What does this streaming do?
        int totalDataSize = 1;
        for (long dim : dataDimensions){
            totalDataSize *= (int)dim;
        }

        // Create buffer of contiguous data to hold everything.
        double[] bigDataBuffer = new double[totalDataSize];
//                    for (int i=0;i<totalDataSize;i++){
//                        bigDataBuffer[i] = i;
//                    }
        int bufferOffset = 0;
        for (int jobIndex=0; jobIndex<numJobs; jobIndex++){
            for (int varIndex = 0; varIndex < numVars; varIndex++) {
                Variable var = sedmlVars.get(varIndex);
                // find data for var and jobIndex
                for (Hdf5DataSourceSpatialVarDataItem varDataItem : dataSourceSpatial.varDataItems) {
                    if (varDataItem.sedmlVariable.equals(var) && varDataItem.jobIndex == jobIndex){
                        double[] dataArray = varDataItem.getSpatialData();
                        System.arraycopy(dataArray,0,bigDataBuffer,bufferOffset,dataArray.length);
                        bufferOffset += dataArray.length;
                        break;
                    }
                }
            }
        }

        Hdf5PreparedData preparedData = new Hdf5PreparedData();
        preparedData.sedmlId = datasetWrapper.datasetMetadata.sedmlId;
        preparedData.dataDimensions = dataDimensions;
        preparedData.flattenedDataBuffer = bigDataBuffer;
        return preparedData;

        //String datasetPath = Paths.get(sedmlUri, datasetWrapper.datasetMetadata.sedmlId).toString();
        //int hdf5DataspaceID = H5.H5Screate_simple(dataDimensions.length, dataDimensions, null);
        //int hdf5DatasetID = H5.H5Dcreate(jobGroupID, File.separator + datasetPath, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        //int hdf5DatasetID = H5.H5Dcreate(jobGroupID, File.separator + datasetWrapper.datasetMetadata.sedmlId, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        //H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (double[])bigDataBuffer);

        //Hdf5Utils.insertAttributes(hdf5DatasetID, "times", firstVarDataItem.times);
    }

    public static Hdf5PreparedData prepareNonspacialData(Hdf5DatasetWrapper datasetWrapper){
        Hdf5DataSourceNonspatial dataSourceNonspatial = (Hdf5DataSourceNonspatial) datasetWrapper.dataSource;
        Map<Variable, double[]> varDataMap = dataSourceNonspatial.jobData.get(0).varData;
        List<Variable> vars = new ArrayList<>(varDataMap.keySet());
        long numVariablesPerJob = varDataMap.keySet().size();
        long numTimePoints = varDataMap.get(vars.get(0)).length;

        List<Long> dataDimensionList = new ArrayList<>();
        int numJobs = 1;
        for (int scanBound : dataSourceNonspatial.scanBounds){
            dataDimensionList.add((long)scanBound+1);
            numJobs *= (scanBound+1);
        }
        dataDimensionList.add(numVariablesPerJob);
        dataDimensionList.add(numTimePoints);
        long[] dataDimensions = dataDimensionList.stream().mapToLong(l -> l).toArray();
        int totalDataSize = 1;
        for (long dim : dataDimensions){
            totalDataSize *= (int)dim;
        }

        double[] bigDataBuffer = new double[totalDataSize];
        int bufferOffset = 0;
        for (Hdf5DataSourceNonspatial.Hdf5JobData jobData : dataSourceNonspatial.jobData) {
            for (int varIndex = 0; varIndex < vars.size(); varIndex++) {
                Variable var = vars.get(varIndex);
                double[] dataArray = jobData.varData.get(var);
                for (int dataIndex = 0; dataIndex < dataArray.length; dataIndex++) {
                    System.arraycopy(dataArray,0, bigDataBuffer, bufferOffset, dataArray.length);
                }
                bufferOffset += (int)numTimePoints;
            }
        }

        Hdf5PreparedData preparedData = new Hdf5PreparedData();
        preparedData.sedmlId = datasetWrapper.datasetMetadata.sedmlId;
        preparedData.dataDimensions = dataDimensions;
        preparedData.flattenedDataBuffer = bigDataBuffer;
        return preparedData;

        //String datasetPath = Paths.get(sedmlUri, datasetWrapper.datasetMetadata.sedmlId).toString();
        //int hdf5DataspaceID = H5.H5Screate_simple(dataDimensions.length, dataDimensions, null);
        //int hdf5DatasetID = H5.H5Dcreate(jobGroupID, File.separator + datasetPath, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        //H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (double[])bigDataBuffer);
                    
    }
}
