package org.vcell.cli.run;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.xml.ExternalDocInfo;
import org.jlibsedml.*;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIUtils;
import org.vcell.cli.PythonStreamException;
import org.vcell.cli.vcml.VCMLHandler;
import org.vcell.util.FileUtils;
import org.vcell.util.GenericExtensionFilter;
import org.vcell.util.exe.Executable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    public Integer call() {
        try {
            PropertyLoader.loadProperties();
            CLIPythonManager.getInstance().instantiatePythonProcess();

            Executable.setTimeoutMS(EXECUTABLE_MAX_WALLCLOCK_MILLIS);

            if (inputFilePath.isDirectory()) {
                ExecuteImpl.batchMode(inputFilePath, outputFilePath, bKeepTempFiles, bExactMatchOnly, bForceLogFiles);
            } else {
                File archiveToProcess = inputFilePath;
                if (archiveToProcess.getName().endsWith("vcml")) {
                    ExecuteImpl.singleExecVcml(archiveToProcess, outputFilePath);
                } else { // archiveToProcess.getName().endsWith("omex")
                    ExecuteImpl.singleExecOmex(archiveToProcess, outputFilePath, bKeepTempFiles, bExactMatchOnly, bForceLogFiles);
                }
            }

            CLIPythonManager.getInstance().closePythonProcess(); // WARNING: Python will need reinstantiation after this is called
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
