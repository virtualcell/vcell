package org.vcell.cli.run;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;

import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import org.vcell.cli.CLIRecordable;
import org.vcell.cli.PythonStreamException;
import org.vcell.cli.exceptions.ExecutionException;
import org.vcell.cli.vcml.VCMLHandler;
import org.vcell.util.FileUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ExecuteImpl {
    
    private final static Logger logger = LogManager.getLogger(ExecuteImpl.class);

    public static void batchMode(File dirOfArchivesToProcess, File outputDir, CLIRecordable cliLogger,
                                 boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bSmallMeshOverride
                                ) throws IOException {
        FilenameFilter filter = (f, name) -> name.endsWith(".omex") || name.endsWith(".vcml");
        File[] inputFiles = dirOfArchivesToProcess.listFiles(filter);
        List<String> failedFiles = new LinkedList<>();
        if (inputFiles == null) throw new RuntimeException("Error trying to retrieve files from input directory.");

        // Build statuses
        for (File inputFile : inputFiles){
            String bioModelBaseName = FileUtils.getBaseName(inputFile.getName());
            String outputBaseDir = outputDir.getAbsolutePath(); // bioModelBaseName = input file without the path
            String targetOutputDir = Paths.get(outputBaseDir, bioModelBaseName).toString();
            File adjustedOutputDir = new File(targetOutputDir);

            logger.info("Preparing output directory...");
            // we don't want to accidentally delete the input...
            // if the output is a subset of the input file's housing directory, we shouldn't delete!!
            if (!inputFile.getParentFile().getCanonicalPath().contains(adjustedOutputDir.getCanonicalPath()))
                RunUtils.removeAndMakeDirs(adjustedOutputDir);
            try {
                PythonCalls.generateStatusYaml(inputFile.getAbsolutePath(), targetOutputDir);    // generate Status YAML
            } catch (PythonStreamException e){
                throw new RuntimeException("Python call did not process correctly:", e);
            }
        }
        try {
            for (File inputFile : inputFiles) {
                String inputFileName = inputFile.getName();
                System.out.println("\n\n");
                logger.info("Processing " + inputFileName + "(" + inputFile + ")");
                try {
                    if (inputFileName.endsWith("vcml"))
                        singleExecVcml(inputFile, outputDir, cliLogger);
                    if (inputFileName.endsWith("omex"))
                        runSingleExecOmex(inputFile, outputDir, cliLogger,
                                bKeepTempFiles, bExactMatchOnly, bSmallMeshOverride);
                } catch (ExecutionException | RuntimeException | HDF5Exception e){
                    logger.error("Error caught executing batch mode", e);
                    failedFiles.add(inputFileName);
                } catch (Exception e){
                    failedFiles.add(inputFileName);
                    throw e;
                }
            }
            if (failedFiles.isEmpty()){
                logger.info("Execution finished with no failures");
                return;
            }
        } catch (Exception e) {
            StringBuilder failedFileString = new StringBuilder();
            logger.fatal("Fatal error caught executing batch mode (ending execution)", e);
            for (String f : failedFiles){
                failedFileString.append(String.format("\t- %s\n", f));
            }
            logger.fatal("Here's the list of all known failing models:\n" + failedFileString);

            throw new RuntimeException("Fatal error caught executing batch mode", e);
        }

        // We had failures.
        StringBuilder failedFileString = new StringBuilder();
        for (String f : failedFiles){
            failedFileString.append(String.format("\t- %s\n", f));
        }
        String errString = "Execution finished, but the following file(s) failed:\n" + failedFileString;
        logger.error(errString);
    }

    private static void runSingleExecOmex(File inputFile, File outputDir, CLIRecordable cliLogger, boolean bKeepTempFiles,
                                          boolean bExactMatchOnly, boolean bSmallMeshOverride)
            throws IOException, ExecutionException, PythonStreamException, HDF5Exception, InterruptedException {
        String bioModelBaseName = inputFile.getName().substring(0, inputFile.getName().indexOf(".")); // ".omex"??
        Files.createDirectories(Paths.get(outputDir.getAbsolutePath() + File.separator + bioModelBaseName)); // make output subdir
        final boolean bEncapsulateOutput = true;

        singleExecOmex(inputFile, outputDir, cliLogger,
                bKeepTempFiles, bExactMatchOnly, bEncapsulateOutput, bSmallMeshOverride);
    }

    public static void singleMode(File inputFile, File rootOutputDir, CLIRecordable cliLogger,
            boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bEncapsulateOutput, boolean bSmallMeshOverride
    ) throws Exception {
        // Build statuses
        String bioModelBaseName = FileUtils.getBaseName(inputFile.getName()); // bioModelBaseName = input file without the path
        String outputBaseDir = rootOutputDir.getAbsolutePath(); 
        String targetOutputDir = bEncapsulateOutput ? Paths.get(outputBaseDir, bioModelBaseName).toString() : outputBaseDir;
        File adjustedOutputDir = new File(targetOutputDir);

        logger.info("Preparing output directory...");
        // we don't want to accidentally delete the input...
        // if the output is a subset of the input file's housing directory, we shouldn't delete!!
        if (!inputFile.getParentFile().getCanonicalPath().contains(adjustedOutputDir.getCanonicalPath()))
            RunUtils.removeAndMakeDirs(adjustedOutputDir);

        PythonCalls.generateStatusYaml(inputFile.getAbsolutePath(), targetOutputDir);    // generate Status YAML

        ExecuteImpl.singleExecOmex(inputFile, rootOutputDir, cliLogger, bKeepTempFiles, bExactMatchOnly,
                bEncapsulateOutput, bSmallMeshOverride);
    }

    public static void singleMode(File inputFile, File outputDir, CLIRecordable cliLogger) throws Exception {
        final boolean bKeepTempFiles = false;
        final boolean bExactMatchOnly = false;
        final boolean bEncapsulateOutput = false;
        final boolean bSmallMeshOverride = false;

        ExecuteImpl.singleMode(inputFile, outputDir, cliLogger, bKeepTempFiles, bExactMatchOnly,
                bEncapsulateOutput, bSmallMeshOverride);
    }

    @Deprecated
    public static void singleExecVcml(File vcmlFile, File outputDir, CLIRecordable cliLogger) {
        logger.warn("Using deprecated function to execute vcml");
        VCMLHandler.outputDir = outputDir.getAbsolutePath();
        logger.debug("Executing VCML file " + vcmlFile);

        // from here on, we need to collect errors, since some subtasks may succeed while other do not
        boolean somethingFailed = false;
        HashMap<String, ODESolverResultSet> resultsHash;

        String vcmlName = vcmlFile.getAbsolutePath().substring(vcmlFile.getAbsolutePath().lastIndexOf(File.separator) + 1);
        File outDirForCurrentVcml = new File(Paths.get(outputDir.getAbsolutePath(), vcmlName).toString());

        try {
            // we don't want to accidentally delete the input...
            // if the output is a subset of the input file's housing directory, we shouldn't delete!!
            if (!vcmlFile.getParentFile().getCanonicalPath().contains(outDirForCurrentVcml.getCanonicalPath()))
                RunUtils.removeAndMakeDirs(outDirForCurrentVcml);
        } catch (Exception e) {
            logger.error("Error in creating required directories: " + e.getMessage(), e);
            somethingFailed = somethingDidFail();
        }

        // Run solvers and make reports; all failures/exceptions are being caught
        SolverHandler solverHandler = new SolverHandler();
        try {
            resultsHash = solverHandler.simulateAllVcmlTasks(vcmlFile, outDirForCurrentVcml);

            for (String simName : resultsHash.keySet()) {
                String CSVFilePath = Paths.get(outDirForCurrentVcml.toString(), simName + ".csv").toString();
                RunUtils.createCSVFromODEResultSet(resultsHash.get(simName), new File(CSVFilePath));
                PythonCalls.transposeVcmlCsv(CSVFilePath);
            }
        } catch (IOException e) {
            logger.error("IOException while processing VCML " + vcmlFile.getName(), e);
            somethingFailed = somethingDidFail();
        } catch (ExpressionException e) {
            logger.error("InterruptedException while creating results CSV from VCML " + vcmlFile.getName(), e);
            somethingFailed = somethingDidFail();
        } catch (InterruptedException e) {
            logger.error("InterruptedException while transposing CSV from VCML " + vcmlFile.getName(), e);
            somethingFailed = somethingDidFail();
        } catch (Exception e) {
            String errorMessage = String.format("Unexpected exception while transposing CSV from VCML <%s>\n%s", vcmlFile.getName(), e.toString());
            logger.error(errorMessage, e);
            somethingFailed = somethingDidFail();
        }

        logger.debug("Finished executing VCML file: " + vcmlFile);
        if (somethingFailed) {
            RuntimeException e = new RuntimeException("One or more errors encountered while executing VCML " + vcmlFile.getName());
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private static void singleExecOmex(File inputFile, File rootOutputDir, CLIRecordable cliRecorder,
            boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bEncapsulateOutput, boolean bSmallMeshOverride)
            throws ExecutionException, PythonStreamException, IOException, InterruptedException, HDF5Exception {

        ExecutionJob requestedExecution = new ExecutionJob(inputFile, rootOutputDir, cliRecorder,
            bKeepTempFiles, bExactMatchOnly, bEncapsulateOutput, bSmallMeshOverride);
        requestedExecution.preprocessArchive();
        requestedExecution.executeArchive();
        requestedExecution.postProcessessArchive();
    }

    private static boolean somethingDidFail(){
        StackTraceElement elem = new Exception().getStackTrace()[1];
        
        logger.debug(String.format("Something failed in %s @ line %d", elem.getClassName(), elem.getLineNumber()));
        return true;
    }
}
