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
import org.jlibsedml.VariableSymbol;

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
        int totalDataVolume = 1;
        int numJobs = 1;
        double[] bigDataBuffer;
        List<Long> dataDimensionList = new ArrayList<>();
        Hdf5DataSourceSpatialVarDataItem exampleVarDataItem = null;
        Hdf5SedmlResultsSpatial spatialDataSource = (Hdf5SedmlResultsSpatial)datasetWrapper.dataSource;
        Hdf5SedmlResultsSpatial spatialDataResults = new Hdf5SedmlResultsSpatial();
        logger.debug(spatialDataResults);

        spatialDataResults.scanBounds = spatialDataSource.scanBounds;
        spatialDataResults.scanParameterNames = spatialDataSource.scanParameterNames;
        for (Hdf5DataSourceSpatialVarDataItem item : spatialDataSource.varDataItems){
            VariableSymbol symbol = item.sedmlVariable.getSymbol();
            if (symbol != null && "TIME".equals(symbol.name())) continue;
            if (exampleVarDataItem == null) exampleVarDataItem = item;
            spatialDataResults.varDataItems.add(item);
        }

        if (exampleVarDataItem == null)
            throw new RuntimeException("We have no spatial datasets here somehow! This error shouldn't happen");

        if (exampleVarDataItem.spaceTimeDimensions == null){
            RuntimeException e = new RuntimeException("No space time dimensions could be found!");
            logger.error(e);
            throw e;
        }

        int[] spaceTimeDimensions = exampleVarDataItem.spaceTimeDimensions;
        //int numSpatialPoints = Arrays.stream(spatialDimensions).sum();
        //long numJobs = spatialDataResults.varDataItems.stream().map(varDataItem -> varDataItem.jobIndex).collect(Collectors.toSet()).size();
        List<Variable> sedmlVars = new ArrayList<>(spatialDataResults.varDataItems.stream().map(varDataItem -> varDataItem.sedmlVariable).collect(Collectors.toSet()));
        long numVars = sedmlVars.size();

        if (exampleVarDataItem.times.length != spaceTimeDimensions[spaceTimeDimensions.length-1]){
            throw new RuntimeException("unexpected dimension " + spaceTimeDimensions
                    + " for data, expected last dimension to be that of time: " + exampleVarDataItem.times.length);
        }

        // Structure dimensionality
        dataDimensionList.add(numVars);                         // ...first by dataSet
        for (int scanBound : spatialDataResults.scanBounds){
            dataDimensionList.add((long)scanBound + 1);         // ...then by scan bounds / "repeated task"
            numJobs *= (scanBound+1);
        }
        for (long dim : spaceTimeDimensions){                   // ...finally by space-time dimensions
            dataDimensionList.add(dim);
        }

        for (long dim : dataDimensionList){
            totalDataVolume *= (int)dim;
        }

        // Create buffer of contiguous data to hold everything.
        bigDataBuffer = new double[totalDataVolume];
        int bufferOffset = 0;
        for (int jobIndex=0; jobIndex<numJobs; jobIndex++){
            for (Variable var : sedmlVars) {
                // find data for var and jobIndex
                for (Hdf5DataSourceSpatialVarDataItem varDataItem : spatialDataResults.varDataItems) {
                    if (varDataItem.sedmlVariable.equals(var) && varDataItem.jobIndex == jobIndex){
                        String varName = varDataItem.sedmlVariable.getSymbol() == null ? null :
                                varDataItem.sedmlVariable.getSymbol().name();
                        double[] dataArray = "TIME".equals(varName) ? varDataItem.times: varDataItem.getSpatialData();
                        System.arraycopy(dataArray,0,bigDataBuffer,bufferOffset,dataArray.length);
                        bufferOffset += dataArray.length;
                        break;
                    }
                }
            }
        }

        Hdf5PreparedData preparedData = new Hdf5PreparedData();
        preparedData.sedmlId = datasetWrapper.datasetMetadata.sedmlId;
        preparedData.dataDimensions = dataDimensionList.stream().mapToLong(l -> l).toArray();
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
