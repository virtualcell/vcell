package org.vcell.cli.run;

import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.xml.ExternalDocInfo;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;
import org.vcell.cli.messaging.CLIRecordable;
import org.vcell.cli.exceptions.ExecutionException;
import org.vcell.cli.exceptions.PreProcessingException;
import org.vcell.cli.run.hdf5.HDF5ExecutionResults;
import org.vcell.cli.run.hdf5.Hdf5DataContainer;
import org.vcell.cli.run.hdf5.Hdf5DataExtractor;
import org.vcell.cli.run.results.*;
import org.vcell.cli.run.plotting.PlottingDataExtractor;
import org.vcell.cli.run.plotting.ChartCouldNotBeProducedException;
import org.vcell.cli.run.plotting.Results2DLinePlot;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;
import org.vcell.sbml.vcell.lazy.LazySBMLNonSpatialDataAccessor;
import org.vcell.sbml.vcell.lazy.LazySBMLSpatialDataAccessor;
import org.vcell.sedml.log.BiosimulationLog;
import org.vcell.trace.Span;
import org.vcell.trace.Tracer;
import org.vcell.util.DataAccessException;
import org.vcell.util.FileUtils;
import org.vcell.util.Pair;
import org.vcell.util.Triplet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that deals with the processing quest of a sedml file.
 */
public class SedmlJob {

    private final boolean SHOULD_KEEP_TEMP_FILES,
            ACCEPT_EXACT_MATCH_ONLY, SHOULD_OVERRIDE_FOR_SMALL_MESH;
    private final String SEDML_NAME, SEDML_LOCATION, BIOMODEL_BASE_NAME, RESULTS_DIRECTORY_PATH;
    private final String[] SEDML_NAME_SPLIT;
    private final StringBuilder LOG_OMEX_MESSAGE;
    private final SedmlStatistics DOC_STATISTICS; // We keep this in object memory for debugging
    private final File MASTER_OMEX_ARCHIVE, OUTPUT_DIRECTORY_FOR_CURRENT_SEDML;
    private final CLIRecordable CLI_RECORDER;
    private boolean somethingFailed;
    private String logDocumentMessage, logDocumentError;
    private SedML sedml;


    private final static Logger logger = LogManager.getLogger(SedmlJob.class);

    /**
     * Constructor to provide all needed info for a SedML processing job.
     * 
     * @param sedmlLocation location of the sedml document with the model to process
     * @param sedmlSpecificOutput location of where sedml-specific processing output goes
     * @param masterOmexArchive the archive containing the sedml file
     * @param resultsDirPath path to where the results should be placed
     * @param cliRecorder recorder object used for CLI applications
     * @param bKeepTempFiles whether temp files shouldn't be deleted, or should.
     * @param bExactMatchOnly enforces a KISAO match, with no substitution
     * @param bSmallMeshOverride whether to use small meshes or standard meshes.
     * @param logOmexMessage a string-builder to contain progress updates of omex execution
     */
    public SedmlJob(String sedmlLocation, String sedmlSpecificOutput, File masterOmexArchive,
                    String resultsDirPath, CLIRecordable cliRecorder,
                    boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bSmallMeshOverride,
                    StringBuilder logOmexMessage){
        final String SAFE_WINDOWS_FILE_SEPARATOR = "\\\\";
        final String SAFE_UNIX_FILE_SEPARATOR = "/";
        this.MASTER_OMEX_ARCHIVE = masterOmexArchive;
        this.SEDML_LOCATION = sedmlLocation;
        this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML = new File(sedmlSpecificOutput);
        this.SEDML_NAME_SPLIT = this.SEDML_LOCATION.split(OperatingSystemInfo.getInstance().isWindows() ?
                SAFE_WINDOWS_FILE_SEPARATOR : SAFE_UNIX_FILE_SEPARATOR, -2);
        this.SEDML_NAME = this.SEDML_NAME_SPLIT[this.SEDML_NAME_SPLIT.length - 1];
        this.DOC_STATISTICS = new SedmlStatistics(this.SEDML_NAME);
        this.BIOMODEL_BASE_NAME = FileUtils.getBaseName(masterOmexArchive.getName());
        this.RESULTS_DIRECTORY_PATH = resultsDirPath;
        this.LOG_OMEX_MESSAGE = logOmexMessage;
        this.CLI_RECORDER = cliRecorder;
        this.SHOULD_KEEP_TEMP_FILES = bKeepTempFiles;
        this.ACCEPT_EXACT_MATCH_ONLY = bExactMatchOnly;
        this.SHOULD_OVERRIDE_FOR_SMALL_MESH = bSmallMeshOverride;

        this.somethingFailed = false;
        this.logDocumentMessage = "Initializing SED-ML document... ";
        this.logDocumentError = "";
    }


