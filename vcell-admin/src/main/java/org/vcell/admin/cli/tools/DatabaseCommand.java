package org.vcell.admin.cli.tools;

import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.admin.cli.CLIDatabaseService;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "download-vcml", description = "download cached VCML documents directly from database")
public class DatabaseCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(DatabaseCommand.class);

    @Option(names = { "-o", "--outputDir" }, description = "output directory for download", type = File.class, required = true)
    private File outputDir;

    @Option(names = "--biomodels-file", description = "csv file with columns for biomodelkey,userid,userkey")
    private File biomodelsFile;

    @Option(names = "--biomodel-id", description = "id of single biomodel - if private, must specify --user as owner")
    private KeyValue biomodelID;

    @Option(names = "--user", description = "userid and key for private models --biomodel-id (format is 'userid:key')")
    private User user = User.tempUser;

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
                if (biomodelsFile != null) {
                    List<String> lines = Files.readAllLines(biomodelsFile.toPath());
                    for (String line : lines) {
                        String[] tokens = line.split(",");
                        KeyValue biomodelID = new KeyValue(tokens[0]);
                        String userid = tokens[1];
                        KeyValue userkey = new KeyValue(tokens[2]);
                        User user = new User(userid, userkey);
                         File vcmlFile = new File(outputDir, "biomodel_" + biomodelID + ".vcml");
                        String vcmlString = cliDatabaseService.getBioModelXML(biomodelID, bRegenerateXML, user);
                        Files.write(vcmlFile.toPath(), vcmlString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                    }
                }else if (biomodelID != null){
                    File vcmlFile = new File(outputDir, "biomodel_" + biomodelID + ".vcml");
                    String vcmlString = cliDatabaseService.getBioModelXML(biomodelID, bRegenerateXML, user);
                    Files.write(vcmlFile.toPath(), vcmlString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                }else {
                    List<BioModelInfo> bioModelInfos;
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
