package org.vcell.cli.vcml;

import cbit.vcell.resource.PropertyLoader;

import org.vcell.cli.CLIRecorder;
import org.vcell.sedml.ModelFormat;
import org.vcell.util.DataAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

@Command(name = "import-omex-batch", description = "convert directory of COMBINE archives (.omex) to VCML documents (1 COMBINE archive could yeild more than one BioModel).")
public class ImportOmexBatchCommand implements Callable<Integer> {
    @Option(names = { "-i", "--inputFilePath" }, description = "directory of .omex files", required = true)
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" }, description = "full path to output directory", required = true)
    private File outputFilePath;

    // TODO: add forceLogFile option to command and pass down
    
    public Integer call() {
        CLIRecorder cliLogger = null;
        try {
            boolean bForceLogFiles = true;
            boolean bKeepFlushingLogs = true;
            cliLogger = new CLIRecorder(outputFilePath, bForceLogFiles, bKeepFlushingLogs);
            PropertyLoader.loadProperties();
            if (!inputFilePath.exists() || !inputFilePath.isDirectory()){
                throw new RuntimeException("inputFilePath '"+inputFilePath+"' should be a directory");
            }
            if (!outputFilePath.exists() || !outputFilePath.isDirectory()){
                throw new RuntimeException("outputFilePath '"+outputFilePath+"' should be a directory");
            }
            VcmlOmexConverter.importOmexFiles(inputFilePath, outputFilePath, cliLogger, true);
            return 0;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage());
        }
    }
}
