package org.vcell.cli.run;

import cbit.vcell.export.server.*;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.VariableType;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.output.DataSet;
import org.jlibsedml.*;
import org.jlibsedml.components.output.Output;
import org.jlibsedml.components.output.Report;
import org.jlibsedml.components.simulation.UniformTimeCourse;
import org.vcell.cli.run.results.ValueHolder;
import org.vcell.sbml.vcell.lazy.LazySBMLNonSpatialDataAccessor;
import org.vcell.util.DataAccessException;
import org.vcell.util.GenericExtensionFilter;
import org.vcell.util.document.User;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cbit.vcell.export.server.ExportEnums.VariableMode.VARIABLE_MULTI;

/**
 * Static class with a series of runtime utilities. Needs to be properly docummented still
 */
public class RunUtils {

    public final static String VCELL_TEMP_DIR_PREFIX = "vcell_temp_";
    private final static Logger logger = LogManager.getLogger(RunUtils.class);

    private RunUtils(){} // Static class, no instances allowed

    public static ODESolverResultSet interpolate(ODESolverResultSet odeSolverResultSet, UniformTimeCourse sedmlSim) throws ExpressionException {
        double outputStart = sedmlSim.getOutputStartTime();
        double outputEnd = sedmlSim.getOutputEndTime();

        int numPoints = sedmlSim.getNumberOfSteps() + 1;


        ColumnDescription[] columnDescriptions = odeSolverResultSet.getColumnDescriptions();
        String[] columnNames = new String[columnDescriptions.length];

        for (int i = 0; i < columnDescriptions.length; i++) {
            columnNames[i] = columnDescriptions[i].getDisplayName();
        }

        // need to construct a new RowColumnResultSet instance
        ODESolverResultSet finalResultSet = new ODESolverResultSet();


        // use same column descriptions
        for (ColumnDescription cd : columnDescriptions) {
            finalResultSet.addDataColumn(cd);
        }


        double deltaTime = ((outputEnd - outputStart) / (numPoints - 1));
        double[] timepoints = new double[numPoints];

        timepoints[0] = outputStart;
        for (int i = 1; i < numPoints; i++) {
            timepoints[i] = timepoints[i - 1] + deltaTime;
        }

        double[] originalTimepoints = odeSolverResultSet.extractColumn(0);


        double[][] columnValues = new double[columnDescriptions.length][];
        columnValues[0] = timepoints;
        for (int i = 1; i < columnDescriptions.length; i++) {
            // each row uses the time index based on the params above and for each column descriptions interpolate the value from the original result set
            columnValues[i] = interpLinear(originalTimepoints, odeSolverResultSet.extractColumn(i), timepoints);
        }


        double[][] rowValues = new double[numPoints][columnDescriptions.length];

        for (int rowCount = 0; rowCount < numPoints; rowCount++) {
            for (int colCount = 0; colCount < columnDescriptions.length; colCount++) {
                rowValues[rowCount][colCount] = columnValues[colCount][rowCount];
            }
        }


        // add a numPoints number of rows one by one as double[]
        for (int rowCount = 0; rowCount < numPoints; rowCount++) {
            finalResultSet.addRow(rowValues[rowCount]);
        }

        return finalResultSet;
    }

    public static double[] interpLinear(double[] x, double[] y, double[] xi) throws IllegalArgumentException {

        if (x.length != y.length) {
            throw new IllegalArgumentException("X and Y must be the same length");
        }
        if (x.length == 1) {
            throw new IllegalArgumentException("X must contain more than one value");
        }
        double[] dx = new double[x.length - 1];
        double[] dy = new double[x.length - 1];
        double[] slope = new double[x.length - 1];
        double[] intercept = new double[x.length - 1];

        // Calculate the line equation (i.e. slope and intercept) between each point
        for (int i = 0; i < x.length - 1; i++) {
            dx[i] = x[i + 1] - x[i];
            if (dx[i] == 0) {
                throw new IllegalArgumentException("X must be montotonic. A duplicate " + "x-value was found");
            }
            if (dx[i] < 0) {
                throw new IllegalArgumentException("X must be sorted");
            }
            dy[i] = y[i + 1] - y[i];
            slope[i] = dy[i] / dx[i];
            intercept[i] = y[i] - x[i] * slope[i];
        }

        // Perform the interpolation here
        double[] yi = new double[xi.length];
        for (int i = 0; i < xi.length; i++) {
            if ((xi[i] > x[x.length - 1]) || (xi[i] < x[0])) {
                yi[i] = Double.NaN;
            } else {
                int loc = Arrays.binarySearch(x, xi[i]);
                if (loc < -1) {
                    loc = -loc - 2;
                    yi[i] = slope[loc] * xi[i] + intercept[loc];
                } else {
                    yi[i] = y[loc];
                }
            }
        }

        return yi;
    }

