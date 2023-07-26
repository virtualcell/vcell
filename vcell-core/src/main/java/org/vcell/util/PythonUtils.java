package org.vcell.util;

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

    public static void callPoetryModule(File workingDir, String pythonModule, String[] commands) throws InterruptedException, IOException {
        List<String> commandList = new ArrayList<>(Arrays.asList("poetry", "run", "python", "-m", pythonModule));
        commandList.addAll(Arrays.asList(commands));
        ProcessBuilder pb = new ProcessBuilder(commandList);
        pb.directory(workingDir);
        System.out.println(pb.command());
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
