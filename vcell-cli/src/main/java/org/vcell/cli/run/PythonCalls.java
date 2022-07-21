package org.vcell.cli.run;

import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIUtils;
import org.vcell.cli.PythonStreamException;

import java.io.IOException;
import java.util.Locale;

public class PythonCalls {

    public static void genSedmlForSed2DAnd3D(String omexFilePath, String outputDir) throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genSedml2d3d", omexFilePath, outputDir);
        cliPythonManager.printPythonErrors(results, "", "Failed generating SED-ML for plot2d and 3D ");
    }

    public static void execPlotOutputSedDoc(String omexFilePath, String idNamePlotsMap, String outputDir)  throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("execPlotOutputSedDoc", omexFilePath, idNamePlotsMap, outputDir);
        cliPythonManager.printPythonErrors(results, "HDF conversion successful\n","HDF conversion failed\n");
    }

    public static void updateTaskStatusYml(String sedmlName, String taskName, Status taskStatus, String outDir, String duration, String algorithm) throws PythonStreamException {
        algorithm = algorithm.toUpperCase(Locale.ROOT);
        algorithm = algorithm.replace("KISAO:", "KISAO_");

        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updateTaskStatus", sedmlName, taskName, taskStatus.toString(), outDir, duration, algorithm);
        cliPythonManager.printPythonErrors(results, "", "Failed updating task status YAML\n");
    }
    public static void updateSedmlDocStatusYml(String sedmlName, Status sedmlDocStatus, String outDir) throws PythonStreamException, InterruptedException, IOException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updateSedmlDocStatus", sedmlName, sedmlDocStatus.toString(), outDir);
        cliPythonManager.printPythonErrors(results, "", "Failed updating sedml document status YAML\n");
    }
    public static void updateOmexStatusYml(Status simStatus, String outDir, String duration) throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("updateOmexStatus", simStatus.toString(), outDir, duration);
        cliPythonManager.printPythonErrors(results);
    }

    public static void genPlots(String sedmlPath, String resultOutDir) throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genPlotPdfs", sedmlPath, resultOutDir);
        cliPythonManager.printPythonErrors(results);
    }

    // sedmlAbsolutePath - full path to location of the actual sedml file (document) used as input
    // entityId          - ex: task_0_0 for task, or biomodel_20754836.sedml for a sedml document
    // outDir            - path to directory where the log files will be placed
    // entityType        - string describing the entity type ex "task" for a task, or "sedml" for sedml document
    // message           - useful info about the execution of the entity (ex: task), could be human readable or concatenation of stdout and stderr
    public static void setOutputMessage(String sedmlAbsolutePath, String entityId, String outDir, String entityType, String message) throws PythonStreamException, InterruptedException, IOException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("setOutputMessage", sedmlAbsolutePath, entityId, outDir, entityType, message);
        cliPythonManager.printPythonErrors(results, "", "Failed updating task status YAML\n");
    }

    // type - exception class, ex RuntimeException
    // message  - exception message
    public static void setExceptionMessage(String sedmlAbsolutePath, String entityId, String outDir, String entityType, String type , String message) throws PythonStreamException {
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("setExceptionMessage", sedmlAbsolutePath, entityId, outDir, entityType, type, message);
        cliPythonManager.printPythonErrors(results, "", "Failed updating task status YAML\n");
    }

    public void convertCSVtoHDF(String omexFilePath, String outputDir, CLIPythonManager cliPythonManager) throws PythonStreamException {

        // Convert CSV to HDF5
        /*
        Usage: cli.py SEDML_FILE_PATH WORKING_DIR BASE_OUT_PATH CSV_DIR <flags>
                    optional flags:        --rel_out_path | --apply_xml_model_changes |
                         --report_formats | --plot_formats | --log | --indent
        * */
        // handle exceptions here
        if (cliPythonManager.checkPythonInstallationError() == 0) {
            String results = cliPythonManager.callPython("execSedDoc", omexFilePath, outputDir);
            cliPythonManager.printPythonErrors(results, "HDF conversion successful\n","HDF conversion failed\n");
        }
    }

}
