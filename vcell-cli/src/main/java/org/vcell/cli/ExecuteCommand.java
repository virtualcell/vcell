package org.vcell.cli;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.xml.ExternalDocInfo;
import org.jlibsedml.*;
import org.vcell.cli.vcml.VCMLHandler;
import org.vcell.util.FileUtils;
import org.vcell.util.GenericExtensionFilter;
import org.vcell.util.exe.Executable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(name = "execute")
class ExecuteCommand implements Callable<Integer> {

    @Option(names = { "-i", "--inputFilePath" })
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath"})
    private File outputFilePath;

    @Option(names = {"--forceLogFiles"})
    private boolean bForceLogFiles;

    @Option(names = {"--keepTempFiles"})
    private boolean bKeepTempFiles;

    @Option(names = {"--exactMatchOnly"})
    private boolean bExactMatchOnly;

    @Option(names = {"--timeout_ms"}, description = "executable wall clock timeout in milliseconds")
    // timeout for compiled solver running long jobs; default 12 hours
    private long EXECUTABLE_MAX_WALLCLOK_MILLIS = 600000;

    public Integer call() {
        System.out.println("in execute()");
        try {
            PropertyLoader.loadProperties();
            CLIPythonManager.getInstance().instantiatePythonProcess();

            Executable.setTimeoutMS(EXECUTABLE_MAX_WALLCLOK_MILLIS);

            if (inputFilePath.isDirectory()) {
                batchMode(inputFilePath, outputFilePath, bKeepTempFiles, bExactMatchOnly, bForceLogFiles);
            } else {
                File archiveToProcess = inputFilePath;
                if (archiveToProcess.getName().endsWith("vcml")) {
                    singleExecVcml(archiveToProcess, outputFilePath);
                } else { // archiveToProcess.getName().endsWith("omex")
                    singleExecOmex(archiveToProcess, outputFilePath, bKeepTempFiles, bExactMatchOnly, bForceLogFiles);
                }
            }

            CLIPythonManager.getInstance().closePythonProcess(); // WARNING: Python will need reinstantiation after this is called
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void batchMode(File dirOfArchivesToProcess, File outputDir, boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bForceLogFiles) {
        FilenameFilter filter = (f, name) -> name.endsWith(".omex") || name.endsWith(".vcml");
        File[] inputFiles = dirOfArchivesToProcess.listFiles(filter);
        if (inputFiles == null) System.out.println("No input files found in the directory");
        assert inputFiles != null;

        createHeader(outputDir, bForceLogFiles);

        for (File inputFile : inputFiles) {
            String inputFileName = inputFile.getName();
            System.out.println(inputFile);
            try {
                if (inputFileName.endsWith("omex")) {
                    String bioModelBaseName = inputFileName.substring(0, inputFileName.indexOf(".")); // ".omex"??
                    Files.createDirectories(Paths.get(outputDir.getAbsolutePath() + File.separator + bioModelBaseName)); // make output subdir
                    singleExecOmex(inputFile, outputDir, bKeepTempFiles, bExactMatchOnly, bForceLogFiles);
                }

                if (inputFileName.endsWith("vcml")) {
                    singleExecVcml(inputFile, outputDir);
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private static void createHeader(File outputFilePath, boolean bForceLogFiles) {
        /**
         * Header Components:
         *  * base name of the omex file
         *  *   foreach sed-ml file:
         *  *   - (current) sed-ml file name
         *  *   - error(s) (if any)
         *  *   - number of models
         *  *   - number of sims
         *  *   - number of tasks
         *  *   - number of outputs
         *  *   - number of biomodels
         *  *   - number of succesful sims that we managed to run
         *  *   (NB: we assume that the # of failures = # of tasks - # of successful simulations)
         *  *   (NB: if multiple sedml files in the omex, we display on multiple rows, one for each sedml)
         */

        String header = "BaseName,SedML,Error,Models,Sims,Tasks,Outputs,BioModels,NumSimsSuccessful";
        try {
            writeDetailedResultList(outputFilePath.getAbsolutePath(), header, bForceLogFiles);
        } catch (IOException e1) {
            // not big deal, we just failed to make the header; we'll find out later what went wrong
            e1.printStackTrace();
        }
    }

    @Deprecated
    private static void singleExecVcml(File vcmlFile, File outputDir) {

        VCMLHandler.outputDir = outputDir.getAbsolutePath();
        System.out.println("VCell CLI input file " + vcmlFile);

        // from here on, we need to collect errors, since some subtasks may succeed while other do not
        boolean somethingFailed = false;
        HashMap<String, ODESolverResultSet> resultsHash;

        String vcmlName = vcmlFile.getAbsolutePath().substring(vcmlFile.getAbsolutePath().lastIndexOf(File.separator) + 1);
        File outDirForCurrentVcml = new File(Paths.get(outputDir.getAbsolutePath(), vcmlName).toString());

        try {
            CLIUtils.removeAndMakeDirs(outDirForCurrentVcml);
        } catch (Exception e) {
            System.err.println("Error in creating required directories: " + e.getMessage());
            e.printStackTrace(System.err);
            somethingFailed = true;
        }

        // Run solvers and make reports; all failures/exceptions are being caught
        SolverHandler solverHandler = new SolverHandler();
        try {
            resultsHash = solverHandler.simulateAllVcmlTasks(vcmlFile, outDirForCurrentVcml);

            for (String simName : resultsHash.keySet()) {
                String CSVFilePath = Paths.get(outDirForCurrentVcml.toString(), simName + ".csv").toString();
                CLIUtils.createCSVFromODEResultSet(resultsHash.get(simName), new File(CSVFilePath));
                CLIUtils.transposeVcmlCsv(CSVFilePath);
            }
        } catch (IOException e) {
            System.err.println("IOException while processing VCML " + vcmlFile.getName());
            e.printStackTrace(System.err);
            somethingFailed = true;
        } catch (ExpressionException e) {
            System.err.println("InterruptedException while creative results CSV from VCML " + vcmlFile.getName());
            e.printStackTrace(System.err);
            somethingFailed = true;
        } catch (InterruptedException e) {
            System.err.println("InterruptedException while transposing CSV from VCML " + vcmlFile.getName());
            e.printStackTrace(System.err);
            somethingFailed = true;
        } catch (Exception e) {
            System.err.println("Unexpected exception while transposing CSV from VCML " + vcmlFile.getName());
            System.err.println(e);
            e.printStackTrace(System.err);
            somethingFailed = true;
        }

        if (somethingFailed) {
            try {
                throw new Exception("One or more errors encountered while executing VCML " + vcmlFile.getName());
            } catch (Exception e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
        }
    }


    private static void singleExecOmex(File inputFile, File rootOutputDir,
                                       boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bForceLogFiles)
            throws Exception {
        int nModels, nSimulations, nSedml, nTasks, nOutputs, nReportsCount = 0, nPlots2DCount = 0, nPlots3DCount = 0;
        String inputFileName = inputFile.getAbsolutePath();
        String bioModelBaseName = FileUtils.getBaseName(inputFile.getName());
        String outputDir = Paths.get(rootOutputDir.getAbsolutePath(), bioModelBaseName).toString();
        String outputBaseDir = rootOutputDir.getAbsolutePath(); // bioModelBaseName = input file without the path
        OmexHandler omexHandler = null;
        List<String> sedmlLocations;
        List<Output> outputs;
        SedML sedml, sedmlFromPseudo;
        Path sedmlPath2d3d;
        File sedmlPathwith2dand3d;

        long startTimeOmex = System.currentTimeMillis();

        System.out.println("VCell CLI input archive " + inputFileName);
        CLIUtils.drawBreakLine("-", 100);
        try {
            sedmlPath2d3d = Paths.get(outputDir, "temp");
            omexHandler = new OmexHandler(inputFileName, outputDir);
            omexHandler.extractOmex();
            sedmlLocations = omexHandler.getSedmlLocationsAbsolute();
            nSedml = sedmlLocations.size();
            // any error up to now is fatal (unlikely, but still...)
        } catch (Throwable exc) {
            assert omexHandler != null;
            omexHandler.deleteExtractedOmex();
            String error = exc.getMessage() + ", error for archive " + inputFileName;
            writeErrorList(outputBaseDir, bioModelBaseName, bForceLogFiles);
            writeDetailedResultList(outputBaseDir, bioModelBaseName + ", " + ",unknown error with the archive file", bForceLogFiles);
            throw new Exception(error);
        }

        CLIUtils.removeAndMakeDirs(new File(outputDir));
        CLIUtils.generateStatusYaml(inputFileName, outputDir);    // generate Status YAML

        // from here on, we need to collect errors, since some subtasks may succeed while other do not
        // we now have the log file created, so that we also have a place to put them
        boolean oneSedmlDocumentSucceeded = false;    // set to true if at least one sedml document run is successful
        boolean oneSedmlDocumentFailed = false;        // set to true if at least one sedml document run fails

        String logOmexMessage = "";
        String logOmexError = "";        // not used for now
        for (String sedmlLocation : sedmlLocations) {        // for each sedml document

            boolean somethingFailed = false;        // shows that the current document suffered a partial or total failure

            String logDocumentMessage = "Initializing sedml document... ";
            String logDocumentError = "";

            HashMap<String, ODESolverResultSet> resultsHash;
            HashMap<String, File> reportsHash = null;
            String sedmlName = "";
            File completeSedmlPath = new File(sedmlLocation);
            File outDirForCurrentSedml = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));

            try {
                CLIUtils.removeAndMakeDirs(outDirForCurrentSedml);

                SedML sedmlFromOmex = Libsedml.readDocument(completeSedmlPath).getSedMLModel();

                String[] sedmlNameSplit;
                sedmlNameSplit = sedmlLocation.split(OperatingSystemInfo.getInstance().isWindows() ? "\\\\" : "/", -2);
                sedmlName = sedmlNameSplit[sedmlNameSplit.length - 1];
                logOmexMessage += "Processing " + sedmlName + ". ";

                nModels = sedmlFromOmex.getModels().size();
                nTasks = sedmlFromOmex.getTasks().size();
                outputs = sedmlFromOmex.getOutputs();
                nOutputs = outputs.size();
                for (Output output : outputs) {
                    if (output instanceof Report) nReportsCount++;
                    if (output instanceof Plot2D) nPlots2DCount++;
                    if (output instanceof Plot3D) nPlots3DCount++;
                }
                nSimulations = sedmlFromOmex.getSimulations().size();
                String summarySedmlContentString = "Found " + nSedml + " SED-ML document(s) with "
                        + nModels + " model(s), "
                        + nSimulations + " simulation(s), "
                        + nTasks + " task(s), "
                        + nReportsCount + "  report(s),  "
                        + nPlots2DCount + " plot2D(s), and "
                        + nPlots3DCount + " plot3D(s)\n";
                System.out.println(summarySedmlContentString);

                logDocumentMessage += "done. ";
                String str = "Successful translation of SED-ML file";
                logDocumentMessage += str + ". ";
                System.out.println(str + " : " + sedmlName);
                CLIUtils.drawBreakLine("-", 100);

                // For appending data for SED Plot2D and Plot3d to HDF5 files following a temp convention
                CLIUtils.genSedmlForSed2DAnd3D(inputFileName, outputDir);
                // SED-ML file generated by python VCell_cli_util
                sedmlPathwith2dand3d = new File(String.valueOf(sedmlPath2d3d), "simulation_" + sedmlName);
                Path path = Paths.get(sedmlPathwith2dand3d.getAbsolutePath());
                if (!Files.exists(path)) {
                    String message = "Failed to create file " + sedmlPathwith2dand3d.getAbsolutePath();
                    writeDetailedResultList(outputBaseDir, bioModelBaseName + "," + sedmlName + "," + message, bForceLogFiles);
                    throw new RuntimeException(message);
                }

                // Converting pseudo SED-ML to biomodel
                sedmlFromPseudo = Libsedml.readDocument(sedmlPathwith2dand3d).getSedMLModel();

                /* If SED-ML has only plots as an output, we will use SED-ML that got generated from vcell_cli_util python code
                 * As of now, we are going to create a resultant dataSet for Plot output, using their respective data generators */
                sedml = sedmlFromPseudo;

            } catch (Exception e) {
                String prefix = "SED-ML processing for " + sedmlLocation + " failed with error: ";
                logDocumentError = prefix + e.getMessage();
                String type = e.getClass().getSimpleName();
                CLIUtils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
                CLIUtils.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", type, logDocumentError);
                CLIUtils.writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError, bForceLogFiles);
                writeDetailedResultList(outputBaseDir, bioModelBaseName + "," + sedmlName + "," + logDocumentError, bForceLogFiles);

                System.err.println(prefix);
                e.printStackTrace(System.err);
                somethingFailed = true;
                oneSedmlDocumentFailed = true;
                CLIUtils.updateSedmlDocStatusYml(sedmlLocation, CLIUtils.Status.FAILED, outputDir);
                continue;
            }


            {
                //
                // temp code to test plot name correctness
                //
                //    		String idNamePlotsMap = utils.generateIdNamePlotsMap(sedml, outDirForCurrentSedml);
                //    		utils.execPlotOutputSedDoc(inputFile, idNamePlotsMap, outputDir);
            }


            // Run solvers and make reports; all failures/exceptions are being caught
            SolverHandler solverHandler = new SolverHandler();
            // we send both the whole OMEX file and the extracted SEDML file path
            // XmlHelper code uses two types of resolvers to handle absolute or relative paths
            ExternalDocInfo externalDocInfo = new ExternalDocInfo(new File(inputFileName), true);
            resultsHash = new LinkedHashMap<String, ODESolverResultSet>();
            try {
                String str = "Starting simulate all tasks... ";
                System.out.println(str);
                logDocumentMessage += str;
                resultsHash = solverHandler.simulateAllTasks(externalDocInfo, sedml, outDirForCurrentSedml, outputDir,
                        outputBaseDir, sedmlLocation, bKeepTempFiles, bExactMatchOnly, bForceLogFiles);
                Map<String, String> sim2Hdf5Map = solverHandler.sim2Hdf5Map;    // may not need it
            } catch (Exception e) {
                somethingFailed = true;
                oneSedmlDocumentFailed = true;
                logDocumentError = e.getMessage();        // probably the hash is empty
                // still possible to have some data in the hash, from some task that was successful - that would be partial success
            }

            String message = nModels + ",";
            message += nSimulations + ",";
            message += nTasks + ",";
            message += nOutputs + ",";
            message += solverHandler.countBioModels + ",";
            message += solverHandler.countSuccessfulSimulationRuns;
            writeDetailedResultList(outputBaseDir, bioModelBaseName + "," + sedmlName + ", ," + message, bForceLogFiles);

            //
            // WARNING!!! Current logic dictates that if any task fails we fail the sedml document
            // change implemented on Nov 11, 2021
            // Previous logic was that if at least one task produces some results we declare the sedml document status as successful
            // that will include spatial simulations for which we don't produce reports or plots!
            //
            try {
                if (resultsHash.containsValue(null)) {        // some tasks failed, but not all
                    oneSedmlDocumentFailed = true;
                    somethingFailed = true;
                    logDocumentMessage += "Failed to execute one or more tasks. ";
                }
                logDocumentMessage += "Generating outputs... ";

                reportsHash = CLIUtils.generateReportsAsCSV(sedml, resultsHash, outDirForCurrentSedml, outputDir, sedmlLocation);

                if (reportsHash == null || reportsHash.size() == 0) {
                    oneSedmlDocumentFailed = true;
                    somethingFailed = true;
                    String msg = "Failed to generate any reports. ";
                    throw new RuntimeException(msg);
                }
                if (reportsHash.containsValue(null)) {
                    oneSedmlDocumentFailed = true;
                    somethingFailed = true;
                    String msg = "Failed to generate one or more reports. ";
                    logDocumentMessage += msg;
                } else {
                    logDocumentMessage += "Done. ";
                }

                logDocumentMessage += "Generating HDF5 file... ";
                String idNamePlotsMap = CLIUtils.generateIdNamePlotsMap(sedml, outDirForCurrentSedml);

                CLIUtils.execPlotOutputSedDoc(inputFileName, idNamePlotsMap, outputDir);                            // create the HDF5 file

                if (!containsExtension(outputDir, "h5")) {
                    oneSedmlDocumentFailed = true;
                    somethingFailed = true;
                    throw new RuntimeException("Failed to generate the HDF5 output file. ");
                } else {
                    logDocumentMessage += "Done. ";
                }

                genPlotsPseudoSedml(sedmlLocation, outDirForCurrentSedml.toString());    // generate the plots
                oneSedmlDocumentSucceeded = true;
            } catch (Exception e) {
                somethingFailed = true;
                oneSedmlDocumentFailed = true;
                logDocumentError += e.getMessage();
                String type = e.getClass().getSimpleName();
                CLIUtils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
                CLIUtils.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", type, logDocumentError);
                CLIUtils.writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError, bForceLogFiles);
                CLIUtils.updateSedmlDocStatusYml(sedmlLocation, CLIUtils.Status.FAILED, outputDir);
                org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));    // removing temp path generated from python
                continue;
            }

            if (somethingFailed == true) {        // something went wrong but no exception was fired
                Exception e = new RuntimeException("Failure executing the sed document. ");
                logDocumentError += e.getMessage();
                String type = e.getClass().getSimpleName();
                CLIUtils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
                CLIUtils.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", type, logDocumentError);
                CLIUtils.writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError, bForceLogFiles);
                CLIUtils.updateSedmlDocStatusYml(sedmlLocation, CLIUtils.Status.FAILED, outputDir);
                org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));    // removing temp path generated from python
                continue;
            }
            org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));    // removing temp path generated from python

            // archiving res files
            CLIUtils.zipResFiles(new File(outputDir));
            CLIUtils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
            CLIUtils.updateSedmlDocStatusYml(sedmlLocation, CLIUtils.Status.SUCCEEDED, outputDir);
        }

        omexHandler.deleteExtractedOmex();

        long endTimeOmex = System.currentTimeMillis();
        long elapsedTime = endTimeOmex - startTimeOmex;
        int duration = (int) Math.ceil(elapsedTime / 1000.0);

        //
        // failure if at least one of the documents in the omex archive fails
        //

        if (oneSedmlDocumentFailed) {
            String error = " All sedml documents in this archive failed to execute";
            if (oneSedmlDocumentSucceeded) {        // some succeeded, some failed
                error = " At least one document in this archive failed to execute";
            }
            CLIUtils.updateOmexStatusYml(CLIUtils.Status.FAILED, outputDir, duration + "");
            System.err.println(error);
            logOmexMessage += error;
            writeErrorList(outputBaseDir, bioModelBaseName, bForceLogFiles);
        } else {
            CLIUtils.updateOmexStatusYml(CLIUtils.Status.SUCCEEDED, outputDir, duration + "");
            writeFullSuccessList(outputBaseDir, bioModelBaseName, bForceLogFiles);
            logOmexMessage += " Done";
        }
        CLIUtils.setOutputMessage("null", "null", outputDir, "omex", logOmexMessage);

    }

    private static boolean containsExtension(String folder, String ext) {
        GenericExtensionFilter filter = new GenericExtensionFilter(ext);
        File dir = new File(folder);
        if (dir.isDirectory() == false) {
            return false;
        }
        String[] list = dir.list(filter);
        if (list.length > 0) {
            return true;
        }
        return false;
    }

    private static void writeFullSuccessList(String outputBaseDir, String s, boolean bForceLogFiles) throws IOException {
        if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
            String dest = outputBaseDir + File.separator + "fullSuccessLog.txt";
            Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    // we just make a list with the omex files that failed
    private static void writeErrorList(String outputBaseDir, String s, boolean bForceLogFiles) throws IOException {
        if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
            String dest = outputBaseDir + File.separator + "errorLog.txt";
            Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    private static void writeDetailedResultList(String outputBaseDir, String s, boolean bForceLogFiles) throws IOException {
        if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
            String dest = outputBaseDir + File.separator + "detailedResultLog.txt";
            Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    // Due to what appears to be a leaky python function call, this method will continue using execShellCommand until the unerlying python is fixed
    private static void genPlotsPseudoSedml(String sedmlPath, String resultOutDir) throws PythonStreamException, InterruptedException, IOException {
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


}
