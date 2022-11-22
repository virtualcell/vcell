package org.vcell.admin.cli.db;

import cbit.vcell.modeldb.SQLCreateAllTables;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "db-destroy-and-recreate", description = "destroy existing database and recreate it")
public class DatabaseDestroyAndRecreateCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(DatabaseDestroyAndRecreateCommand.class);

    @Option(names = { "--database-type"}, type = String.class, required = true, description = "oracle or postgres")
    private String databaseType = "";
    @Option(names = { "--database-url-to-destroy-and-recreate"}, type = String.class, required = true, description = "database url - that will be destroyed and recreated")
    private String databaseUrl = "";
    @Option(names = { "--database-username"}, type = String.class, required = true, description = "database username")
    private String databaseUsername = "";
    @Option(names = { "--database-password"}, type = String.class, required = true, description = "database password")
    private String databasePassword = "";

    @Option(names = { "--I-know-what-I-am-doing"}, type = Boolean.class, required = true, description = "testify that you know what you are doing")
    private boolean bIKnowWhatIAmDoing = false;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            if (!bIKnowWhatIAmDoing){
                throw new RuntimeException("didn't testify that you know what you are doing - better to be safe !!!");
            }
            SQLCreateAllTables.main(new String[] { databaseType, databaseUrl, databaseUsername, databasePassword });
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
