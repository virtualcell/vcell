package org.vcell.sedml.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jlibsedml.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BiosimulationLog {

    public static final String LOG_YML = "log.yml";

    public enum Status {
        QUEUED,
        RUNNING,
        SUCCEEDED,
        FAILED,
        ABORTED,
        SKIPPED
    }
    public static class ExceptionLog {
        public String type;
        public String message;
    }
    public static class DataSetLog {
        public String id;
        public Status status;
    }
    public static class CurveLog {
        public String id;
        public Status status;
    }
    public static class SkipReason {
        public String type;
        public String message;
    }
    public static class OutputLog {
        public String id;
        public Status status;
        public ExceptionLog exception;
        public SkipReason skipReason;
        public String output;
        public BigDecimal duration;
        @JsonInclude(JsonInclude.Include.NON_NULL) public List<DataSetLog> dataSets;
        @JsonInclude(JsonInclude.Include.NON_NULL) public List<CurveLog> curves;
    }
    public static class TaskLog {
        public String id;
        public Status status;
        public ExceptionLog exception;
        public SkipReason skipReason;
        public String output;
        public BigDecimal duration;
        public String algorithm;
        public String simulatorDetails;
    }
    public static class SedDocumentLog {
        public String location;
        public Status status;
        public ExceptionLog exception;
        public SkipReason skipReason;
        public String output;
        public BigDecimal duration;
        public List<TaskLog> tasks = new ArrayList<>();
        public List<OutputLog> outputs = new ArrayList<>();
    }
    public static class ArchiveLog {
        public Status status;
        public ExceptionLog exception;
        public SkipReason skipReason;
        public String output;
        public BigDecimal duration;
        public List<SedDocumentLog> sedDocuments = new ArrayList<>();
    }

    public static ArchiveLog readArchiveLogFromJsonFile(Path path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(path.toFile(), ArchiveLog.class);
    }

    public static ArchiveLog readArchiveLogFromJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json.getBytes(StandardCharsets.UTF_8), ArchiveLog.class);
    }

    public static void writeArchiveLogToJsonFile(ArchiveLog archiveLog, Path path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(path.toFile(), archiveLog);
    }

    public static String writeArchiveLogToJson(ArchiveLog archiveLog) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper.writeValueAsString(archiveLog);
    }

    public static void updateTaskStatusYml(String sedmlName, String taskName, Status taskStatus, String outDir, String duration, String algorithm) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, LOG_YML);
        ArchiveLog log = mapper.readValue(yamlFile, ArchiveLog.class);

        for (SedDocumentLog sedDocument : log.sedDocuments) {
            if (sedmlName.endsWith(sedDocument.location)) {
                for (TaskLog taskItem : sedDocument.tasks) {
                    if (taskItem.id.equals(taskName)) {
                        taskItem.status = taskStatus;
                        taskItem.duration = new BigDecimal(duration);
                        taskItem.algorithm = algorithm != null ? algorithm.replace("KISAO:","KISAO_") : null;
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

    public static void updateSedmlDocStatusYml(String sedmlName, Status sedmlDocStatus, String outDir) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, LOG_YML);
        ArchiveLog log = mapper.readValue(yamlFile, ArchiveLog.class);

        for (SedDocumentLog sedDocument : log.sedDocuments) {
            if (sedmlName.endsWith(sedDocument.location)) {
                sedDocument.status = sedmlDocStatus;
            }
        }
        mapper.writeValue(yamlFile, log);
    }

    public static void updateOmexStatusYml(Status simStatus, String outDir, String duration) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, LOG_YML);
        ArchiveLog log = mapper.readValue(yamlFile, ArchiveLog.class);

        log.status = simStatus;
        log.duration = new BigDecimal(duration);

        mapper.writeValue(yamlFile, log);
    }

    // sedmlAbsolutePath - full path to location of the actual sedml file (document) used as input
    // entityId          - ex: task_0_0 for task, or biomodel_20754836.sedml for a sedml document
    // outDir            - path to directory where the log files will be placed
    // entityType        - string describing the entity type ex "task" for a task, or "sedml" for sedml document
    // message           - useful info about the execution of the entity (ex: task), could be human readable or concatenation of stdout and stderr
    public static void setOutputMessage(String sedmlAbsolutePath, String entityId, String outDir, String entityType, String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, LOG_YML);
        ArchiveLog log = mapper.readValue(yamlFile, ArchiveLog.class);

        if (entityType.equals("omex")) {
            log.output = message;
        } else {
            for (SedDocumentLog sedDocument : log.sedDocuments) {
                if (sedmlAbsolutePath.endsWith(sedDocument.location)) {
                    if (entityType.equals("sedml") && sedDocument.location.equals(entityId)) {
                        sedDocument.output = message;
                    } else if (entityType.equals("task")) {
                        for (TaskLog task : sedDocument.tasks) {
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
        File yamlFile = new File(outDir, LOG_YML);
        ArchiveLog log = mapper.readValue(yamlFile, ArchiveLog.class);

        for (SedDocumentLog sedDocument : log.sedDocuments) {
            if (sedmlAbsolutePath.endsWith(sedDocument.location)) {
                if (entityType.equals("sedml") && sedDocument.location.equals(entityId)) {
                    ExceptionLog exceptionLog = new ExceptionLog();
                    exceptionLog.type = type;
                    exceptionLog.message = message;
                    sedDocument.exception = exceptionLog;
                } else if (entityType.equals("task")) {
                    for (TaskLog task : sedDocument.tasks) {
                        if (task.id.equals(entityId)) {
                            ExceptionLog exceptionLog = new ExceptionLog();
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
        List<SedDocumentLog> sedDocumentLogs = new ArrayList<>();
        // read sedml file
        ArchiveComponents ac = Libsedml.readSEDMLArchive(new FileInputStream(omexFile));
        List<SEDMLDocument> sedmlDocs = ac.getSedmlDocuments();

        for (SEDMLDocument sedmlDoc : sedmlDocs) {
            SedML sedmlModel = sedmlDoc.getSedMLModel();

            SedDocumentLog sedDocumentLog = new SedDocumentLog();
            sedDocumentLog.location = sedmlModel.getFileName();
            sedDocumentLog.status = Status.QUEUED;

            List<AbstractTask> tasks = sedmlModel.getTasks();
            for (AbstractTask task : tasks) {
                TaskLog taskItem = new TaskLog();
                taskItem.id = task.getId();
                taskItem.status = Status.QUEUED;
                sedDocumentLog.tasks.add(taskItem);
            }

            List<Output> outputs = sedmlModel.getOutputs();
            for (Output output : outputs) {
                OutputLog outputItem = new OutputLog();
                outputItem.id = output.getId();
                outputItem.status = Status.QUEUED;

                if (output instanceof Plot2D) {
                    for (Curve curve : ((Plot2D) output).getListOfCurves()) {
                        CurveLog curveItem = new CurveLog();
                        curveItem.id = curve.getId();
                        curveItem.status = Status.QUEUED;
                        if (outputItem.curves == null) {
                            outputItem.curves = new ArrayList<>();
                        }
                        outputItem.curves.add(curveItem);
                    }
                } else if (output instanceof Report) {
                    for (DataSet dataSet : ((Report) output).getListOfDataSets()) {
                        DataSetLog dataSetItem = new DataSetLog();
                        dataSetItem.id = dataSet.getId();
                        dataSetItem.status = Status.QUEUED;
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

        ArchiveLog archiveLog = new ArchiveLog();
        archiveLog.sedDocuments = sedDocumentLogs;
        archiveLog.status = Status.QUEUED;

        String statusYamlPath = Paths.get(outDir, LOG_YML).toString();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.writeValue(new File(statusYamlPath), archiveLog);
    }

    public static void updateDatasetStatusYml(String sedmlName, String report, String dataset, Status simStatus, String outDir) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, LOG_YML);
        ArchiveLog log = mapper.readValue(yamlFile, ArchiveLog.class);

        for (SedDocumentLog sedDocument : log.sedDocuments) {
            if (sedmlName.endsWith(sedDocument.location)) {
                for (OutputLog output : sedDocument.outputs) {
                    if (output.id.equals(report)) {
                        for (DataSetLog dataSet : output.dataSets) {
                            if (dataSet.id.equals(dataset)) {
                                dataSet.status = simStatus;
                                if (simStatus == Status.QUEUED || simStatus == Status.SUCCEEDED) {
                                    output.status = Status.SUCCEEDED;
                                } else {
                                    output.status = Status.FAILED;
                                }
                            }
                        }
                    }
                }
            }
        }

        mapper.writeValue(yamlFile, log);
    }

    public static void updatePlotStatusYml(String sedmlName, String plotId, Status simStatus, String outDir) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File(outDir, LOG_YML);
        ArchiveLog log = mapper.readValue(yamlFile, ArchiveLog.class);

        for (SedDocumentLog sedDocument : log.sedDocuments) {
            if (sedmlName.endsWith(sedDocument.location)) {
                for (OutputLog output : sedDocument.outputs) {
                    if (output.id.equals(plotId)) {
                        for (CurveLog curveLog : output.curves) {
                            curveLog.status = simStatus;
                            if (simStatus == Status.QUEUED || simStatus == Status.SUCCEEDED) {
                                output.status = Status.SUCCEEDED;
                            } else {
                                output.status = Status.FAILED;
                            }
                        }
                    }
                }
            }
        }

        mapper.writeValue(yamlFile, log);
    }
}
