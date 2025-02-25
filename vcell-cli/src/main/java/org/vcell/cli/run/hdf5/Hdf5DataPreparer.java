package org.vcell.cli.run.hdf5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.DataSet;
import org.jlibsedml.Report;
import org.vcell.sbml.vcell.SBMLDataRecord;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;
import org.vcell.sbml.vcell.lazy.LazySBMLNonSpatialDataAccessor;
import org.vcell.util.DataAccessException;
import org.vcell.util.Pair;

import java.util.*;

/**
 * Static data preparation class for Hdf5 files
 */
public class Hdf5DataPreparer {
    private final static Logger logger = LogManager.getLogger(Hdf5DataPreparer.class);


    /**
     * Reads a `Hdf5DatasetWrapper` contents and generates `Hdf5PreparedData` with data for writing out to Hdf5 format via BiosimulationsHdf5Writer
     *
     * @param datasetWrapper the data relevant to an HDF5 output file
     * @param sedmlReport the SedML Report object the data should pertain to
     * @param reportSubSets whether or not intermediate dimensions of size 1 should be included (for instance, BioSimulators wants it)
     * @return the prepared data
     */
    public static Hdf5PreparedData prepareData(Hdf5SedmlResults datasetWrapper, Report sedmlReport, boolean reportSubSets) throws DataAccessException {
        int numDataSetValues = -1;
        int totalDataVolume = 1;
        double[] bigDataBuffer;
        List<Long> dataDimensionList = new LinkedList<>();
        Hdf5SedmlResultData sedmlResultData = datasetWrapper.dataSource;
        Map<DataSet, ? extends List<? extends LazySBMLDataAccessor>> dataSetMap = sedmlResultData.dataItems.getDataSetMappings(sedmlReport);

        for (DataSet dataSet : dataSetMap.keySet()){
            for (LazySBMLDataAccessor resultsSet : dataSetMap.get(dataSet)){
                if (numDataSetValues < 0) numDataSetValues = resultsSet.getFlatSize();
                if (numDataSetValues == resultsSet.getFlatSize()) continue;
                throw new IllegalArgumentException("Shape of data across DataSets in same report are not equal! Jagged data not supported!");
            }
        }

        // Structure dimensionality:
        dataDimensionList.add((long) dataSetMap.size());        // ...first by # of dataSets
        for (int scanBound : sedmlResultData.scanBounds){
            dataDimensionList.add((long)scanBound + 1);         // ...then by scan bounds / "repeated task" dimensions
            if (reportSubSets) dataDimensionList.add((long)1);  // ...then by num of subtasks (VCell only supports 1)
        }
        // We need to properly add the data dimensions later, so that we're efficient with data access costs.

        // Calculate how much data our bigDataBuffer needs to store
        for (long dim : dataDimensionList){
            totalDataVolume *= (int)dim;
        }
        // We skipped spatial dimensions, so we need to account for that here.
        totalDataVolume *= numDataSetValues;

        bigDataBuffer = new double[totalDataVolume];
        int bufferOffset = 0;
        List<Integer> spatialDimensions = new LinkedList<>();
        double[] times = null;

        for (DataSet dataSet : dataSetMap.keySet()) {
            List<? extends LazySBMLDataAccessor> dataArrays = sedmlResultData.dataItems.get(sedmlReport, dataSet);
            for (LazySBMLDataAccessor dataAccessor : dataArrays){
                SBMLDataRecord data;
                try {
                    data = dataAccessor.getData();
                } catch (Exception e){
                    throw new DataAccessException("Unable to get lazy data", e);
                }

                // Check spatial dimensions
                if (spatialDimensions.isEmpty()) spatialDimensions = data.dimensions();
                else if (!spatialDimensions.equals(data.dimensions())) throw new DataAccessException("Spatial dimensions do not match!");

                // Check times
                if (times == null && data.times() != null) times = data.times();
                else if (data.times() != null && !Arrays.equals(times, data.times())) throw new DataAccessException("Times do not match!");

                // Get data
                // Note that numTimePoints should always equal to or greater than data.one.length!
                int desiredDataLength = Math.toIntExact(Math.max(data.data().length, numDataSetValues));
                double[] dataArray = new double[desiredDataLength];
                Arrays.fill(dataArray, Double.NaN);
                System.arraycopy(data.data(), 0, dataArray, 0, data.data().length);
                System.arraycopy(dataArray, 0, bigDataBuffer, bufferOffset, dataArray.length);

                bufferOffset += desiredDataLength;
            }
        }
        // Remember we skipped accounting for spatial dimensions earlier? Now we factor it in!
        for (int dim : spatialDimensions) dataDimensionList.add((long) dim); // ...then by the size of the (non-)spatial data

        Hdf5PreparedData preparedData = new Hdf5PreparedData();
        preparedData.sedmlId = datasetWrapper.datasetMetadata.sedmlId;
        preparedData.dataDimensions = dataDimensionList.stream().mapToLong(l -> l).toArray();
        preparedData.flattenedDataBuffer = bigDataBuffer;
        preparedData.spatialDimensions = spatialDimensions;
        preparedData.times = times;
        return preparedData;
    }
}
