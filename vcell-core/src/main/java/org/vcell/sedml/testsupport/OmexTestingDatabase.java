package org.vcell.sedml.testsupport;

import cbit.vcell.mapping.MappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.sedml.SEDMLImportException;
import org.vcell.trace.Span;
import org.vcell.trace.TraceEvent;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class OmexTestingDatabase {

    public enum TestDataRepo {
        vcell, sysbio, vcdb
    }
    public enum TestCollection {
        VCELL_QUANT_OMEX(TestDataRepo.vcell, "vcell-cli/src/test/resources/OmexWithThirdPartyResults"),
        VCELL_BIOMD(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/misc-projects"),
        VCELL_BSTS_VCML(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/vcml"),
        VCELL_BSTS_SBML_CORE(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/sbml-core"),
        VCELL_BSTS_SYNTHS(TestDataRepo.vcell, "vcell-cli/src/test/resources/bsts-omex/synths"),
        VCELL_SPATIAL(TestDataRepo.vcell, "vcell-cli/src/test/resources/spatial"),
        SYSBIO_BIOMD(TestDataRepo.sysbio, "omex_files"),
        VCELL_PUBLISHED_OMEX(TestDataRepo.vcdb,"published/biomodel/omex/sbml"),
        VCELL_PUBLISHED_VCML(TestDataRepo.vcdb,"published/biomodel/vcml");

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

    public static FailureType determineFault(Exception caughtException, List<TraceEvent> errorEvents){
        FailureType determinedFailure;
        if (caughtException != null) if ((determinedFailure = determineFault(caughtException)) != null) return determinedFailure;
        if (errorEvents == null) return FailureType.UNCATEGORIZED_FAULT;
        for (TraceEvent errorEvent : errorEvents) if ((determinedFailure = determineFault(errorEvent)) != null) return determinedFailure;
        return FailureType.UNCATEGORIZED_FAULT;
    }

    private static FailureType determineFault(TraceEvent traceEvent){
        if (traceEvent.hasException(SBMLImportException.class)) {
            FailureType sbmlFailureType;
            return (sbmlFailureType = determineFault(traceEvent.exception)) != null ? sbmlFailureType : FailureType.SBML_IMPORT_FAILURE;
        }

        if (traceEvent.hasException(RuntimeException.class) && traceEvent.exception.getCause() != null && traceEvent.exception.getCause() instanceof Exception causalException) {
            if (causalException instanceof IllegalArgumentException && causalException.getMessage().contains("invalid species name; not located"))
                return FailureType.SPECIES_NOT_LOCATED_IN_RESULTS;
        }

        if (traceEvent.message.contains("convert necessary file to sbml/sedml combine archive"))
            return FailureType.VCML_EXPORT_FAILURE;

        if (traceEvent.exception instanceof RuntimeException && traceEvent.message.contains("Failed execution")){
            if (traceEvent.message.contains("divide by zero")) return FailureType.DIVIDE_BY_ZERO;
            if (traceEvent.message.contains("infinite loop")) return FailureType.SOLVER_FAILURE;
        }

        if (traceEvent.exception != null && traceEvent.message.toLowerCase().contains("non-compatible sedml simulations"))
            return FailureType.SEDML_NON_UTC_SIMULATION_FOUND;

        if (traceEvent.hasException(SEDMLImportException.class) && traceEvent.exception != null){
            String errMsg = traceEvent.exception.getMessage() == null ? "" : traceEvent.exception.getMessage();
            if (errMsg.contains("expecting vcell var") && errMsg.contains("to be constant valued")) return FailureType.BIOMODEL_IMPORT_SEDML_FAILURE;
            return FailureType.SEDML_IMPORT_FAILURE;
        }
        if (traceEvent.span.getNestedContextName().contains(Span.ContextType.PROCESSING_SEDML.name()+"(preProcessDoc)")){
            return FailureType.SEDML_PREPROCESS_FAILURE;
        }
        if (traceEvent.hasException(MappingException.class)) {
            return FailureType.MATH_GENERATION_FAILURE;
        }
        // One last check:
        if (traceEvent.exception != null) return determineFault(traceEvent.exception);
        return null;
    }

    private static FailureType determineFault(Exception caughtException){
        String errorMessage = caughtException.getMessage() == null ? "" : caughtException.getMessage();

        if (caughtException instanceof ExecutionException && caughtException.getCause() != null && caughtException.getCause() instanceof TimeoutException) {
            return FailureType.TIMEOUT_ENCOUNTERED;
        } else if (caughtException instanceof TimeoutException && errorMessage.contains("timed out")) {
            return FailureType.TIMEOUT_ENCOUNTERED;
        } else if (errorMessage.contains("refers to either a non-existent model")) { //"refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)"
            return FailureType.SEDML_UNSUPPORTED_MODEL_REFERENCE;
        } else if (errorMessage.contains("Non-integer stoichiometry")) {
            return FailureType.UNSUPPORTED_NON_INT_STOCH;
        } else if (errorMessage.contains("Non-numeric stoichiometry")) {
            return FailureType.UNSUPPORTED_NON_NUMERIC_STOCH;
        } else if (errorMessage.contains("compartment") && errorMessage.contains("has constant attribute set to")) {
            return FailureType.UNSUPPORTED_NON_CONSTANT_COMPARTMENTS;
        } else if (errorMessage.contains("XMLNode cannot be cast to")) {
            return FailureType.SBML_XML_NODE_FAILURE;
        } else if (errorMessage.contains("System IO encountered a fatal error")){
            Throwable subException = caughtException.getCause();
            //String subMessage = (subException == null) ? "" : subException.getMessage();
            if (subException instanceof FileAlreadyExistsException){
                return FailureType.HDF5_FILE_ALREADY_EXISTS;
            }
        } else if (errorMessage.contains("Could not execute code")){
            if (errorMessage.contains("CVODE")) return FailureType.SOLVER_FAILURE;
            if (errorMessage.contains("SundialsSolverStandalone")) return FailureType.SOLVER_FAILURE;
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
        } else if (errorMessage.contains("delay") && errorMessage.contains("SBML")) {
            return FailureType.UNSUPPORTED_DELAY_SBML;
        } else if (errorMessage.contains("are in unnamed module of loader")){
            return FailureType.SBML_IMPORT_FAILURE;
        }
        return null;
    }

}
