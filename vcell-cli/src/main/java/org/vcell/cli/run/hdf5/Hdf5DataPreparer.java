package org.vcell.cli.run.hdf5;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.jlibsedml.DataSet;
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
     * Spatial Data has a special attribute called "times". This function extracts that value
     * 
     * @param hdf5SedmlResults the results in a sedml friendly format
     * @return the times data
     */
    public static double[] getSpatialHdf5Attribute_Times(Hdf5SedmlResults hdf5SedmlResults){
        return ((Hdf5SedmlResultsSpatial)hdf5SedmlResults.dataSource).varDataItems.get(0).times;
    }

    /**
     * Reads a `Hdf5DatasetWrapper` contents and generates `Hdf5PreparedData` with spatial data for writing out to Hdf5 format via Hdf5Writer
     * 
     * @param datasetWrapper the data relevant to an HDF5 output file
     * @return the prepared spatial data
     */
    public static Hdf5PreparedData prepareSpatialData (Hdf5SedmlResults datasetWrapper){
        Hdf5SedmlResultsSpatial dataSourceSpatial = (Hdf5SedmlResultsSpatial)datasetWrapper.dataSource; 
        logger.debug(dataSourceSpatial);
        Hdf5DataSourceSpatialVarDataItem exampleVarDataItem = null;

        for (Hdf5DataSourceSpatialVarDataItem item : dataSourceSpatial.varDataItems){
            if (item.spaceTimeDimensions == null) continue;
            exampleVarDataItem = item;
            break;
        }

        if (exampleVarDataItem.spaceTimeDimensions == null){
            RuntimeException e = new RuntimeException("No space time dimensions could be found!");
            logger.error(e);
            throw e;
        }

        int[] spaceTimeDimensions = exampleVarDataItem.spaceTimeDimensions;
        int[] spatialDimensions = Arrays.copyOf(spaceTimeDimensions, spaceTimeDimensions.length - 1);
        //int numSpatialPoints = Arrays.stream(spatialDimensions).sum();
        //long numJobs = dataSourceSpatial.varDataItems.stream().map(varDataItem -> varDataItem.jobIndex).collect(Collectors.toSet()).size();
        List<Variable> sedmlVars = new ArrayList<>(dataSourceSpatial.varDataItems.stream().map(varDataItem -> varDataItem.sedmlVariable).collect(Collectors.toSet()));
        long numVars = sedmlVars.size();
        long numTimes = exampleVarDataItem.times.length;
        List<Long> dataDimensionList = new ArrayList<>();
        int numJobs = 1;

        if (numTimes != spaceTimeDimensions[spaceTimeDimensions.length-1]){
            throw new RuntimeException("unexpected dimension " + spaceTimeDimensions + " for data, expected last dimension to be that of time: " + numTimes);
        }
        
        
        for (int scanBound : dataSourceSpatial.scanBounds){
            dataDimensionList.add((long)scanBound + 1);
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
     * Reads a `Hdf5DatasetWrapper` contents and generates `Hdf5PreparedData` with nonspatial data for writing out to Hdf5 format via Hdf5Writer
     * 
     * @param datasetWrapper the data relevant to an hdf5 output file
     * @return the prepared nonspatial data
     */
    public static Hdf5PreparedData prepareNonspatialData(Hdf5SedmlResults datasetWrapper){
        long numTimePoints = 0;
        int totalDataVolume = 1;
        long numDataSets;
        double[] bigDataBuffer;
        List<Long> dataDimensionList = new ArrayList<>();
        
        Hdf5SedmlResultsNonspatial dataSourceNonspatial = (Hdf5SedmlResultsNonspatial) datasetWrapper.dataSource;
        Map<DataSet, List<double[]>> dataSetMap = dataSourceNonspatial.allJobResults;
        numDataSets = dataSetMap.keySet().size();
        
        for (DataSet dataSet : dataSetMap.keySet()){
            for (double[] resultsSet : dataSetMap.get(dataSet)){
                numTimePoints = Long.max(numTimePoints, resultsSet.length);
            }
        }

        // Structure dimensionality:
        dataDimensionList.add(numDataSets);                     // ...first by dataSet
        for (int scanBound : dataSourceNonspatial.scanBounds){
            dataDimensionList.add((long)scanBound + 1);         // ...then by scan bounds / "repeated task" dimensions
        }
        dataDimensionList.add(numTimePoints);                   // ...finally by max time points
        for (long dim : dataDimensionList){
            totalDataVolume *= (int)dim;
        }

        bigDataBuffer = new double[totalDataVolume];
        int bufferOffset = 0;
        
        for (DataSet dataSet : dataSetMap.keySet()) {
            List<double[]> dataArrays = dataSourceNonspatial.allJobResults.get(dataSet);
            for (double[] dataArray : dataArrays){
                if (dataArray.length < numTimePoints){
                    double[] paddedArray = new double[Math.toIntExact(numTimePoints)];
                    System.arraycopy(dataArray, 0, paddedArray, 0, dataArray.length);
                    for (int i = dataArray.length; i < numTimePoints; i++){
                        paddedArray[i] = Double.NaN;
                    }
                    dataArray = paddedArray;
                }
                System.arraycopy(dataArray, 0, bigDataBuffer, bufferOffset, dataArray.length);
                bufferOffset += (int)numTimePoints;
            }
        }
        

        Hdf5PreparedData preparedData = new Hdf5PreparedData();
        preparedData.sedmlId = datasetWrapper.datasetMetadata.sedmlId;
        preparedData.dataDimensions = dataDimensionList.stream().mapToLong(l -> l).toArray();
        preparedData.flattenedDataBuffer = bigDataBuffer;
        return preparedData;
    }
}
