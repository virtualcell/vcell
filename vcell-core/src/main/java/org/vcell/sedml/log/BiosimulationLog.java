package org.vcell.sedml.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BiosimulationLog {

    public static class LogValidationException extends RuntimeException {
        public LogValidationException(String message) {
            super(message);
        }
    }

    public static final Logger lg = LogManager.getLogger(BiosimulationLog.class);
    public static final String LOG_YML = "log.yml";
    public static final boolean VALIDATE_LOG = false;
    private static final ThreadLocal<BiosimulationLog> instanceThreadLocal = new ThreadLocal<>();

    final ArchiveLog archiveLog;
    final String outDir;
    boolean dirty = false;

    private BiosimulationLog(Path omexFilePath, Path outDir) throws XMLException, IOException {
        this.archiveLog = initializeLogFromOmex(omexFilePath.toString());;
        this.outDir = outDir.toString();
        this.dirty = true;
    }
//        ArchiveLog readCopyFromDisk() throws IOException {
//            // may be used for testing, but shouldn't have to read from disk, only write.
//            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//            return mapper.readValue(this.logFilePath.toFile(), ArchiveLog.class);
//        }

    void write() throws IOException {
        if (!this.dirty) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.writeValue(new File(this.outDir, LOG_YML), this.archiveLog);
        this.dirty = false;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public void close() throws IOException {
        if (this.dirty) {
            write();
        }
        if (VALIDATE_LOG) {
            validate();
        }
        if (instanceThreadLocal.get() == this) {
            instanceThreadLocal.remove();
        }
    }

    public static BiosimulationLog instance() {
        if (instanceThreadLocal.get() == null) {
            throw new RuntimeException("LogSession not initialized");
        }
        return instanceThreadLocal.get();
    }

    public static BiosimulationLog initialize(String omexFilePath, String outDir) throws XMLException, IOException {
        BiosimulationLog log = new BiosimulationLog(Paths.get(omexFilePath), Paths.get(outDir));
        instanceThreadLocal.set(log);
        return log;
    }

    public void validate() throws LogValidationException {
        try {
            // prepare HTTP POST to https://api.biosimulations.org/logs/validate with content type application/json
            String jsonText = this.archiveLog.writeToJson();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.biosimulations.org/logs/validate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonText))
                    .build();

            // send the request and verify return status of 204
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 204) {
                throw new LogValidationException("Failed to validate log file");
            }else{
                lg.info("Biosimulation Log file validated successfully to remote API, consider disabling");
            }
        } catch (IOException | InterruptedException e) {
            lg.error("Failed to validate log file", e);
        }
    }

    public enum Status {
        QUEUED,
        RUNNING,
        SUCCEEDED,
        FAILED,
        ABORTED,
        SKIPPED
    }
    static class ExceptionLog {
        public String type;
        public String message;
    }
    static class DataSetLog {
        public String id;
        public Status status;
    }
    static class CurveLog {
        public String id;
        public Status status;
    }
    static class SkipReason {
        public String type;
        public String message;
    }
    static class OutputLog {
        public String id;
        public Status status;
        public ExceptionLog exception;
        public SkipReason skipReason;
        public String output;
        public BigDecimal duration;
        @JsonInclude(JsonInclude.Include.NON_NULL) public List<DataSetLog> dataSets;
        @JsonInclude(JsonInclude.Include.NON_NULL) public List<CurveLog> curves;
    }
    static class TaskLog {
        public String id;
        public Status status;
        public ExceptionLog exception;
        public SkipReason skipReason;
        public String output;
        public BigDecimal duration;
        public String algorithm;
        public String simulatorDetails;
    }
    static class SedDocumentLog {
        public String location;
        public Status status;
        public ExceptionLog exception;
        public SkipReason skipReason;
        public String output;
        public BigDecimal duration;
        public List<TaskLog> tasks = new ArrayList<>();
        public List<OutputLog> outputs = new ArrayList<>();
    }
    static class ArchiveLog {
        public Status status;
        public ExceptionLog exception;
        public SkipReason skipReason;
        public String output;
        public BigDecimal duration;
        public List<SedDocumentLog> sedDocuments = new ArrayList<>();

        static ArchiveLog fromJson(String json) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json.getBytes(StandardCharsets.UTF_8), ArchiveLog.class);
        }

        String writeToJson() throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            return objectMapper.writeValueAsString(this);
        }
    }

    public void updateTaskStatusYml(String sedmlName, String taskName, Status taskStatus, BigDecimal duration_s, String algorithm) {
        for (SedDocumentLog sedDocument : this.archiveLog.sedDocuments) {
            if (sedmlName.endsWith(sedDocument.location)) {
                for (TaskLog taskItem : sedDocument.tasks) {
                    if (taskItem.id.equals(taskName)) {
                        taskItem.status = taskStatus;
                        taskItem.duration = duration_s;
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
        setDirty();
    }

    public void updateSedmlDocStatusYml(String sedmlName, Status sedmlDocStatus) {
        for (SedDocumentLog sedDocument : this.archiveLog.sedDocuments) {
            if (sedmlName.endsWith(sedDocument.location)) {
                sedDocument.status = sedmlDocStatus;
            }
        }
        setDirty();
    }

    public void updateOmexStatusYml(Status simStatus, BigDecimal duration_s) {
        this.archiveLog.status = simStatus;
        this.archiveLog.duration = duration_s;
        setDirty();
    }

    // sedmlAbsolutePath - full path to location of the actual sedml file (document) used as input
    // entityId          - ex: task_0_0 for task, or biomodel_20754836.sedml for a sedml document
    // outDir            - path to directory where the log files will be placed
    // entityType        - string describing the entity type ex "task" for a task, or "sedml" for sedml document
    // message           - useful info about the execution of the entity (ex: task), could be human readable or concatenation of stdout and stderr
    public void setOutputMessage(String sedmlAbsolutePath, String entityId, String entityType, String message) {
        if (entityType.equals("omex")) {
            this.archiveLog.output = message;
        } else {
            for (SedDocumentLog sedDocument : this.archiveLog.sedDocuments) {
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
        setDirty();
    }

    // type - exception class, ex RuntimeException
    // message  - exception message
    public void setExceptionMessage(String sedmlAbsolutePath, String entityId, String entityType, String type, String message) {
        for (SedDocumentLog sedDocument : this.archiveLog.sedDocuments) {
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
        setDirty();
    }

    private static ArchiveLog initializeLogFromOmex(String omexFile) throws IOException, XMLException {
        // read sedml files from omex
        ArchiveComponents ac = Libsedml.readSEDMLArchive(new FileInputStream(omexFile));
        List<SEDMLDocument> sedmlDocs = ac.getSedmlDocuments();

        List<SedDocumentLog> sedDocumentLogs = new ArrayList<>();
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

        return archiveLog;
    }

    public void updateDatasetStatusYml(String sedmlName, String report, String dataset, Status simStatus) {
        for (SedDocumentLog sedDocument : this.archiveLog.sedDocuments) {
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
        setDirty();
    }

    public void updatePlotStatusYml(String sedmlName, String plotId, Status simStatus) {
        for (SedDocumentLog sedDocument : this.archiveLog.sedDocuments) {
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
        setDirty();
    }

}
