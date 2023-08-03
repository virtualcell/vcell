package org.vcell.cli.vcml;

import cbit.vcell.resource.PropertyLoader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.sedml.ModelFormat;
import org.vcell.util.DataAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "export-omex", description = "export a VCML document to a COMBINE archive (.omex)")
public class ExportOmexCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ExportOmexCommand.class);

    @Option(names = { "-m", "--outputModelFormat" }, defaultValue = "SBML", description = "expecting SBML or VCML")
    private ModelFormat outputModelFormat = ModelFormat.SBML;

    @Option(names = { "-i", "--inputFilePath" }, required = true, description = "full path to .vcml file to export to .omex")
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" }, required = true, description = "full path to .omex file to create")
    private File outputFilePath;

    @Option(names = { "--skipUnsupportedApps" }, defaultValue = "false", description = "skip unsupported applications (e.g. electrical in SBML)")
    private boolean bSkipUnsupportedApps = false;

    @Option(names = { "--writeLogFiles" }, defaultValue = "false")
    private boolean bWriteLogFiles;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = "--validate")
    boolean bValidateOmex;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            PropertyLoader.loadProperties();

            logger.debug("Beginning export");
            VcmlOmexConverter.convertOneFile(
                    inputFilePath, outputFilePath, outputModelFormat, bWriteLogFiles, bValidateOmex, bSkipUnsupportedApps);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("Completed all exports");
        }
    }
}
