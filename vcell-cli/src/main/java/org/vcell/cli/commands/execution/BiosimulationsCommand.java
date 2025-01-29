package org.vcell.cli.commands.execution;

import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.cli.messaging.CLIRecorder;
import org.vcell.cli.run.ExecuteImpl;
import org.vcell.trace.Tracer;
import org.vcell.util.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;
import java.util.concurrent.Callable;

@Command(name = "biosimulations", description = "BioSimulators-compliant command-line interface to the vcell simulation program <https://vcell.org>.")
public class BiosimulationsCommand extends ExecutionBasedCommand {

    private final static Logger logger = LogManager.getLogger(BiosimulationsCommand.class);

    @Option(names = {"-i", "--archive"}, description = "Path to a COMBINE/OMEX archive file which contains one or more SED-ML-encoded simulation experiments")
    private File ARCHIVE;

    @Option(names = {"-o", "--out-dir"}, description = "Directory to save outputs")
    private File OUT_DIR;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = {"-q", "--quiet"}, description = "suppress all console output")
    private boolean bQuiet = false;

    @Option(names = {"-v", "--version"}, description = "show program's version number and exit")
    private boolean bVersion = false;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    /**
     * All the processing done related to validating and storing the arguments provided at runtime.
     *
     * @return true if the execution needs to proceed, otherwise false
     * <br/>
     * Note that returning false does NOT mean execution failed, but rather succeeded (think: show help / version)
     */
    @Override
    protected boolean executionShouldContinue() {
        return -1 == BiosimulationsCommand.noFurtherActionNeeded(this.bQuiet, this.bDebug, this.bVersion);
    }

    /**
     * Perform the desired command
     *
     * @return return code of the command
     */
    @Override
    protected Integer executeCommand() {
        if (this.ARCHIVE == null) {
            logger.error("ARCHIVE file not specified, try --help for usage");
            return 1;
        }
        if (this.OUT_DIR == null) {
            logger.error("OUT_DIR not specified, try --help for usage");
            return 1;
        }
        String trace_args =  String.format(
                "Arguments:\nArchive\t: \"%s\"\nOut Dir\t: \"%s\"\nDebug\t: %b\n" +
                        "Quiet\t: %b\nVersion\t: %b\nHelp\t: %b",
                this.ARCHIVE.getAbsolutePath(), this.OUT_DIR.getAbsolutePath(), this.bDebug, this.bQuiet, this.bVersion, this.help
        );

        logger.trace(trace_args);
        return BiosimulationsCommand.executeVCellBiosimulationsMode(this.ARCHIVE, this.OUT_DIR, this.bQuiet, this.bDebug);
    }

    public static int executeVCellBiosimulationsMode(File inFile, File outDir){
        return BiosimulationsCommand.executeVCellBiosimulationsMode(inFile, outDir, false, false);
    }

    public static int executeVCellBiosimulationsMode(File inFile, File outDir, boolean bQuiet, boolean bDebug){
        CLIRecorder cliRecorder;

        try {
            cliRecorder = new CLIRecorder(outDir); // CLILogger will throw an execption if our output dir isn't valid.
            Level logLevel = logger.getLevel();
            if (!bQuiet && bDebug) {
                logLevel = Level.DEBUG;
            } else if (bQuiet) {
                logLevel = Level.OFF;
            }

            BiosimulationsCommand.generateLoggerContext(logLevel, bDebug);

            logger.debug("Biosimulations mode requested");

            logger.trace("Validating input");
            if (!inFile.isFile()) {
                logger.error("ARCHIVE file " + inFile.getAbsolutePath() + " not found, try --help for usage");
                return 1;
            }

            logger.trace("Validating output");
            if (!outDir.isDirectory()) {
                logger.error("OUT_DIR " + outDir.getAbsolutePath() + " not found or is not a directory, try --help for usage");
                return 1;
            }

            logger.info("Beginning execution");
            File tmpDir = Files.createTempDirectory("VCell_CLI_" + Long.toHexString(new Date().getTime())).toFile();
            try {
                ExecuteImpl.singleMode(inFile, tmpDir, cliRecorder, true);
                if (!Tracer.hasErrors()) return 0;
                if (!bQuiet) {
                    logger.error("Errors occurred during execution");
                    Tracer.reportErrors(bDebug);
                }
                return 1;
            } finally {
                logger.debug("Finished all execution.");
                FileUtils.copyDirectoryContents(tmpDir, outDir, true, null);
            }
        } catch (Exception e) {
            if (!bQuiet) {
                Tracer.reportErrors(bDebug);
                logger.error(e.getMessage(), e);
            }
            return 1;
        }
    }

    private static int noFurtherActionNeeded(boolean bQuiet, boolean bDebug, boolean bVersion){
        logger.debug("Validating CLI arguments");
        if (bVersion) {
            String version = PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion);
            logger.info("Outputing version:");
            System.out.println(version);
            return 0;
        }

        if (bDebug && bQuiet) {
            logger.error("cannot specify both debug and quiet, try --help for usage");
            return 1;
        }

        return -1;
    }
}
