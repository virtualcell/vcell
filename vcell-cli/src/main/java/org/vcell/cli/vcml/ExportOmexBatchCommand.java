package org.vcell.cli.vcml;

import cbit.vcell.resource.PropertyLoader;

import org.vcell.cli.CLILocalLogFileManager;
import org.vcell.util.DataAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

@Command(name = "export-omex-batch", description = "convert directory of VCML documents to COMBINE archives (.omex)")
public class ExportOmexBatchCommand implements Callable<Integer> {
    @Option(names = { "-m", "--outputModelFormat" }, defaultValue = "SBML", description = "expecting SBML or VCML")
    private ModelFormat outputModelFormat = ModelFormat.SBML;

    @Option(names = { "-i", "--inputFilePath" }, description = "directory of .vcml files")
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" })
    private File outputFilePath;

    @Option(names = "--hasDataOnly")
    boolean bHasDataOnly;

    @Option(names = "--makeLogsOnly")
    boolean bMakeLogsOnly;

    @Option(names = "--nonSpatialOnly")
    boolean bNonSpatialOnly;

    @Option(names = "--validate")
    boolean bValidateOmex;

    @Option(names = "--forceLogFiles")
    boolean bForceLogFiles;

    @Option(names = "--keepFlushingLogs")
    boolean bKeepFlushingLogs;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    public Integer call() {
        CLILocalLogFileManager logManager = null;
        try {
            logManager = new CLILocalLogFileManager(outputFilePath, bForceLogFiles, bKeepFlushingLogs);
            PropertyLoader.loadProperties();
            if (inputFilePath == null || !inputFilePath.exists() || !inputFilePath.isDirectory()){
                throw new RuntimeException("inputFilePath '"+inputFilePath+"' should be a directory");
            }

            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
                VcmlOmexConverter.queryVCellDbPublishedModels(cliDatabaseService, outputFilePath, bForceLogFiles);

                VcmlOmexConverter.convertFiles(cliDatabaseService, inputFilePath, outputFilePath,
                        outputModelFormat, logManager, bHasDataOnly, bMakeLogsOnly, bNonSpatialOnly, bForceLogFiles, bValidateOmex);
            } catch (IOException | SQLException | DataAccessException e) {
                e.printStackTrace(System.err);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (logManager != null) logManager.finalizeAndExportLogFiles();
        }
    }
}
