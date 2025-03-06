package org.vcell.cli.run.hdf5;

import cbit.vcell.export.server.JhdfUtils;
import io.jhdf.HdfFile;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.Report;
import org.jlibsedml.SedML;
import org.vcell.util.DataAccessException;
import org.vcell.util.trees.GenericStringTree;
import org.vcell.util.trees.Tree;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
        logger.info("Saving all simulation results to Hdf5 file...");
        if (logger.isDebugEnabled()) logger.info("Creating HDF5 file `reports.h5` in {}", outDirForCurrentSedml.getAbsolutePath());
        File tempFile = new File(outDirForCurrentSedml, "reports.h5");
        try {
            try (WritableHdfFile hdf5File = HdfFile.write(tempFile.toPath())){
                // Sanity Check
                for (SedML sedml : hdf5ExecutionResults){
                    Hdf5DataContainer hdf5DataWrapper = hdf5ExecutionResults.getData(sedml);
                    Set<Report> uriKeySet = hdf5DataWrapper.reportToUriMap.keySet(),
                            resultsSet = hdf5DataWrapper.reportToResultsMap.keySet();
                    if (uriKeySet.size() != resultsSet.size()) throw new RuntimeException("Sets are mismatched");
                    // We need to build the paths, especially for sub-dir sedml

                    Tree<String> parentTree = new GenericStringTree();
                    for (Report report : uriKeySet){
                        parentTree.addElement(true, hdf5DataWrapper.reportToUriMap.get(report).split("/"));
                    }
                    // Populate groups in jhdf5 file
                    Map<String, WritableGroup> pathToGroupMapping = JhdfUtils.addGroups(hdf5File, parentTree);

                    // Start processing reports
                    for (Report report : resultsSet){
                        // Process Parent Groups
                        String groupPath = hdf5DataWrapper.reportToUriMap.get(report);
                        Node parent = pathToGroupMapping.get(groupPath);
                        if (!(parent instanceof WritableGroup parentGroup)){
                            throw new BiosimulationsHdfWriterException("Unexpected parent encountered");
                        }

                        // Process the Dataset
                        for (Hdf5SedmlResults data : hdf5DataWrapper.reportToResultsMap.get(report)){
                            final Hdf5PreparedData preparedData = Hdf5DataPreparer.prepareData(data, report, hdf5DataWrapper.trackSubSetsInReports);

                            // multiDimDataArray is a double[], double[][], double[][][], ... depending on the data dimensions
                            final String datasetName = preparedData.sedmlId;
                            final Object multiDimDataArray = JhdfUtils.createMultidimensionalArray(preparedData.dataDimensions, preparedData.flattenedDataBuffer);
                            WritiableDataset dataset = parentGroup.putDataset(datasetName, multiDimDataArray);

                            if (data.dataSource instanceof Hdf5SedmlResultsSpatial && preparedData.times != null){
                                JhdfUtils.putAttribute(dataset,"times", preparedData.times);
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
            } catch (RuntimeException | DataAccessException e) { // Catch runtime exceptions
                didFail = true;
                String message = "Error encountered while writing to BioSim-style HDF5.";
                logger.error(message, e);
                throw new BiosimulationsHdfWriterException(message, e);
            }
        } finally {
            if (!didFail) logger.info("HDF5 writing completed successfully.");
        }
    }

    private static class ShortestPathComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            int count1 = 0, count2 = 0;
            for (char c : o1.toCharArray()) if (c == '/') count1++;
            for (char c : o2.toCharArray()) if (c == '/') count2++;
            return count1 - count2;
        }
    }
}
