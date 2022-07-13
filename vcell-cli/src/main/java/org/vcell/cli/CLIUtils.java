package org.vcell.cli;

import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportOutput;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.FileDataContainerManager;
import cbit.vcell.export.server.FormatSpecificSpecs;
import cbit.vcell.export.server.GeometrySpecs;
import cbit.vcell.export.server.JobRequest;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.server.ClientExportController;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.export.server.ASCIIExporter;
import cbit.vcell.export.server.ASCIISpecs;
import cbit.vcell.export.server.ASCIISpecs.csvRoiLayout;
import cbit.vcell.export.server.ExportSpecs.ExportParamScanInfo;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputFunctionContext;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jlibsedml.*;
import org.jlibsedml.execution.IXPathToVariableIDResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.vcell.sedml.SEDMLUtil;
import org.vcell.stochtest.TimeSeriesMultitrialData;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import com.google.common.io.Files;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.concurrent.TimeoutException;

import cbit.vcell.resource.PropertyLoader;

//import java.nio.file.Files;

public class CLIUtils {
    // Singleton Instance Variable
    private static CLIUtils singleInstance;

	// timeout for compiled solver running long jobs; default 12 hours
	//public static long EXECUTABLE_MAX_WALLCLOK_MILLIS = 600000;
	public static long EXECUTABLE_MAX_WALLCLOK_MILLIS = 0;

    // Docker hardcode path
    // Note: Docker Working Directory and Singularity working directory works in different way.
    // Those paths are hardcoded because to run python code from a submodule path
    // Assuming Singularity image runs on HPC CRBMAPI service user
    private static final Path currentWorkingDir = Paths.get("").toAbsolutePath();
    private static final Path homeDir = Paths.get(currentWorkingDir.normalize().toString());
    // user.dir is not working for windows
//    private static final Path homeDir = Paths.get(String.valueOf(System.getProperty("user.dir")));
    private static final String defaultWorkingDir = "/usr/local/app/vcell/installDir";
    private Path workingDirectory = Paths.get(defaultWorkingDir);
    // Submodule path for VCell_CLI_UTILS
    private Path utilPath = Paths.get(workingDirectory.toString(), "python", "vcell_cli_utils");
    private Path cliUtilPath = Paths.get(utilPath.toString(), "vcell_cli_utils");
    private Path cliPath = Paths.get(cliUtilPath.toString(), "cli.py");
    private Path statusPath = Paths.get(cliUtilPath.toString(), "status.py");
    private Path wrapperPath = Paths.get(cliUtilPath.toString(), "wrapper.py");


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

    // Python Process Variables
    private static Process pythonProcess; 			// hold python interpreter instance used in updateXxx...() methods
    private static OutputStreamWriter pythonOSW; 	// input channel *to* python interpreter (see above)
    private static BufferedReader pythonISB; 		// output channel ("Input Stream Buffer") *from* python interpreter (see above)

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
        FAILED("Simulation failed"),
        ABORTED("Simulation aborted");

