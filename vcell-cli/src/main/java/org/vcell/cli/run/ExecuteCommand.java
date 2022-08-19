package org.vcell.cli.run;

import cbit.vcell.resource.PropertyLoader;

import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIUtils;
import org.vcell.util.exe.Executable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "execute", description = "run .vcml or .omex files via Python API")
public class ExecuteCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ExecuteCommand.class);

    @Option(names = { "-i", "--inputFilePath" })
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath"})
    private File outputFilePath;

    @Option(names = {"--forceLogFiles"})
    private boolean bForceLogFiles;

    @Option(names = {"--keepTempFiles"})
    private boolean bKeepTempFiles;

    @Option(names = {"--exactMatchOnly"})
    private boolean bExactMatchOnly;

    @Option(names = {"--encapsulateOutput"}, defaultValue = "true", description = 
        "VCell will encapsulate output results in a sub directory when executing with a single input archive; has no effect when providing an input directory")
    private boolean bEncapsulateOutput;

    @Option(names = {"--timeout_ms"}, defaultValue = "600000", description = "executable wall clock timeout in milliseconds")
    // timeout for compiled solver running long jobs; default 12 hours
    private long EXECUTABLE_MAX_WALLCLOCK_MILLIS;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = {"-q", "--quiet"}, description = "suppress all console output")
    private boolean bQuiet = false;

    public Integer call() {
        try {
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            Level logLevel = logger.getLevel();
            if (!bQuiet && bDebug) {
                logLevel = Level.DEBUG;
            } else if (bQuiet) {
                logLevel = Level.OFF;
            }
            CLIUtils.setLogLevel(ctx, logLevel);

            logger.debug("Execution mode requested");

            String trace_args =  String.format(
                "Arguments:\nInput\t: \"%s\"\nOutput\t: \"%s\"\nForceLogs\t: %b\n" +
                    "KeepTemp\t: %b\nExactMatch\t: %b\nEncapOut\t: %b\nTimeout\t: %dms\n" + 
                    "Help\t: %b\nDebug\t: %b\nQuiet\t: %b",
                inputFilePath.getAbsolutePath(), outputFilePath.getAbsolutePath(), bForceLogFiles, 
                    bKeepTempFiles, bExactMatchOnly, bEncapsulateOutput, 
                    EXECUTABLE_MAX_WALLCLOCK_MILLIS, help, bDebug, bQuiet
            );
            logger.trace(trace_args);

            logger.debug("Validating CLI arguments");
            if (bDebug && bQuiet) {
                System.err.println("cannot specify both debug and quiet, try --help for usage");
                return 1;
            }

            PropertyLoader.loadProperties();
            CLIPythonManager.getInstance().instantiatePythonProcess();

            Executable.setTimeoutMS(EXECUTABLE_MAX_WALLCLOCK_MILLIS);
            logger.info("Beginning execution");
            if (inputFilePath.isDirectory()) {
                logger.debug("Batch mode requested");
                CLIUtils.createHeader(outputFilePath, bForceLogFiles);
                ExecuteImpl.batchMode(inputFilePath, outputFilePath, bKeepTempFiles, bExactMatchOnly, bForceLogFiles);
            } else {
                logger.debug("Single mode requested");
                File archiveToProcess = inputFilePath;
                if (bForceLogFiles) CLIUtils.createHeader(outputFilePath, bForceLogFiles);

                if (archiveToProcess.getName().endsWith("vcml")) {
                    ExecuteImpl.singleExecVcml(archiveToProcess, outputFilePath);
                } else { // archiveToProcess.getName().endsWith("omex")
                    ExecuteImpl.singleExecOmex(archiveToProcess, outputFilePath, bKeepTempFiles, bExactMatchOnly, bForceLogFiles, bEncapsulateOutput);
                }
            }

            logger.trace("Closing Python Instance");
            CLIPythonManager.getInstance().closePythonProcess(); // WARNING: Python will need reinstantiation after this is called
            return 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 1;
        }
    }
}
