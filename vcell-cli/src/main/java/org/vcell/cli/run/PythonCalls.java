package org.vcell.cli.run;

import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.PythonStreamException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class PythonCalls {

    private final static Logger logger = LogManager.getLogger(PythonCalls.class);

    public static void genSedmlForSed2DAnd3D(String omexFilePath, String outputDir) throws PythonStreamException {
        logger.trace("Dialing Python function genSedml2d3d");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genSedml2d3d", omexFilePath, outputDir);
        cliPythonManager.parsePythonReturn(results, "", "Failed generating SED-ML for plot2d and 3D ");
    }

    public static void updateTaskStatusYml(String sedmlName, String taskName, Status taskStatus, String outDir, String duration, String algorithm) throws PythonStreamException {
        algorithm = algorithm.toUpperCase(Locale.ROOT);
        algorithm = algorithm.replace("KISAO:", "KISAO_");

        logger.trace("Dialing Python function updateTaskStatus");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updateTaskStatus", sedmlName, taskName, taskStatus.toString(), outDir, duration, algorithm);
        cliPythonManager.parsePythonReturn(results, "", "Failed updating task status YAML\n");
    }
    public static void updateSedmlDocStatusYml(String sedmlName, Status sedmlDocStatus, String outDir) throws PythonStreamException, InterruptedException, IOException {
        logger.trace("Dialing Python function updateSedmlDupdateSedmlDocStatusocStatus");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updateSedmlDocStatus", sedmlName, sedmlDocStatus.toString(), outDir);
        cliPythonManager.parsePythonReturn(results, "", "Failed updating sedml document status YAML\n");
    }
    public static void updateOmexStatusYml(Status simStatus, String outDir, String duration) throws PythonStreamException {
        logger.trace("Dialing Python function updateOmexStatus");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updateOmexStatus", simStatus.toString(), outDir, duration);
        cliPythonManager.parsePythonReturn(results);
    }

    public static void genPlots(String sedmlPath, String resultOutDir) throws PythonStreamException {
        logger.trace("Dialing Python function genPlotPdfs");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genPlotPdfs", sedmlPath, resultOutDir);
        cliPythonManager.parsePythonReturn(results);
    }

    // sedmlAbsolutePath - full path to location of the actual sedml file (document) used as input
    // entityId          - ex: task_0_0 for task, or biomodel_20754836.sedml for a sedml document
    // outDir            - path to directory where the log files will be placed
    // entityType        - string describing the entity type ex "task" for a task, or "sedml" for sedml document
    // message           - useful info about the execution of the entity (ex: task), could be human readable or concatenation of stdout and stderr
    public static void setOutputMessage(String sedmlAbsolutePath, String entityId, String outDir, String entityType, String message) throws PythonStreamException, InterruptedException, IOException {
        logger.trace("Dialing Python function setOutputMessage");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("setOutputMessage", sedmlAbsolutePath, entityId, outDir, entityType, message);
        cliPythonManager.parsePythonReturn(results, "", "Failed updating task status YAML\n");
    }

    // type - exception class, ex RuntimeException
    // message  - exception message
    public static void setExceptionMessage(String sedmlAbsolutePath, String entityId, String outDir, String entityType, String type , String message) throws PythonStreamException {
        logger.trace("Dialing Python function setExceptionMessage");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("setExceptionMessage", sedmlAbsolutePath, entityId, outDir, entityType, type, stripIllegalChars(message));
        cliPythonManager.parsePythonReturn(results, "", "Failed updating task status YAML\n");
    }

    public void convertCSVtoHDF(String omexFilePath, String outputDir, CLIPythonManager cliPythonManager) throws PythonStreamException {

        // Convert CSV to HDF5
        /*
        Usage: cli.py SEDML_FILE_PATH WORKING_DIR BASE_OUT_PATH CSV_DIR <flags>
                    optional flags:        --rel_out_path | --apply_xml_model_changes |
                         --report_formats | --plot_formats | --log | --indent
        * */
        // handle exceptions here
        logger.trace("Dialing Python function execSedDoc");
        String results = cliPythonManager.callPython("execSedDoc", omexFilePath, outputDir);
        cliPythonManager.parsePythonReturn(results, "HDF conversion successful\n","HDF conversion failed\n");
    }

    // Sample STATUS YML
    /*
    sedDocuments:
      BIOMD0000000912_sim.sedml:
        outputs:
          BIOMD0000000912_sim:
            dataSets:
              data_set_E: SKIPPED
              data_set_I: PASSED
              data_set_T: SKIPPED
              data_set_time: SKIPPED
            status: SKIPPED
          plot_1:
            curves:
              plot_1_E_time: SKIPPED
              plot_1_I_time: SKIPPED
              plot_1_T_time: SKIPPED
            status: SKIPPED
        status: SUCCEEDED
        tasks:
          BIOMD0000000912_sim:
            status: SKIPPED
    status: SUCCEEDED
    * */
    public static void generateStatusYaml(String omexPath, String outDir) throws PythonStreamException {
        // Note: by default every status is being skipped
        Path omexFilePath = Paths.get(omexPath);
        /*
         USAGE:

         NAME
         status.py

         SYNOPSIS
         status.py COMMAND

         COMMANDS
         COMMAND is one of the following:

         status_yml
        */

        logger.trace("Dialing Python function genStatusYaml");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genStatusYaml", String.valueOf(omexFilePath), outDir);
        cliPythonManager.parsePythonReturn(results, "", "Failed generating status YAML\n");
    }

    public static void transposeVcmlCsv(String csvFilePath) throws PythonStreamException {
        logger.trace("Dialing Python function transposeVcmlCsv");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("transposeVcmlCsv", csvFilePath);
        cliPythonManager.parsePythonReturn(results);
    }

    public static void updateDatasetStatusYml(String sedmlName, String dataSet, String var, Status simStatus, String outDir) throws PythonStreamException {
        logger.trace("Dialing Python function updateDataSetStatus");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updateDataSetStatus", sedmlName, dataSet, var, simStatus.toString(), outDir);
        cliPythonManager.parsePythonReturn(results);
    }

    public static void updatePlotStatusYml(String sedmlName, String var, Status simStatus, String outDir) throws PythonStreamException {
        logger.trace("Dialing Python function updatePlotStatus");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updatePlotStatus", sedmlName, var, simStatus.toString(), outDir);
        cliPythonManager.parsePythonReturn(results);
    }

    // Due to what appears to be a leaky python function call, this method will continue using execShellCommand until the unerlying python is fixed
    public static void genPlotsPseudoSedml(String sedmlPath, String resultOutDir) throws PythonStreamException, InterruptedException, IOException {
        logger.trace("Dialing Python function genPlotsPseudoSedml");
        CLIPythonManager.callNonSharedPython("genPlotsPseudoSedml", sedmlPath, resultOutDir);

        /**
         * replace with the following once the leak is fixed
         */
//        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
//        String results = cliPythonManager.callPython("genPlotsPseudoSedml", sedmlPath, resultOutDir);
//        cliPythonManager.printPythonErrors(results);
    }

    private static String stripIllegalChars(String s){
        String fStr = "";
        for (char c : s.toCharArray()){
            char cAppend = ((int)c) < 16 ? ' ' : c;
            if (cAppend == '"') 
            	cAppend = '\'';
            fStr += cAppend;
        }
        return fStr;
    }
}
