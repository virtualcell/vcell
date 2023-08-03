package org.vcell.sbml;

import cbit.vcell.resource.PropertyLoader;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.vcell.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

        int retcode = callCLIPython("validateOmex", omexFile, omexTempDir.toPath(), reportJsonFile.toPath());
        if (retcode != 0){
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

    private static int callCLIPython(String command, Path omexFile, Path tempDir, Path reportJsonFile) throws InterruptedException, IOException {
        File installDir = PropertyLoader.getRequiredDirectory(PropertyLoader.installationRoot);
        File cliPythonDir = Paths.get(installDir.getAbsolutePath(), "vcell-cli-utils").toAbsolutePath().toFile();
        ProcessBuilder pb = new ProcessBuilder(
                "poetry", "run", "python",
                "-m", "vcell_cli_utils.wrapper",
                command,
                String.valueOf(omexFile.toAbsolutePath()),
                String.valueOf(tempDir.toAbsolutePath()),
                String.valueOf(reportJsonFile.toAbsolutePath()));
        pb.directory(cliPythonDir);
        System.out.println(pb.command());
        return runAndPrintProcessStreams(pb, tempDir);
    }

    private static int runAndPrintProcessStreams(ProcessBuilder pb, Path tempDir) throws InterruptedException, IOException {
        // Process printing code goes here
        File of = File.createTempFile("temp-", ".out", tempDir.toFile());
        File ef = File.createTempFile("temp-", ".err", tempDir.toFile());
        pb.redirectError(ef);
        pb.redirectOutput(of);
        Process process = pb.start();
        process.waitFor();
        StringBuilder sberr = new StringBuilder();
        StringBuilder sbout = new StringBuilder();
        List<String> lines = com.google.common.io.Files.readLines(ef, StandardCharsets.UTF_8);
        lines.forEach(line -> sberr.append(line).append("\n"));
        String es = sberr.toString();
        lines = Files.readLines(of, StandardCharsets.UTF_8);
        lines.forEach(line -> sbout.append(line).append("\n"));
        String os = sbout.toString();
        of.delete();
        ef.delete();
        System.out.println("stdout: "+os);
        System.err.println("stderr: "+es);
        if (process.exitValue() != 0) {
            throw new RuntimeException(es);
        }
        return process.exitValue();
    }

}