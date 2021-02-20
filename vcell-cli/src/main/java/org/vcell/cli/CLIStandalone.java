package org.vcell.cli;

import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.xml.ExternalDocInfo;
import org.jlibsedml.Libsedml;
import org.jlibsedml.Output;
import org.jlibsedml.Report;
import org.jlibsedml.SedML;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CLIStandalone {
    public static void main(String[] args) {

        File input = null;

        // Arguments may not always be files, trying for other scenarios
        try {
            input = new File(args[1]);
        } catch (Exception e1) {
            // Non file or invalid argument received, let it pass, CLIHandler will handle the invalid (or non file) arguments
        }

        if (input != null && input.isDirectory()) {
            FilenameFilter filter = (f, name) -> name.endsWith(".omex");
            String[] omexFiles = input.list(filter);
            for (String omexFile : omexFiles) {
                File file = new File(input, omexFile);
                System.out.println(file);
                args[1] = file.toString();
                try {
                    singleExec(args);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        } else {
            try {
                singleExec(args);
            } catch (Exception e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
        }
    }

    private static void singleExec(String[] args) throws Exception {
        OmexHandler omexHandler = null;
        CLIHandler cliHandler;
        String inputFile;
        String outputDir;
        ArrayList<String> sedmlLocations;
        int nModels;
        int nSimulations;
        int nSedml;
        int nTasks;
        List<Output> PlotsReports;
        int nReportsCount = 0;
        int nPlotsCount = 0;

        try {
            cliHandler = new CLIHandler(args);
            inputFile = cliHandler.getInputFilePath();
            outputDir = cliHandler.getOutputDirPath();
            System.out.println("VCell CLI input archive " + inputFile);
            CLIUtils.drawBreakLine("-", 100);
            omexHandler = new OmexHandler(inputFile, outputDir);
            omexHandler.extractOmex();
            sedmlLocations = omexHandler.getSedmlLocationsAbsolute();
            nSedml = sedmlLocations.size();
            // any error up to now is fatal (unlikely, but still...)
        } catch (Throwable exc) {
            assert omexHandler != null;
            omexHandler.deleteExtractedOmex();
            String error = "======> FAILED OMEX handling for archive " + args[1];
            throw new Exception(error);
        }
        // from here on, we need to collect errors, since some subtasks may succeed while other do not
        boolean somethingFailed = false;
        for (String sedmlLocation : sedmlLocations) {
            HashMap<String, ODESolverResultSet> resultsHash;
            HashMap<String, File> reportsHash;
            String sedmlName;
            File completeSedmlPath = new File(sedmlLocation);
            File outDirForCurrentSedml = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));
            SedML sedml;
            try {
                CLIUtils.makeDirs(outDirForCurrentSedml);
                sedml = Libsedml.readDocument(completeSedmlPath).getSedMLModel();
                String[] sedmlNameSplit;
                if (CLIUtils.isWindowsPlatform) {
                    sedmlNameSplit = sedmlLocation.split("\\\\", -2);
                } else {
                    sedmlNameSplit = sedmlLocation.split("/", -2);
                }
                sedmlName = sedmlNameSplit[sedmlNameSplit.length - 1];
                nModels = sedml.getModels().size();
                nTasks = sedml.getTasks().size();
                PlotsReports = sedml.getOutputs();
                for (Output data : PlotsReports) {
                    if (!(data instanceof Report)) nPlotsCount++;
                    else nReportsCount++;
                }
                nSimulations = sedml.getSimulations().size();
                String summarySedmlContentString = "Found " + nSedml + " SED-ML document(s) with "
                        + nModels + " model(s), "
                        + nSimulations + " simulation(s), "
                        + nTasks + " task(s), "
                        + nReportsCount + "  report(s), and "
                        + nPlotsCount + " plot(s)\n";
                System.out.println(summarySedmlContentString);
                System.out.println("Successful translation: SED-ML file " + sedmlName);
                CLIUtils.drawBreakLine("-", 100);
            } catch (Exception e) {
                System.err.println("SED-ML processing for " + sedmlLocation + " failed with error: " + e.getMessage());
                e.printStackTrace(System.err);
                somethingFailed = true;
                continue;
            }
            // Run solvers and make reports; all failures/exceptions are being caught
            SolverHandler solverHandler = new SolverHandler();
            // send the the whole OMEX file since we do better handling of malformed model URIs in XMLHelper code
            ExternalDocInfo externalDocInfo = new ExternalDocInfo(new File(inputFile), true);
            resultsHash = solverHandler.simulateAllTasks(externalDocInfo, sedml, outDirForCurrentSedml);
            // python installation
            CLIUtils.checkPythonInstallation();
            // pip install requirements before status generation
            CLIUtils.pipInstallRequirements();
            CLIUtils.generateStatusYaml(inputFile, outputDir);
            reportsHash = CLIUtils.generateReportsAsCSV(sedml, resultsHash, outDirForCurrentSedml, sedmlName);


            // HDF5 conversion
            CLIUtils.convertCSVtoHDF(Paths.get(outputDir, sedmlName).toString(), sedmlLocation, Paths.get(outputDir, sedmlName).toString());

            if (resultsHash.containsValue(null) || reportsHash.containsValue(null)) {
                somethingFailed = true;
            }
        }
        CLIUtils.finalStatusUpdate(CLIUtils.Status.SUCCEEDED, outputDir);
        omexHandler.deleteExtractedOmex();
        if (somethingFailed) {
            String error = "One or more errors encountered while executing archive " + args[1];
            CLIUtils.finalStatusUpdate(CLIUtils.Status.FAILED, outputDir);
            throw new Exception(error);
        }
    }
}
