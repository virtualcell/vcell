package org.vcell.cli;

import cbit.vcell.xml.ExternalDocInfo;
import org.jlibsedml.AbstractTask;
import org.jlibsedml.Libsedml;
import org.jlibsedml.SedML;
import org.jlibsedml.XMLException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class CLIStandalone {
    public static void main(String[] args) {

//        CLIUtils.makeDirs(new File(outputDir));

		File input = null;
        try {
			input = new File(args[1]);
		} catch (ArrayIndexOutOfBoundsException e ) {
//        	e.printStackTrace();
		}

        
        OmexHandler omexHandler;
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
                args[1]=file.toString();
                singleExec(args);
            }
        } else {
            singleExec(args);
        }
    }

    private static void singleExec(String[] args) {
        try {
            OmexHandler omexHandler;
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
            omexHandler = new OmexHandler(inputFile, outputDir);
            omexHandler.extractOmex();
            ArrayList<String> sedmlLocations = omexHandler.getSedmlLocationsAbsolute();
            for (String sedmlLocation : sedmlLocations) {
                File completeSedmlPath = new File(sedmlLocation);
                File outDirForCurrentSedml = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));
                CLIUtils.makeDirs(outDirForCurrentSedml);
                // Run solvers
                SedML sedml = null;
                sedml = Libsedml.readDocument(completeSedmlPath).getSedMLModel();
                SolverHandler solverHandler = new SolverHandler();
                // send the omex file since we do better handling of malformed model URIs in XMLHelper code
                ExternalDocInfo externalDocInfo = new ExternalDocInfo(new File(inputFile), true);
                solverHandler.simulateAllTasks(externalDocInfo, sedml, outDirForCurrentSedml);
            }
            // At the end
            omexHandler.deleteExtractedOmex();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            System.err.println("======> FAILED omex execution for archive "+args[1]);
            e.printStackTrace(System.err);
        }
    }
}