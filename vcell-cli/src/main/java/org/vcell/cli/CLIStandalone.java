package org.vcell.cli;

import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.xml.ExternalDocInfo;
import org.jlibsedml.Libsedml;
import org.jlibsedml.SedML;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

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
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    return name.endsWith(".omex");
                }
            };
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
        CLIHandler cliHandler = null;
        String inputFile = null;
        String outputDir = null;
        ArrayList<String> sedmlLocations = null;
        try {
            cliHandler = new CLIHandler(args);
            inputFile = cliHandler.getInputFilePath();
            outputDir = cliHandler.getOutputDirPath();
            System.out.println("VCell CLI input archive " + inputFile);
            System.out.println(CLIUtils.breakLine);
            omexHandler = new OmexHandler(inputFile, outputDir);
            omexHandler.extractOmex();
            sedmlLocations = omexHandler.getSedmlLocationsAbsolute();
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
            HashMap<String, ODESolverResultSet> resultsHash = null;
            HashMap<String, File> reportsHash = null;
            String sedmlName = null;
            File completeSedmlPath = new File(sedmlLocation);
            File outDirForCurrentSedml = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));
            SedML sedml = null;
            try {
                CLIUtils.makeDirs(outDirForCurrentSedml);
                sedml = Libsedml.readDocument(completeSedmlPath).getSedMLModel();
                String[] sedmlNameSplit;
                if (CLIUtils.windowsPlatform) {
                    sedmlNameSplit = sedmlLocation.split("\\\\", -2);
                } else {
                    sedmlNameSplit = sedmlLocation.split("/", -2);
                }
                sedmlName = sedmlNameSplit[sedmlNameSplit.length - 1];
                System.out.println("Successful translation: SED-ML file " + sedmlName);
                System.out.println(CLIUtils.breakLine);
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
            reportsHash = CLIUtils.generateReportsAsCSV(sedml, resultsHash, outDirForCurrentSedml);
            /*
            Note:
            Internally biosimulators_utils python package uses a capturer package, which is developed for UNIX based systems.
            Either way we need to find an alternate for capturer on windows in biosimulators_utils
            */
            if (!CLIUtils.windowsPlatform) {
                if (CLIUtils.checkPythonInstallation() == 0) {
                    CLIUtils.convertCSVtoHDF(Paths.get(outputDir, sedmlName).toString(), sedmlLocation, Paths.get(outputDir, sedmlName).toString());
                } else {
                    if (CLIUtils.checkPythonInstallation() != 0) {
                        System.err.println("Python installation required");
                        System.err.println("Update submodule codebase");
                    }
                    System.err.println("HDF5 conversion failed...");
                }
            }
            if (resultsHash.containsValue(null) || reportsHash.containsValue(null)) {
                somethingFailed = true;
            }
        }
        omexHandler.deleteExtractedOmex();
        if (somethingFailed) {
            String error = "======> One or more errors encountered while executing archive " + args[1];
            throw new Exception(error);
        }
    }
}

