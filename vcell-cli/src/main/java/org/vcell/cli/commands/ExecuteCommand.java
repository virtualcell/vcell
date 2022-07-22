package org.vcell.cli.commands;

import org.vcell.cli.run.ExecuteImplementation;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

import java.io.File;

@Command(name = "execute", description = "run .vcml or .omex files via Python API")
public class ExecuteCommand implements Callable<Integer> {
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

    @Option(names = {"--timeout_ms"}, defaultValue = "600000", description = "executable wall clock timeout in milliseconds")
    // timeout for compiled solver running long jobs; default 12 hours
    private long EXECUTABLE_MAX_WALLCLOCK_MILLIS;

    public Integer call(){
        ExecuteImplementation ec = new ExecuteImplementation(inputFilePath, outputFilePath, bForceLogFiles, 
            bKeepTempFiles, bExactMatchOnly, EXECUTABLE_MAX_WALLCLOCK_MILLIS);
        return ec.call();
    }
}
