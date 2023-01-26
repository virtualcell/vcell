package org.vcell.cli;

import org.vcell.admin.cli.AdminCLI;
import org.vcell.cli.biosimulation.BiosimulationsCommand;
import org.vcell.cli.run.ExecuteCommand;
import org.vcell.cli.sbml.ModelCommand;
import org.vcell.cli.vcml.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.mongodb.VCMongoMessage;
import org.vcell.util.VCellUtilityHub;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(name = "CLIStandalone", subcommands = {
        BiosimulationsCommand.class,
        ExportOmexCommand.class,
        ExportOmexBatchCommand.class,
        ImportOmexCommand.class,
        ImportOmexBatchCommand.class,
        ExecuteCommand.class,
        VersionCommand.class,
        ModelCommand.class,
        ValidateBatchCommand.class,
        CommandLine.HelpCommand.class,
        DatabaseCommand.class
})
public class CLIStandalone {
    private final static Logger logger = LogManager.getLogger(CLIStandalone.class);
    public static void main(String[] args) {
        int exitCode = -1;
        try{
            logger.info("Starting Vcell...");
            if (logger.isDebugEnabled()) logger.debug("!!!DEBUG Mode Active!!!");
            VCMongoMessage.enabled = false;
            VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);
            CommandLine commandLine = new CommandLine(new CLIStandalone());
            commandLine.registerConverter(KeyValue.class, new CLIStandalone.KeyValueTypeConverter());
            commandLine.registerConverter(User.class, new CLIStandalone.UserTypeConverter());
            exitCode = commandLine.execute(args);
        } catch (Throwable t){
            logger.fatal("VCell encountered a serious error: " + t.getMessage(), t);
        } finally {
            try {
                VCellUtilityHub.shutdown();
            } catch (Exception e){
                logger.error("VCellUtilityHub encountered a problem during shutdown: " + e.getMessage(), e);
                exitCode = -1;
            }
            System.exit(exitCode);
        }
    }

    static class KeyValueTypeConverter implements CommandLine.ITypeConverter<KeyValue> {
        @Override
        public KeyValue convert(String value) {
            return new KeyValue(value);
        }
    }

    static class UserTypeConverter implements CommandLine.ITypeConverter<User> {
        @Override
        public User convert(String userString) {
            // expecting "userid:key" format
            String[] tokens = userString.split(":");
            if (tokens == null || tokens.length != 2){
                throw new RuntimeException("'"+userString+"' not in 'userid:userkey' format");
            }
            String userid = tokens[0];
            KeyValue key = new KeyValue(tokens[1]);
            return new User(userid, key);
        }
    }


}




