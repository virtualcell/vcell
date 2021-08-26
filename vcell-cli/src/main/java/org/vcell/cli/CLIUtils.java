package org.vcell.cli;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jlibsedml.*;
import org.jlibsedml.execution.IXPathToVariableIDResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.vcell.stochtest.TimeSeriesMultitrialData;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//import java.nio.file.Files;

public class CLIUtils {

    // Docker hardcode path
    // Note: Docker Working Directory and Singularity working directory works in different way.
    // Those paths are hardcoded because to run python code from a submodule path
    // Assuming Singularity image runs on HPC CRBMAPI service user
    private static final Path currentWorkingDir = Paths.get("").toAbsolutePath();
    private static final Path homeDir = Paths.get(currentWorkingDir.normalize().toString());
    // user.dir is not working for windows
//    private static final Path homeDir = Paths.get(String.valueOf(System.getProperty("user.dir")));
    private static final Path workingDirectory = Paths.get("/usr/local/app/vcell/installDir");
    // Submodule path for VCell_CLI_UTILS
    private static final Path utilPath = Paths.get(workingDirectory.toString(), "python", "vcell_cli_utils");
    private static final Path cliUtilPath = Paths.get(utilPath.toString(), "vcell_cli_utils");
    private static final Path cliPath = Paths.get(cliUtilPath.toString(), "cli.py");
    private static final Path statusPath = Paths.get(cliUtilPath.toString(), "status.py");


    // Absolute Submodule path for VCell_CLI_UTILS
//    private static final Path utilPath = Paths.get(new File("submodules", "vcell_cli_utils").getAbsolutePath());
//    private static final Path cliUtilPath = Paths.get(utilPath.toString(), "vcell_cli_utils");
//    private static final Path cliPath = Paths.get(cliUtilPath.toString(), "cli.py");
//    private static final Path statusPath = Paths.get(cliUtilPath.toString(), "status.py");

    // Supported platforms
    public static boolean isWindowsPlatform = OperatingSystemInfo.getInstance().isWindows();
    public static boolean isMacPlatform = OperatingSystemInfo.getInstance().isMac();
    public static boolean isLinuxPlatform = OperatingSystemInfo.getInstance().isLinux();

    public static final String python = isWindowsPlatform ? "python" : "python3";
    public static final String pip = isWindowsPlatform ? "pip" : "pip3";


    // TODO: Implement this to remove System properties dynamically from Run time, Remove hardcoded from docker_run.sh
    private static void removeCliSystemProperties() throws IOException {
        if (System.getProperty("vcell.cli").equals("true")) {
            PropertyLoader.loadProperties(REQUIRED_CLIENT_PROPERTIES);
            for (int i = 0; i <= NOT_REQUIRED_LOCAL_CLI_PROPERTIES.length; i++)
                PropertyLoader.loadProperties((String[]) ArrayUtils.remove(NOT_REQUIRED_LOCAL_CLI_PROPERTIES, i));
        }
    }

    private static final String[] REQUIRED_CLIENT_PROPERTIES = {
            PropertyLoader.installationRoot
    };

    private static final String[] NOT_REQUIRED_LOCAL_CLI_PROPERTIES = {
            PropertyLoader.primarySimDataDirInternalProperty,
            PropertyLoader.secondarySimDataDirInternalProperty,
            PropertyLoader.dbPasswordValue,
            PropertyLoader.dbUserid,
            PropertyLoader.dbDriverName,
            PropertyLoader.dbConnectURL,
            PropertyLoader.vcellServerIDProperty,
            PropertyLoader.mongodbDatabase,
            PropertyLoader.mongodbHostInternal,
            PropertyLoader.mongodbPortInternal

    };


    // Simulation Status enum
    public enum Status {
        RUNNING("Simulation still running"),
        SKIPPED("Simulation skipped"),
        SUCCEEDED("Simulation succeeded"),
        FAILED("Simulation failed");

        Status(String desc) {
        }
    }


    // Breakline
    public static void drawBreakLine(String breakString, int times) {
        System.out.println(breakString + StringUtils.repeat(breakString, times));
    }

    public CLIUtils() throws IOException {

    }

    public static boolean removeDirs(File f) {
        try {
            deleteRecursively(f);
        } catch (IOException ex) {
            System.err.println("Failed to delete the file: " + f);
            return false;
        }
        return true;
    }

    public static boolean makeDirs(File f) {
        if (f.exists()) {
            boolean isRemoved = removeDirs(f);
            if (!isRemoved)
                return false;
        }
        return f.mkdirs();
    }