    public static HashMap<String, File> generateReportsAsCSV(SedMLDataContainer sedml, Map<DataGenerator, ValueHolder<LazySBMLNonSpatialDataAccessor>> organizedNonSpatialResults, File outDirForCurrentSedml) {
        // finally, the real work
        HashMap<String, File> reportsHash = new HashMap<>();
        for (Output sedmlOutput : sedml.getOutputs()) {
            // We only want Reports
            if (!(sedmlOutput instanceof Report sedmlReport)) {
                if (logger.isDebugEnabled()) logger.info("Ignoring unsupported output `" + sedmlOutput.getId() + "` while CSV generation.");
                continue;
            }

            StringBuilder sb = new StringBuilder();

            if (logger.isDebugEnabled()) logger.info("Generating report `" + sedmlReport.getId() +"`.");
            /*
             * we go through each entry (dataset) in the list of datasets
             * for each dataset, we use the data reference to obtain the data generator
             * we get the list of variables associated with the data reference
             * * each variable has an id (which is the data reference above, the task and the sbml symbol urn
             * * for each variable we recover the task, from the task we get the sbml model
             * * we search the sbml model to find the vcell variable name associated with the urn
             */
            try {
                List<DataSet> datasets = sedmlReport.getListOfDataSets();
                Map<DataSet, DataGenerator> dataGeneratorMapping = new LinkedHashMap<>();
                for (DataSet dataset : datasets) {
                    DataGenerator referencedGenerator = sedml.getDataGeneratorWithId(dataset.getDataReference());
                    if (referencedGenerator == null) throw new NullPointerException("SedML DataGenerator referenced by report is missing!");
                    if (!organizedNonSpatialResults.containsKey(referencedGenerator)) break;
                    dataGeneratorMapping.put(dataset, referencedGenerator);
                }

                if (dataGeneratorMapping.size() != datasets.size()) {
                    if (logger.isDebugEnabled()) logger.info("Skipping report with unsupported data (needed data not found in non-spatial results");
                    continue;
                }

                for (DataSet validDataSet: dataGeneratorMapping.keySet()) {
                    DataGenerator referencedGenerator = dataGeneratorMapping.get(validDataSet);
                    boolean isReservedVCellPrefix = validDataSet.getId().startsWith("__vcell_reserved_data_set_prefix__");
                    ValueHolder<LazySBMLNonSpatialDataAccessor> dataHolder = organizedNonSpatialResults.get(referencedGenerator);


                    boolean timeAlreadyIncluded = false;
                    int numTrials = dataHolder.listOfResultSets.size();
                    for(int i = 0; i < numTrials; i++) {
                        if (timeAlreadyIncluded) break;
                        if (validDataSet.getId().contains("time_")) timeAlreadyIncluded = true;
                        LazySBMLNonSpatialDataAccessor data = dataHolder.listOfResultSets.get(i);

                        String formattedId = isReservedVCellPrefix ? "VCell::" + validDataSet.getId().substring(34) : validDataSet.getId();
                        sb.append(RunUtils.generateCsvItem(formattedId, ',', false, i, numTrials));
                        sb.append(RunUtils.generateCsvItem(validDataSet.getLabel(), ',', true, i, numTrials));
                        String referencedGeneratorName = referencedGenerator.getName() == null ? "" : referencedGenerator.getName();
                        sb.append(RunUtils.generateCsvItem(referencedGeneratorName.isEmpty() ? referencedGenerator.getId() : referencedGenerator.getName(), ',', true, i, numTrials));
                        String[] dataStrings = Arrays.stream(data.getData().data()).boxed().map(String::valueOf).toArray(String[]::new);
                        sb.append(String.join(",", dataStrings)).append('\n');
                    } // end of trials loop
                } // end of dataset			

                if (sb.isEmpty()) {
                    if (logger.isDebugEnabled()) logger.info("no csv file for report {}", sedmlReport.getId());
                    return reportsHash;
                }
                File f = new File(outDirForCurrentSedml, sedmlReport.getId() + ".csv");
                PrintWriter out = new PrintWriter(f);
                out.print(sb);
                out.flush();
                out.close();
                if (logger.isDebugEnabled()) logger.info("created csv file for report {}: {}", sedmlReport.getId(), f.getAbsolutePath());
                reportsHash.put(sedmlReport.getId(), f);
            } catch (Exception e) {
                throw new RuntimeException("CSV generation failed: " + e.getMessage(), e);
            }
        }
        return reportsHash;
    }

