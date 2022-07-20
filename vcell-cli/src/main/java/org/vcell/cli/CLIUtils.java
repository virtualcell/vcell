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
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.export.server.ASCIIExporter;
import cbit.vcell.export.server.ASCIISpecs;
import cbit.vcell.export.server.ASCIISpecs.csvRoiLayout;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputFunctionContext;
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
import org.vcell.stochtest.TimeSeriesMultitrialData;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import com.google.common.io.Files;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//import java.nio.file.Files;

public class CLIUtils {
    // Simulation Status enum
    public enum Status {
        RUNNING("Simulation still running"),
        SKIPPED("Simulation skipped"),
        SUCCEEDED("Simulation succeeded"),
        FAILED("Simulation failed"),
        ABORTED("Simulation aborted");

        private final String description;

        Status(String desc) {
            this.description = desc;
        }
    }

    // Breakline
    public static void drawBreakLine(String breakString, int times) {
        System.out.println(breakString + StringUtils.repeat(breakString, times));
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

    public static HashMap<String, File> generateReportsAsCSV(SedML sedml, HashMap<String, ODESolverResultSet> resultsHash, File outDirForCurrentSedml, String outDir, String sedmlLocation) throws DataAccessException, IOException {
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
    
    public static String generateIdNamePlotsMap(SedML sedml, File outDirForCurrentSedml) {
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

    // Ignoring biosimulator_utils warnings with -W ignore flag

    public static void genSedmlForSed2DAnd3D(String omexFilePath, String outputDir) throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genSedml2d3d", omexFilePath, outputDir);
        cliPythonManager.printPythonErrors(results, "", "Failed generating SED-ML for plot2d and 3D ");
    }

    public static void execPlotOutputSedDoc(String omexFilePath, String idNamePlotsMap, String outputDir)  throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("execPlotOutputSedDoc", omexFilePath, idNamePlotsMap, outputDir);
        cliPythonManager.printPythonErrors(results, "HDF conversion successful\n","HDF conversion failed\n");
    }

    public void convertCSVtoHDF(String omexFilePath, String outputDir, CLIPythonManager cliPythonManager) throws PythonStreamException {

        // Convert CSV to HDF5
        /*
        Usage: cli.py SEDML_FILE_PATH WORKING_DIR BASE_OUT_PATH CSV_DIR <flags>
                    optional flags:        --rel_out_path | --apply_xml_model_changes |
                         --report_formats | --plot_formats | --log | --indent
        * */
        // handle exceptions here
        if (cliPythonManager.checkPythonInstallationError() == 0) {
            String results = cliPythonManager.callPython("execSedDoc", omexFilePath, outputDir);
            cliPythonManager.printPythonErrors(results, "HDF conversion successful\n","HDF conversion failed\n");
        }
    }

    // Due to what appears to be a leaky python function call, this method will continue using execShellCommand until the unerlying python is fixed
    public static void genPlotsPseudoSedml(String sedmlPath, String resultOutDir) throws PythonStreamException, InterruptedException, IOException {
        CLIPythonManager.callNonsharedPython("genPlotsPseudoSedml", sedmlPath, resultOutDir);
//        ProcessBuilder pb = new ProcessBuilder(new String[]{CLIResourceManager.python, cliDirs.cliPath.toString(), "genPlotsPseudoSedml", sedmlPath, resultOutDir});
//        runAndPrintProcessStreams(pb, "","");

        /**
         * replace with the following once the leak is fixed
         */
//        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
//        String results = cliPythonManager.callPython("genPlotsPseudoSedml", sedmlPath, resultOutDir);
//        cliPythonManager.printPythonErrors(results);
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
    public static void generateStatusYaml(String omexPath, String outDir) throws PythonStreamException {
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

        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genStatusYaml", String.valueOf(omexFilePath), outDir);
        cliPythonManager.printPythonErrors(results, "", "Failed generating status YAML\n");
    }

    public static void updateTaskStatusYml(String sedmlName, String taskName, Status taskStatus, String outDir, String duration, String algorithm) throws PythonStreamException {
    	algorithm = algorithm.toUpperCase(Locale.ROOT);
    	algorithm = algorithm.replace("KISAO:", "KISAO_");

        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updateTaskStatus", sedmlName, taskName, taskStatus.toString(), outDir, duration, algorithm);
        cliPythonManager.printPythonErrors(results, "", "Failed updating task status YAML\n");
    }
    public static void updateSedmlDocStatusYml(String sedmlName, Status sedmlDocStatus, String outDir) throws PythonStreamException, InterruptedException, IOException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updateSedmlDocStatus", sedmlName, sedmlDocStatus.toString(), outDir);
        cliPythonManager.printPythonErrors(results, "", "Failed updating sedml document status YAML\n");
    }
    public static void updateOmexStatusYml(Status simStatus, String outDir, String duration) throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updateOmexStatus", simStatus.toString(), outDir, duration);
        cliPythonManager.printPythonErrors(results);
    }

    public static void updateDatasetStatusYml(String sedmlName, String dataSet, String var, Status simStatus, String outDir) throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updateDataSetStatus", sedmlName, dataSet, var, simStatus.toString(), outDir);
        cliPythonManager.printPythonErrors(results);
    }
    
    public static void updateDatasetStatusYml(String sedmlName, String dataSet, String var, Status simStatus, String outDir, boolean usePython) throws PythonStreamException {
    	if (!usePython) updateDatasetStatusYml(sedmlName, dataSet, var, simStatus, outDir);
        else {
            CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
            String results = cliPythonManager.callPython("updateDataSetStatus", sedmlName, dataSet, var, simStatus.toString(), outDir);
            cliPythonManager.printPythonErrors(results);
        }
    }

    public static void transposeVcmlCsv(String csvFilePath) throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("transposeVcmlCsv", csvFilePath);
        cliPythonManager.printPythonErrors(results);
    }

    public static void genPlots(String sedmlPath, String resultOutDir) throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genPlotPdfs", sedmlPath, resultOutDir);
        cliPythonManager.printPythonErrors(results);
    }

    // sedmlAbsolutePath - full path to location of the actual sedml file (document) used as input
    // entityId          - ex: task_0_0 for task, or biomodel_20754836.sedml for a sedml document
    // outDir            - path to directory where the log files will be placed
    // entityType        - string describing the entity type ex "task" for a task, or "sedml" for sedml document
    // message           - useful info about the execution of the entity (ex: task), could be human readable or concatenation of stdout and stderr
    public static void setOutputMessage(String sedmlAbsolutePath, String entityId, String outDir, String entityType, String message) throws PythonStreamException, InterruptedException, IOException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("setOutputMessage", sedmlAbsolutePath, entityId, outDir, entityType, message);
        cliPythonManager.printPythonErrors(results, "", "Failed updating task status YAML\n");
    }
    // type - exception class, ex RuntimeException
    // message  - exception message
    public static void setExceptionMessage(String sedmlAbsolutePath, String entityId, String outDir, String entityType, String type , String message) throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("setExceptionMessage", sedmlAbsolutePath, entityId, outDir, entityType, type, message);
        cliPythonManager.printPythonErrors(results, "", "Failed updating task status YAML\n");
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

    public static String getTempDir() throws IOException {
        String tempPath = String.valueOf(java.nio.file.Files.createTempDirectory("vcell_temp_" + UUID.randomUUID().toString()).toAbsolutePath());
        System.out.println("TempPath Created: " + tempPath);
        return tempPath;
    }

    // Process builder commands


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
