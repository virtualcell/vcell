package org.vcell.cli.run.hdf5;

import cbit.vcell.resource.NativeLib;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.Report;
import org.jlibsedml.SedML;
import org.vcell.cli.run.hdf5.Hdf5DataPreparer.Hdf5PreparedData;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * Static class for writing out Hdf5 formatted files
 */
public class BiosimulationsHdf5Writer {

    public static class BiosimulationsHdfWriterException extends Exception {
        private static final long serialVersionUID = 1L;
        public BiosimulationsHdfWriterException(String message, Exception e) {
            super(message, e);
        }
    }

    private final static Logger logger = LogManager.getLogger(BiosimulationsHdf5Writer.class);

    private BiosimulationsHdf5Writer(){} // Static class = no instances allowed

    /**
     * Writes an HDF5 formatted file given a hdf5FileWrapper and a destination to write the file to.
     * 
     * @param hdf5ExecutionResults the wrapper of hdf5 relevant data
     * @param outDirForCurrentSedml the directory to place the report file into, NOT the report file itself.
     * @throws BiosimulationsHdfWriterException if there is an expection thrown from hdf5 while using the library.
     * @throws IOException if the computer encounteres an unexepcted system IO problem
     */
    public static void writeHdf5(HDF5ExecutionResults hdf5ExecutionResults, File outDirForCurrentSedml) throws BiosimulationsHdfWriterException, IOException {
        boolean didFail = false;
        BiosimulationsHdf5File masterHdf5;

        // Boot Hdf5 Library
        NativeLib.HDF5.load();

        // Create and open the Hdf5 file
        logger.info("Creating hdf5 file `reports.h5` in" + outDirForCurrentSedml.getAbsolutePath());
        masterHdf5 = new BiosimulationsHdf5File(outDirForCurrentSedml);
        masterHdf5.open();

        try {
            // Sanity Check
            for (SedML sedml : hdf5ExecutionResults){
                Hdf5DataContainer hdf5DataWrapper = hdf5ExecutionResults.getData(sedml);
                Set<Report> uriSet = hdf5DataWrapper.reportToUriMap.keySet(),
                        resultsSet = hdf5DataWrapper.reportToResultsMap.keySet();
                if (uriSet.size() != resultsSet.size()) throw new RuntimeException("Sets are mismatched");
                for (Report report : resultsSet){
                    // Process Parent Groups
                    String path = "";
                    for (String group : hdf5DataWrapper.reportToUriMap.get(report).split("/")){
                        path += ("/" + group); // Start from first group, then travel down the path.
                        if (masterHdf5.containsGroup(path)) continue;
                        int groupId = masterHdf5.addGroup(path);
                        String relativePath = path.substring(1);
                        masterHdf5.insertFixedStringAttribute(groupId, "combineArchiveLocation", relativePath);
                        masterHdf5.insertFixedStringAttribute(groupId, "uri", relativePath);
                        // We leave them open because there may be other datasets that share (some of) the same parent groups
                    }

                    // Process the Dataset
                    for (Hdf5SedmlResults data : hdf5DataWrapper.reportToResultsMap.get(report)){
                        Hdf5PreparedData preparedData;
                        if (data.dataSource instanceof Hdf5SedmlResultsNonspatial)
                            preparedData = Hdf5DataPreparer.prepareNonspatialData(data, report, hdf5DataWrapper.trackSubSetsInReports);
                        else if (data.dataSource instanceof Hdf5SedmlResultsSpatial)
                            preparedData = Hdf5DataPreparer.prepareSpatialData(data, report, hdf5DataWrapper.trackSubSetsInReports);
                        else continue;

                        int currentDatasetId = masterHdf5.insertSedmlData(path, preparedData);

                        if (data.dataSource instanceof Hdf5SedmlResultsSpatial){
                            masterHdf5.insertNumericAttributes(currentDatasetId, "times", Hdf5DataPreparer.getSpatialHdf5Attribute_Times(report, data));
                        }
                        masterHdf5.insertFixedStringAttribute(currentDatasetId, "_type", data.datasetMetadata._type);
                        masterHdf5.insertFixedStringAttributes(currentDatasetId, "scanParameterNames", Arrays.asList(data.dataSource.scanParameterNames));
                        masterHdf5.insertFixedStringAttributes(currentDatasetId, "sedmlDataSetDataTypes", data.datasetMetadata.sedmlDataSetDataTypes);
                        masterHdf5.insertFixedStringAttributes(currentDatasetId, "sedmlDataSetIds", data.datasetMetadata.sedmlDataSetIds);

                        if (data.datasetMetadata.sedmlDataSetNames.contains(null)) {
                            for (int i = 0; i < data.datasetMetadata.sedmlDataSetNames.size(); i++){
                                String oldValue = data.datasetMetadata.sedmlDataSetNames.get(i);
                                String newValue = oldValue == null ? "" : oldValue;
                                data.datasetMetadata.sedmlDataSetNames.set(i, newValue);
                            }
                        }
                        masterHdf5.insertFixedStringAttributes(currentDatasetId, "sedmlDataSetNames", data.datasetMetadata.sedmlDataSetNames);
                        masterHdf5.insertFixedStringAttributes(currentDatasetId, "sedmlDataSetLabels", data.datasetMetadata.sedmlDataSetLabels);
                        masterHdf5.insertFixedStringAttributes(currentDatasetId, "sedmlDataSetShapes", data.datasetMetadata.sedmlDataSetShapes);
                        masterHdf5.insertFixedStringAttribute(currentDatasetId, "sedmlId", data.datasetMetadata.sedmlId);
                        masterHdf5.insertFixedStringAttribute(currentDatasetId, "sedmlName", data.datasetMetadata.sedmlName);
                        masterHdf5.insertFixedStringAttribute(currentDatasetId, "uri", path.substring(1) + "/" + data.datasetMetadata.sedmlId);

                        masterHdf5.closeDataset(currentDatasetId);
                    }
                }
            }
        } catch (HDF5Exception e) { // Catch runtime exceptions
            didFail = true;
            String message = "Error encountered while writing to BioSim-style HDF5.";
            logger.error(message, e);
            throw new BiosimulationsHdfWriterException(message, e);
        } finally {
            try {
                final Level errorLevel = didFail ? Level.ERROR : Level.INFO;
                final String message = didFail ?
                        "HDF5 successfully closed, but there were errors preventing proper execution." :
                        "HDF5 file successfully written to.";
                // Close up the file; lets deliver what we can write and flush out.
                masterHdf5.close();
                logger.log(errorLevel, message);
            } catch (BiosimulationsHdfWriterException e){
                masterHdf5.printErrorStack();
                String message = "HDF5 Library Exception encountered while writing out to HDF5 file; Check std::err for stack";
                logger.error(message);
                if (!didFail) throw new BiosimulationsHdfWriterException(message, e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