        Status(String desc) {
        }
    }

    public void recalculatePaths() {
    	String wd = PropertyLoader.getProperty(PropertyLoader.cliWorkingDir, defaultWorkingDir);
        workingDirectory = Paths.get(wd);
        utilPath = Paths.get(workingDirectory.toString());
        cliUtilPath = Paths.get(utilPath.toString(), "vcell_cli_utils");
        cliPath = Paths.get(cliUtilPath.toString(), "cli.py");
        statusPath = Paths.get(cliUtilPath.toString(), "status.py");
        wrapperPath = Paths.get(cliUtilPath.toString(), "wrapper.py");
    }
    
    // Breakline
    public static void drawBreakLine(String breakString, int times) {
        System.out.println(breakString + StringUtils.repeat(breakString, times));
    }

    private CLIUtils(){}

    // Singleton Constructor
    public static CLIUtils getCLIUtils(){
        if (CLIUtils.singleInstance == null){
            try{
                CLIUtils.singleInstance = new CLIUtils();
                PropertyLoader.loadProperties();
                singleInstance.recalculatePaths();
                singleInstance.instantiatePythonProcess();
            } catch (IOException e){
                System.err.println("Encountered fatal IOException while creating CLIUtils; killing VCell.");
                e.printStackTrace();
                System.exit(1);
            }
        }
        return CLIUtils.singleInstance;
    }

    public static boolean removeDirs(File f) {
        try {
            CLIUtils.deleteRecursively(f);
        } catch (IOException ex) {
            System.err.println("Failed to delete the file: " + f);
            return false;
        }
        return true;
    }

    public static boolean removeAndMakeDirs(File f) {
        if (f.exists()) {
            boolean isRemoved = CLIUtils.removeDirs(f);
            if (!isRemoved)
                return false;
        }
        return f.mkdirs();
    }

    private static void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : Objects.requireNonNull(f.listFiles())) {
                CLIUtils.deleteRecursively(c);
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

    public static void exportPDE2HDF5(cbit.vcell.solver.Simulation sim, File userDir, File hdf5OutputFile) throws DataAccessException, IOException {
        
        SimulationContext sc = (SimulationContext)sim.getSimulationOwner();
        BioModel bm = sc.getBioModel();
        
    	
//        VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(sim.getKey(), sim.getSimulationInfo().getVersion().getOwner());
        User user = new User(userDir.getName(), null);
        VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(sim.getKey(), user);
        
        if(sim.getScanCount() > 1) {
        	throw new IllegalArgumentException("Parameter scans to be implemented");
        }
    	VCSimulationDataIdentifier vcId = new VCSimulationDataIdentifier(vcSimID, 0);
    	
        SpeciesContext[] species = bm.getModel().getSpeciesContexts();
        String[] variableNames = new String[species.length];
        for(int i = 0; i<species.length; i++) {
        	variableNames[i] = species[i].getName();
        }
    	VariableSpecs variableSpecs = new VariableSpecs(variableNames, ExportConstants.VARIABLE_MULTI);
    	
        DataSetControllerImpl dsControllerImpl = new DataSetControllerImpl(null, userDir.getParentFile(), null);
        double[] dataSetTimes = dsControllerImpl.getDataSetTimes(vcId);
    	TimeSpecs timeSpecs = new TimeSpecs(0,dataSetTimes.length-1, dataSetTimes, ExportConstants.TIME_RANGE);
    	//timeSpecs = new TimeSpecs(0, 0, dataSetTimes, ExportConstants.TIME_RANGE);

    	
    	
    	int geoMode = ExportConstants.GEOMETRY_FULL;
    	SpatialSelection[] selections = new SpatialSelection[0];
    	selections = null;
    	int axis = 2;
    	int sliceNumber = 0;
        GeometrySpecs geometrySpecs = new GeometrySpecs(selections, axis, sliceNumber, geoMode);
         
        ExportConstants.DataType dataType = ExportConstants.DataType.PDE_VARIABLE_DATA;
        boolean switchRowsColumns = false;
        
        // String simulationName,VCSimulationIdentifier vcSimulationIdentifier,ExportParamScanInfo exportParamScanInfo
        ExportSpecs.SimNameSimDataID snsdi= new ExportSpecs.SimNameSimDataID(sim.getName(), vcSimID, null);
        ExportSpecs.SimNameSimDataID[] simNameSimDataIDs = { snsdi };
        int[] exportMultipleParamScans = null;
        boolean isHDF5 = true;
        FormatSpecificSpecs formatSpecificSpecs = new ASCIISpecs(ExportFormat.CSV, dataType, switchRowsColumns, simNameSimDataIDs, exportMultipleParamScans, csvRoiLayout.var_time_val, isHDF5);
        
        OutputFunctionContext ofc = sc.getOutputFunctionContext();
        ArrayList<AnnotatedFunction> outputFunctionsList = ofc.getOutputFunctionsList();
        AnnotatedFunction[] af = outputFunctionsList.toArray(new AnnotatedFunction[0]);
        OutputContext outputContext = new OutputContext(af);
        ExportServiceImpl exportServiceImpl = new ExportServiceImpl();
        ASCIIExporter ae = new ASCIIExporter(exportServiceImpl);
        String contextName = bm.getName() + ":" + sc.getName();
        ExportSpecs exportSpecs = new ExportSpecs(vcId, ExportFormat.HDF5, variableSpecs, timeSpecs, geometrySpecs, formatSpecificSpecs, sim.getName(), contextName);
        DataServerImpl dataServerImpl = new DataServerImpl(dsControllerImpl, exportServiceImpl);
        FileDataContainerManager fileDataContainerManager = new FileDataContainerManager();
        
        JobRequest jobRequest = JobRequest.createExportJobRequest(vcId.getOwner());
        
        Collection<ExportOutput > eo = ae.makeASCIIData(outputContext, jobRequest, vcId.getOwner(), dataServerImpl, exportSpecs, fileDataContainerManager);
        Iterator<ExportOutput> iterator = eo.iterator();
        ExportOutput aaa = iterator.next();
        

		if(((ASCIISpecs)exportSpecs.getFormatSpecificSpecs()).isHDF5()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			aaa.writeDataToOutputStream(baos, fileDataContainerManager);//Get location of temp HDF5 file
			File tempHDF5File = new File(baos.toString());
			Files.copy(tempHDF5File, hdf5OutputFile);
			tempHDF5File.delete();
		}
    }

    public HashMap<String, File> generateReportsAsCSV(SedML sedml, HashMap<String, ODESolverResultSet> resultsHash, File outDirForCurrentSedml, String outDir, String sedmlLocation) throws DataAccessException, IOException {
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
                        }
                        updateDatasetStatusYml(sedmlLocation, oo.getId(), dataset.getId(), Status.SUCCEEDED, outDir);
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
    
    public String generateIdNamePlotsMap(SedML sedml, File outDirForCurrentSedml) {
    	StringBuilder sb = new StringBuilder();
        List<Output> ooo = sedml.getOutputs();
        for (Output oo : ooo) {
            if (!(oo instanceof Report)) {
                System.out.println("Ignoring unsupported output `" + oo.getId() + "` while generating idNamePlotsMap.");
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
        	System.out.println("Unable to create the idNamePlotsMap");
        	e.printStackTrace(System.err);
        }
        return f.toString();
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
        // return new ProcessBuilder(args).inheritIO();
    	
    	// the above is stupid because you can't capture and handle the process errors; need to use the default pipe, not inherit
        return new ProcessBuilder(args);
    }

    public static void runAndPrintProcessStreams(ProcessBuilder pb, String outString, String errString) throws InterruptedException, IOException {
        // Process printing code goes here
    	File of = File.createTempFile("temp-", ".out", currentWorkingDir.toFile());
    	File ef = File.createTempFile("temp-", ".err", currentWorkingDir.toFile());
    	pb.redirectError(ef);
    	pb.redirectOutput(of);
    	Process process = pb.start();
        process.waitFor();
        StringBuilder sberr = new StringBuilder();
        StringBuilder sbout = new StringBuilder();
        List<String> lines = Files.readLines(ef, StandardCharsets.UTF_8);
        lines.forEach(line -> sberr.append(line).append("\n"));        
        String es = sberr.toString();
        lines = Files.readLines(of, StandardCharsets.UTF_8);
        lines.forEach(line -> sbout.append(line).append("\n"));        
        String os = sbout.toString();
        of.delete();
        ef.delete();
        if (process.exitValue() != 0) {
            System.err.println(errString);
            // don't print here, send the error down to caller who is responsible for dealing with it
            throw new RuntimeException(es);
            } else {
            System.out.println(outString);
            System.out.println(os);
        }
    }

    public static int checkPythonInstallationError() {
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
                    // search string can be one or more... (potential python 2.7.X bug here?)
                        if (!stringBuilder.toString().toLowerCase().startsWith("python")) System.err.println("Please check your local Python and PIP Installation, install required packages");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return exitCode;
    }

    // Ignoring biosimulator_utils warnings with -W ignore flag

    public void genSedmlForSed2DAnd3D(String omexFilePath, String outputDir) throws PythonStreamException {
        String results = singleInstance.callPython("genSedml2d3d", omexFilePath, outputDir);
        singleInstance.printPythonErrors(results, "", "Failed generating SED-ML for plot2d and 3D ");
    }

    public void execPlotOutputSedDoc(String omexFilePath, String idNamePlotsMap, String outputDir)  throws PythonStreamException {
        String results = singleInstance.callPython("execPlotOutputSedDoc", omexFilePath, idNamePlotsMap, outputDir);
        singleInstance.printPythonErrors(results, "HDF conversion successful\n","HDF conversion failed\n");
    }

    public void convertCSVtoHDF(String omexFilePath, String outputDir) throws PythonStreamException {

        // Convert CSV to HDF5
        /*
        Usage: cli.py SEDML_FILE_PATH WORKING_DIR BASE_OUT_PATH CSV_DIR <flags>
                    optional flags:        --rel_out_path | --apply_xml_model_changes |
                         --report_formats | --plot_formats | --log | --indent
        * */
        // handle exceptions here
        if (checkPythonInstallationError() == 0) {
            String results = singleInstance.callPython("execSedDoc", omexFilePath, outputDir);
            singleInstance.printPythonErrors(results, "HDF conversion successful\n","HDF conversion failed\n");
        }
    }

    // Due to what appears to be a leaky python function call, this method will continue using execShellCommand until the unerlying python is fixed
    public void genPlotsPseudoSedml(String sedmlPath, String resultOutDir) throws PythonStreamException, InterruptedException, IOException {
        ProcessBuilder pb = execShellCommand(new String[]{python, cliPath.toString(), "genPlotsPseudoSedml", sedmlPath, resultOutDir});
        runAndPrintProcessStreams(pb, "","");

        //String results = singleInstance.callPython("genPlotsPseudoSedml", sedmlPath, resultOutDir);
        //singleInstance.printPythonErrors(results);
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
    public void generateStatusYaml(String omexPath, String outDir) throws PythonStreamException {
        System.out.println("utilPath: " + utilPath);
        System.out.println("cliUtilPath: " + cliUtilPath);
        System.out.println("cliPath: " + cliPath);
        System.out.println("statusPath: " + statusPath);
        System.out.println("wrapperPath: " + wrapperPath);
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

        String results = singleInstance.callPython("genStatusYaml", String.valueOf(omexFilePath), outDir);
        singleInstance.printPythonErrors(results, "", "Failed generating status YAML\n");
    }

    public void updateTaskStatusYml(String sedmlName, String taskName, Status taskStatus, String outDir, String duration, String algorithm) throws PythonStreamException {
    	algorithm = algorithm.toUpperCase(Locale.ROOT);
    	algorithm = algorithm.replace("KISAO:", "KISAO_");

        String results = singleInstance.callPython("updateTaskStatus", sedmlName, taskName, taskStatus.toString(), outDir, duration, algorithm);
        singleInstance.printPythonErrors(results, "", "Failed updating task status YAML\n");
    }
    public void updateSedmlDocStatusYml(String sedmlName, Status sedmlDocStatus, String outDir) throws PythonStreamException, InterruptedException, IOException {
        String results = singleInstance.callPython("updateSedmlDocStatus", sedmlName, sedmlDocStatus.toString(), outDir);
        singleInstance.printPythonErrors(results, "", "Failed updating sedml document status YAML\n");
    }
    public void updateOmexStatusYml(Status simStatus, String outDir, String duration) throws PythonStreamException {
        String results = singleInstance.callPython("updateOmexStatus", simStatus.toString(), outDir, duration);
        singleInstance.printPythonErrors(results);
    }

    public void updateDatasetStatusYml(String sedmlName, String dataSet, String var, Status simStatus, String outDir) throws PythonStreamException {
        String results = singleInstance.callPython("updateDataSetStatus", sedmlName, dataSet, var, simStatus.toString(), outDir);
        singleInstance.printPythonErrors(results);
    }
    
    public void updateDatasetStatusYml(String sedmlName, String dataSet, String var, Status simStatus, String outDir, boolean usePython) throws PythonStreamException {
    	if (!usePython) this.updateDatasetStatusYml(sedmlName, dataSet, var, simStatus, outDir);
        else {
            String results = this.callPython("updateDataSetStatus", sedmlName, dataSet, var, simStatus.toString(), outDir);
            this.printPythonErrors(results);
        }
    }

    public void transposeVcmlCsv(String csvFilePath) throws PythonStreamException {
        String results = singleInstance.callPython("transposeVcmlCsv", csvFilePath);
        singleInstance.printPythonErrors(results);
    }

    public void genPlots(String sedmlPath, String resultOutDir) throws PythonStreamException {
        String results = singleInstance.callPython("genPlotPdfs", sedmlPath, resultOutDir);
        singleInstance.printPythonErrors(results);
    }

    // sedmlAbsolutePath - full path to location of the actual sedml file (document) used as input
    // entityId          - ex: task_0_0 for task, or biomodel_20754836.sedml for a sedml document
    // outDir            - path to directory where the log files will be placed
    // entityType        - string describing the entity type ex "task" for a task, or "sedml" for sedml document
    // message           - useful info about the execution of the entity (ex: task), could be human readable or concatenation of stdout and stderr
    public void setOutputMessage(String sedmlAbsolutePath, String entityId, String outDir, String entityType, String message) throws PythonStreamException, InterruptedException, IOException {
        String results = singleInstance.callPython("setOutputMessage", sedmlAbsolutePath, entityId, outDir, entityType, message);
        singleInstance.printPythonErrors(results, "", "Failed updating task status YAML\n");
    }
    // type - exception class, ex RuntimeException
    // message  - exception message
    public void setExceptionMessage(String sedmlAbsolutePath, String entityId, String outDir, String entityType, String type , String message) throws PythonStreamException {
        String results = this.callPython("setExceptionMessage", sedmlAbsolutePath, entityId, outDir, entityType, type, message);
        singleInstance.printPythonErrors(results, "", "Failed updating task status YAML\n");
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

    // Process builder commands
    

    
    // Python Process Accessory Methods
    /**
     * Facilitates the construction of the python instance connection
     */
    public void instantiatePythonProcess() throws IOException {
        if (CLIUtils.pythonProcess != null) return; // prevent override

        // Confirm we have python properly installed or kill this exe where it stands.
        this.checkPythonInstallation();

        // Start Python
        String pythonKeyWord = this.isWindowsPlatform ? "python.exe" : "python3";
    	ProcessBuilder pb = new ProcessBuilder(pythonKeyWord, "-i", "-W ignore");
    	pb.redirectErrorStream(true);
        CLIUtils.pythonProcess = pb.start();
        CLIUtils.pythonOSW = new OutputStreamWriter(pythonProcess.getOutputStream());
        CLIUtils.pythonISB = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));
        
        // "construction" commands
        try {
            this.getResultsOfLastCommand(); // Clear Buffer of Python Interpeter
            this.executeThroughPython("import sys");
            this.executeThroughPython(String.format("sys.path.append(r'%s')", this.utilPath.toString()));
            this.executeThroughPython(String.format("sys.path.append(r'%s')", this.cliUtilPath.toString()));
            //this.callPython(String.format("exec(open(\"%s\").read())", sanitizePath(this.wrapperPath.toString())));
            this.executeThroughPython("import wrapper");
            System.out.println("\nPython initalization success!\n");
        } catch (IOException | TimeoutException | InterruptedException e){
            System.out.println("Python instantiation Exception Thrown:\n" + e);
            System.exit(1);
        }
    }

    public void closePythonProcess() throws IOException {
        // Exit the living Python Process
        this.sendNewCommand("exit()"); // Sends kill command ("exit()") to python.exe instance;
        if (CLIUtils.pythonProcess.isAlive()) CLIUtils.pythonProcess.destroyForcibly(); // Making sure it's quite dead
        
        // Making sure we clean up
        CLIUtils.pythonOSW.close(); 
        CLIUtils.pythonISB.close();
        
        // Unbind references to allow reinstantiation
        CLIUtils.pythonISB = null;
        CLIUtils.pythonOSW = null;
        CLIUtils.pythonProcess = null;
    }

    private String getResultsOfLastCommand() throws IOException, TimeoutException, InterruptedException {
        String importantPrefix = ">>> ";
        String results = "";

        int currentTime = 0, TIMEOUT_LIMIT = 600000; // 600 seconds (10 minutes)

        // Wait for python to finish what it's working on
        while(!CLIUtils.pythonISB.ready()){
            if (currentTime++ >= TIMEOUT_LIMIT) throw new TimeoutException();
            Thread.sleep(1); // wait ~1ms at a time
        }

        // Python's ready (or we had a timeout?); lets get the buffer without going too far and getting blocked (see note 1 at bottom of file)
        while ( results.length() < importantPrefix.length() 
                || !results.substring(results.length() - importantPrefix.length()).equals(importantPrefix)){ // unless we've found the prefix we need at the end of the results
            results += (char)CLIUtils.pythonISB.read();
        }

        // Got the results we need. Now lets clean the results string up before returning it
        results = CLIUtils.stripString(results.substring(0, results.length() - importantPrefix.length()));

        return results == "" ? null : results;
    }

    private String processPythonArguments(String... arguments){
        String argList = "";
        int adjArgLength;
        for (String arg : arguments){
            argList += "r'" + CLIUtils.stripString(arg) + "'" + ",";
        }
        adjArgLength = argList.length() == 0 ? 0 : argList.length() - 1;
        return argList.substring(0, adjArgLength);
    }

    private boolean printPythonErrors(String returnedString){
        return this.printPythonErrors(returnedString, null, null);
    }

    private boolean printPythonErrors(String returnedString, String outString, String errString){
        boolean DEBUG_NORMAL_OUTPUT = false; // Manually override
        boolean hasPassed;
        String ERROR_PHRASE1 = "Traceback", ERROR_PHRASE2 = "File \"<stdin>\"";

        // Null or empty strings are considered passing results
        if(returnedString == null || (returnedString = CLIUtils.stripString(returnedString)).equals("")) 
            return true;

        if (
                (returnedString.length() >= ERROR_PHRASE1.length() && ERROR_PHRASE1.equals(returnedString.substring(0, ERROR_PHRASE1.length()))) 
            ||  (returnedString.length() >= ERROR_PHRASE2.length() && ERROR_PHRASE2.equals(returnedString.substring(0, ERROR_PHRASE2.length())))
            ||  (returnedString.contains("File \"<stdin>\""))
        
        ){ // Report an error:
            String resultString = (errString != null && errString.length() > 0) ? String.format("Result: %s\n", errString) : "";
            System.err.printf("Python error caught: <%s>\n%s", returnedString, resultString);
            hasPassed = false;
            System.exit(1);
        } else {
            String resultString = (outString != null && outString.length() > 0) ? String.format("Result: %s\n", outString) : "";
            System.out.printf("Python returned%s\nResult: %s\n", DEBUG_NORMAL_OUTPUT? String.format(": [%s]", returnedString) : " sucessfully.", resultString);
            hasPassed = true;
        }
        return hasPassed;
    }

    private void sendNewCommand(String cmd) throws IOException {
        // we can easily send the command, but we need to format it first.
        
        String command = String.format("%s\n", CLIUtils.stripString(cmd));
        CLIUtils.pythonOSW.write(command);
        CLIUtils.pythonOSW.flush();
    }

    private String formatPythonFuctionCall(String functionName, String... arguments){
        return String.format("wrapper.%s(%s)", CLIUtils.stripString(functionName), this.processPythonArguments(arguments));
    }

    // returns parsed interpreter return
    private String callPython(String functionName, String... arguments) throws PythonStreamException {
        return this.callPython(this.formatPythonFuctionCall(functionName, arguments));
    }

    private String callPython(String command) throws PythonStreamException {
        String returnString = "";
        try {
            this.instantiatePythonProcess(); // Make sure we have a python instance; calling will not override an existing intance
            this.sendNewCommand(command);
            returnString = this.getResultsOfLastCommand();
        } catch (IOException | InterruptedException | TimeoutException e){
            throw new PythonStreamException("Python process encounted an exception:\n" + e);
        }
        return returnString;
    }
    
    private void executeThroughPython(String command) throws PythonStreamException {
        String results = callPython(command);
        if (!this.printPythonErrors(results)) System.exit(1);
    }

    private int checkPythonInstallation() {
        String version = "--version", stdOutLog;
        ProcessBuilder processBuilder;
        Process process;
        int exitCode = -10;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            processBuilder = execShellCommand(new String[]{python, version});
            process = processBuilder.start();
            exitCode = process.waitFor();
            if (exitCode == 0) {
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((stdOutLog = bufferedReader.readLine()) != null) {
                    stringBuilder.append(stdOutLog);
                    // search string can be one or more... (potential python 2.7.X bug here?) Maybe use regex?
                        if (!stringBuilder.toString().toLowerCase().startsWith("python 3")) throw new PythonStreamException();
                            
                }
            }
        } catch (PythonStreamException e) {
            System.err.println("Please check your local Python and PIP Installation, install required packages and versions");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
        return exitCode;
    }

    public static String stripString(String str){
        return str.replaceAll("^[ \t]+|[ \t]+$", ""); // replace whitespace at the front and back with nothing
    }

    public static String encloseString(String s){
        return "r\"" + s + "\"";
    }

}

/*
 * NOTES:
 * 
 * 1) BufferedReader will block if asked to read the Python interpreter while the interpreter waiting for input (assuming the buffer has already been iterated through).
 *      To prevent this, we catch the prefix to Python's prompt for a new command (">>> ") to stop before we wait for etinity and break our program.
 */
