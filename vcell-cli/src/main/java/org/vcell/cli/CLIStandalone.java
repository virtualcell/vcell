package org.vcell.cli;

import cbit.vcell.xml.ExternalDocInfo;
import org.jlibsedml.Libsedml;
import org.jlibsedml.SedML;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

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
                e.printStackTrace(System.err);
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
            omexHandler = new OmexHandler(inputFile, outputDir);
            omexHandler.extractOmex();
            sedmlLocations = omexHandler.getSedmlLocationsAbsolute();
            // any error up to now is fatal (unlikely, but still...)
        } catch (Throwable exc) {
            assert omexHandler != null;
            omexHandler.deleteExtractedOmex();
            String error = "======> FAILED OMEX handling for archive " + args[1];
            exc.printStackTrace(System.err);
            throw new Exception(error);
        }
        // from here on, we need to collect errors, since some subtasks may succeed while other do not
        boolean somethingWorked = false;
        for (String sedmlLocation : sedmlLocations) {
            try {
                File completeSedmlPath = new File(sedmlLocation);
                File outDirForCurrentSedml = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));
                CLIUtils.makeDirs(outDirForCurrentSedml);
                // Run solvers
                SedML sedml = Libsedml.readDocument(completeSedmlPath).getSedMLModel();
                SolverHandler solverHandler = new SolverHandler();
                // send the the whole omex file since we do better handling of malformed model URIs in XMLHelper code
                ExternalDocInfo externalDocInfo = new ExternalDocInfo(new File(inputFile), true);
                somethingWorked = solverHandler.simulateAllTasks(externalDocInfo, sedml, outDirForCurrentSedml);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace(System.err);
            }
        }
        omexHandler.deleteExtractedOmex();
        if (!somethingWorked) {
            String error = "======> Could not execute any task from archive " + args[1];
            throw new Exception(error);
        }
    }
}

