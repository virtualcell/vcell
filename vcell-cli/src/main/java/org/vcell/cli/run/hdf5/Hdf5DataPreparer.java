package org.vcell.cli.run.hdf5;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.jlibsedml.Variable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Static data preparation class for Hdf5 files
 */
public class Hdf5DataPreparer {
    private final static Logger logger = LogManager.getLogger(Hdf5File.class);

    public static class Hdf5PreparedData{
        public String sedmlId;
        public long[] dataDimensions;
        public double[] flattenedDataBuffer;
    }

    /**
     * Spacial Data has a special attribute called "times". This function extracts that value
     * 
     * @param datasetWrapper
     * @return the times data
     */
    public static double[] getSpacialHdf5Attribute_Times(Hdf5DatasetWrapper datasetWrapper){
        return ((Hdf5DataSourceSpatial)datasetWrapper.dataSource).varDataItems.get(0).times;
    }

    /**
     * Reads a `Hdf5DatasetWrapper` contents and generates `Hdf5PreparedData` with spacial data for writing out to Hdf5 format via Hdf5Writer
     * 
     * @param datasetWrapper the data relevant to an hdf5 output file
     * @return the prepared spacial data
     */
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
            throw new RuntimeException("unexpected dimension " + spaceTimeDimensions + " for data, expected last dimension to be that of time: " + numTimes);
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
    }

    /**
     * Reads a `Hdf5DatasetWrapper` contents and generates `Hdf5PreparedData` with nonspacial data for writing out to Hdf5 format via Hdf5Writer
     * 
     * @param datasetWrapper the data relevant to an hdf5 output file
     * @return the prepared nonspacial data
     */
    public static Hdf5PreparedData prepareNonspacialData(Hdf5DatasetWrapper datasetWrapper){
        final double NaN = 0.0/0.0;
        Hdf5DataSourceNonspatial dataSourceNonspatial = (Hdf5DataSourceNonspatial) datasetWrapper.dataSource;
        Map<Variable, double[]> varDataMap = dataSourceNonspatial.allJobResults.get(0).varData;
        List<Variable> vars = new ArrayList<>(varDataMap.keySet());
        long numVariablesPerJob = varDataMap.keySet().size();
        long numTimePoints = 0;
        List<Long> dataDimensionList = new ArrayList<>();
        for (Variable var : vars){
            numTimePoints = Long.max(numTimePoints, varDataMap.get(var).length);
        }
        
        //int numJobs = 1;
        for (int scanBound : dataSourceNonspatial.scanBounds){
            dataDimensionList.add((long)scanBound+1);
            //numJobs *= (scanBound+1);
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
        for (Hdf5DataSourceNonspatial.Hdf5JobData jobData : dataSourceNonspatial.allJobResults) {
            for (int varIndex = 0; varIndex < vars.size(); varIndex++) {
                Variable var = vars.get(varIndex);
                double[] dataArray = jobData.varData.get(var);
                if (dataArray.length < numTimePoints){
                    double[] paddedArray = new double[Math.toIntExact(numTimePoints)];
                    System.arraycopy(dataArray, 0, paddedArray, 0, dataArray.length);
                    for (int i = dataArray.length; i < numTimePoints; i++){
                        paddedArray[i] = NaN;
                    }
                    dataArray = paddedArray;
                }

                System.arraycopy(dataArray, 0, bigDataBuffer, bufferOffset, dataArray.length);

                /*
                for (int dataIndex = 0; dataIndex < dataArray.length; dataIndex++) {
                    System.arraycopy(dataArray,0, bigDataBuffer, bufferOffset, dataArray.length);
                }*/
                bufferOffset += (int)numTimePoints;
            }
        }

        Hdf5PreparedData preparedData = new Hdf5PreparedData();
        preparedData.sedmlId = datasetWrapper.datasetMetadata.sedmlId;
        preparedData.dataDimensions = dataDimensions;
        preparedData.flattenedDataBuffer = bigDataBuffer;
        return preparedData;
    }
}