    private static String generateCsvItem(String desiredEntry, char endChar, boolean isLabel, int setNum, int maxSets){
        String safeEntry = desiredEntry.contains(",") ? '"' + desiredEntry  + '"' : desiredEntry; // Handling row labels that contains ","
        String roundNum = maxSets == 0 ? "" : isLabel ? "(" + setNum + ")" : "_" + setNum + "_";
        return String.format("%s%s%c", safeEntry, roundNum, endChar);
    }


    public static void zipResFiles(File dirFile) throws IOException {
        FileInputStream fileInputstream;
        FileOutputStream fileOutputStream;
        ZipOutputStream zipOutputStream;
        ArrayList<File> srcFiles;
        String relativePath;
        ZipEntry zipEntry;

        // TODO: Add SED-ML name as base dirFile to avoid zipping all available CSV, PDF
        // Map for naming to extension
        Map<String, String> extensionListMap = new HashMap<String, String>() {{
            put("csv", "reports.zip");
            put("pdf", "plots.zip");
        }};

        for (String ext : extensionListMap.keySet()) {
            srcFiles = listFilesForFolder(dirFile, ext);

            if (srcFiles.isEmpty()) {
                if (logger.isDebugEnabled()) logger.warn("No {} files found, skipping archiving `{}` files", ext.toUpperCase(), extensionListMap.get(ext));
            } else {
                fileOutputStream = new FileOutputStream(Paths.get(dirFile.toString(), extensionListMap.get(ext)).toFile());
                zipOutputStream = new ZipOutputStream(fileOutputStream);
                if (!srcFiles.isEmpty() && logger.isDebugEnabled()) logger.info("Archiving resultant {} files to `{}`.", ext.toUpperCase(), extensionListMap.get(ext));
                for (File srcFile : srcFiles) {
                    fileInputstream = new FileInputStream(srcFile);
                    // get relative path

                    relativePath = dirFile.toPath().relativize(srcFile.toPath()).toString();
                    zipEntry = new ZipEntry(relativePath);
                    zipOutputStream.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fileInputstream.read(bytes)) >= 0) {
                        zipOutputStream.write(bytes, 0, length);
                    }
                    fileInputstream.close();
                }
                zipOutputStream.close();
                fileOutputStream.close();
            }
        }
    }

    public static String getTempDir() throws IOException {
        String tempPath = String.valueOf(java.nio.file.Files.createTempDirectory(
            RunUtils.VCELL_TEMP_DIR_PREFIX + UUID.randomUUID()).toAbsolutePath());
        logger.trace("TempPath Created: " + tempPath);
        return tempPath;
    }

    public static ArrayList<File> listFilesForFolder(File dirPath, String extensionType) {
        File dir = new File(String.valueOf(dirPath));
        String[] extensions = new String[]{extensionType};
        List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
        return new ArrayList<>(files);
    }


    // Breakline
    public static void drawBreakLine(String breakString, int times) {
        if (logger.isInfoEnabled()) System.out.println(breakString + StringUtils.repeat(breakString, times));
        //logger.info(breakString + StringUtils.repeat(breakString, times));
    }


    public static boolean removeAndMakeDirs(File f) {
        if (!f.exists()) return f.mkdirs();
        String filePath = "";
        try {
            filePath += f.getCanonicalPath();
            org.vcell.util.FileUtils.deleteDirectoryContents(f, true, null);
        } catch (IOException e) {
            // if we got to here, we've tried to clear the contents of a directory a failed.
            // we don't throw an exception up the stack here for two reasons:
            // 1) deletion of files differs on different OS. Punishing users for their OS or their understanding thereof seems wrong.
            // 2) the user may be using software that is necessarily holding resources; by blocking on failed deletion, we are
            //      denying the potential use cases of VCell.
            String msg = "File '" + filePath + "' could not be deleted!";
            logger.error(msg, e);
            return false;
        }
        return true;
    }

    public static void createCSVFromODEResultSet(ODESolverResultSet resultSet, File f, boolean shouldTranspose) throws ExpressionException {
        ColumnDescription[] descriptions = resultSet.getColumnDescriptions();
        Map<String, List<Double>> resultsMapping = new LinkedHashMap<>();

        for (int i = 0; i < descriptions.length; i++){
            resultsMapping.put(descriptions[i].getDisplayName(), Arrays.stream(resultSet.extractColumn(i)).boxed().toList());
        }

        try (PrintWriter out = new PrintWriter(f)) {
            out.print(RunUtils.formatCSVContents(resultsMapping, !shouldTranspose));
            out.flush();
        } catch (FileNotFoundException e) {
            logger.error("Unable to find path, failed with err: " + e.getMessage(), e);
        }
    }

    public static <T> String formatCSVContents(Map<String, List<T>> csvContents, boolean organizeDataVertically){
        if (!(csvContents instanceof LinkedHashMap<String, List<T>>))
            logger.warn("Warning; using a non-linked hash map will result in random ordering of lines!");
        List<List<String>> csvLines = new ArrayList<>();
        int numTopics = csvContents.size();
        int maxNumValuesPerTopic = 0;
        for (List<T> values : csvContents.values())
            if (values.size() > maxNumValuesPerTopic)
                maxNumValuesPerTopic = values.size();

        // Initialize with empties
        for (int i = 0; i < (organizeDataVertically ? maxNumValuesPerTopic + 1 : numTopics); i++){
            csvLines.add(new ArrayList<>());
        }

        // Fill out lines
        List<String> topics = new ArrayList<>(csvContents.keySet());
        if (organizeDataVertically){
            for (String topic : topics) csvLines.get(0).add(topic);
        } else {
            for (int topicNum = 0; topicNum < topics.size(); topicNum++){
                csvLines.get(topicNum).add(topics.get(topicNum));
            }
        }
        for (int topicNum = 0; topicNum < topics.size(); topicNum++){
            String topic = topics.get(topicNum);
            List<T> data = csvContents.get(topic);

            for (int i = 0; i < maxNumValuesPerTopic; i++){
                String value = i < data.size() ? data.get(i).toString() : "";
                csvLines.get(organizeDataVertically ? i + 1 : topicNum).add(value);
            }
        }

        // Build CSV
        StringBuilder sb = new StringBuilder();
        for (List<String> row : csvLines){
            for (String value : row){
                sb.append(value).append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(",")).append("\n");
        }

        return sb.deleteCharAt(sb.lastIndexOf("\n")).toString();
    }

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    public static void removeIntermediarySimFiles(File path) {
        if (!path.isDirectory()) throw new IllegalArgumentException("Provided path does not lead to a directory!");
        File[] files = path.listFiles();
        for (File f : files) {
            if (f.getName().endsWith(".csv")) continue;
            f.delete();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean containsExtension(String folder, String ext) {
        GenericExtensionFilter filter = new GenericExtensionFilter(ext);
        File dir = new File(folder);
        return dir.isDirectory() && dir.list(filter).length > 0;
    }

    private static List<String> getListOfVariableNames(DataIdentifier... dataIDArr){
        List<String> variableNames = new ArrayList<>();

        for (DataIdentifier dataId : dataIDArr)
            if (dataId.getVariableType().getType() == VariableType.VOLUME.getType()) // maybe convert to a .equals()??
                variableNames.add(dataId.getName());

        return variableNames;
    }

    private static void exportDocument(ExportServiceImpl exportServiceImpl, DataServerImpl dataServerImpl, OutputContext outputContext,
                                       ExportSpecs exportSpecs, VCSimulationDataIdentifier vcId, File outputFilePointer)
            throws DataAccessException, IOException {
        Collection<ExportOutput > exportOutputs;

        FileDataContainerManager fileDataContainerManager = new FileDataContainerManager();
        ASCIIExporter asciiExporter = new ASCIIExporter(exportServiceImpl.getEventController());
        JobRequest jobRequest = JobRequest.createExportJobRequest(vcId.getOwner());

        try {
            exportOutputs = asciiExporter.makeASCIIData(outputContext, jobRequest, vcId.getOwner(), dataServerImpl, exportSpecs, fileDataContainerManager);
        } catch (DataAccessException | IOException e) {
            logger.error(e);
            throw e;
        }

        Iterator<ExportOutput> iterator = exportOutputs.iterator();
        ExportOutput exportOutput = iterator.next();

        // Takes data in fileDataContainerManager and puts it to an HDF5 file?
        if(((ASCIISpecs)exportSpecs.getFormatSpecificSpecs()).isHDF5()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            exportOutput.writeDataToOutputStream(baos, fileDataContainerManager);//Get location of temp HDF5 file
            File tempHDF5File = new File(baos.toString());
            Files.copy(tempHDF5File, outputFilePointer);
            tempHDF5File.delete();
        }
    }

    private static ExportSpecs getExportSpecs(OutputContext outputContext, User user, DataServerImpl dataServerImpl, VCSimulationDataIdentifier vcId,
                                       DataSetControllerImpl dsControllerImpl, cbit.vcell.solver.Simulation vcellSim, int jobIndex,
                                       VCSimulationIdentifier vcSimID, SimulationContext simContext) throws DataAccessException {

        PDEDataContext pdeDataContext = new ServerPDEDataContext(outputContext, user, dataServerImpl, vcId);
        List<String> variableNames = RunUtils.getListOfVariableNames(pdeDataContext.getDataIdentifiers());
        VariableSpecs variableSpecs = new VariableSpecs(variableNames, VARIABLE_MULTI);

        double[] dataSetTimes = dsControllerImpl.getDataSetTimes(vcId);
        TimeSpecs timeSpecs = new TimeSpecs(0,dataSetTimes.length-1, dataSetTimes, ExportEnums.TimeMode.TIME_RANGE);
        GeometrySpecs geometrySpecs = new GeometrySpecs(null, 2, 0, ExportEnums.GeometryMode.GEOMETRY_FULL);

        // String simulationName,VCSimulationIdentifier vcSimulationIdentifier,ExportParamScanInfo exportParamScanInfo
        ExportParamScanInfo exportParamScanInfo = ExportParamScanInfo.getParamScanInfo(vcellSim,jobIndex);
        SimNameSimDataID snsdi= new SimNameSimDataID(vcellSim.getName(), vcSimID, exportParamScanInfo);
        SimNameSimDataID[] simNameSimDataIDs = { snsdi };

        FormatSpecificSpecs formatSpecificSpecs = new ASCIISpecs(simNameSimDataIDs, ExportEnums.ExportableDataType.PDE_VARIABLE_DATA,
                ExportFormat.CSV, ASCIISpecs.CsvRoiLayout.var_time_val, true, false);

        return new ExportSpecs(vcId, ExportFormat.HDF5, variableSpecs, timeSpecs, geometrySpecs,
                formatSpecificSpecs, vcellSim.getName(), simContext.getBioModel().getName() + ":" + simContext.getName());
    }

}
