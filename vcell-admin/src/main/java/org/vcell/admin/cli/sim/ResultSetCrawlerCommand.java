package org.vcell.admin.cli.sim;

import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.admin.cli.CLIDatabaseService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "result-set-crawler", description = "scan (and optionally remove) simdata files which are unreachable")
public class ResultSetCrawlerCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ResultSetCrawlerCommand.class);

    @Option(names = {"--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = {"-u", "--username"}, description = "username to scan")
    private String username = null;

    @Option(names = {"--start-username"}, description = "continue scanning from this username in alphabetical order")
    private String startUsername = null;

    @Option(names = { "-o", "--outputDir"}, description = "directory where scan results are stored (default is current directory)", required = false)
    private File outputDir;

    @Option(names = {"--remove"}, description = "remove unreferenced datasets")
    private boolean bRemove = false;

//    @Option(names = {"--amplistor-cred-user"}, description = "Amplistor Credential (username), must have delete permission on Amplistor")
//    private String amplistorUsername = null;
//
//    @Option(names = {"--amplistor-cred-pswd"}, description = "Amplistor Credential (password)")
//    private String amplistorPassword = null;

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
            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
                if (outputDir == null) {
                    outputDir = new File(".");
                } else if (!outputDir.exists()) {
                    throw new RuntimeException("Output directory doesn't exist!");
                }
                ResultSetCrawler resultSetCrawler = cliDatabaseService.getResultSetCrawler();
                boolean bScanOnly = !bRemove;
                resultSetCrawler.run(outputDir, bScanOnly, username, startUsername /*, amplistorUsername, amplistorPassword */);
            } catch (RuntimeException e) {
                e.printStackTrace(System.err);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
