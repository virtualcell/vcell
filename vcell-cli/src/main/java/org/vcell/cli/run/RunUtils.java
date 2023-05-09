package org.vcell.cli.run;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.data.SimResultsViewer;
import cbit.vcell.export.server.*;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.VariableType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jlibsedml.*;
import org.jlibsedml.DataSet;
import org.jlibsedml.Simulation;
import org.jlibsedml.execution.IXPathToVariableIDResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.vcell.cli.CLIUtils;
import org.vcell.stochtest.TimeSeriesMultitrialData;
import org.vcell.util.DataAccessException;
import org.vcell.util.GenericExtensionFilter;
import org.vcell.util.document.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    /**
     * Exports the data of a spatial simulation into hdf5 in a VCell-friendly organization.
     * ...
     * Note that this function is extremely dense and confusing. It mostly works in side effects and preparations,
     * but the end result is correct (at least, that's what we believe)
     * @param vcellSimJob The sim job to get the data from
     * @param userDir directory to get the user info with
     * @param outputFilePointer where to put the final hdf5 file.
     * @throws DataAccessException if the necessary data could not be accessed.
     * @throws IOException if the system encountered some trouble with file or system IO
     */
    public static void exportPDE2HDF5(SimulationJob vcellSimJob, File userDir, File outputFilePointer) throws DataAccessException, IOException {
        // A ton of initialization
        int jobIndex = vcellSimJob.getJobIndex();
        User user = new User(userDir.getName(), null);
        cbit.vcell.solver.Simulation vcellSim = vcellSimJob.getSimulation();
        ExportServiceImpl exportServiceImpl = new ExportServiceImpl();

    	SimulationContext simContext = (SimulationContext)vcellSim.getSimulationOwner();
        VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(vcellSim.getKey(), user);
        DataSetControllerImpl dsControllerImpl = new DataSetControllerImpl(null, userDir.getParentFile(), null);

        DataServerImpl dataServerImpl = new DataServerImpl(dsControllerImpl, exportServiceImpl);
        OutputContext outputContext = new OutputContext(simContext.getOutputFunctionContext().getOutputFunctionsList().toArray(new AnnotatedFunction[0]));
        VCSimulationDataIdentifier vcId = new VCSimulationDataIdentifier(vcSimID, jobIndex);

        // Create the export specifications, and prepare to export to a file
        ExportSpecs exportSpecs = RunUtils.getExportSpecs(outputContext, user, dataServerImpl, vcId,
                dsControllerImpl, vcellSim, jobIndex, vcSimID, simContext);

        // Export to a temp file, then copy the file over to the correct location
        RunUtils.exportDocument(exportServiceImpl, dataServerImpl, outputContext, exportSpecs, vcId, outputFilePointer);
    }

    public static HashMap<String, File> generateReportsAsCSV(SedML sedml, Map<TaskJob, ODESolverResultSet> resultsHash, File outDirForCurrentSedml, String outDir, String sedmlLocation) throws DataAccessException, IOException {
        // finally, the real work
        HashMap<String, File> reportsHash = new HashMap<>();
        List<Output> ooo = sedml.getOutputs();
        for (Output oo : ooo) {
            // We only want Reports
            if (!(oo instanceof Report)) {
                logger.info("Ignoring unsupported output `" + oo.getId() + "` while CSV generation.");
                continue;
            }

            StringBuilder sb = new StringBuilder();

            logger.info("Generating report `" + oo.getId() +"`.");
            /*
             * we go through each entry (dataset) in the list of datasets
             * for each dataset, we use the data reference to obtain the data generator
             * we get the list of variables associated with the data reference
             * * each variable has an id (which is the data reference above, the task and the sbml symbol urn
             * * for each variable we recover the task, from the task we get the sbml model
             * * we search the sbml model to find the vcell variable name associated with the urn
             */
            try {
                List<DataSet> datasets = ((Report) oo).getListOfDataSets();
                for (DataSet dataset : datasets) {
                    DataGenerator datagen = sedml.getDataGeneratorWithId(dataset.getDataReference()); assert datagen != null;
                    List<String> varIDs = new ArrayList<>();     
                    List<Variable> vars = new ArrayList<>(datagen.getListOfVariables());
                    Map<Variable, List<double[]> > values = new HashMap<>();
                    int mxlen = 0;
//                        boolean supportedDataset = true;
                    
                    // get target values
                    for (Variable var : vars) {
                        AbstractTask task = sedml.getTaskWithId(var.getReference());
                        Simulation sedmlSim = null;
                        Task actualTask = null;

                        if(task instanceof RepeatedTask) { // We assume that we can never have a sequential repeated task at this point, we check for that in SEDMLImporter
                            RepeatedTask rt = (RepeatedTask)task;
                            
                            if (!rt.getResetModel() || rt.getSubTasks().size() != 1) {
                                logger.warn("Sequential RepeatedTask not yet supported, task " + task.getElementName() + " is being skipped");
                                continue;
                   			}
                            
                            AbstractTask referredTask;
                            do { // find the actual Task and extract the simulation
                                SubTask st = rt.getSubTasks().entrySet().iterator().next().getValue(); // single subtask
                                String taskId = st.getTaskId();
                                referredTask = sedml.getTaskWithId(taskId);
                                if (referredTask instanceof RepeatedTask) rt = (RepeatedTask)referredTask;
                            } while (referredTask instanceof RepeatedTask);
                            actualTask = (Task)referredTask;
                            sedmlSim = sedml.getSimulation(actualTask.getSimulationReference());
                        } else {
                            actualTask = (Task)task;
                            sedmlSim = sedml.getSimulation(task.getSimulationReference());
                        }

                        // Confirm uniform time
                        if (!(sedmlSim instanceof UniformTimeCourse)){
                            logger.error("only uniform time course simulations are supported");
                            continue;
                        }

                        IXPathToVariableIDResolver variable2IDResolver = new SBMLSupport(); // must get variable ID from SBML model
                        String sbmlVarId = "";
                        if (var.getSymbol() != null) { // it is a predefined symbol
                            // translate SBML official symbols
                            // TODO: check spec for other symbols

                            // Time
                            sbmlVarId = var.getSymbol().name();      
                            if ("TIME".equals(sbmlVarId)) 
                                sbmlVarId = "t";// this is VCell reserved symbold for time
                        } else {// it is an XPATH target in model
                            String target = var.getTarget();
                            sbmlVarId = variable2IDResolver.getIdFromXPathIdentifer(target);
                        }

                        // Get task job(s)
                        List<TaskJob> taskJobs = new ArrayList<>();
                        for (Map.Entry<TaskJob, ODESolverResultSet> entry : resultsHash.entrySet()) {
                            TaskJob taskJob = entry.getKey();
                            ODESolverResultSet value = entry.getValue();
                            if(value != null && taskJob.getTaskId().equals(task.getId())) {
                                taskJobs.add(taskJob);
                                if (!(task instanceof RepeatedTask)) break; // Only have one entry for non-repeated tasks
                            }
                        }
                        if (taskJobs.isEmpty()) continue; 
                        
                        varIDs.add(var.getId());
                        
                        // we want to keep the last outputNumberOfPoints only
                        int outputNumberOfPoints = ((UniformTimeCourse) sedmlSim).getNumberOfPoints();
                        double outputStartTime = ((UniformTimeCourse) sedmlSim).getOutputStartTime();
                        List<double[]> variablesList = new ArrayList<>();

                        for (TaskJob taskJob : taskJobs){
                            // key format in resultsHash is taskId + "_" + simJobId
                            // ex: task_0_0_0 where the last 0 is the simJobId (always 0 when no parameter scan)
                            double[] data;
                            ODESolverResultSet results = resultsHash.get(taskJob); // hence the added "_0"
                            if (results==null) continue;
                            int column = results.findColumn(sbmlVarId);

                            if (outputStartTime > 0){
                                data = new double[outputNumberOfPoints+1];
                                double[] tmpData = results.extractColumn(column);
                                for(int i=tmpData.length-outputNumberOfPoints-1, j=0; i<tmpData.length; i++, j++) {
                                    data[j] = tmpData[i];
                                }
                            } else {
                                data = results.extractColumn(column);
                            }

                            mxlen = Integer.max(mxlen, data.length);
                            if(!values.containsKey(var)) {		// this is the first double[]
                                variablesList.add(data);
                                values.put(var, variablesList);
                            } else {
                                List<double[]> variablesListTemp = values.get(var);
                                variablesListTemp.add(data);
                                values.put(var, variablesListTemp);
                            }
                        }
                    }
                    if (varIDs.isEmpty()) continue;
                    
                    String mathMLStr = datagen.getMathAsString(); //get math
                    Expression expr = new Expression(mathMLStr);
                    SymbolTable st = new SimpleSymbolTable(varIDs.toArray(new String[vars.size()]));
                    expr.bindExpression(st);

                    Variable firstVar = vars.get(0);
                    List<double[]> v = values.get(firstVar);
                    int overridesCount = v!=null ? v.size() : 0;
                    for(int k=0; k < overridesCount; k++) {

                        if(k>0 && dataset.getId().contains("time_")) {
                            continue;
                        }
                        //compute and write result, padding with NaN if unequal length or errors
                        double[] row = new double[vars.size()];

                        // Handling row labels that contains ","
                        if (dataset.getId().startsWith("__vcell_reserved_data_set_prefix__")) {		// also used in cli.py
                            if (dataset.getLabel().contains(",")) sb.append("\"" + dataset.getLabel() + "\"").append(",");
                            else sb.append(dataset.getLabel()).append(",");
                        } else {
                            if (dataset.getId().contains(",")) sb.append("\"" + dataset.getId() + "\"").append(",");
                            else sb.append(dataset.getId()).append(",");
                        }

                        if (dataset.getLabel().contains(",")) sb.append("\"" + dataset.getLabel() + "\"").append(",");
                        else sb.append(dataset.getLabel()).append(",");

                        DataGenerator dg = sedml.getDataGeneratorWithId(dataset.getDataReference());
                        if(dg != null && dg.getName() != null && !dg.getName().isEmpty()) {
                            // name may contain spaces or other things
                            sb.append("\"" + dg.getName() + "\"").append(",");
                        } else {			// dg may be null, name may be null
                            sb.append("").append(",");
                        }

                        // TODO: here was the for(k : overridesCount)
                        for (int i = 0; i < mxlen; i++) {
                            for (int j = 0; j < vars.size(); j++) {
                                //                                double[] varVals = ((double[]) values.get(vars.get(j)));
                                Variable var = vars.get(j);
                                List<double[]> variablesList = values.get(var);
                                double[] varVals = variablesList.get(k);

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
                        //	}	// here would have been the close bracket for the loop over k
                        sb.deleteCharAt(sb.lastIndexOf(","));
                        sb.append("\n");

                    }	//end of k loop
                    PythonCalls.updateDatasetStatusYml(sedmlLocation, oo.getId(), dataset.getId(), Status.SUCCEEDED, outDir);

                } // end of dataset			

                if (sb.length() > 0) {
                    File f = new File(outDirForCurrentSedml, oo.getId() + ".csv");
                    PrintWriter out = new PrintWriter(f);
                    out.print(sb.toString());
                    out.flush();
                    out.close();
                    logger.info("created csv file for report " + oo.getId() + ": " + f.getAbsolutePath());
                    reportsHash.put(oo.getId(), f);
                } else {
                    logger.info("no csv file for report " + oo.getId());
                }
            } catch (Exception e) {
                logger.error("Encountered exception: " + e.getMessage(), e);
                reportsHash.put(oo.getId(), null);
            }
        }
        return reportsHash;
    }


    public static String generateIdNamePlotsMap(SedML sedml, File outDirForCurrentSedml) {
        StringBuilder sb = new StringBuilder();
        List<Output> ooo = sedml.getOutputs();
        for (Output oo : ooo) {
            if (!(oo instanceof Report)) {
                logger.info("Ignoring unsupported output `" + oo.getId() + "` while generating idNamePlotsMap.");
            } else {
                String id = oo.getId();
                sb.append(id).append("|");	// hopefully no vcell name contains '|', so I can use it as separator
                sb.append(oo.getName()).append("\n");
                if(id.startsWith("__plot__")) {
                    id = id.substring("__plot__".length());
                    sb.append(id).append("|");	// the creation of the csv files is whimsical, we also use an id with __plot__ removed
                    sb.append(oo.getName()).append("\n");
                }
            }
        }

        File f = new File(outDirForCurrentSedml, "idNamePlotsMap.txt");
        try {
            PrintWriter out = new PrintWriter(f);
            out.print(sb.toString());
            out.flush();
            out.close();
        } catch(Exception e) {
            logger.error("Unable to create the idNamePlotsMap; " + e.getMessage(), e);
        }
        return f.toString();
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
            this.put("csv", "reports.zip");
            this.put("pdf", "plots.zip");
        }};

        for (String ext : extensionListMap.keySet()) {
            srcFiles = listFilesForFolder(dirPath, ext);

            if (srcFiles.isEmpty()) {
                logger.error("No " + ext.toUpperCase() + " files found, skipping archiving `" + extensionListMap.get(ext) + "` files");
            } else {
                fileOutputStream = new FileOutputStream(Paths.get(dirPath.toString(), extensionListMap.get(ext)).toFile());
                zipOutputStream = new ZipOutputStream(fileOutputStream);
                if (!srcFiles.isEmpty()) logger.info("Archiving resultant " + ext.toUpperCase() + " files to `" + extensionListMap.get(ext) + "`.");
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

    public static String getTempDir() throws IOException {
        String tempPath = String.valueOf(java.nio.file.Files.createTempDirectory(
            RunUtils.VCELL_TEMP_DIR_PREFIX + UUID.randomUUID().toString()).toAbsolutePath());
        logger.info("TempPath Created: " + tempPath);
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
        if (!f.exists() || CLIUtils.removeDirs(f)) return f.mkdirs(); // Short-circuiting makes this safe
        // we don't throw an exception up the stack here for two reasons:
        // 1) deletion of files differs on different OS. Punishing users for their OS or their understanding thereof seems wrong.
        // 2) the user may be using software that is necessarily holding resources; by blocking on failed deletion, we are
        //      denying the potential use cases of VCell.
        try{
            throw new Exception("File '" + f.getCanonicalPath() + "' could not be deleted!");
        } catch (Exception e){
            logger.error(e);
        }
        if (!f.mkdirs()) logger.error("Remaking the directory failed as well!");
        return false;
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

        try (PrintWriter out = new PrintWriter(f)) {
            out.print(sb);
            out.flush();
        } catch (FileNotFoundException e) {
            logger.error("Unable to find path, failed with err: " + e.getMessage(), e);
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
        PrintWriter out = null;
        try {
            out = new PrintWriter(outFile);
            out.print(csvAsString);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) out.close();
        }

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
        ASCIIExporter asciiExporter = new ASCIIExporter(exportServiceImpl);
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
        VariableSpecs variableSpecs = new VariableSpecs(variableNames, ExportConstants.VARIABLE_MULTI);

        double[] dataSetTimes = dsControllerImpl.getDataSetTimes(vcId);
        TimeSpecs timeSpecs = new TimeSpecs(0,dataSetTimes.length-1, dataSetTimes, ExportConstants.TIME_RANGE);
        GeometrySpecs geometrySpecs = new GeometryFullSpecs(null, 2, 0);

        // String simulationName,VCSimulationIdentifier vcSimulationIdentifier,ExportParamScanInfo exportParamScanInfo
        ExportSpecs.ExportParamScanInfo exportParamScanInfo = SimResultsViewer.getParamScanInfo(vcellSim,jobIndex);
        ExportSpecs.SimNameSimDataID snsdi= new ExportSpecs.SimNameSimDataID(vcellSim.getName(), vcSimID, exportParamScanInfo);
        ExportSpecs.SimNameSimDataID[] simNameSimDataIDs = { snsdi };

        FormatSpecificSpecs formatSpecificSpecs = new ASCIISpecs(simNameSimDataIDs, ExportConstants.DataType.PDE_VARIABLE_DATA,
                ExportFormat.CSV, ASCIISpecs.CsvRoiLayout.var_time_val, true, false);

        return new ExportSpecs(vcId, ExportFormat.HDF5, variableSpecs, timeSpecs, geometrySpecs,
                formatSpecificSpecs, vcellSim.getName(), simContext.getBioModel().getName() + ":" + simContext.getName());
    }

}
