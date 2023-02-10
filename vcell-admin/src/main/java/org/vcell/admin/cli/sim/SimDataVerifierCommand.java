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
import java.util.EnumSet;
import java.util.concurrent.Callable;

@Command(name = "simdata-verifier", description = "verify existence of simulation (and optionally run/rerun if missing)")
public class SimDataVerifierCommand implements Callable<Integer> {

    public enum ModelVisibility {
        PUBLIC, PRIVATE, PUBLISHED
    }

    private final static Logger logger = LogManager.getLogger(SimDataVerifierCommand.class);

    @Option(names = {"--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = { "-o", "--outputDir"}, description = "directory where scan report is stored (default is current directory)", required = false)
    private File outputDir;

    @Option(names = {"-u", "--username"}, description = "username to restrict scan")
    private String username;

    @Option(names = {"--start-username"}, description = "continue scanning from this username in alphabetical order")
    private String startUsername;

    @Option(names = {"--model-visibility"}, description = "model visibility (PRIVATE, PUBLIC, PUBLISHED), " +
            "where PUBLIC and PUBLISHED are disjoint", defaultValue = "PUBLISHED", required = true)
    private EnumSet<ModelVisibility> modelVisibilities = EnumSet.of(ModelVisibility.PUBLISHED);

    @Option(names = {"--ignore-test-account"}, description = "ignore vcell test account (default is true)", defaultValue = "true")
    private boolean bIgnoreTestAccount = true;

    @Option(names = {"--rerun-lost-data"}, description = "rerun simulations whose data is lost (default is false)")
    private boolean bRerunLostData;

    @Option(names = {"--run-never-ran"}, description = "rerun simulations whose data is lost (default is false)")
    private boolean bRunNeverRan;

//    @Option(names = {"--amplistor-cred-user"}, description = "Amplistor Credential (username), must have delete permission on Amplistor")
//    private String amplistorUsername;
//
//    @Option(names = {"--amplistor-cred-pswd"}, description = "Amplistor Credential (password)")
//    private String amplistorPassword;

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
                SimDataVerifier simDataVerifier = cliDatabaseService.getSimDataVerifier();
                simDataVerifier.run(outputDir, bRerunLostData, bRunNeverRan, username, startUsername,
                        bIgnoreTestAccount, modelVisibilities
//                        , amplistorUsername, amplistorPassword
                        );
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
