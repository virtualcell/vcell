package org.vcell.cli.run;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.export.server.*;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTable;
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
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jlibsedml.*;
import org.jlibsedml.execution.IXPathToVariableIDResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.vcell.cli.CLIUtils;
import org.vcell.stochtest.TimeSeriesMultitrialData;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RunUtils {

    public final static String VCELL_TEMP_DIR_PREFIX = "vcell_temp_";
    private final static Logger logger = LogManager.getLogger(RunUtils.class);

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
        FormatSpecificSpecs formatSpecificSpecs = new ASCIISpecs(ExportFormat.CSV, dataType, switchRowsColumns, simNameSimDataIDs, exportMultipleParamScans, ASCIISpecs.csvRoiLayout.var_time_val, isHDF5);

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
                logger.info("Ignoring unsupported output `" + oo.getId() + "` while CSV generation.");
            } else {
                logger.info("Generating report `" + oo.getId() +"`.");
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
                        HashMap<Variable, double[]> values = new HashMap<>();
                        for (Variable var : vars) {
                            AbstractTask task = sedml.getTaskWithId(var.getReference());
                            //Model model = sedml.getModelWithId(task.getModelReference());
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
                                    logger.error("only uniform time course simulations are supported");
                                }

                            }
                        }
                        PythonCalls.updateDatasetStatusYml(sedmlLocation, oo.getId(), dataset.getId(), Status.SUCCEEDED, outDir);
                        if (!supportedDataset) {
                            logger.error("Dataset " + dataset.getId() + " references unsupported RepeatedTask and is being skipped");
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
                    logger.error("Encountered exception: " + e.getMessage(), e);
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
            put("csv", "reports.zip");
            put("pdf", "plots.zip");
        }};

        for (String ext : extensionListMap.keySet()) {
            srcFiles = listFilesForFolder(dirPath, ext);

            if (srcFiles.size() == 0) {
                logger.error("No " + ext.toUpperCase() + " files found, skipping archiving `" + extensionListMap.get(ext) + "` files");
            } else {
                fileOutputStream = new FileOutputStream(Paths.get(dirPath.toString(), extensionListMap.get(ext)).toFile());
                zipOutputStream = new ZipOutputStream(fileOutputStream);
                if (srcFiles.size() != 0) logger.info("Archiving resultant " + ext.toUpperCase() + " files to `" + extensionListMap.get(ext) + "`.");
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
        if (f.exists()) {
            boolean isRemoved = CLIUtils.removeDirs(f);
            if (!isRemoved)
                return false;
        }
        return f.mkdirs();
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

        try {
            PrintWriter out = new PrintWriter(outFile);
            out.print(csvAsString);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
