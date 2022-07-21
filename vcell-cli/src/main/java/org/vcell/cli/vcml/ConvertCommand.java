package org.vcell.cli.vcml;

import cbit.vcell.resource.PropertyLoader;
import org.vcell.util.DataAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

@Command(name = "convert", description = "convert from VCML to COMBINE archive (.omex)")
public class ConvertCommand implements Callable<Integer> {
    @Option(names = "-m", defaultValue = "SBML")
    private ModelFormat outputModelFormat;

    @Option(names = { "-i", "--inputFilePath" })
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" })
    private File outputFilePath;

    @Option(names = "--hasDataOnly")
    boolean bHasDataOnly;

    @Option(names = "--makeLogsOnly")
    boolean bMakeLogsOnly;

    @Option(names = "--nonSpatialOnly")
    boolean bNonSpatialOnly;

    @Option(names = "--forceLogFiles")
    boolean bForceLogFiles;

    public Integer call() {
        try {
            PropertyLoader.loadProperties();

            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
                VcmlOmexConverter.convertFiles(cliDatabaseService, inputFilePath, outputFilePath,
                        outputModelFormat, bHasDataOnly, bMakeLogsOnly, bNonSpatialOnly, bForceLogFiles);
            } catch (IOException | SQLException | DataAccessException e) {
                e.printStackTrace(System.err);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
