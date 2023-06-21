package org.vcell.cli;

import cbit.util.xml.VCLogger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.nio.file.Files;
import java.util.*;

//import java.nio.file.Files;

/*
 * Static class for VCell CLI utility purposes.
 */
public class CLIUtils {
    private final static Logger logger = LogManager.getLogger(CLIUtils.class);

    private CLIUtils(){}; // Static Class, no instances can be made

    /**
     * Remove the directory and any contents inside
     * 
     * @param file to recursively delete (if applicable)
     * @return success status of the operation
     */
    public static boolean removeDirs(File f) {
        try {
            CLIUtils.deleteRecursively(f);
        } catch (IOException ex) {
            logger.error("Failed to delete the file: " + f);
            return false;
        }
        return true;
    }

    /**
     * Changes the logging level at runtime for Vcell CLI
     * 
     * @param ctx context to apply the logging change to
     * @param logLevel the level of logging to set to
     */
    public static void setLogLevel(LoggerContext ctx, Level logLevel){
        org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
        
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.getLogger("org.vcell").getName());
        loggerConfig.setLevel(logLevel);
        loggerConfig = config.getLoggerConfig(LogManager.getLogger("cbit").getName());
        loggerConfig.setLevel(logLevel);
        ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
    }

    /**
     * Since VCell operates on Java 8, this is a string strip utility.
     * 
     * @param str String to Strip whitespace from
     * @return the stripped string
     */
    public static String stripString(String str){
        return str.replaceAll("^[ \t]+|[ \t]+$", ""); // replace whitespace at the front and back with nothing
    }

    /**
     * Local Logger sub0class for `VCLogger` purposes
     */
    public static class LocalLogger extends VCLogger {
        @Override
        public void sendMessage(Priority p, ErrorType et, String message) {
            logger.info("LOGGER: msgLevel=" + p + ", msgType=" + et + ", " + message);
        }

        public void sendAllMessages() {
        }

        public boolean hasMessages() {
            return false;
        }
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
}

