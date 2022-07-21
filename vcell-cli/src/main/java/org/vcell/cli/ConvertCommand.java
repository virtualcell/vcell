package org.vcell.cli;

import cbit.vcell.resource.PropertyLoader;
import org.vcell.cli.vcml.ModelFormat;
import org.vcell.cli.vcml.VcmlOmexConverter;
import org.vcell.util.DataAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

@Command(name = "convert")
class ConvertCommand implements Callable<Integer> {
    @Option(names = "-m")
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
        System.out.println("in convert() with outputModelFormat = " + outputModelFormat);
        try {
            PropertyLoader.loadProperties();
            CLIPythonManager.getInstance().instantiatePythonProcess();

            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
                VcmlOmexConverter.convertFiles(cliDatabaseService, inputFilePath, outputFilePath,
                        outputModelFormat, bHasDataOnly, bMakeLogsOnly, bNonSpatialOnly, bForceLogFiles);
            } catch (IOException | SQLException | DataAccessException e) {
                e.printStackTrace(System.err);
            }

            CLIPythonManager.getInstance().closePythonProcess(); // WARNING: Python will need reinstantiation after this is called
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
