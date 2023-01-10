package org.vcell.cli.vcml;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XmlHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.admin.cli.CLIDatabaseService;
import org.vcell.cli.CLIRecorder;
import org.vcell.sedml.ModelFormat;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "download-vcml", description = "download cached VCML documents directly from database")
public class DatabaseCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(DatabaseCommand.class);

    @Option(names = { "-o", "--outputDir" }, description = "output directory for download", type = File.class, required = true)
    private File outputDir;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = "--regenerateXML", required = true, type = Boolean.class)
    boolean bRegenerateXML;

    @Option(names = "--publishedOnly", required = true, type = Boolean.class)
    boolean bPublishedOnly;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel();
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            
            PropertyLoader.loadProperties();
            if (outputDir == null)
                throw new RuntimeException("outputDir '" + (outputDir == null ? "" : outputDir) + "' is not a 'valid directory'");

            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
                final List<BioModelInfo> bioModelInfos;
                if (bPublishedOnly) {
                    bioModelInfos = cliDatabaseService.queryPublishedBioModels();
                } else {
                    bioModelInfos = cliDatabaseService.queryPublicBioModels();
                }
                for (BioModelInfo bioModelInfo : bioModelInfos) {
                    File vcmlFile = new File(outputDir, "biomodel_" + bioModelInfo.getVersion().getVersionKey() + ".vcml");
                    String vcmlString = cliDatabaseService.getPublicBiomodelXML(bioModelInfo.getVersion().getVersionKey(), bRegenerateXML);
                    Files.write(vcmlFile.toPath(), vcmlString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                }
            }

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("BioModel XML download completed");
        }
    }

}
