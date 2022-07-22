package org.vcell.cli.vcml;

import org.vcell.cli.Implementation;

import cbit.util.xml.VCLogger;
import cbit.vcell.resource.PropertyLoader;
import org.vcell.cli.CLIUtils;
import org.vcell.util.DataAccessException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ConvertImplementation implements Implementation {
    private ModelFormat outputModelFormat = ModelFormat.SBML;

    private File inputFilePath, outputFilePath;
    private boolean bHasDataOnly, bMakeLogsOnly, bNonSpatialOnly, bForceLogFiles, bValidateOmex;

    public ConvertImplementation(File inputFilePath, File outputFilePath, boolean bHasDataOnly, boolean bMakeLogsOnly, 
            boolean bNonSpatialOnly, boolean bForceLogFiles, boolean bValidateOmex){
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.bHasDataOnly = bHasDataOnly;
        this.bMakeLogsOnly = bMakeLogsOnly;
        this.bNonSpatialOnly = bNonSpatialOnly;
        this.bForceLogFiles = bForceLogFiles;
        this.bValidateOmex = bValidateOmex;
    }

    public Integer call() {
        try {
            PropertyLoader.loadProperties();
            VCLogger vcLogger = new CLIUtils.LocalLogger();

            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
                VcmlOmexConverter.convertFiles(cliDatabaseService, inputFilePath, outputFilePath,
                        outputModelFormat, bHasDataOnly, bMakeLogsOnly, bNonSpatialOnly, bForceLogFiles, bValidateOmex,
                        vcLogger);
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
