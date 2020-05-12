package org.vcell.cli;

import java.util.ArrayList;

public class CLIStandalone {
    public static void main(String[] args) {
        CLIHandler cliHandler = new CLIHandler(args);
        String inputFile = cliHandler.getInputFilePath();
        String outputDir = cliHandler.getOutputDirPath();
        OmexHandler omexHandler = new OmexHandler(inputFile, outputDir);
        omexHandler.extractOmex();

        ArrayList<String> sedmlLocations = omexHandler.getSedmlLocationsAbsolute();
        for(String location: sedmlLocations) {
            String outDirForCurrentSedml = omexHandler.getOutputPathFromSedml(location);
            // Run solvers
            // dowWork(sedmlPath, outDir)
        }

        // At the end
        omexHandler.deleteExtractedOmex();
        System.out.println("CLI works");
    }
}
