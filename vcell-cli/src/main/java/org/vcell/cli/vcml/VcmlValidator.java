package org.vcell.cli.vcml;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Model;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionUtils;
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
            BioModel orig_biomodel = XmlHelper.XMLToBioModel(new XMLSource(inputFile));
            orig_biomodel.refreshDependencies();

            BioModel transformed_biomodel = XmlHelper.XMLToBioModel(new XMLSource(inputFile));
            transformed_biomodel.refreshDependencies();

            restoreOldReservedSymbolsIfNeeded(transformed_biomodel);

            int i = 0;
            int number_succeeded = 0;
            for (SimulationContext orig_simContext : orig_biomodel.getSimulationContexts()) {
                MathDescription originalMath = orig_simContext.getMathDescription();
                SimulationContext new_simContext = transformed_biomodel.getSimulationContexts(orig_simContext.getName());
                new_simContext.updateAll(false);
                MathDescription newMath = new_simContext.getMathDescription();
                MathCompareResults results = MathDescription.testEquivalencyWithRename(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);
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

    public static void restoreOldReservedSymbolsIfNeeded(BioModel bioModel) {
        /**
         *   old values for reserved symbols (prior to commit on 8/9/2019)
         *
         * 			new ReservedSymbol(ReservedSymbolRole.FARADAY_CONSTANT, "_F_","Faraday constant",unitSystem.getFaradayConstantUnit(), new Expression(9.648e4)),
         * 			new ReservedSymbol(ReservedSymbolRole.FARADAY_CONSTANT_NMOLE, "_F_nmol_","Faraday constant", unitSystem.getFaradayConstantNMoleUnit(),new Expression(9.648e-5)),
         * 			new ReservedSymbol(ReservedSymbolRole.N_PMOLE, "_N_pmol_","Avagadro Num (scaled)",unitSystem.getN_PMoleUnit(),new Expression(6.02e11)),
         * 			new ReservedSymbol(ReservedSymbolRole.GAS_CONSTANT, "_R_","Gas Constant",unitSystem.getGasConstantUnit(), new Expression(8314.0)),
         * 			new ReservedSymbol(ReservedSymbolRole.KMOLE, "KMOLE", "Flux unit conversion", unitSystem.getKMoleUnit(), new Expression(new RationalNumber(1, 602)))
         *
         * 	new values for reserved symbols (after commit on 8/9/2019)
         *
         * 	        put(Model.ReservedSymbolRole.FARADAY_CONSTANT, 9.64853321e4);			// exactly 96485.3321233100184 C/mol
         *          put(Model.ReservedSymbolRole.FARADAY_CONSTANT_NMOLE, 9.64853321e-5);	// was 9.648
         *          put(Model.ReservedSymbolRole.N_PMOLE, 6.02214179e11);
         *          put(Model.ReservedSymbolRole.GAS_CONSTANT, 8314.46261815);			// exactly 8314.46261815324  (was 8314.0)
         *          put(Model.ReservedSymbolRole.KMOLE, 1.0/602.214179);
         */
        final double FARADAY_CONSTANT_old = 9.648e4;
        final double FARADAY_CONSTANT_NMOLE_old = 9.648e-5;
        final double N_PMOLE_value_old = 6.02e11;
        final double GAS_CONSTANT_old = 8314.0;
        final double KMOLE_value_old = 1.0/602.0;

        // restore old values of KMOLE and other ReservedSymbols if present in original Math
        // this ensures that newly generated math descriptions will use the same values
        boolean bHasOldKMOLE = false;
        for (SimulationContext simulationContext : bioModel.getSimulationContexts()){
            MathDescription orig_math = simulationContext.getMathDescription();
            Variable orig_math_KMOLE = orig_math.getVariable("KMOLE");
            if (orig_math_KMOLE != null && orig_math_KMOLE.getExpression()!=null){
                if (ExpressionUtils.functionallyEquivalent(orig_math_KMOLE.getExpression(), new Expression(1.0/602.0))){
                    bHasOldKMOLE = true;
                    break;
                }
            }
        }

        if (!bHasOldKMOLE){
            return;
        }

        // set old values for physical constants for backward compatibility
        Model.ReservedSymbol transformed_FARADAYS_CONSTANT = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.FARADAY_CONSTANT);
        transformed_FARADAYS_CONSTANT.getExpression().substituteInPlace(transformed_FARADAYS_CONSTANT.getExpression(),new Expression(FARADAY_CONSTANT_old));

        Model.ReservedSymbol transformed_FARADAYS_CONSTANT_NMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.FARADAY_CONSTANT_NMOLE);
        transformed_FARADAYS_CONSTANT_NMOLE.getExpression().substituteInPlace(transformed_FARADAYS_CONSTANT_NMOLE.getExpression(),new Expression(FARADAY_CONSTANT_NMOLE_old));

        Model.ReservedSymbol transformed_N_PMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.N_PMOLE);
        transformed_N_PMOLE.getExpression().substituteInPlace(transformed_N_PMOLE.getExpression(),new Expression(N_PMOLE_value_old));

        Model.ReservedSymbol transformed_GAS_CONSTANT = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.GAS_CONSTANT);
        transformed_GAS_CONSTANT.getExpression().substituteInPlace(transformed_GAS_CONSTANT.getExpression(),new Expression(GAS_CONSTANT_old));

        Model.ReservedSymbol transformed_KMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.KMOLE);
        transformed_KMOLE.getExpression().substituteInPlace(transformed_KMOLE.getExpression(),new Expression(KMOLE_value_old));
    }

}
