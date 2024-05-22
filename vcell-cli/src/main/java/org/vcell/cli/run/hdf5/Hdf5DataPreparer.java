package org.vcell.cli.run.hdf5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.DataSet;
import org.jlibsedml.Report;

import java.util.*;

/**
 * Static data preparation class for Hdf5 files
 */
public class Hdf5DataPreparer {
    private final static Logger logger = LogManager.getLogger(Hdf5DataPreparer.class);

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
    public static double[] getSpatialHdf5Attribute_Times(Report report, Hdf5SedmlResults hdf5SedmlResults){
        // Fake for-loop; just to get the first item
        if (!(hdf5SedmlResults.dataSource instanceof Hdf5SedmlResultsSpatial spatialResults))
            throw new ClassCastException("A non-spatial dataset was applied to a spatial function");
        return spatialResults.dataMapping.get(report).metadata.getTimes();
    }

    /**
     * Reads a `Hdf5DatasetWrapper` contents and generates `Hdf5PreparedData` with spatial data for writing out to Hdf5 format via BiosimulationsHdf5Writer
     * 
     * @param datasetWrapper the data relevant to an HDF5 output file
     * @return the prepared spatial data
     */
    public static Hdf5PreparedData prepareSpatialData (Hdf5SedmlResults datasetWrapper, Report sedmlReport, boolean reportSubSets){
        int totalDataVolume = 1;
        double[] bigDataBuffer;
        List<Long> dataDimensionList = new ArrayList<>();
        Hdf5SedmlResultsSpatial spatialDataSource = (Hdf5SedmlResultsSpatial)datasetWrapper.dataSource;
        Hdf5SedmlResultsSpatial.SpatialComponents comps = spatialDataSource.dataMapping.get(sedmlReport);
        List<Hdf5DataSourceSpatialVarDataLocation> exampleLocations =
                spatialDataSource.dataMapping.get(sedmlReport).varsToData.getLocations((new ArrayList<>(comps.varsToData.getVariableSet())).get(0));

        if (comps.varsToData == null || comps.varsToData.getVariableSet().isEmpty())
            throw new RuntimeException("We have no spatial datasets here somehow! This error shouldn't happen");

        if (comps.metadata.getSpaceTimeDimensions() == null){
            RuntimeException e = new RuntimeException("No space time dimensions could be found!");
            logger.error(e);
            throw e;
        }

        int[] spaceTimeDimensions = comps.metadata.getSpaceTimeDimensions();

        // Structure dimensionality
        dataDimensionList.add((long)comps.varsToData.getVariableSet().size());  // ...first by num of dataSet / species
        dataDimensionList.add((long)exampleLocations.size());                   // ...then by scan bounds / "repeated task"
        if (reportSubSets) dataDimensionList.add((long)1);                      // ...then by num of subtasks (VCell only supports 1)
        for (long dim : spaceTimeDimensions) dataDimensionList.add(dim);        // ...finally by space-time dimensions

        for (long dim : dataDimensionList){
            totalDataVolume *= (int)dim;
        }
        bigDataBuffer = comps.varsToData.getFlatDataForAllVars();
        if (bigDataBuffer.length != totalDataVolume){
            String format = "Calculated total volume of data (%d) does not match returned value (%d)!";
            throw new RuntimeException(String.format(format, totalDataVolume, bigDataBuffer.length));
        }

        Hdf5PreparedData preparedData = new Hdf5PreparedData();
        preparedData.sedmlId = datasetWrapper.datasetMetadata.sedmlId;
        preparedData.dataDimensions = dataDimensionList.stream().mapToLong(l -> l).toArray();
        preparedData.flattenedDataBuffer = bigDataBuffer;
        return preparedData;
    }

    /**
     * Reads a `Hdf5DatasetWrapper` contents and generates `Hdf5PreparedData` with nonspatial data for writing out to Hdf5 format via BiosimulationsHdf5Writer
     * 
     * @param datasetWrapper the data relevant to an hdf5 output file
     * @return the prepared nonspatial data
     */
    public static Hdf5PreparedData prepareNonspatialData(Hdf5SedmlResults datasetWrapper, Report report, boolean reportSubSets){
        long numTimePoints = 0;
        int totalDataVolume = 1;
        long numDataSets;
        double[] bigDataBuffer;
        List<Long> dataDimensionList = new LinkedList<>();
        
        Hdf5SedmlResultsNonspatial dataSourceNonspatial = (Hdf5SedmlResultsNonspatial) datasetWrapper.dataSource;
        Map<DataSet, List<double[]>> dataSetMap = dataSourceNonspatial.dataItems.getDataSetMappings(report);
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
            if (reportSubSets) dataDimensionList.add((long)1);  // ...then by num of subtasks (VCell only supports 1)
        }
        dataDimensionList.add(numTimePoints);                   // ...finally by max time points


        for (long dim : dataDimensionList){
            totalDataVolume *= (int)dim;
        }

        bigDataBuffer = new double[totalDataVolume];
        int bufferOffset = 0;
        
        for (DataSet dataSet : dataSetMap.keySet()) {
            List<double[]> dataArrays = dataSourceNonspatial.dataItems.get(report, dataSet);
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
