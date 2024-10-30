package org.vcell.cli.run.hdf5;

import cbit.vcell.export.server.JhdfUtils;
import io.jhdf.HdfFile;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.Report;
import org.jlibsedml.SedML;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Static class for writing out Hdf5 formatted files
 */
public class BiosimulationsHdf5Writer {

    private final static Logger logger = LogManager.getLogger(BiosimulationsHdf5Writer.class);

    private BiosimulationsHdf5Writer(){} // Static class = no instances allowed

    /**
     * Writes an HDF5 formatted file given a hdf5FileWrapper and a destination to write the file to.
     * 
     * @param hdf5ExecutionResults the wrapper of hdf5 relevant data
     * @param outDirForCurrentSedml the directory to place the report file into, NOT the report file itself.
     * @throws BiosimulationsHdfWriterException if there is an exception thrown from hdf5 while using the library.
     * @throws IOException if the computer encounters an unexpected system IO problem
     */
    public static void writeHdf5(HDF5ExecutionResults hdf5ExecutionResults, File outDirForCurrentSedml) throws BiosimulationsHdfWriterException, IOException {
        boolean didFail = false;

        // Create and open the Hdf5 file
        logger.info("Creating hdf5 file `reports.h5` in" + outDirForCurrentSedml.getAbsolutePath());
        File tempFile = new File(outDirForCurrentSedml, "reports.h5");
        WritableHdfFile hdf5File = HdfFile.write(tempFile.toPath());

        try {
            // Sanity Check
            for (SedML sedml : hdf5ExecutionResults){
                Hdf5DataContainer hdf5DataWrapper = hdf5ExecutionResults.getData(sedml);
                Set<Report> uriSet = hdf5DataWrapper.reportToUriMap.keySet(),
                        resultsSet = hdf5DataWrapper.reportToResultsMap.keySet();
                if (uriSet.size() != resultsSet.size()) throw new RuntimeException("Sets are mismatched");
                for (Report report : resultsSet){
                    // Process Parent Groups
                    String groupPath = hdf5DataWrapper.reportToUriMap.get(report);
                    Node child = hdf5File.getChild(groupPath);
                    WritableGroup group = null;
                    if (child instanceof WritableGroup) {
                        group = (WritableGroup) child;
                    } else {
                        group = hdf5File.putGroup(groupPath);
                        JhdfUtils.putAttribute(group,"combineArchiveLocation", groupPath);
                        JhdfUtils.putAttribute(group,"uri", groupPath);
                    }

                    // Process the Dataset
                    for (Hdf5SedmlResults data : hdf5DataWrapper.reportToResultsMap.get(report)){
                        final Hdf5PreparedData preparedData;
                        if (data.dataSource instanceof Hdf5SedmlResultsNonspatial)
                            preparedData = Hdf5DataPreparer.prepareNonspatialData(data, report, hdf5DataWrapper.trackSubSetsInReports);
                        else if (data.dataSource instanceof Hdf5SedmlResultsSpatial)
                            preparedData = Hdf5DataPreparer.prepareSpatialData(data, report, hdf5DataWrapper.trackSubSetsInReports);
                        else continue;

                        // multiDimDataArray is a double[], double[][], double[][][], ... depending on the data dimensions
                        final String datasetName = preparedData.sedmlId;
                        final Object multiDimDataArray = JhdfUtils.createMultidimensionalArray(preparedData.dataDimensions, preparedData.flattenedDataBuffer);
                        WritiableDataset dataset = group.putDataset(datasetName, multiDimDataArray);

                        if (data.dataSource instanceof Hdf5SedmlResultsSpatial){
                            JhdfUtils.putAttribute(dataset,"times", Hdf5DataPreparer.getSpatialHdf5Attribute_Times(report, data));
                        }
                        JhdfUtils.putAttribute(dataset, "_type", data.datasetMetadata._type);
                        JhdfUtils.putAttribute(dataset, "sedmlDataSetDataTypes", data.datasetMetadata.sedmlDataSetDataTypes);
                        JhdfUtils.putAttribute(dataset, "sedmlDataSetIds", data.datasetMetadata.sedmlDataSetIds);
                        JhdfUtils.putAttribute(dataset, "sedmlDataSetNames", data.datasetMetadata.sedmlDataSetNames);
                        JhdfUtils.putAttribute(dataset, "sedmlDataSetLabels", data.datasetMetadata.sedmlDataSetLabels);
                        JhdfUtils.putAttribute(dataset, "sedmlDataSetShapes", data.datasetMetadata.sedmlDataSetShapes);
                        if (data.dataSource.scanParameterValues != null && data.dataSource.scanParameterValues.length > 0) {
                            List<String> scanValues = Arrays.stream(data.dataSource.scanParameterValues).map(Arrays::toString).toList();
                            JhdfUtils.putAttribute(dataset, "sedmlRepeatedTaskValues", scanValues);
                        }
                        if (data.dataSource.scanParameterNames != null && data.dataSource.scanParameterNames.length > 0) {
                            JhdfUtils.putAttribute(dataset, "sedmlRepeatedTaskParameterNames", Arrays.asList(data.dataSource.scanParameterNames));
                        }
                        JhdfUtils.putAttribute(dataset, "sedmlId", data.datasetMetadata.sedmlId);
                        if (data.datasetMetadata.sedmlName != null) {
                            JhdfUtils.putAttribute(dataset, "sedmlName", data.datasetMetadata.sedmlName);
                        }else{
                            JhdfUtils.putAttribute(dataset, "sedmlName", data.datasetMetadata.sedmlId);
                        }
                        JhdfUtils.putAttribute(dataset, "uri", groupPath + "/" + data.datasetMetadata.sedmlId);
                    }
                }
            }
        } catch (RuntimeException e) { // Catch runtime exceptions
            didFail = true;
            String message = "Error encountered while writing to BioSim-style HDF5.";
            logger.error(message, e);
            throw new BiosimulationsHdfWriterException(message, e);
        } finally {
            try {
                hdf5File.close();
                if (didFail) {
                    logger.error("HDF5 successfully closed, but there were errors preventing proper execution.\"");
                } else {
                    logger.info("HDF5 file successfully written to.");
                }
            } catch (Exception e){
                String message = "HDF5 Library Exception encountered while writing out to HDF5 file; Check std::err for stack";
                logger.error(message);
                if (!didFail) throw new BiosimulationsHdfWriterException(message, e);
            }
        }
    }

}
