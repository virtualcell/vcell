package org.vcell.cli.run;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIRecordable;
import org.vcell.cli.CliTracer;
import org.vcell.cli.testsupport.OmexExecSummary;
import org.vcell.cli.testsupport.OmexTestingDatabase;
import org.vcell.trace.Tracer;
import org.vcell.util.FileUtils;
import org.vcell.util.exe.Executable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.concurrent.Callable;

@Command(name = "execute-omex", description = "run a single .omex file and log into exec_summary.json and tracer.json")
public class ExecuteOmexCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ExecuteOmexCommand.class);

    @Option(names = { "-i", "--inputFilePath" }, required = true, description = "Path to a COMBINE/OMEX archive file")
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath"}, required = true, description = "Directory to save outputs")
    private File outputFilePath;

    @Option(names = {"--keepTempFiles"}, defaultValue = "false")
    private boolean bKeepTempFiles = false;

    @Option(names = {"--exactMatchOnly"}, defaultValue = "false")
    private boolean bExactMatchOnly = false;

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

        CLIRecordable cliTracer = new CliTracer();
        long startTime_ms = System.currentTimeMillis();
        try {
            if (bDebug && bQuiet) {
                System.err.println("cannot specify both debug and quiet, try --help for usage");
                return 1;
            }
            Level logLevel;
            if (!bQuiet && bDebug) {
                logLevel = Level.DEBUG;
            } else if (bQuiet) {
                logLevel = Level.OFF;
            } else {
                logLevel = logger.getLevel();
            }

            if (this.inputFilePath.exists() && !this.inputFilePath.isFile()) {
                System.err.println("Input path must be a file");
                return 1;
            }
            if (this.outputFilePath.exists() && !this.outputFilePath.isDirectory()) {
                System.err.println("Output path must be a directory");
                return 1;
            }

            LoggerContext config = (LoggerContext)(LogManager.getContext(false));
            config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
            config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
            config.updateLoggers();


            CLIPythonManager.getInstance().instantiatePythonProcess();

            Executable.setGlobalTimeoutMS(EXECUTABLE_MAX_WALLCLOCK_MILLIS);
            logger.info("Beginning execution");
            File tmpDir = Files.createTempDirectory("VCell_CLI_" + Long.toHexString(new Date().getTime())).toFile();

            Tracer.clearTraceEvents();
            ExecuteImpl.singleMode(inputFilePath, tmpDir, cliTracer, bKeepTempFiles, bExactMatchOnly,
                            bEncapsulateOutput, bSmallMeshOverride);
            CLIPythonManager.getInstance().closePythonProcess();
            // WARNING: Python needs re-instantiation once the above line is called!
            FileUtils.copyDirectoryContents(tmpDir, outputFilePath, true, null);
            final OmexExecSummary omexExecSummary;
            if (Tracer.hasErrors()){
                omexExecSummary = OmexTestingDatabase.summarize(inputFilePath,(Exception)null,Tracer.getErrors(), System.currentTimeMillis() - startTime_ms);
            } else {
                omexExecSummary = new OmexExecSummary();
                omexExecSummary.file_path = String.valueOf(inputFilePath);
                omexExecSummary.status = OmexExecSummary.ActualStatus.PASSED;
                omexExecSummary.elapsed_time_ms = System.currentTimeMillis() - startTime_ms;
            }
            new ObjectMapper().writeValue(new File(outputFilePath, "exec_summary.json"), omexExecSummary);
            new ObjectMapper().writeValue(new File(outputFilePath, "tracer.json"), Tracer.getTraceEvents());
            return 0;
        } catch (Exception e) { ///TODO: Break apart into specific exceptions to maximize logging.
            LogManager.getLogger(this.getClass()).error(e.getMessage(), e);
            OmexExecSummary omexExecSummary = OmexTestingDatabase.summarize(inputFilePath,e,Tracer.getErrors(),System.currentTimeMillis() - startTime_ms);
            try {
                new ObjectMapper().writeValue(new File(outputFilePath, "exec_summary.json"), omexExecSummary);
                new ObjectMapper().writeValue(new File(outputFilePath, "tracer.json"), Tracer.getTraceEvents());
            } catch (IOException ex) {
                logger.error("Failed to write exec summary and structured logs", ex);
            }
            return 1;
        } finally {
            logger.debug("Completed all execution");
        }
    }
}
