package org.vcell.cli.run.hdf5;

import io.jhdf.HdfFile;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.*;
import io.jhdf.object.datatype.StringData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.Report;
import org.jlibsedml.SedML;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.*;

/**
 * Static class for writing out Hdf5 formatted files
 */
public class BiosimulationsHdf5Writer {

    public static class BiosimulationsHdfWriterException extends Exception {
        @Serial
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
                        putAttribute(group,"combineArchiveLocation", groupPath);
                        putAttribute(group,"uri", groupPath);
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
                        final Object multiDimDataArray = preparedData.createMultidimensionalArray();
                        WritiableDataset dataset = group.putDataset(datasetName, multiDimDataArray);

                        if (data.dataSource instanceof Hdf5SedmlResultsSpatial){
                            putAttribute(dataset,"times", Hdf5DataPreparer.getSpatialHdf5Attribute_Times(report, data));
                        }
                        putAttribute(dataset, "_type", data.datasetMetadata._type);
                        putAttribute(dataset, "sedmlDataSetDataTypes", data.datasetMetadata.sedmlDataSetDataTypes);
                        putAttribute(dataset, "sedmlDataSetIds", data.datasetMetadata.sedmlDataSetIds);
                        putAttribute(dataset, "sedmlDataSetNames", data.datasetMetadata.sedmlDataSetNames);
                        putAttribute(dataset, "sedmlDataSetNames_fake00", Arrays.asList( new String[]{ "hello", "hello", "hello" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake01", Arrays.asList( new String[]{ "hello", "hell", "hello" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake02", Arrays.asList( new String[]{ "hello", "", "hello" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake03", Arrays.asList( new String[]{ "hello", null, "hello" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake04", Arrays.asList( new String[]{ null, null, "hello" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake05", Arrays.asList( new String[]{ null, null, null, "hello" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake06", Arrays.asList( new String[]{ null, null, null, "Time" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake07", Arrays.asList( new String[]{ null, null, null, null, "hello" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake08", Arrays.asList( new String[]{ "1234567" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake09", Arrays.asList( new String[]{ null, "1234567" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake10", Arrays.asList( new String[]{ null, null, "1234567" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake11", Arrays.asList( new String[]{ null, null, null, "1234567" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake12", Arrays.asList( new String[]{ null, null, null, "1234567", "1234567" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake13", Arrays.asList( new String[]{ "a", null, null, "1234567", "1234567" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake14", Arrays.asList( new String[]{ "ab", null, null, "1234567", "1234567" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake15", Arrays.asList( new String[]{ null, "a", null, "1234567", "1234567" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake16", Arrays.asList( new String[]{ null, "ab", null, "1234567", "1234567" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake17", Arrays.asList( new String[]{ "", "a", "", "1234567", "1234567" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake18", Arrays.asList( new String[]{ "", "ab", "", "1234567", "1234567" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake19", Arrays.asList( new String[]{ "", "ab", "", "1234567", "1234567", "", "", "", "" }));
                        putAttribute(dataset, "sedmlDataSetNames_fake20", Arrays.asList( new String[]{ "", "ab", "", "1234567", "1234567", null, "", "", "" }));

                        putAttribute(dataset, "sedmlDataSetLabels", data.datasetMetadata.sedmlDataSetLabels);
                        putAttribute(dataset, "sedmlDataSetShapes", data.datasetMetadata.sedmlDataSetShapes);
                        if (data.dataSource.scanParameterValues != null && data.dataSource.scanParameterValues.length > 0) {
                            List<String> scanValues = Arrays.stream(data.dataSource.scanParameterValues).map(Arrays::toString).toList();
                            putAttribute(dataset, "sedmlRepeatedTaskValues", scanValues);
                        }
                        if (data.dataSource.scanParameterNames != null && data.dataSource.scanParameterNames.length > 0) {
                            putAttribute(dataset, "sedmlRepeatedTaskParameterNames", Arrays.asList(data.dataSource.scanParameterNames));
                        }
                        putAttribute(dataset, "sedmlId", data.datasetMetadata.sedmlId);
                        putAttribute(dataset, "sedmlName", data.datasetMetadata.sedmlName);
                        putAttribute(dataset, "uri", groupPath + "/" + data.datasetMetadata.sedmlId);
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

    public static void putAttribute(WritableNode node, String name, List<String> values) {
        if (values.contains(null)) {
            //
            // WORKAROUND for apparent bug in io.jhdf 0.8.3
            //
            // replace all null entries with max length of non-null entries
            // replacing with the empty string is not good enough, and a single space also doesn't work
            //
            // e.g. for a list of ["a", null, "abc"], replace null with ["a","   ,"abc"]
            //
            int maxStringLength = 0;
            for (String s : values) {
                if (s != null && s.length() > maxStringLength) {
                    maxStringLength = s.length();
                }
            }
            final String nullString = " ".repeat(maxStringLength);
            String[] paddedValues = values.stream().map(s -> s == null ? nullString : s).toArray(String[]::new);
            System.out.println("name="+name+", orig=" + values + ", paddedValues=" + Arrays.toString(paddedValues));
            Attribute attribute = node.putAttribute(name, paddedValues);
            //attribute.getDataSpace().toString();

        } else {
            System.out.println("name="+name+", values=" + values);
            node.putAttribute(name, values.toArray(new String[0]));
        }
    }

    public static void putAttribute(WritableNode node, String name, String value) {
        node.putAttribute(name, value);
    }

    public static void putAttribute(WritableNode node, String name, double[] value) {
        node.putAttribute(name, value);
    }

}