    private static void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : Objects.requireNonNull(f.listFiles())) {
                deleteRecursively(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

    public static void saveTimeSeriesMultitrialDataAsCSV(TimeSeriesMultitrialData data, File outDir) {
        File outFile = Paths.get(outDir.toString(), data.datasetName + ".csv").toFile();
        int numberOfRows = data.times.length;
        int numberOfVariables = data.varNames.length;
        // Headers for CSV
        ArrayList<String> headersList = new ArrayList<>();
        headersList.add("times");
        Collections.addAll(headersList, data.varNames);

        // Complete rows for CSV
        ArrayList<ArrayList<Double>> allRows = new ArrayList<>();

        for (int rowCounter = 0; rowCounter < numberOfRows; rowCounter++) {
            ArrayList<Double> row = new ArrayList<>();
            row.add(data.times[rowCounter]);

            for (int varCounter = 0; varCounter < numberOfVariables; varCounter++) {
                row.add(data.data[varCounter][rowCounter][0]);
            }

            allRows.add(row);

        }

        // Writing CSV in string buffer
        StringBuilder headersBuilder = new StringBuilder();

        for (String headerName : headersList) {
            headersBuilder.append(headerName);
            headersBuilder.append(",");
        }


        String headers = headersBuilder.replace(headersBuilder.length() - 1, headersBuilder.length(), "\n").toString();

        StringBuilder allRowsBuilder = new StringBuilder(headers);

        for (ArrayList<Double> rowValues : allRows) {
            StringBuilder rowBuilder = new StringBuilder();
            for (Double val : rowValues) {
                rowBuilder.append(val);
                rowBuilder.append(",");
            }
            allRowsBuilder.append(rowBuilder.replace(rowBuilder.length() - 1, rowBuilder.length(), "\n").toString());
        }

        String csvAsString = allRowsBuilder.toString();

        try {
            PrintWriter out = new PrintWriter(outFile);
            out.print(csvAsString);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public static void createCSVFromODEResultSet(ODESolverResultSet resultSet, File f) throws ExpressionException {
        ColumnDescription[] descriptions = resultSet.getColumnDescriptions();
        StringBuilder sb = new StringBuilder();


        int numberOfColumns = descriptions.length;
        int numberOfRows = resultSet.getRowCount();

        double[][] dataPoints = new double[numberOfColumns][];
        // Write headers
        for (ColumnDescription description : descriptions) {
            sb.append(description.getDisplayName());
            sb.append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("\n");


        // Write rows
        for (int i = 0; i < numberOfColumns; i++) {
            dataPoints[i] = resultSet.extractColumn(i);
        }

        for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
            for (int colNum = 0; colNum < numberOfColumns; colNum++) {
                sb.append(dataPoints[colNum][rowNum]);
                sb.append(",");
            }

            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("\n");
        }


        try {
            PrintWriter out = new PrintWriter(f);
            out.print(sb.toString());
            out.flush();
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find path, failed with err: " + e.getMessage());
        }

    }

    public static void removeIntermediarySimFiles(File path) {
        File[] files = path.listFiles();
        for (File f : files) {
            if (f.getName().endsWith(".csv")) {
                // Do nothing
                continue;
            } else {
                f.delete();
            }
        }
    }

    public static HashMap<String, File> generateReportsAsCSV(SedML sedml, HashMap<String, ODESolverResultSet> resultsHash, File outDirForCurrentSedml, String outDir, String sedmlLocation) {
        // finally, the real work
        HashMap<String, File> reportsHash = new HashMap<>();
        List<Output> ooo = sedml.getOutputs();
        for (Output oo : ooo) {
            if (!(oo instanceof Report)) {
                System.out.println("Ignoring unsupported output `" + oo.getId() + "` while CSV generation.");
            } else {
                System.out.println("Generating report `" + oo.getId() +"`.");
                try {
                    StringBuilder sb = new StringBuilder();

                    // we go through each entry (dataset) in the list of datasets
                    // for each dataset, we use the data reference to obtain the data generator
                    // ve get the list of variables associated with the data reference
                    //   each variable has an id (which is the data reference above, the task and the sbml symbol urn
                    //   for each variable we recover the task, from the task we get the sbml model
                    //   we search the sbml model to find the vcell variable name associated with the urn

                    List<DataSet> datasets = ((Report) oo).getListOfDataSets();
                    for (DataSet dataset : datasets) {
                        DataGenerator datagen = sedml.getDataGeneratorWithId(dataset.getDataReference());
                        ArrayList<String> varIDs = new ArrayList<>();
                        assert datagen != null;
                        ArrayList<Variable> vars = new ArrayList<>(datagen.getListOfVariables());
                        int mxlen = 0;
                        boolean supportedDataset = true;
                        // get target values
                        HashMap values = new HashMap<Variable, double[]>();
                        for (Variable var : vars) {
                            AbstractTask task = sedml.getTaskWithId(var.getReference());
                            Model model = sedml.getModelWithId(task.getModelReference());
                            Simulation sim = sedml.getSimulation(task.getSimulationReference());
                            IXPathToVariableIDResolver variable2IDResolver = new SBMLSupport();
                            // must get variable ID from SBML model
                            String sbmlVarId = "";
                            if (var.getSymbol() != null) {
                                // it is a predefined symbol
                                sbmlVarId = var.getSymbol().name();
                                // translate SBML official symbols
                                // TIME is t, etc.
                                if ("TIME".equals(sbmlVarId)) {
                                    // this is VCell reserved symbold for time
                                    sbmlVarId = "t";
                                }
                                // TODO
                                // check spec for other symbols
                            } else {
                                // it is an XPATH target in model
                                String target = var.getTarget();
                                sbmlVarId = variable2IDResolver.getIdFromXPathIdentifer(target);
                            }

                            if (task instanceof RepeatedTask) {
                                supportedDataset = false;
                            } else {
                                varIDs.add(var.getId());
                                assert task != null;
            					if(sim instanceof UniformTimeCourse) {
            						// we want to keep the last outputNumberOfPoints only
            						int outputNumberOfPoints = ((UniformTimeCourse) sim).getNumberOfPoints();
            						double outputStartTime = ((UniformTimeCourse) sim).getOutputStartTime();
            						if(outputStartTime > 0) {
            							
                                        ODESolverResultSet results = resultsHash.get(task.getId());
                                        int column = results.findColumn(sbmlVarId);
                                        double[] tmpData = results.extractColumn(column);
                                        double[] data = new double[outputNumberOfPoints+1];
                                        for(int i=tmpData.length-outputNumberOfPoints-1, j=0; i<tmpData.length; i++, j++) {
                                        	data[j] = tmpData[i];
                                        }
                                        
                                        mxlen = Integer.max(mxlen, data.length);
                                        values.put(var, data);
                                        
            						} else {
                                        ODESolverResultSet results = resultsHash.get(task.getId());
                                        int column = results.findColumn(sbmlVarId);
                                        double[] data = results.extractColumn(column);
                                        mxlen = Integer.max(mxlen, data.length);
                                        values.put(var, data);
            						}

            					} else {
            						System.err.println("only uniform time course simulations are supported");
            					}

                            }

                            CLIUtils.updateDatasetStatusYml(sedmlLocation, oo.getId(), dataset.getId(), Status.SUCCEEDED, outDir);
                            CLIUtils.updateTaskStatusYml(sedmlLocation, task.getId(), Status.SUCCEEDED, outDir);
                        }
                        if (!supportedDataset) {
                            System.err.println("Dataset " + dataset.getId() + " references unsupported RepeatedTask and is being skipped");
                            continue;
                        }
                        //get math
                        String mathMLStr = datagen.getMathAsString();
                        Expression expr = new Expression(mathMLStr);
                        SymbolTable st = new SimpleSymbolTable(varIDs.toArray(new String[vars.size()]));
                        expr.bindExpression(st);
                        //compute and write result, padding with NaN if unequal length or errors
                        double[] row = new double[vars.size()];
                        
                        // Handling row labels that contains ","
                        if (dataset.getId().contains(",")) sb.append("\"" + dataset.getId() + "\"").append(",");
                        else sb.append(dataset.getId()).append(",");
                        if (dataset.getLabel().contains(",")) sb.append("\"" + dataset.getLabel() + "\"").append(",");
                        else sb.append(dataset.getLabel()).append(",");
                        sb.append("").append(",");	// no name
                        
                        for (int i = 0; i < mxlen; i++) {
                            for (int j = 0; j < vars.size(); j++) {
                                double[] varVals = ((double[]) values.get(vars.get(j)));
                                if (i < varVals.length) {
                                    row[j] = varVals[i];
                                } else {
                                    row[j] = Double.NaN;
                                }
                            }
                            double computed = Double.NaN;
                            try {
                                computed = expr.evaluateVector(row);
                            } catch (Exception e) {
                                // do nothing, we leave NaN and don't warn/log since it could flood
                            }
                            sb.append(computed).append(",");
                        }
                        sb.deleteCharAt(sb.lastIndexOf(","));
                        sb.append("\n");
                    }
                    File f = new File(outDirForCurrentSedml, oo.getId() + ".csv");
                    System.out.println(" --- Writing file: " + oo.getId() + ".csv");
                    PrintWriter out = new PrintWriter(f);
                    out.print(sb.toString());
                    out.flush();
                    out.close();
                    reportsHash.put(oo.getId(), f);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    reportsHash.put(oo.getId(), null);
                }
            }
        }
        return reportsHash;
    }

    public static ODESolverResultSet interpolate(ODESolverResultSet odeSolverResultSet, UniformTimeCourse sedmlSim) throws ExpressionException {
        double outputStart = sedmlSim.getOutputStartTime();
        double outputEnd = sedmlSim.getOutputEndTime();

        int numPoints = sedmlSim.getNumberOfPoints() + 1;


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

    public static ProcessBuilder execShellCommand(String[] args) {
        // Setting the source and destination for subprocess standard I/O to be the same as those of the current Java process
        return new ProcessBuilder(args).inheritIO();
    }

    public static void printProcessErrors(Process process, String outString, String errString) throws InterruptedException, IOException {
        // Process printing code goes here
        process.waitFor();
        if (process.exitValue() != 0) {
            System.err.print(errString);
            InputStream errorStream = process.getErrorStream();
            int c;
            while ((c = errorStream.read()) != -1) {
                System.err.print((char)c);
            }
        } else {
            System.out.print(outString);
        }

    }

    public static int checkInstallationError() {
        String version = "--version";
        ProcessBuilder processBuilder;
        Process process;
        int exitCode = -10;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder;
        String stdOutLog;

        try {
            processBuilder = execShellCommand(new String[]{python, version});
            process = processBuilder.start();
            exitCode = process.waitFor();
            if (exitCode == 0) {
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                stringBuilder = new StringBuilder();
                while ((stdOutLog = bufferedReader.readLine()) != null) {
                    stringBuilder.append(stdOutLog);
                    // search string can be one or more...
                        if (!stringBuilder.toString().toLowerCase().startsWith("python")) System.err.println("Please check your local Python and PIP Installation, install required packages");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return exitCode;
    }


    // Ignoring biosimulator_utils warnings with -W ignore flag

    public static void genSedmlForSed2DAnd3D(String omexFilePath, String outputDir) throws IOException, InterruptedException {
        Process process = execShellCommand(new String[]{python, "-W", "ignore", cliPath.toString(), "genSedml2d3d", omexFilePath, outputDir}).start();
        printProcessErrors(process, "","Failed generating SED-ML for plot2d and 3D ");
    }

    public static void execPlotOutputSedDoc(String omexFilePath, String outputDir)  throws IOException, InterruptedException {
        Process process = execShellCommand(new String[]{python, "-W", "ignore", cliPath.toString(), "execPlotOutputSedDoc", omexFilePath, outputDir}).start();
        printProcessErrors(process, "HDF conversion successful\n","HDF conversion failed\n");
    }

    public static void convertCSVtoHDF(String omexFilePath, String outputDir) throws IOException, InterruptedException {

        // Convert CSV to HDF5
        /*
        Usage: cli.py SEDML_FILE_PATH WORKING_DIR BASE_OUT_PATH CSV_DIR <flags>
                    optional flags:        --rel_out_path | --apply_xml_model_changes |
                         --report_formats | --plot_formats | --log | --indent
        * */
        // handle exceptions here
        if (checkInstallationError() == 0) {
            Process process = execShellCommand(new String[]{python, "-W", "ignore", cliPath.toString(), "execSedDoc", omexFilePath, outputDir}).start();
            printProcessErrors(process, "HDF conversion successful\n","HDF conversion failed\n");
        }
    }

    public static void genPlotsPseudoSedml(String sedmlPath, String resultOutDir) throws IOException, InterruptedException {
        Process process = execShellCommand(new String[]{python, cliPath.toString(), "genPlotsPseudoSedml", sedmlPath, resultOutDir}).start();
        printProcessErrors(process, "","");
    }

    // Sample STATUS YML
    /*
    sedDocuments:
      BIOMD0000000912_sim.sedml:
        outputs:
          BIOMD0000000912_sim:
            dataSets:
              data_set_E: SKIPPED
              data_set_I: PASSED
              data_set_T: SKIPPED
              data_set_time: SKIPPED
            status: SKIPPED
          plot_1:
            curves:
              plot_1_E_time: SKIPPED
              plot_1_I_time: SKIPPED
              plot_1_T_time: SKIPPED
            status: SKIPPED
        status: SUCCEEDED
        tasks:
          BIOMD0000000912_sim:
            status: SKIPPED
    status: SUCCEEDED
    * */
    public static void generateStatusYaml(String omexPath, String outDir) throws IOException, InterruptedException {
        System.out.println("utilPath: " + utilPath);
        System.out.println("cliUtilPath: " + cliUtilPath);
        System.out.println("cliPath: " + cliPath);
        System.out.println("statusPath: " + statusPath);
        // Note: by default every status is being skipped
        Path omexFilePath = Paths.get(omexPath);
        /*
         USAGE:

         NAME
         status.py

         SYNOPSIS
         status.py COMMAND

         COMMANDS
         COMMAND is one of the following:

         status_yml
        */
        Process process = execShellCommand(new String[]{python, statusPath.toString(), "genStatusYaml", String.valueOf(omexFilePath), outDir}).start();
        printProcessErrors(process, "","Failed generating status YAML\n");
    }

    public static void updateTaskStatusYml(String sedmlName, String taskName, Status taskStatus, String outDir) throws IOException, InterruptedException {
        Process process = execShellCommand(new String[]{python, statusPath.toString(), "updateTaskStatus", sedmlName, taskName, taskStatus.toString(), outDir}).start();
        printProcessErrors(process, "","Failed updating task status YAML\n");

    }

    public static void finalStatusUpdate(Status simStatus, String outDir) throws IOException, InterruptedException {
        Process process = execShellCommand(new String[]{python, statusPath.toString(), "simStatus", simStatus.toString(), outDir}).start();
        printProcessErrors(process, "","");
    }

    public static void updateDatasetStatusYml(String sedmlName, String dataSet, String var, Status simStatus, String outDir) throws IOException, InterruptedException {
        Process process = execShellCommand(new String[]{python, statusPath.toString(), "updateDataSetStatus", sedmlName, dataSet, var, simStatus.toString(), outDir}).start();
        printProcessErrors(process, "","");
    }

    public static void transposeVcmlCsv(String csvFilePath) throws IOException, InterruptedException {
        Process process = execShellCommand(new String[]{python, cliPath.toString(), "transposeVcmlCsv", csvFilePath}).start();
        printProcessErrors(process, "","");
    }

    public static void genPlots(String sedmlPath, String resultOutDir) throws IOException, InterruptedException {
        Process process = execShellCommand(new String[]{python, cliPath.toString(), "genPlotPdfs", sedmlPath, resultOutDir}).start();
        printProcessErrors(process, "","");
    }


    private static ArrayList<File> listFilesForFolder(File dirPath, String extensionType) {
        File dir = new File(String.valueOf(dirPath));
        String[] extensions = new String[]{extensionType};
        List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
        return new ArrayList<>(files);
    }

    public static void zipResFiles(File dirPath) throws IOException {

        FileInputStream fileInputstream;
        FileOutputStream fileOutputStream;
        ZipOutputStream zipOutputStream;
        ArrayList<File> srcFiles;
        String relativePath;
        ZipEntry zipEntry;

        // TODO: Add SED-ML name as base dirPath to avoid zipping all available CSV, PDF
        // Map for naming to extension
        Map<String, String> extensionListMap = new HashMap<String, String>() {{
            put("csv", "reports.zip");
            put("pdf", "plots.zip");
        }};

        for (String ext : extensionListMap.keySet()) {
            srcFiles = listFilesForFolder(dirPath, ext);

            if (srcFiles.size() == 0) {
                System.err.println("No " + ext.toUpperCase() + " files found, skipping archiving `" + extensionListMap.get(ext) + "` files");
            } else {
                fileOutputStream = new FileOutputStream(Paths.get(dirPath.toString(), extensionListMap.get(ext)).toFile());
                zipOutputStream = new ZipOutputStream(fileOutputStream);
                if (srcFiles.size() != 0) System.out.println("Archiving resultant " + ext.toUpperCase() + " files to `" + extensionListMap.get(ext) + "`.");
                for (File srcFile : srcFiles) {

                    fileInputstream = new FileInputStream(srcFile);

                    // get relative path
                    relativePath = dirPath.toURI().relativize(srcFile.toURI()).toString();
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

    public String getTempDir() throws IOException {
        String tempPath = String.valueOf(java.nio.file.Files.createTempDirectory("vcell_temp_" + UUID.randomUUID().toString()).toAbsolutePath());
        System.out.println("TempPath Created: " + tempPath);
        return tempPath;
    }
}
