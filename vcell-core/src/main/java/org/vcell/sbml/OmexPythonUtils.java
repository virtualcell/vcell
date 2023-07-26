package org.vcell.sbml;

import cbit.vcell.resource.PropertyLoader;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.vcell.util.FileUtils;
import org.vcell.util.PythonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmexPythonUtils {

    public static class OmexValidationException extends Exception {
        public final List<OmexValidationError> errors;

        public OmexValidationException(List<OmexValidationError> errors) {
            this.errors = errors;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("OMEX VALIDATION FAILED\n");
            for (OmexValidationError error : errors) {
                sb.append(error.toString());
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    public enum OmexValidationErrorType {
        OMEX_PARSE_ERROR,
        OMEX_VALIDATION_ERROR
    }

    public static class OmexValidationError {
        public final OmexValidationErrorType type;
        public final String message;

        public OmexValidationError(OmexValidationErrorType type, String message) {
            this.type = type;
            this.message = message;
        }

        public String toString() {
            return type + ": " + message;
        }
    }

    public static void validateOmex(Path omexFile) throws OmexValidationException, InterruptedException, IOException {
        File reportJsonFile = File.createTempFile("validation_report_",".json");
        File omexTempDir = Files.createTempDir();
        try {
            callCLIPython("validateOmex", omexFile, omexTempDir.toPath(), reportJsonFile.toPath());
         }catch (Exception e){
            throw new RuntimeException("OMEX VALIDATION FAILED");
        }
        String reportString = FileUtils.readFileToString(reportJsonFile);
        JsonObject reportRoot = JsonParser.parseString(reportString).getAsJsonObject();
        List<OmexValidationError> errors = getErrorsFromReport(reportRoot);
        if (!errors.isEmpty()) {
            throw new OmexValidationException(errors);
        }
     }

    private static List<OmexValidationError> getErrorsFromReport(JsonObject reportRoot) {
        List<OmexValidationError> errors = new ArrayList<>();
        reportRoot.get("parse_errors").getAsJsonArray().forEach(error -> errors.add(new OmexValidationError(OmexValidationErrorType.OMEX_PARSE_ERROR, error.toString().replace("\n", " "))));
        reportRoot.get("validator_errors").getAsJsonArray().forEach(error -> errors.add(new OmexValidationError(OmexValidationErrorType.OMEX_PARSE_ERROR, error.toString().replace("\n", " "))));
        return errors;
    }

    private static void callCLIPython(String command, Path omexFile, Path tempDir, Path reportJsonFile) throws InterruptedException, IOException {
        File cliPythonDir = PropertyLoader.getRequiredDirectory(PropertyLoader.cliWorkingDir);
        String[] commands = new String[] {
                command,
                String.valueOf(omexFile.toAbsolutePath()),
                String.valueOf(tempDir.toAbsolutePath()),
                String.valueOf(reportJsonFile.toAbsolutePath()) };
        PythonUtils.callPoetryModule(cliPythonDir, "vcell_cli_utils.wrapper", commands);
    }

}