package org.vcell.cli.run;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIRecorder;
import org.vcell.util.FileUtils;
import org.vcell.util.exe.Executable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.Date;

@Command(name = "execute", description = "run .vcml or .omex files.")
public class ExecuteCommand implements Callable<Integer> {

    private final static Logger logger = org.apache.logging.log4j.LogManager.getLogger(ExecuteCommand.class);

    @Option(names = { "-i", "--inputFilePath" }, required = true)
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath"}, required = true)
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


    public Integer call() {
        CLIRecorder cliLogger;
        try {
            Level logLevel;
            if (!bQuiet && bDebug) {
                logLevel = Level.DEBUG;
            } else if (bQuiet) {
                logLevel = Level.OFF;
            } else {
                logLevel = logger.getLevel();
            }

            // CLILogger will throw an exception if our output dir isn't valid.
            boolean shouldFlush = this.bKeepFlushingLogs || this.bDebug;
            cliLogger = new CLIRecorder(this.outputFilePath, this.bWriteLogFiles, shouldFlush);
            
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
                inputFilePath.getAbsolutePath(), outputFilePath.getAbsolutePath(), bWriteLogFiles,
                    bKeepTempFiles, bExactMatchOnly, bEncapsulateOutput, 
                    EXECUTABLE_MAX_WALLCLOCK_MILLIS, help, bDebug, bQuiet
            );
            logger.debug(trace_args);

            logger.debug("Validating CLI arguments");
            if (bDebug && bQuiet) {
                System.err.println("cannot specify both debug and quiet, try --help for usage");
                return 1;
            }

            CLIPythonManager.getInstance().instantiatePythonProcess();
            

            Executable.setGlobalTimeoutMS(EXECUTABLE_MAX_WALLCLOCK_MILLIS);
            logger.info("Beginning execution");
            File tmpDir = Files.createTempDirectory("VCell_CLI_" + Long.toHexString(new Date().getTime())).toFile();
            if (inputFilePath.isDirectory()) {
                logger.debug("Batch mode requested");
                ExecuteImpl.batchMode(inputFilePath, tmpDir, cliLogger, bKeepTempFiles, bExactMatchOnly,
                        bSmallMeshOverride);
            } else {
                logger.debug("Single mode requested");
                File archiveToProcess = inputFilePath;

                if (archiveToProcess.getName().endsWith("vcml")) {
                    ExecuteImpl.singleExecVcml(archiveToProcess, tmpDir, cliLogger);
                } else { // archiveToProcess.getName().endsWith("omex")
                    ExecuteImpl.singleMode(archiveToProcess, tmpDir, cliLogger, bKeepTempFiles, bExactMatchOnly,
                            bEncapsulateOutput, bSmallMeshOverride);
                }
            }
            CLIPythonManager.getInstance().closePythonProcess();
            // WARNING: Python needs re-instantiation once the above line is called!
            FileUtils.copyDirectoryContents(tmpDir, outputFilePath, true, null);
            return 0;
        } catch (Exception e) { ///TODO: Break apart into specific exceptions to maximize logging.
            org.apache.logging.log4j.LogManager.getLogger(this.getClass()).error(e.getMessage(), e);
            return bGuaranteeGoodReturnCode ? 0 : 1;
        } finally {
            logger.debug("Completed all execution");
        }
    }
}
