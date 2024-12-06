package org.vcell.sedml.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BiosimulationLog {

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
}
