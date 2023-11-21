package org.vcell.admin.cli.db;

import cbit.sql.CompareDatabaseSchema;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.admin.cli.CLIDatabaseService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "db-compare-schema", description = "compare a database schema against vcell software table definitions")
public class DatabaseCompareSchemaCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(DatabaseCompareSchemaCommand.class);

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
                CompareDatabaseSchema compareDatabaseSchema = cliDatabaseService.getCompareDatabaseSchemas();
                compareDatabaseSchema.runCompareSchemas();
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
