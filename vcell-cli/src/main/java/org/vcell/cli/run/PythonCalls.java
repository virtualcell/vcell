package org.vcell.cli.run;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.PythonStreamException;

import java.io.IOException;


public class PythonCalls {

    private final static Logger logger = LogManager.getLogger(PythonCalls.class);

    public static void genSedmlForSed2DAnd3D(String omexFilePath, String outputDir) throws PythonStreamException {
        logger.trace("Dialing Python function genSedml2d3d");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genSedml2d3d", omexFilePath, outputDir);
        cliPythonManager.parsePythonReturn(results, "", "Failed generating SED-ML for plot2d and 3D ");
    }

    public static void genPlots(String sedmlPath, String resultOutDir) throws PythonStreamException {
        logger.trace("Dialing Python function genPlotPdfs");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genPlotPdfs", sedmlPath, resultOutDir);
        cliPythonManager.parsePythonReturn(results);
    }

    public static void transposeVcmlCsv(String csvFilePath) throws PythonStreamException {
        logger.trace("Dialing Python function transposeVcmlCsv");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("transposeVcmlCsv", csvFilePath);
        cliPythonManager.parsePythonReturn(results);
    }

    // Due to what appears to be a leaky python function call, this method will continue using execShellCommand until the underlying python is fixed
    public static void genPlotsPseudoSedml(String sedmlPath, String resultOutDir) throws PythonStreamException, InterruptedException, IOException {
        logger.trace("Dialing Python function genPlotsPseudoSedml");
        //CLIPythonManager.callNonSharedPython("genPlotsPseudoSedml", sedmlPath, resultOutDir);
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genPlotsPseudoSedml", sedmlPath, resultOutDir);
        cliPythonManager.parsePythonReturn(results);
    }

    private static String stripIllegalChars(String s){
        StringBuilder fStr = new StringBuilder();
        for (char c : s.toCharArray()){
            char cAppend = ((int)c) < 16 ? ' ' : c;
            if (cAppend == '"') 
            	cAppend = '\'';
            fStr.append(cAppend);
        }
        return fStr.toString();
    }
}
