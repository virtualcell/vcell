package org.vcell.admin.cli.db;

import cbit.vcell.modeldb.SQLCreateAllTables;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.db.DatabaseSyntax;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.Callable;

@Command(name = "db-create-script", description = "create sql script to create new schema")
public class DatabaseCreateScriptCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(DatabaseCreateScriptCommand.class);

    @Option(names = { "--database-type"}, type = DatabaseSyntax.class, required = true, description = "oracle or postgres")
    private DatabaseSyntax databaseSyntax;

    @Option(names = { "--create-script"}, type = File.class, required = true, description = "database creation script path")
    private File creationScript;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try (FileWriter fw = new FileWriter(creationScript)){
            SQLCreateAllTables.writeScript(databaseSyntax, fw);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
