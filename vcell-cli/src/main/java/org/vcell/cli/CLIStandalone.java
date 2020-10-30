package org.vcell.cli;

import cbit.vcell.xml.ExternalDocInfo;
import org.jlibsedml.AbstractTask;
import org.jlibsedml.Libsedml;
import org.jlibsedml.SedML;
import org.jlibsedml.XMLException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CLIStandalone {
    public static void main(String[] args) {
        CLIHandler cliHandler = null;
        try {
            cliHandler = new CLIHandler(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputFile = null;
        if (cliHandler != null) {
            inputFile = cliHandler.getInputFilePath();
        }
        String outputDir = null;
        if (cliHandler != null) {
            outputDir = cliHandler.getOutputDirPath();
        }
//        CLIUtils.makeDirs(new File(outputDir));
        OmexHandler omexHandler = new OmexHandler(inputFile, outputDir);
        omexHandler.extractOmex();

        ArrayList<String> sedmlLocations = omexHandler.getSedmlLocationsAbsolute();
        for(String sedmlLocation: sedmlLocations) {
            File completeSedmlPath = new File(sedmlLocation);
            File outDirForCurrentSedml = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));
            CLIUtils.makeDirs(outDirForCurrentSedml);
            // Run solvers
            SedML sedml = null;
            try {
                sedml = Libsedml.readDocument(completeSedmlPath).getSedMLModel();
            } catch (XMLException xmlEx) {
                System.err.println("Unable to parse SED-ML file, failed with error: " + xmlEx.getMessage());
                System.exit(1);
            }

            if (sedml == null || sedml.getModels().isEmpty()) {
                System.err.println("The SED-ML file '" + completeSedmlPath.getName() + "'does not contain a valid document");
                System.exit(99);
            }

            SolverHandler solverHandler = new SolverHandler();
            ExternalDocInfo externalDocInfo = new ExternalDocInfo(completeSedmlPath, true);
            for (AbstractTask abstractTask : sedml.getTasks()) {
                solverHandler.simulateTask(externalDocInfo, abstractTask, sedml, outDirForCurrentSedml);
            }
        }


        // At the end
        omexHandler.deleteExtractedOmex();
    }
}
