package org.vcell.sedml.testsupport;

import cbit.vcell.mapping.MappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.trace.Span;
import org.vcell.trace.TraceEvent;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OmexTestingDatabase {

    public enum TestDataRepo {
        vcell, sysbio
    }
    public enum TestCollection {
        VCELL_QUANT_OMEX(TestDataRepo.vcell, "vcell-cli/src/test/resources/OmexWithThirdPartyResults"),
        VCELL_BIOMD(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/misc-projects"),
        VCELL_BSTS_VCML(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/vcml"),
        VCELL_BSTS_SBML_CORE(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/sbml-core"),
        VCELL_BSTS_SYNTHS(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/synths"),
        VCELL_SPATIAL(TestDataRepo.vcell, "vcell-cli/src/test/resources/spatial"),
        SYSBIO_BIOMD(TestDataRepo.sysbio, "omex_files");

        public final TestDataRepo repo;
        public final String pathPrefix;

        TestCollection(TestDataRepo repo, String pathPrefix) {
            this.repo = repo;
            this.pathPrefix = pathPrefix;
        }
    }

    public static List<OmexTestCase> queryOmexTestCase(List<OmexTestCase> omexTestCases, Path path, Path commonPrefix) {
        if (commonPrefix != null){
            path = path.subpath(commonPrefix.getNameCount(), path.getNameCount());
        }
        final Path finalPath = path;
        return omexTestCases.stream().filter(tc -> {
            Path fullPath = Paths.get(tc.test_collection.pathPrefix, tc.file_path);
            return fullPath.endsWith(finalPath);
        }).toList();
    }

    public static OmexTestReport generateReport(List<OmexTestCase> omexTestCases, List<OmexExecSummary> execSummaries) {
        return new OmexTestReport(omexTestCases, execSummaries);
    }

    public static List<OmexExecSummary> loadOmexExecSummaries(String execSummariesNdjson) throws IOException {
        List<OmexExecSummary> execSummaries = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try (MappingIterator<OmexExecSummary> it = objectMapper.readerFor(OmexExecSummary.class).readValues(execSummariesNdjson)) {
            while (it.hasNext()) {
                execSummaries.add(it.next());
            }
        }
        return execSummaries;
    }


    // read a newline-delimited json file into a list of OmexTextCase objects
    public static List<OmexTestCase> loadOmexTestCases() throws IOException {
        String fileName = "test_cases.ndjson";
        try (InputStream inputStream = OmexTestingDatabase.class.getResourceAsStream("/"+fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
            String testCasesNdjson = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            return parseOmexTestCases(testCasesNdjson);
        }
    }

    public static List<OmexTestCase> parseOmexTestCases(String testCasesNdjson) throws IOException {
        List<OmexTestCase> testCases = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try (MappingIterator<OmexTestCase> it = objectMapper.readerFor(OmexTestCase.class).readValues(testCasesNdjson)) {
            while (it.hasNext()) {
                testCases.add(it.next());
            }
        }
        return testCases;
    }

    public static OmexExecSummary summarize(File inputFilePath, Exception exception, List<TraceEvent> errorEvents, long elapsedTime_ms) {
        OmexExecSummary execSummary = new OmexExecSummary();
        execSummary.file_path = inputFilePath.toString();
        execSummary.status = OmexExecSummary.ActualStatus.FAILED;
        execSummary.elapsed_time_ms = elapsedTime_ms;
        if (exception != null || !errorEvents.isEmpty()) {
            execSummary.failure_type = determineFault(exception, errorEvents);
            execSummary.failure_desc = null;
            if (exception != null) {
                execSummary.failure_desc = exception.getMessage();
            }
            if (!errorEvents.isEmpty()) {
                execSummary.failure_desc = errorEvents.get(0).message + " " + errorEvents.get(0).exception;
            }
        }
        return execSummary;
    }

    public static FailureType determineFault(Exception caughtException, List<TraceEvent> errorEvents){ // Throwable because Assertion Error
        String errorMessage = caughtException == null ? "" : caughtException.getMessage();

        if (errorMessage.contains("refers to either a non-existent model")) { //"refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)"
            return FailureType.SEDML_UNSUPPORTED_MODEL_REFERENCE;
        } else if (errorMessage.contains("System IO encountered a fatal error")){
            Throwable subException = caughtException.getCause();
            //String subMessage = (subException == null) ? "" : subException.getMessage();
            if (subException instanceof FileAlreadyExistsException){
                return FailureType.HDF5_FILE_ALREADY_EXISTS;
            }
        } else if (errorMessage.contains("error while processing outputs: null")){
            Throwable subException = caughtException.getCause();
            if (subException instanceof ArrayIndexOutOfBoundsException){
                return FailureType.ARRAY_INDEX_OUT_OF_BOUNDS;
            }
        } else if (errorMessage.contains("nconsistent unit system in SBML model") ||
                errorMessage.contains("ust be of type")){
            return FailureType.SEDML_ERRONEOUS_UNIT_SYSTEM;
        } else if (errorMessage.contains("There are no SED-MLs in the archive to execute")) {
            return FailureType.SEDML_NO_SEDMLS_TO_EXECUTE;
        } else if (errorMessage.contains("MappingException occurred: failed to generate math")) {
            return FailureType.MATH_GENERATION_FAILURE;
        }

        // else check Tracer error events for known faults
        for (TraceEvent event : errorEvents) {
            if (event.hasException(SBMLImportException.class)) {
                return FailureType.SBML_IMPORT_FAILURE;
            }
            if (event.span.getNestedContextName().contains(Span.ContextType.PROCESSING_SEDML.name()+"(preProcessDoc)")){
                return FailureType.SEDML_PREPROCESS_FAILURE;
            }
            if (event.hasException(MappingException.class)) {
                return FailureType.MATH_GENERATION_FAILURE;
            }
        }

        return FailureType.UNCATETORIZED_FAULT;
    }
}
