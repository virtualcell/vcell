package org.vcell.admin.cli.models;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.admin.cli.CLIDatabaseService;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "ls", description = "list biomodels and mathmodels by user")
public class ModelListCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ModelListCommand.class);

    @Option(names = "--owner", required = true, description = "model owner (format is 'userid:key')")
    private User owner;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel();
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {

            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {

                List<BioModelInfo> bioModelInfos = cliDatabaseService.queryBiomodelsByOwner(owner);
                for (BioModelInfo bioModelInfo : bioModelInfos) {
                    System.out.println("BioModel: "+bioModelInfo);
                }
                List<MathModelInfo> mathModelInfos = cliDatabaseService.queryMathmodelsByOwner(owner);
                for (MathModelInfo mathModelInfo : mathModelInfos) {
                    System.out.println("MathModel: "+mathModelInfo);
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
