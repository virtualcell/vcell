package org.vcell.cli.biosimulation;

import cbit.vcell.resource.PropertyLoader;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIUtils;
import org.vcell.cli.run.ExecuteImpl;
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
        try {
            if (bDebug && bQuiet) {
                System.err.println("cannot specify both debug and quiet, try --help for usage");
                return 1;
            }

            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            Level logLevel = Level.WARN;
            if (bDebug) {
                logLevel = Level.DEBUG;
            } else if (bQuiet) {
                logLevel = Level.OFF;
            }
            
            CLIUtils.setLogLevel(ctx, logLevel);

            PropertyLoader.loadProperties();
            if (bVersion) {
                System.out.println(PropertyLoader.getRequiredDirectory(PropertyLoader.vcellSoftwareVersion));
                return 0;
            }

            if (ARCHIVE == null) {
                System.err.println("ARCHIVE file not specified, try --help for usage");
                return 1;
            }
            if (!ARCHIVE.isFile()) {
                System.err.println("ARCHIVE file " + ARCHIVE.getAbsolutePath() + " not found, try --help for usage");
                return 1;
            }
            if (OUT_DIR == null) {
                System.err.println("OUT_DIR not specified, try --help for usage");
                return 1;
            }
            if (!OUT_DIR.isDirectory()) {
                System.err.println("OUT_DIR " + OUT_DIR.getAbsolutePath() + " not found or is not a directory, try --help for usage");
                return 1;
            }
            long EXECUTABLE_MAX_WALLCLOCK_MILLIS = 600000;
            Executable.setTimeoutMS(EXECUTABLE_MAX_WALLCLOCK_MILLIS);

            final boolean bForceLogFiles = false;
            final boolean bKeepTempFiles = false;
            final boolean bExactMatchOnly = false;

            try {
                CLIPythonManager.getInstance().instantiatePythonProcess();
                ExecuteImpl.singleExecOmex(ARCHIVE, OUT_DIR, bKeepTempFiles, bExactMatchOnly, bForceLogFiles, false);
                return 0;
            } finally {
                try {
                    CLIPythonManager.getInstance().closePythonProcess(); // WARNING: Python will need reinstantiation after this is called
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return 1;
        }
    }
}
