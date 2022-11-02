package org.vcell.cli.vcml;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.cli.CLIRecorder;
import org.vcell.cli.CLIUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class VcmlValidator {

    private static final Logger logger = LogManager.getLogger(VcmlValidator.class);
    public static final String jobConfigFile = "jobConfig.txt";
    public static final String jobLogFile = "jobLog.txt";

    public static void validateVcmlFiles(File inputDirectory, File outputDirectory, CLIRecorder cliLogger, boolean bForceLogFiles) throws IOException {
        // TODO: make use of CLIRecorder
        if (inputDirectory == null || !inputDirectory.isDirectory()) {
            throw new RuntimeException("expecting inputFilePath to be an existing directory");
        }

        FilenameFilter filterOmexFiles = (f, name) -> name.endsWith(".vcml");
        String[] inputFiles = inputDirectory.list(filterOmexFiles);
        if (inputFiles == null || inputFiles.length == 0) {
            throw new RuntimeException("No VCML files found in the directory `" + inputDirectory + "`");
        }

        writeFileEntry(outputDirectory.getAbsolutePath(), "inputDirectory is " + inputDirectory.getAbsolutePath(), jobConfigFile, bForceLogFiles);
        for (String inputFileName : inputFiles) {
            File inputFile = Paths.get(inputDirectory.getAbsolutePath()).resolve(inputFileName).toFile();
            validateOneVcmlFile(inputFile, outputDirectory, bForceLogFiles);
        }
    }

    public static void validateOneVcmlFile(File inputFile, File outputDirectory, boolean bForceLogFiles) throws IOException {
        logger.debug("Beginning import of `" + inputFile.getName() + "`");
        try {
            cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
                @Override
                public void sendMessage(Priority p, ErrorType et, String message) throws VCLoggerException {
                    System.err.println("LOGGER: msgLevel=" + p + ", msgType=" + et + ", " + message);
                    if (p == VCLogger.Priority.HighPriority) {
                        throw new VCLoggerException("Import failed : " + message);
                    }
                }

                public void sendAllMessages() {
                }

                public boolean hasMessages() {
                    return false;
                }
            };
            BioModel biomodel = XmlHelper.XMLToBioModel(new XMLSource(inputFile));
            biomodel.refreshDependencies();
            int i = 0;
            for (SimulationContext simContext : biomodel.getSimulationContexts()) {
                MathDescription originalMath = simContext.getMathDescription();
                simContext.updateAll(false);
                MathDescription newMath = simContext.getMathDescription();
                MathCompareResults results = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);
                if (results.isEquivalent()) {
                    writeFileEntry(outputDirectory.getAbsolutePath(), inputFile.getName() + "," + simContext.getName() + ",SUCCEEDED," + i, jobLogFile, bForceLogFiles);
                } else {
                    writeFileEntry(outputDirectory.getAbsolutePath(), inputFile.getName() + "," + simContext.getName() + ",FAILED," + 1 + "," + results.toDatabaseStatus(), jobLogFile, bForceLogFiles);
                }
                i++;
            }
            writeFileEntry(outputDirectory.getAbsolutePath(), inputFile.getName() + ",SUCCEEDED," + i, jobLogFile, bForceLogFiles);
        } catch (Exception e) {
            String loggedString = inputFile.getName() + ",FAILED,";
            if (e.getCause() != null) {
                loggedString += e.getCause();
            } else {
                loggedString += e;
            }
            logger.error(loggedString, e);
            writeFileEntry(outputDirectory.getAbsolutePath(), loggedString, jobLogFile, bForceLogFiles);
        } finally {
            logger.debug("Import of `" + inputFile.getName() + "` completed");
        }
    }

    public static void writeFileEntry(String outputBaseDir, String entry, String fileName, boolean bForceLogFiles) throws IOException {
        if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
            String dest = outputBaseDir + File.separator + fileName;
            Files.write(Paths.get(dest), (entry + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

}