    /**
     * Prepare the SedML model for execution
     * Called before: `simulateSedml()`
     *
     * @throws IOException if there are system I/O issues
     */
    public SedmlStatistics preProcessDoc() throws IOException, PreProcessingException {
        BiosimulationLog biosimLog = BiosimulationLog.instance();

        Span span = Tracer.startSpan(Span.ContextType.PROCESSING_SEDML, "preProcessDoc", null);
        RunUtils.removeAndMakeDirs(this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML);

        this.LOG_OMEX_MESSAGE.append("Processing ").append(this.SEDML_NAME).append(". ");

        // Load SedML
        logger.info("Initializing and Pre-Processing SedML document: {}", this.SEDML_NAME);
        biosimLog.updateSedmlDocStatusYml(this.SEDML_LOCATION, BiosimulationLog.Status.RUNNING);
        try {
            this.sedml = SedmlJob.getSedMLFile(this.SEDML_NAME_SPLIT, this.MASTER_OMEX_ARCHIVE);
        } catch (Exception e) {
            String prefix = "SedML pre-processing for " + this.SEDML_LOCATION + " failed";
            this.logDocumentError = prefix + ": " + e.getMessage();
            Tracer.failure(e, prefix);
            this.reportProblem(e);
            this.somethingFailed = SedmlJob.somethingDidFail();
            biosimLog.updateSedmlDocStatusYml(this.SEDML_LOCATION, BiosimulationLog.Status.FAILED);
            span.close();
            throw new PreProcessingException(prefix, e);
        }

        // If we got here, we have a successful load!!
        this.logDocumentMessage += "done. ";
        String resultString = String.format("Successfully loaded and translated SED-ML file: %s.\n", this.SEDML_NAME);
        this.logDocumentMessage += resultString;

        // Generate Doc Statistics
        this.DOC_STATISTICS.setNumModels(this.sedml.getModels().size());

        this.DOC_STATISTICS.setNumSimulations(this.sedml.getSimulations().size());
        for (Simulation simulation : this.sedml.getSimulations()){
            boolean isUTC = simulation instanceof UniformTimeCourse;
            if (isUTC) this.DOC_STATISTICS.setNumUniformTimeCourseSimulations(this.DOC_STATISTICS.getNumUniformTimeCourseSimulations() + 1);
            else if (simulation instanceof OneStep) this.DOC_STATISTICS.setNumOneStepSimulations(this.DOC_STATISTICS.getNumOneStepSimulations() + 1);
            else if (simulation instanceof SteadyState) this.DOC_STATISTICS.setNumSteadyStateSimulations(this.DOC_STATISTICS.getNumSteadyStateSimulations() + 1);
            else this.DOC_STATISTICS.setNumAnalysisSimulations(this.DOC_STATISTICS.getNumAnalysisSimulations() + 1);
        }

        this.DOC_STATISTICS.setNumTasks(this.sedml.getTasks().size());

        this.DOC_STATISTICS.setNumOutputs(this.sedml.getOutputs().size());
        for (Output output : this.sedml.getOutputs()) {
            if (output instanceof Report) this.DOC_STATISTICS.setReportsCount(this.DOC_STATISTICS.getReportsCount() + 1);
            if (output instanceof Plot2D) this.DOC_STATISTICS.setPlots2DCount(this.DOC_STATISTICS.getPlots2DCount() + 1);
            if (output instanceof Plot3D) this.DOC_STATISTICS.setPlots3DCount(this.DOC_STATISTICS.getPlots3DCount() + 1);
        }

        // Check for overrides
        for(Model m : this.sedml.getModels()) {
            if (m.getListOfChanges().isEmpty()) continue;
            this.DOC_STATISTICS.setHasOverrides(true);
            break;
        }

        // Check for parameter scans
        for(AbstractTask at : this.sedml.getTasks()) {
            if (!(at instanceof RepeatedTask rt)) continue;
            List<SetValue> changes = rt.getChanges();
            if(changes == null || changes.isEmpty()) continue;
            this.DOC_STATISTICS.setHasScans(true);
            break;
        }

        logger.info("{}{}", resultString, this.DOC_STATISTICS.toFormattedString());


        // Before we leave, we need to throw an exception if we have any VCell Sims we can't run.
        if (this.DOC_STATISTICS.getNumUniformTimeCourseSimulations() != this.DOC_STATISTICS.getNumSimulations()){
            biosimLog.updateSedmlDocStatusYml(this.SEDML_LOCATION, BiosimulationLog.Status.SKIPPED);
            PreProcessingException exception = new PreProcessingException("There are SedML simulations VCell is not capable of running at this time!");
            Tracer.failure(exception, "Fatal discovery encountered while processing SedML: non-compatible SedML Simulations found.");
            throw exception;
        }

        span.close();
        return this.DOC_STATISTICS;
    }

