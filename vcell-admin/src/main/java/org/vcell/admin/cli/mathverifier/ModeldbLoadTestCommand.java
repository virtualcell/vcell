package org.vcell.admin.cli.mathverifier;

import cbit.vcell.modeldb.MathVerifier;
import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.admin.cli.CLIDatabaseService;
import org.vcell.util.Compare;
import org.vcell.util.document.KeyValue;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.ArrayList;
import java.util.concurrent.Callable;

@Command(name = "modeldb-load-test", description = "test the loading biomodels/mathmodels from db tables and cached XML")
public class ModeldbLoadTestCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ModeldbLoadTestCommand.class);

    @Option(names = { "-u", "--userids" }, required = false, description = "vcell user ids (separated by commas) - defaults to all")
    private String[] userids = null;

    @Option(names = {  "--biomodel-keys" }, split=",", required = false, description = "biomodel key(s) to scan")
    private ArrayList<KeyValue> biomodelkeys = new ArrayList<>();

    @Option(names = { "--mathmodel-keys" }, split=",", required = false, description = "mathmodel key(s) to scan")
    private ArrayList<KeyValue> mathmodelkeys = new ArrayList<>();

    @Option(names = { "-v", "--software-version" }, required = true, description = "vcell software version")
    private String softwareVersion = null;

    @Option(names = { "--database-mode" }, type = MathVerifier.DatabaseMode.class, defaultValue = "skip", required = false, description = "update database - use with caution")
    private MathVerifier.DatabaseMode databaseMode = MathVerifier.DatabaseMode.skip;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            PropertyLoader.loadProperties();
            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
                MathVerifier mathVerifier = cliDatabaseService.getMathVerifier();
                Compare.CompareLogger compareLogger = new Compare.CompareLogger() {
                    @Override
                    public void compareFailed() {
                        logger.warn("comparison failed");
                    }
                };

                mathVerifier.runLoadTest(userids, biomodelkeys.toArray(new KeyValue[0]), mathmodelkeys.toArray(new KeyValue[0]), softwareVersion, databaseMode, compareLogger);
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
