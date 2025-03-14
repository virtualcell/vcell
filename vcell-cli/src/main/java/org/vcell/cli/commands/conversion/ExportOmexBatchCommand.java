package org.vcell.cli.commands.conversion;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.cli.vcml.VcmlOmexConverter;
import org.vcell.trace.Tracer;
import org.vcell.sedml.ModelFormat;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "export-omex-batch", description = "convert directory of VCML documents to COMBINE archives (.omex)")
public class ExportOmexBatchCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ExportOmexBatchCommand.class);

    @Option(names = { "-m", "--outputModelFormat" }, defaultValue = "SBML", description = "expecting SBML or VCML")
    private ModelFormat outputModelFormat = ModelFormat.SBML;

    @Option(names = { "-i", "--inputFilePath" }, required = true, description = "directory of .vcml files")
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" }, required = true, description = "directory to create .omex files")
    private File outputFilePath;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = "--validate")
    boolean bValidateOmex;

    @Option(names = "--writeLogFiles", defaultValue = "false")
    boolean bWriteLogFiles = false;

    @Option(names = { "--skipUnsupportedApps" }, defaultValue = "false", description = "skip unsupported applications (e.g. electrical in SBML)")
    boolean bSkipUnsupportedApps = false;

    @Option(names = { "--add-publication-info" }, defaultValue = "false", description = "retrieve published abstract and citation from pubmed")
    boolean bAddPublicationInfo = false;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel();

        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {

            logger.debug("Batch export of omex files requested");
            if (inputFilePath == null || !inputFilePath.exists() || !inputFilePath.isDirectory())
                throw new RuntimeException("inputFilePath '" + (inputFilePath == null ? "" : inputFilePath) + "' is not a 'valid directory'");

            if (outputFilePath.exists() && !outputFilePath.isDirectory())
                throw new RuntimeException("outputFilePath '" + outputFilePath + "' is not a 'valid directory'");

            VcmlOmexConverter.convertFilesNoDatabase(
                    inputFilePath, outputFilePath, outputModelFormat, bWriteLogFiles, bValidateOmex, bSkipUnsupportedApps, bAddPublicationInfo);

            return Tracer.hasErrors() ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("Batch export completed");
            if (Tracer.hasErrors()){
                logger.error("Errors encountered during batch export");
                Tracer.reportErrors(false);
            }
        }
    }

}