    /**
     * Prepare the SedML model for execution
     * Called after: `preProcessDoc()`
     *
     * @throws IOException if there are system I/O issues
     */
    public boolean simulateSedml(HDF5ExecutionResults masterHdf5File) throws ExecutionException, IOException {
        /*
         * - Run solvers and generate outputs
         * - XmlHelper code uses two types of resolvers to handle absolute or relative paths
         */
        SolverHandler solverHandler = new SolverHandler();
        this.runSimulations(solverHandler);
        this.processOutputs(solverHandler, masterHdf5File);
        return this.evaluateResults();
    }

    private void runSimulations(SolverHandler solverHandler) throws ExecutionException {
        /*
         * - Run solvers and make reports; all failures/exceptions are being caught
         * - we send both the whole OMEX file and the extracted SEDML file path
         * - XmlHelper code uses two types of resolvers to handle absolute or relative paths
         */
        ExternalDocInfo externalDocInfo = new ExternalDocInfo(this.MASTER_OMEX_ARCHIVE, true);
        Span span = null;

        logger.info("SedML Parsing complete.");
        RunUtils.drawBreakLine("-", 100);
        String str = "Queuing all tasks for simulation... ";
        logger.info(str);
        this.logDocumentMessage += str;
        try {
            span = Tracer.startSpan(Span.ContextType.SIMULATIONS_RUN, "runSimulations", null);
            Map<AbstractTask, Triplet<BiosimulationLog.Status, Double, Exception >> taskResults =  solverHandler.simulateAllTasks(externalDocInfo, this.sedml, this.CLI_RECORDER,
                    this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML, this.RESULTS_DIRECTORY_PATH,
                    this.SEDML_LOCATION, this.ACCEPT_EXACT_MATCH_ONLY, this.SHOULD_OVERRIDE_FOR_SMALL_MESH);
            int numSimulationsUnsuccessful = 0;
            logger.info("Finished all solver executions for {}", this.SEDML_NAME);
            StringBuilder executionSummary = new StringBuilder("Summary of Task Results\n");
            for (AbstractTask sedmlTask : taskResults.keySet()){
                String sedmlTaskName = (sedmlTask.getName() == null || sedmlTask.getName().isBlank()) ? sedmlTask.getId() : sedmlTask.getName() + " (" + sedmlTask.getId() + ")" ;
                executionSummary.append("\t> ").append(sedmlTaskName).append("::").append(taskResults.get(sedmlTask).one.name()).append("\n");
                if (!taskResults.get(sedmlTask).one.equals(BiosimulationLog.Status.SUCCEEDED)){
                    logger.error("Unsuccessful run #{} detected: Status :: {}\n", ++numSimulationsUnsuccessful, taskResults.get(sedmlTask).one, taskResults.get(sedmlTask).three);
                }
            }
            logger.info(executionSummary.toString());
            if (numSimulationsUnsuccessful > 0) throw new ExecutionException(numSimulationsUnsuccessful + " simulation" + (numSimulationsUnsuccessful == 1 ? "s" : "") + " were unsuccessful.");
        } catch (Exception e) {
            Throwable currentTierOfException = e;
            StringBuilder errorMessage = new StringBuilder();
            Tracer.failure(e, errorMessage.toString());
            this.somethingFailed = somethingDidFail();
            while (currentTierOfException != null && !currentTierOfException.equals(currentTierOfException.getCause())){
                errorMessage.append(currentTierOfException.getMessage());
                currentTierOfException = currentTierOfException.getCause();
            }
            this.logDocumentError = errorMessage.toString();        // probably the hash is empty
            throw new ExecutionException(errorMessage.toString(), e);
        } finally {
            if (span != null) {
                span.close();
            }
        }
    }

