package org.vcell.util;

import cbit.vcell.resource.PropertyLoader;
import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PythonUtils {
    private final static Logger lg = LogManager.getLogger(PythonUtils.class);

    /**
     * How to run Python: a command prefix that callers append their own arguments to.
     *
     * Every call site used to hard-code {@code poetry run python}, which decided in Java how a
     * deployment must install its Python packages. {@code vcell.python.executable} already existed
     * for exactly this and was already being passed to the sim-data service, but nothing read it.
     *
     * It is not only a tidiness question. In the deployed container {@code poetry run} fails
     * outright: the image runs as uid 10001 while {@code poetry.toml} is {@code -rw------- root},
     * so Poetry cannot read its own configuration and exits with
     * "[Errno 13] Permission denied: .../poetry.toml". The interpreter the property points at
     * imports the same packages perfectly well, because the image installs them into it.
     *
     * When the property is absent -- a developer's machine, the CLI run from a checkout -- the
     * behaviour is exactly what it was, so nothing changes outside a deployment that opts in by
     * setting it.
     */
    public static List<String> pythonCommandPrefix() {
        String configured = PropertyLoader.getProperty(PropertyLoader.pythonExe, null);
        if (configured != null && !configured.trim().isEmpty()) {
            return new ArrayList<>(List.of(configured.trim()));
        }
        return new ArrayList<>(Arrays.asList("poetry", "run", "python"));
    }

    public static void callPythonModule(File workingDir, String pythonModule, String[] commands) throws InterruptedException, IOException {
        List<String> commandList = pythonCommandPrefix();
        commandList.addAll(Arrays.asList("-m", pythonModule));
        commandList.addAll(Arrays.asList(commands));
        ProcessBuilder pb = new ProcessBuilder(commandList);
        pb.directory(workingDir);
        lg.info(pb.command());
        runAndPrintProcessStreams(pb);
    }

    public static void runAndPrintProcessStreams(ProcessBuilder pb) throws InterruptedException, IOException {
        // Process printing code goes here
        File of = File.createTempFile("temp-", ".out");
        File ef = File.createTempFile("temp-", ".err");
        try {
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
            if (process.exitValue() != 0) {
                lg.error("Python process failed with exit code " + process.exitValue()+": "+es);
                throw new RuntimeException(es);
            } else {
                if (!os.equals("")) lg.info(os);
            }
        }finally {
            of.delete();
            ef.delete();
        }
    }
}
