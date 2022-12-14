package org.vcell.cli.vcml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelTransforms;
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

    public static void validateVcmlFiles(File inputDirectory, File outputDirectory, CLIRecorder cliLogger, boolean bTransformKMOLE, boolean bTransformVariables) throws IOException {
        // TODO: make use of CLIRecorder
        if (inputDirectory == null || !inputDirectory.isDirectory()) {
            throw new RuntimeException("expecting inputFilePath to be an existing directory");
        }

        FilenameFilter filterOmexFiles = (f, name) -> name.endsWith(".vcml");
        String[] inputFiles = inputDirectory.list(filterOmexFiles);
        if (inputFiles == null || inputFiles.length == 0) {
            throw new RuntimeException("No VCML files found in the directory `" + inputDirectory + "`");
        }

        final boolean bForceLogFiles = true;
        writeFileEntry(outputDirectory.getAbsolutePath(), "inputDirectory is " + inputDirectory.getAbsolutePath(), jobConfigFile, bForceLogFiles);
        for (String inputFileName : inputFiles) {
            File inputFile = Paths.get(inputDirectory.getAbsolutePath()).resolve(inputFileName).toFile();
            validateOneVcmlFile(inputFile, outputDirectory, bTransformKMOLE, bTransformVariables);
        }
    }

    public static void validateOneVcmlFile(File inputFile, File outputDirectory, boolean bTransformKMOLE, boolean bTransformVariables) throws IOException {
        logger.debug("Beginning import of `" + inputFile.getName() + "`");
        final boolean bForceLogFiles = true;
        try {
            BioModel orig_biomodel = XmlHelper.XMLToBioModel(new XMLSource(inputFile));
            orig_biomodel.refreshDependencies();

            BioModel transformed_biomodel = XmlHelper.XMLToBioModel(new XMLSource(inputFile));
            transformed_biomodel.refreshDependencies();

            if (bTransformKMOLE){
                BioModelTransforms.restoreOldReservedSymbolsIfNeeded(transformed_biomodel);
            }

            int i = 0;
            int number_succeeded = 0;
            for (SimulationContext orig_simContext : orig_biomodel.getSimulationContexts()) {
                MathDescription originalMath = orig_simContext.getMathDescription();
                MathDescription origMathClone = new MathDescription(originalMath); // test round trip to/from MathDescription.readFromDatabase()
                SimulationContext new_simContext = transformed_biomodel.getSimulationContexts(orig_simContext.getName());
                new_simContext.updateAll(false);
                MathDescription newMath = new_simContext.getMathDescription();
                MathDescription newMathClone = new MathDescription(newMath); // test round trip to/from MathDescription.readFromDatabase()
                MathCompareResults results = null;
                if (bTransformVariables) {
                    results = MathDescription.testEquivalencyWithRename(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);
                }else {
                    results = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);
                }
                if (results.isEquivalent()) {
                    writeFileEntry(outputDirectory.getAbsolutePath(), inputFile.getName() + "," + orig_simContext.getName() + ",SUCCEEDED," + i, jobLogFile, bForceLogFiles);
                    number_succeeded++;
                } else {
                    writeFileEntry(outputDirectory.getAbsolutePath(), inputFile.getName() + "," + orig_simContext.getName() + ",FAILED," + 1 + "," + results.toDatabaseStatus(), jobLogFile, bForceLogFiles);
                }
                i++;
            }
            writeFileEntry(outputDirectory.getAbsolutePath(), inputFile.getName() + ",SUCCEEDED," + number_succeeded, jobLogFile, bForceLogFiles);
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
