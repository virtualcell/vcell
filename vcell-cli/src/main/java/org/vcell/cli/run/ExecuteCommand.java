package org.vcell.cli.run;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIRecorder;
import org.vcell.util.exe.Executable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "execute", description = "run .vcml or .omex files via Python API")
public class ExecuteCommand implements Callable<Integer> {

    private final static Logger logger = org.apache.logging.log4j.LogManager.getLogger(ExecuteCommand.class);

    @Option(names = { "-i", "--inputFilePath" }, required = true)
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath"}, required = true)
    private File outputFilePath;

    @Option(names = {"--writeLogFiles"}, defaultValue = "false")
    private boolean shouldWriteLogFiles = false;

    @Option(names = {"--keepTempFiles"}, defaultValue = "false")
    private boolean shouldKeepTempFiles = false;

    @Option(names = {"--exactMatchOnly"}, defaultValue = "false")
    private boolean shouldEnforceExactMatchOnly = false;

    @Option(names = "--keepFlushingLogs", defaultValue = "false")
    private boolean shouldKeepFlushingLogs = false;

    @Option(names = "--small-mesh", defaultValue = "false", description = "force spatial simulations to have a very small mesh to make execution faster")
    private boolean shouldEngageSmallMeshOverride = false;

    @Option(names = {"--encapsulateOutput"}, defaultValue = "true", description =
        "VCell will encapsulate output results in a sub directory when executing with a single input archive; has no effect when providing an input directory")
    private boolean shouldEncapsulateOutput = true;

    @Option(names = {"--timeout_ms"}, defaultValue = "600000", description = "executable wall clock timeout in milliseconds")
    // timeout for compiled solver running long jobs; default 10 minutes
    private long EXECUTABLE_MAX_WALLCLOCK_MILLIS = 10 * 60 * 1000;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean shouldDisplayHelp = false;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean shouldEngageDebugMode = false;

    @Option(names = {"-q", "--quiet"}, description = "suppress all console output")
    private boolean shouldEngageQuietMode = false;


    public Integer call() {
        CLIRecorder cliRecorder;
        try {
            Level logLevel;
            if (!shouldEngageQuietMode && shouldEngageDebugMode) {
                logLevel = Level.DEBUG;
            } else if (shouldEngageQuietMode) {
                logLevel = Level.OFF;
            } else {
                logLevel = logger.getLevel();
            }

            // CLILogger will throw an exception if our output dir isn't valid.
            boolean shouldFlush = this.shouldKeepFlushingLogs || this.shouldEngageDebugMode;
            cliRecorder = new CLIRecorder(this.outputFilePath, this.shouldWriteLogFiles, shouldFlush);
            
            LoggerContext config = (LoggerContext)(LogManager.getContext(false));
            config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
            config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
            config.updateLoggers();

            logger.debug("Execution mode requested");

            String trace_args =  String.format(
                    """
                            Arguments:
                            Input\t: "%s"
                            Output\t: "%s"
                            WriteLogs\t: %b
                            KeepTemp\t: %b
                            ExactMatch\t: %b
                            EncapsulateOut\t: %b
                            Timeout\t: %dms
                            Help\t: %b
                            Debug\t: %b
                            Quiet\t: %b""",
                inputFilePath.getAbsolutePath(), outputFilePath.getAbsolutePath(), shouldWriteLogFiles,
                    shouldKeepTempFiles, shouldEnforceExactMatchOnly, shouldEncapsulateOutput,
                    EXECUTABLE_MAX_WALLCLOCK_MILLIS, shouldDisplayHelp, shouldEngageDebugMode, shouldEngageQuietMode
            );
            logger.trace(trace_args);

            logger.debug("Validating CLI arguments");
            if (shouldEngageDebugMode && shouldEngageQuietMode) {
                System.err.println("cannot specify both debug and quiet, try --help for usage");
                return 1;
            }

            CLIPythonManager.getInstance().instantiatePythonProcess();
            

            Executable.setTimeoutMS(EXECUTABLE_MAX_WALLCLOCK_MILLIS);
            logger.info("Beginning execution");
            if (inputFilePath.isDirectory()) {
                return launchBatchMode(cliRecorder);
            } else {
                return launchSingleMode(cliRecorder);
            }
        } catch (Exception e) { ///TODO: Break apart into specific exceptions to maximize logging.
            org.apache.logging.log4j.LogManager.getLogger(this.getClass()).error(e.getMessage(), e);
            return 1;
        } finally {
            try {
                CLIPythonManager.getInstance().closePythonProcess();
                // WARNING: Python needs re-instantiation once the above line is called!
            } catch (IOException e){
                logger.error("Unable to properly close Python Process:", e);
            }
            logger.debug("Completed all execution");
        }
    }

    public Integer launchBatchMode(CLIRecorder recorder) throws IOException {
        logger.debug("Batch mode requested");
        return ExecuteImpl.batchMode(inputFilePath, outputFilePath, recorder, shouldKeepTempFiles, shouldEnforceExactMatchOnly,
                shouldEngageSmallMeshOverride);
    }

    public Integer launchSingleMode(CLIRecorder recorder) throws Exception {
        logger.debug("Single mode requested");

        if (inputFilePath.getName().endsWith("vcml")) {
            return ExecuteImpl.singleExecVcml(inputFilePath, outputFilePath, recorder);
        } else { // archiveToProcess.getName().endsWith("omex")
            return ExecuteImpl.singleMode(inputFilePath, outputFilePath, recorder, shouldKeepTempFiles, shouldEnforceExactMatchOnly,
                    shouldEncapsulateOutput, shouldEngageSmallMeshOverride);
        }
    }
}
