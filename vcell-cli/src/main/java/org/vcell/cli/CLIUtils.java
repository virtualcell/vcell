package org.vcell.cli;

import cbit.util.xml.VCLogger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.*;
import java.nio.file.*;
import java.util.*;

//import java.nio.file.Files;

public class CLIUtils {
    // Simulation Status enum

    public static boolean removeDirs(File f) {
        try {
            CLIUtils.deleteRecursively(f);
        } catch (IOException ex) {
            System.err.println("Failed to delete the file: " + f);
            return false;
        }
        return true;
    }

    private static void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : Objects.requireNonNull(f.listFiles())) {
                CLIUtils.deleteRecursively(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

    public static void setLogLevel(LoggerContext ctx, Level logLevel){
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(logLevel);
        ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
    }

    public static boolean isBatchExecution(String outputBaseDir, boolean bForceKeepLogs) {
        Path path = Paths.get(outputBaseDir);
        boolean isDirectory = java.nio.file.Files.isDirectory(path);
        return isDirectory || bForceKeepLogs;
    }

    public static void createHeader(File outputFilePath, boolean bForceLogFiles) {
        /**
         * Header Components:
         *  * base name of the omex file
         *  *   foreach sed-ml file:
         *  *   - (current) sed-ml file name
         *  *   - error(s) (if any)
         *  *   - number of models
         *  *   - number of sims
         *  *   - number of tasks
         *  *   - number of outputs
         *  *   - number of biomodels
         *  *   - number of succesful sims that we managed to run
         *  *   (NB: we assume that the # of failures = # of tasks - # of successful simulations)
         *  *   (NB: if multiple sedml files in the omex, we display on multiple rows, one for each sedml)
         */

        String header = "BaseName,SedML,Error,Models,Sims,Tasks,Outputs,BioModels,NumSimsSuccessful";
        try {
            CLIUtils.writeDetailedResultList(outputFilePath.getAbsolutePath(), header, bForceLogFiles);
        } catch (IOException e1) {
            // not big deal, we just failed to make the header; we'll find out later what went wrong
            e1.printStackTrace();
        }
    }

    public static String stripString(String str){
        return str.replaceAll("^[ \t]+|[ \t]+$", ""); // replace whitespace at the front and back with nothing
    }

    public static void writeDetailedErrorList(String outputBaseDir, String s, boolean bForceKeepLogs) throws IOException {
        if (isBatchExecution(outputBaseDir, bForceKeepLogs)) {
            String dest = outputBaseDir + File.separator + "detailedErrorLog.txt";
            java.nio.file.Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    public static void writeFullSuccessList(String outputBaseDir, String s, boolean bForceLogFiles) throws IOException {
        if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
            String dest = outputBaseDir + File.separator + "fullSuccessLog.txt";
            java.nio.file.Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    // we just make a list with the omex files that failed
    public static void writeErrorList(String outputBaseDir, String s, boolean bForceLogFiles) throws IOException {
        if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
            String dest = outputBaseDir + File.separator + "errorLog.txt";
            java.nio.file.Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    public static void writeDetailedResultList(String outputBaseDir, String s, boolean bForceLogFiles) throws IOException {
        if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
            String dest = outputBaseDir + File.separator + "detailedResultLog.txt";
            java.nio.file.Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    public static class LocalLogger extends VCLogger {
        @Override
        public void sendMessage(Priority p, ErrorType et, String message) throws Exception {
            System.out.println("LOGGER: msgLevel=" + p + ", msgType=" + et + ", " + message);
        }

        public void sendAllMessages() {
        }

        public boolean hasMessages() {
            return false;
        }
    }
}

