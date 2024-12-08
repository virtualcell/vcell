package org.vcell.cli.run;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.PythonStreamException;
import org.vcell.sedml.log.BiosimulationLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class PythonCalls {

    private final static Logger logger = LogManager.getLogger(PythonCalls.class);

    public static void genSedmlForSed2DAnd3D(String omexFilePath, String outputDir) throws PythonStreamException {
        logger.trace("Dialing Python function genSedml2d3d");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("genSedml2d3d", omexFilePath, outputDir);
        cliPythonManager.parsePythonReturn(results, "", "Failed generating SED-ML for plot2d and 3D ");
    }

    public static void updateTaskStatusYml(String sedmlName, String taskName, BiosimulationLog.Status taskStatus, String outDir, String duration, String algorithm) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, "log.yml");
        BiosimulationLog.ArchiveLog log = mapper.readValue(yamlFile, BiosimulationLog.ArchiveLog.class);

        for (BiosimulationLog.SedDocumentLog sedDocument : log.sedDocuments) {
            if (sedmlName.endsWith(sedDocument.location)) {
                for (BiosimulationLog.TaskLog taskItem : sedDocument.tasks) {
                    if (taskItem.id.equals(taskName)) {
                        taskItem.status = taskStatus;
                        taskItem.duration = new BigDecimal(duration);
                        taskItem.algorithm = algorithm;
//                        // update individual task status
//                        if ( taskItem.status == BiosimulationLog.Status.QUEUED || taskItem.status == BiosimulationLog.Status.SUCCEEDED){
//                            sedDocument.status = BiosimulationLog.Status.SUCCEEDED;
//                        } else {
//                            sedDocument.status = BiosimulationLog.Status.FAILED;
//                        }
                    }
                }
            }
        }

        mapper.writeValue(yamlFile, log);
    }

    public static void updateSedmlDocStatusYml(String sedmlName, BiosimulationLog.Status sedmlDocStatus, String outDir) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, "log.yml");
        BiosimulationLog.ArchiveLog log = mapper.readValue(yamlFile, BiosimulationLog.ArchiveLog.class);

        for (BiosimulationLog.SedDocumentLog sedDocument : log.sedDocuments) {
            if (sedmlName.endsWith(sedDocument.location)) {
                sedDocument.status = sedmlDocStatus;
            }
        }
        mapper.writeValue(yamlFile, log);
    }

    public static void updateOmexStatusYml(BiosimulationLog.Status simStatus, String outDir, String duration) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, "log.yml");
        BiosimulationLog.ArchiveLog log = mapper.readValue(yamlFile, BiosimulationLog.ArchiveLog.class);

        log.status = simStatus;
        log.duration = new BigDecimal(duration);

        mapper.writeValue(yamlFile, log);
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

    public static void setOutputMessage(String sedmlAbsolutePath, String entityId, String outDir, String entityType, String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, "log.yml");
        BiosimulationLog.ArchiveLog log = mapper.readValue(yamlFile, BiosimulationLog.ArchiveLog.class);

        if (entityType.equals("omex")) {
            log.output = message;
        } else {
            for (BiosimulationLog.SedDocumentLog sedDocument : log.sedDocuments) {
                if (sedmlAbsolutePath.endsWith(sedDocument.location)) {
                    if (entityType.equals("sedml") && sedDocument.location.equals(entityId)) {
                        sedDocument.output = message;
                    } else if (entityType.equals("task")) {
                        for (BiosimulationLog.TaskLog task : sedDocument.tasks) {
                            if (task.id.equals(entityId)) {
                                task.output = message;
                            }
                        }
                    }
                }
            }
        }

        mapper.writeValue(yamlFile, log);
    }

    // type - exception class, ex RuntimeException
    // message  - exception message
    public static void setExceptionMessage(String sedmlAbsolutePath, String entityId, String outDir, String entityType, String type, String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, "log.yml");
        BiosimulationLog.ArchiveLog log = mapper.readValue(yamlFile, BiosimulationLog.ArchiveLog.class);

        for (BiosimulationLog.SedDocumentLog sedDocument : log.sedDocuments) {
            if (sedmlAbsolutePath.endsWith(sedDocument.location)) {
                if (entityType.equals("sedml") && sedDocument.location.equals(entityId)) {
                    BiosimulationLog.ExceptionLog exceptionLog = new BiosimulationLog.ExceptionLog();
                    exceptionLog.type = type;
                    exceptionLog.message = message;
                    sedDocument.exception = exceptionLog;
                } else if (entityType.equals("task")) {
                    for (BiosimulationLog.TaskLog task : sedDocument.tasks) {
                        if (task.id.equals(entityId)) {
                            BiosimulationLog.ExceptionLog exceptionLog = new BiosimulationLog.ExceptionLog();
                            exceptionLog.type = type;
                            exceptionLog.message = message;
                            task.exception = exceptionLog;
                        }
                    }
                }
            }
        }

        mapper.writeValue(yamlFile, log);
        System.out.println("Success!");
    }

    public static void generateStatusYaml(String omexFile, String outDir) throws IOException, XMLException {
        List<BiosimulationLog.SedDocumentLog> sedDocumentLogs = new ArrayList<>();
        // read sedml file
        ArchiveComponents ac = Libsedml.readSEDMLArchive(new FileInputStream(omexFile));
        List<SEDMLDocument> sedmlDocs = ac.getSedmlDocuments();

        for (SEDMLDocument sedmlDoc : sedmlDocs) {
            SedML sedmlModel = sedmlDoc.getSedMLModel();

            BiosimulationLog.SedDocumentLog sedDocumentLog = new BiosimulationLog.SedDocumentLog();
            sedDocumentLog.location = sedmlModel.getPathForURI();
            sedDocumentLog.status = BiosimulationLog.Status.QUEUED;

            List<AbstractTask> tasks = sedmlModel.getTasks();
            for (AbstractTask task : tasks) {
                BiosimulationLog.TaskLog taskItem = new BiosimulationLog.TaskLog();
                taskItem.id = task.getId();
                taskItem.status = BiosimulationLog.Status.QUEUED;
                sedDocumentLog.tasks.add(taskItem);
            }

            List<Output> outputs = sedmlModel.getOutputs();
            for (Output output : outputs) {
                BiosimulationLog.OutputLog outputItem = new BiosimulationLog.OutputLog();
                outputItem.id = output.getId();
                outputItem.status = BiosimulationLog.Status.QUEUED;

                if (output instanceof Plot2D) {
                    for (org.jlibsedml.Curve curve : ((Plot2D) output).getListOfCurves()) {
                        BiosimulationLog.CurveLog curveItem = new BiosimulationLog.CurveLog();
                        curveItem.id = curve.getId();
                        curveItem.status = BiosimulationLog.Status.QUEUED;
                        if (outputItem.curves == null) {
                            outputItem.curves = new ArrayList<>();
                        }
                        outputItem.curves.add(curveItem);
                    }
                } else if (output instanceof Report) {
                    for (org.jlibsedml.DataSet dataSet : ((Report) output).getListOfDataSets()) {
                        BiosimulationLog.DataSetLog dataSetItem = new BiosimulationLog.DataSetLog();
                        dataSetItem.id = dataSet.getId();
                        dataSetItem.status = BiosimulationLog.Status.QUEUED;
                        if (outputItem.dataSets == null) {
                            outputItem.dataSets = new ArrayList<>();
                        }
                        outputItem.dataSets.add(dataSetItem);
                    }
                }
                sedDocumentLog.outputs.add(outputItem);
            }
            sedDocumentLogs.add(sedDocumentLog);
        }

        BiosimulationLog.ArchiveLog archiveLog = new BiosimulationLog.ArchiveLog();
        archiveLog.sedDocuments = sedDocumentLogs;
        archiveLog.status = BiosimulationLog.Status.QUEUED;

        String statusYamlPath = Paths.get(outDir, "log.yml").toString();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.writeValue(new File(statusYamlPath), archiveLog);
    }

    public static void transposeVcmlCsv(String csvFilePath) throws PythonStreamException {
        logger.trace("Dialing Python function transposeVcmlCsv");
        CLIPythonManager cliPythonManager = CLIPythonManager.getInstance();
        String results = cliPythonManager.callPython("transposeVcmlCsv", csvFilePath);
        cliPythonManager.parsePythonReturn(results);
    }

    public static void updateDatasetStatusYml(String sedmlName, String report, String dataset, BiosimulationLog.Status simStatus, String outDir) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, "log.yml");
        BiosimulationLog.ArchiveLog log = mapper.readValue(yamlFile, BiosimulationLog.ArchiveLog.class);

        for (BiosimulationLog.SedDocumentLog sedDocument : log.sedDocuments) {
            if (sedmlName.endsWith(sedDocument.location)) {
                for (BiosimulationLog.OutputLog output : sedDocument.outputs) {
                    if (output.id.equals(report)) {
                        for (BiosimulationLog.DataSetLog dataSet : output.dataSets) {
                            if (dataSet.id.equals(dataset)) {
                                dataSet.status = simStatus;
                                if (simStatus == BiosimulationLog.Status.QUEUED || simStatus == BiosimulationLog.Status.SUCCEEDED) {
                                    output.status = BiosimulationLog.Status.SUCCEEDED;
                                } else {
                                    output.status = BiosimulationLog.Status.FAILED;
                                }
                            }
                        }
                    }
                }
            }
        }

        mapper.writeValue(yamlFile, log);
    }

    public static void updatePlotStatusYml(String sedmlName, String plotId, BiosimulationLog.Status simStatus, String outDir) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, "log.yml");
        BiosimulationLog.ArchiveLog log = mapper.readValue(yamlFile, BiosimulationLog.ArchiveLog.class);

        for (BiosimulationLog.SedDocumentLog sedDocument : log.sedDocuments) {
            if (sedmlName.endsWith(sedDocument.location)) {
                for (BiosimulationLog.OutputLog output : sedDocument.outputs) {
                    if (output.id.equals(plotId)) {
                        for (BiosimulationLog.CurveLog curveLog : output.curves) {
                            curveLog.status = simStatus;
                            if (simStatus == BiosimulationLog.Status.QUEUED || simStatus == BiosimulationLog.Status.SUCCEEDED) {
                                output.status = BiosimulationLog.Status.SUCCEEDED;
                            } else {
                                output.status = BiosimulationLog.Status.FAILED;
                            }
                        }
                    }
                }
            }
        }

        mapper.writeValue(yamlFile, log);
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
