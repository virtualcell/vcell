package org.vcell.cli.biosimulation;

import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIRecorder;
import org.vcell.cli.run.ExecuteImpl;
import org.vcell.cli.trace.Tracer;
import org.vcell.util.exe.Executable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "biosimulations", description = "BioSimulators-compliant command-line interface to the vcell simulation program <https://vcell.org>.")
public class BiosimulationsCommand implements Callable<Integer> {

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

    public Integer call() {
        CLIRecorder cliRecorder = null;
        int returnCode;
        
        if ((returnCode = this.noFurtherActionNeeded(bQuiet, bDebug, bVersion)) != -1)
            return returnCode;
        
        try {
            cliRecorder = new CLIRecorder(OUT_DIR); // CLILogger will throw an execption if our output dir isn't valid.
            Level logLevel = logger.getLevel();
            if (!bQuiet && bDebug) {
                logLevel = Level.DEBUG;
            } else if (bQuiet) {
                logLevel = Level.OFF;
            }
            LoggerContext config = (LoggerContext)(LogManager.getContext(false));
            config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
            config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
            config.updateLoggers();

            logger.debug("Biosimulations mode requested");

            String trace_args =  String.format(
                "Arguments:\nArchive\t: \"%s\"\nOut Dir\t: \"%s\"\nDebug\t: %b\n" +
                    "Quiet\t: %b\nVersion\t: %b\nHelp\t: %b",
                ARCHIVE.getAbsolutePath(), OUT_DIR.getAbsolutePath(), bDebug, bQuiet, bVersion, help
            );

            logger.trace(trace_args);

            logger.trace("Validating input");
            if (ARCHIVE == null) {
                logger.error("ARCHIVE file not specified, try --help for usage");
                return 1;
            }
            if (!ARCHIVE.isFile()) {
                logger.error("ARCHIVE file " + ARCHIVE.getAbsolutePath() + " not found, try --help for usage");
                return 1;
            }

            logger.trace("Validating output");
            if (OUT_DIR == null) {
                logger.error("OUT_DIR not specified, try --help for usage");
                return 1;
            }
            if (!OUT_DIR.isDirectory()) {
                logger.error("OUT_DIR " + OUT_DIR.getAbsolutePath() + " not found or is not a directory, try --help for usage");
                return 1;
            }
            long EXECUTABLE_MAX_WALLCLOCK_MILLIS = 600000;
            Executable.setTimeoutMS(EXECUTABLE_MAX_WALLCLOCK_MILLIS);

            logger.info("Beginning execution");
            try {
                CLIPythonManager.getInstance().instantiatePythonProcess();
                ExecuteImpl.singleMode(ARCHIVE, OUT_DIR, cliRecorder);
                if (Tracer.hasErrors()){
                    if (!bQuiet) {
                        logger.error("Errors occurred during execution");
                        Tracer.reportErrors(bDebug);
                    }
                    return 1;
                }else {
                    return 0;
                }
            } finally {
                try {
                    CLIPythonManager.getInstance().closePythonProcess(); // WARNING: Python will need reinstantiation after this is called
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                logger.debug("Finished all execution.");
            }
        } catch (Exception e) {
            if (!bQuiet) {
                Tracer.reportErrors(bDebug);
                logger.error(e.getMessage(), e);
            }
            return 1;
        }
    }

    private int noFurtherActionNeeded(boolean bQuiet, boolean bDebug, boolean bVersion){
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