    private void processOutputs(SolverHandler solverHandler, HDF5ExecutionResults masterHdf5File) throws ExecutionException {
        // WARNING!!! Current logic dictates that if any task fails we fail the sedml document
        // change implemented on Nov 11, 2021
        // Previous logic was that if at least one task produces some results we declare the sedml document status as successful
        // that will include spatial simulations for which we don't produce reports or plots!
        this.evaluateSolverHandlerResultIntegrity(solverHandler);
        this.logDocumentMessage += "Generating outputs... ";
        logger.info("Generating outputs... ");
        Span span = null;
        try {
            span = Tracer.startSpan(Span.ContextType.PROCESSING_SIMULATION_OUTPUTS, "processOutputs", null);
            /////////////////////////////////////////////////////
            Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> organizedNonSpatialResults =
                    NonSpatialResultsConverter.organizeNonSpatialResultsBySedmlDataGenerator(
                            this.sedml, solverHandler.nonSpatialResults, solverHandler.taskToTempSimulationMap);

            Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> organizedSpatialResults =
                    SpatialResultsConverter.organizeSpatialResultsBySedmlDataGenerator(
                        this.sedml, solverHandler.spatialResults, solverHandler.taskToTempSimulationMap);

            boolean hasReports = !this.sedml.getOutputs().stream().filter(Report.class::isInstance).map(Report.class::cast).toList().isEmpty();
            boolean has2DPlots = !this.sedml.getOutputs().stream().filter(Plot2D.class::isInstance).map(Plot2D.class::cast).toList().isEmpty();
            if (!solverHandler.nonSpatialResults.isEmpty()) {
                if (hasReports) this.generateCSV(organizedNonSpatialResults);
                if (has2DPlots) this.generatePlots(organizedNonSpatialResults);
            }

            this.indexHDF5Data(organizedNonSpatialResults, organizedSpatialResults, solverHandler, masterHdf5File);

        } catch (Exception e) {
            this.somethingFailed = somethingDidFail();
            this.logDocumentError += e.getMessage();
            try {
                this.reportProblem(e);
            } catch (IOException ioe){
                throw new RuntimeException("Encountered IOException while trying to report problem", ioe);
            }
            throw new ExecutionException("error while processing outputs: " + e.getMessage(), e);
        } finally {
            if (span != null) span.close();
        }
    }

    private void evaluateSolverHandlerResultIntegrity(SolverHandler solverHandler){
        if (!solverHandler.nonSpatialResults.containsValue(null) && !solverHandler.spatialResults.containsValue(null)) return;
        // some tasks failed, but not all
        this.somethingFailed = somethingDidFail();
        this.logDocumentMessage += "Failed to execute one or more tasks. ";
        Tracer.failure(new Exception("Failed to execute one or more tasks in " + this.SEDML_NAME), "Failed to execute one or more tasks in " + this.SEDML_NAME);
        logger.warn("Failed to execute one or more tasks in " + this.SEDML_NAME);
    }

    private boolean evaluateResults() throws IOException {
        return this.somethingFailed ? this.declareFailedResult() : this.declarePassedResult();
    }

    private boolean declareFailedResult(){
        // something went wrong but no exception was fired
        Exception e = new RuntimeException("Failure executing the sed document. ");
        this.logDocumentError += e.getMessage();
        try{
            this.reportProblem(e);
        } catch (IOException ioe){
            String failureMessage = "Encountered IOException while trying to declare failed result";
            Tracer.failure(ioe, failureMessage);
            logger.warn(failureMessage, ioe);
        }
        logger.warn(this.logDocumentError);
        return false;
    }

    private boolean declarePassedResult() throws IOException {
        // archiving result files
        if (logger.isDebugEnabled()) logger.info("Archiving result files");
        RunUtils.zipResFiles(new File(this.RESULTS_DIRECTORY_PATH));

        // Declare success!
        BiosimulationLog biosimLog = BiosimulationLog.instance();
        biosimLog.setOutputMessage(this.SEDML_LOCATION, this.SEDML_NAME, "sedml", this.logDocumentMessage);
        biosimLog.updateSedmlDocStatusYml(this.SEDML_LOCATION, BiosimulationLog.Status.SUCCEEDED);
        logger.info("SED-ML : " + this.SEDML_NAME + " successfully completed");
        return true;
    }

    private void generateCSV(Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> organizedNonSpatialResults) {
        Map<String, File> csvReports;
        this.logDocumentMessage += "Generating CSV file... ";
        logger.info("Generating CSV file... ");

        // csvReports is never null (?)
        csvReports = RunUtils.generateReportsAsCSV(this.sedml, organizedNonSpatialResults, this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML);
        if (csvReports.isEmpty() || csvReports.containsValue(null)) {
            this.somethingFailed = somethingDidFail();
            Tracer.failure(new Exception("Failed to generate one or more reports"), "Failed to generate one or more reports");
            String msg = "Failed to generate one or more reports. ";
            this.logDocumentMessage += msg;
        } else {
            this.logDocumentMessage += "Done. ";
        }
    }

