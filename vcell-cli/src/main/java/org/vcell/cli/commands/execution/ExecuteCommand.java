package org.vcell.cli.commands.execution;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.vcell.cli.messaging.CLIRecorder;
import org.vcell.cli.run.ExecuteImpl;
import org.vcell.util.FileUtils;
import org.vcell.util.exe.Executable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Command(name = "execute", description = "run .vcml or .omex files.")
public class ExecuteCommand extends ExecutionBasedCommand {

    private final static Logger logger = org.apache.logging.log4j.LogManager.getLogger(ExecuteCommand.class);

    @Option(names = {"-i", "--inputFilePath"}, required = true)
    private File inputFilePath;

    @Option(names = {"-o", "--outputFilePath"}, required = true)
    private File outputFilePath;

    @Option(names = {"--writeLogFiles"}, defaultValue = "false")
    private boolean bWriteLogFiles = false;

    @Option(names = {"--keepTempFiles"}, defaultValue = "false")
    private boolean bKeepTempFiles = false;

    @Option(names = {"--exactMatchOnly"}, defaultValue = "false")
    private boolean bExactMatchOnly = false;

    @Option(names = "--keepFlushingLogs", defaultValue = "false")
    private boolean bKeepFlushingLogs = false;

    @Option(names = "--guaranteeGoodReturnCode", defaultValue = "false", description = "Even on failure, return error code 0 for script purposes.")
    private boolean bGuaranteeGoodReturnCode = false;

    @Option(names = "--small-mesh", defaultValue = "false", description = "force spatial simulations to have a very small mesh to make execution faster")
    private boolean bSmallMeshOverride = false;

    @Option(names = {"--encapsulateOutput"}, defaultValue = "true", description =
        "VCell will encapsulate output results in a sub directory when executing with a single input archive; has no effect when providing an input directory")
    private boolean bEncapsulateOutput = true;

    @Option(names = {"--timeout_ms"}, defaultValue = "600000", description = "executable wall clock timeout in milliseconds")
    // timeout for compiled solver running long jobs; default 10 minutes
    private long EXECUTABLE_MAX_WALLCLOCK_MILLIS = 10 * 60 * 1000;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help = false;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = {"-q", "--quiet"}, description = "suppress all console output")
    private boolean bQuiet = false;


    /**
     * All the processing done related to validating and storing the arguments provided at runtime.
     *
     * @return true if the execution needs to proceed, otherwise false
     * <br/>
     * Note that returning false does NOT mean execution failed, but rather succeeded (think: show help / version)
     */
    @Override
    protected boolean executionShouldContinue() {
        Level logLevel;
        if (!this.bQuiet && this.bDebug) {
            logLevel = Level.DEBUG;
        } else if (this.bQuiet) {
            logLevel = Level.OFF;
        } else {
            logLevel = logger.getLevel();
        }

        ExecuteCommand.generateLoggerContext(logLevel, this.bDebug);

        String trace_args = String.format(
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
                this.inputFilePath.getAbsolutePath(), this.outputFilePath.getAbsolutePath(), this.bWriteLogFiles,
                this.bKeepTempFiles, this.bExactMatchOnly, this.bEncapsulateOutput,
                this.EXECUTABLE_MAX_WALLCLOCK_MILLIS, this.help, this.bDebug, this.bQuiet
        );
        logger.debug(trace_args);

        logger.debug("Validating CLI arguments");
        if (this.bDebug && this.bQuiet) {
            System.err.println("cannot specify both debug and quiet, try --help for usage");
            return false;
        }

        this.setTimeout(this.EXECUTABLE_MAX_WALLCLOCK_MILLIS, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     * Perform the desired command
     *
     * @return return code of the command
     */
    @Override
    protected Integer executeCommand() {
        CLIRecorder cliLogger; // CLILogger will throw an exception if our output dir isn't valid.
        boolean shouldFlush = this.bKeepFlushingLogs || this.bDebug;

        logger.debug("Execution mode requested");
        try {
            cliLogger = new CLIRecorder(this.outputFilePath, this.bWriteLogFiles, shouldFlush);
            logger.info("Beginning execution");
            File tmpDir = Files.createTempDirectory("VCell_CLI_" + Long.toHexString(new Date().getTime())).toFile();
            if (this.inputFilePath.isDirectory()) {
                logger.debug("Batch mode requested");
                ExecuteImpl.batchMode(this.inputFilePath, tmpDir, cliLogger, this.bKeepTempFiles, this.bExactMatchOnly,
                        this.bSmallMeshOverride);
            } else {
                logger.debug("Single mode requested");
                File archiveToProcess = this.inputFilePath;

                if (archiveToProcess.getName().endsWith("vcml")) {
                    ExecuteImpl.singleExecVcml(archiveToProcess, tmpDir, cliLogger);
                } else { // archiveToProcess.getName().endsWith("omex")
                    ExecuteImpl.singleMode(archiveToProcess, tmpDir, cliLogger, this.bKeepTempFiles, this.bExactMatchOnly,
                            this.bEncapsulateOutput, this.bSmallMeshOverride);
                }
            }

            // WARNING: Python needs re-instantiation once the above line is called!
            FileUtils.copyDirectoryContents(tmpDir, this.outputFilePath, true, null);
            return 0;
        } catch (Exception e) { ///TODO: Break apart into specific exceptions to maximize logging.
            org.apache.logging.log4j.LogManager.getLogger(this.getClass()).error(e.getMessage(), e);
            return this.bGuaranteeGoodReturnCode ? 0 : 1;
        } finally {
            logger.debug("Completed all execution");
        }
    }
}
