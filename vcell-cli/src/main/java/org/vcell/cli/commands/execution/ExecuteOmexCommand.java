package org.vcell.cli.commands.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.cli.messaging.CLIRecordable;
import org.vcell.cli.messaging.CliTracer;
import org.vcell.cli.run.ExecuteImpl;
import org.vcell.cli.vcml.VcmlOmexConverter;
import org.vcell.sedml.ModelFormat;
import org.vcell.sedml.testsupport.OmexExecSummary;
import org.vcell.sedml.testsupport.OmexTestingDatabase;
import org.vcell.trace.Span;
import org.vcell.trace.Tracer;
import org.vcell.util.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.concurrent.*;

@Command(name = "execute-omex", description = "run a single .omex file and log into exec_summary.json and tracer.json")
public class ExecuteOmexCommand extends ExecutionBasedCommand {

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


    @Override
    public Integer call() throws Exception {
        if (!this.executionShouldContinue()) return 0;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> futureResult = executor.submit(this::executeCommand);
        long startTime_ms = System.currentTimeMillis();
        try {
            try { // This try is explicitly for timeouts
                if (this.getTimeout() == 0) {
                    futureResult.get();
                } else {
                    futureResult.get(this.getTimeout(), this.getTimeUnit());
                }
            } catch (TimeoutException e) {
                // In the event of timeout, we want to log like we're talking from the command in question
                // We'll leave an in-log reference to the actual class and method
                futureResult.cancel(true);
                String debugSnipIt = logger.getLevel().isInRange(Level.TRACE, Level.DEBUG) ? "(TimeLimitedCommand::call) " : "";
                String msg = String.format("%sProcess timed out: Task took too long, exceeding %s %s.", debugSnipIt, this.getTimeout(), this.getTimeUnit().toString().toLowerCase());
                LogManager.getLogger(this.getClass()).error(msg);
                throw new ExecutionException(msg, e);
            } finally {
                executor.shutdown();
            }
            final OmexExecSummary omexExecSummary;
            if (Tracer.hasErrors()){
                omexExecSummary = OmexTestingDatabase.summarize(this.inputFilePath, null,Tracer.getErrors(), System.currentTimeMillis() - startTime_ms);
            } else {
                omexExecSummary = new OmexExecSummary();
                omexExecSummary.file_path = String.valueOf(this.inputFilePath);
                omexExecSummary.status = OmexExecSummary.ActualStatus.PASSED;
                omexExecSummary.elapsed_time_ms = System.currentTimeMillis() - startTime_ms;
            }
            new ObjectMapper().writeValue(new File(this.outputFilePath, "exec_summary.json"), omexExecSummary);
            new ObjectMapper().writeValue(new File(this.outputFilePath, "tracer.json"), Tracer.getTraceEvents());
            return 0;
        } catch (Exception e) { ///TODO: Break apart into specific exceptions to maximize logging.
            LogManager.getLogger(this.getClass()).error(e.getMessage(), e);
            OmexExecSummary omexExecSummary = OmexTestingDatabase.summarize(this.inputFilePath,e,Tracer.getErrors(),System.currentTimeMillis() - startTime_ms);
            try {
                new ObjectMapper().writeValue(new File(this.outputFilePath, "exec_summary.json"), omexExecSummary);
                new ObjectMapper().writeValue(new File(this.outputFilePath, "tracer.json"), Tracer.getTraceEvents());
            } catch (IOException ex) {
                logger.error("Failed to write exec summary and structured logs", ex);
            }
            return 1;
        } finally {
            logger.debug("Completed all execution");
        }
    }


    /**
     * All the processing done related to validating and storing the arguments provided at runtime.
     *
     * @return true if the execution needs to proceed, otherwise false
     * <br/>
     * Note that returning false does NOT mean execution failed, but rather succeeded (think: show help / version)
     */
    @Override
    protected boolean executionShouldContinue() {
        if (this.bDebug && this.bQuiet) {
            System.err.println("cannot specify both debug and quiet, try --help for usage");
            return false;
        }
        Level logLevel;
        if (!this.bQuiet && this.bDebug) {
            logLevel = Level.DEBUG;
        } else if (this.bQuiet) {
            logLevel = Level.OFF;
        } else {
            logLevel = logger.getLevel();
        }

        if (this.inputFilePath.exists() && !this.inputFilePath.isFile()) {
            System.err.println("Input path must be a file");
            return false;
        }
        if (this.outputFilePath.exists() && !this.outputFilePath.isDirectory()) {
            System.err.println("Output path must be a directory");
            return false;
        }

        ExecuteOmexCommand.generateLoggerContext(logLevel, this.bDebug);
        this.setTimeout(this.EXECUTABLE_MAX_WALLCLOCK_MILLIS, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     * Perform the desired command
     *
     * @return return code of the command
     */
    @Override
    protected Integer executeCommand() throws Exception {
            File tmpDirExec = Files.createTempDirectory("VCell_CLI_" + Long.toHexString(new Date().getTime())).toFile();
            File tmpDirConv = Files.createTempDirectory("VCell_CLI_" + Long.toHexString(new Date().getTime())).toFile();
            Tracer.clearTraceEvents();

            boolean needsNoConversion = !this.inputFilePath.getName().endsWith(".vcml");
            File inFile = needsNoConversion ? this.inputFilePath : ExecuteOmexCommand.performInputFileConversion(this.inputFilePath, tmpDirConv);

            CLIRecordable cliTracer = new CliTracer();
            if (inFile != null)
                ExecuteImpl.singleMode(inFile, tmpDirExec, cliTracer,
                        this.bKeepTempFiles, this.bExactMatchOnly, this.bEncapsulateOutput, this.bSmallMeshOverride);


            FileUtils.copyDirectoryContents(tmpDirExec, this.outputFilePath, true, null);
            return 0;

    }

    private static File performInputFileConversion(File inputFile, File outputDir) {
        File toReturn;
        Span span = null;
        try {
            span = Tracer.startSpan(Span.ContextType.OMEX_EXPORT, "convertOneFile", null);
            VcmlOmexConverter.convertOneFile(inputFile, outputDir, ModelFormat.SBML,
                    false, true, false, false);
            toReturn = new File(outputDir, inputFile.getName().replace(".vcml", ".omex"));
        } catch (Exception e){
            String prefix = "Failed to convert necessary file to sbml/sedml combine archive";
            logger.error(prefix, e);
            Tracer.failure(e, prefix);
            toReturn = null;
        } finally {
            if (span != null) span.close();
        }
        return toReturn;
    }
}