    private void generatePlots(Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> organizedNonSpatialResults) throws ExecutionException {
        logger.info("Generating Plots... ");
        // We assume if no exception is returned that the plots pass
        PlottingDataExtractor plotExtractor = new PlottingDataExtractor(this.sedml, this.SEDML_NAME);
        Map<Results2DLinePlot, Pair<String, String>> plot2Ds = plotExtractor.extractPlotRelevantData(organizedNonSpatialResults);
        for (Results2DLinePlot plotToExport : plot2Ds.keySet()){
            try {
                plotToExport.generatePng(plot2Ds.get(plotToExport).one + ".png", this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML);
                plotToExport.generatePdf(plot2Ds.get(plotToExport).one + ".pdf", this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML);
                BiosimulationLog.instance().updatePlotStatusYml(this.SEDML_NAME, plot2Ds.get(plotToExport).two, BiosimulationLog.Status.SUCCEEDED);
            } catch (ChartCouldNotBeProducedException e){
                logger.error("Failed creating plot:", e);
                throw new ExecutionException("Failed to create plot: " + plotToExport.getTitle(), e);
            }
        }
    }

    private void indexHDF5Data(Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> organizedNonSpatialResults, Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> organizedSpatialResults, SolverHandler solverHandler, HDF5ExecutionResults masterHdf5File) {
        this.logDocumentMessage += "Indexing HDF5 data... ";
        logger.info("Indexing HDF5 data... ");

        Hdf5DataExtractor hdf5Extractor = new Hdf5DataExtractor(this.sedml, solverHandler.taskToTempSimulationMap);

        Hdf5DataContainer partialHdf5File = hdf5Extractor.extractHdf5RelevantData(organizedNonSpatialResults, organizedSpatialResults, masterHdf5File.isBioSimHdf5);
        masterHdf5File.addResults(this.sedml, partialHdf5File);
    }

    // This method is a bit weird; it uses a temp file as a reference to compare against while getting the file straight from the archive.
    private static SedML getSedMLFile(String[] tokenizedPathToSedml, File inputFile) throws XMLException, IOException {
        Path convertedPath = SedmlJob.getRelativePath(tokenizedPathToSedml);
        if (convertedPath == null) throw new RuntimeException("Was not able to get relative path to " + inputFile.getName());
        String identifyingPath = FilenameUtils.separatorsToUnix(convertedPath.toString());
        try (FileInputStream omexStream = new FileInputStream(inputFile)) {
            for (SEDMLDocument doc : Libsedml.readSEDMLArchive(omexStream).getSedmlDocuments()){
                SedML potentiallyCorrectFile = doc.getSedMLModel();
                String potentiallyCorrectPath = potentiallyCorrectFile.getPathForURI() + potentiallyCorrectFile.getFileName();
                if (!identifyingPath.equals(potentiallyCorrectPath)) continue;
                return potentiallyCorrectFile;
            }
        }
        throw new PreProcessingException("Unable to find desired SedML within path");
    }

    private static Path getRelativePath(String[] tokenizedPath){
        for (int i = 0; i < tokenizedPath.length; i++){
            if (tokenizedPath[i].startsWith(RunUtils.VCELL_TEMP_DIR_PREFIX)){
                return  Paths.get(tokenizedPath[i + 1], Arrays.copyOfRange(tokenizedPath, i + 2, tokenizedPath.length));
            }
        }
        return null;
    }

    private static boolean somethingDidFail(){
        StackTraceElement elem = new Exception().getStackTrace()[1];
        
        logger.debug(String.format("Something failed in %s @ line %d", elem.getClassName(), elem.getLineNumber()));
        return true;
    }

    private void reportProblem(Exception e) throws IOException{
        logger.error(e.getMessage(), e);
        String type = e.getClass().getSimpleName();
        BiosimulationLog biosimLog = BiosimulationLog.instance();
        biosimLog.setOutputMessage(this.SEDML_LOCATION, this.SEDML_NAME, "sedml", this.logDocumentMessage);
        biosimLog.setExceptionMessage(this.SEDML_LOCATION, this.SEDML_NAME, "sedml", type, this.logDocumentError);
        this.CLI_RECORDER.writeDetailedErrorList(e, this.BIOMODEL_BASE_NAME + ",  doc:    " + type + ": " + this.logDocumentError);
        biosimLog.updateSedmlDocStatusYml(this.SEDML_LOCATION, BiosimulationLog.Status.FAILED);
    }
}
