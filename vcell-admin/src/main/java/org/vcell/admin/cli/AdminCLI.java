package org.vcell.admin.cli;

import cbit.vcell.modeldb.MathVerifier;
import cbit.vcell.mongodb.VCMongoMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.admin.cli.db.DatabaseCompareSchemaCommand;
import org.vcell.admin.cli.db.DatabaseDestroyAndRecreateCommand;
import org.vcell.admin.cli.mathverifier.ModeldbMathGenTestCommand;
import org.vcell.admin.cli.mathverifier.ModeldbLoadTestCommand;
import org.vcell.admin.cli.tools.UsageCommand;
import org.vcell.util.document.KeyValue;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(name = "AdminCLI", subcommands = {
        ModeldbLoadTestCommand.class,
        ModeldbMathGenTestCommand.class,
        DatabaseCompareSchemaCommand.class,
        DatabaseDestroyAndRecreateCommand.class,
        UsageCommand.class,
        CommandLine.HelpCommand.class,
})
public class AdminCLI {

    private final static Logger logger = LogManager.getLogger(AdminCLI.class);

    public static void main(String[] args) {
        int exitCode = -1;
        try{
            if (logger.isDebugEnabled()) logger.debug("!!!DEBUG Mode Active!!!");
            VCMongoMessage.enabled = false;
            CommandLine commandLine = new CommandLine(new AdminCLI());
            commandLine.registerConverter(KeyValue.class, new KeyValueTypeConverter());
            commandLine.registerConverter(MathVerifier.DatabaseMode.class, new DatabaseModeTypeConverter());
            exitCode = commandLine.execute(args);
        } catch (Throwable t){
            t.printStackTrace();
            logger.fatal("VCell encountered a serious error: " + t.getMessage(), t);
        } finally {
             System.exit(exitCode);
        }
    }

    static class KeyValueTypeConverter implements CommandLine.ITypeConverter<KeyValue> {
        @Override
        public KeyValue convert(String value) {
            return new KeyValue(value);
        }
    }

    static class DatabaseModeTypeConverter implements CommandLine.ITypeConverter<MathVerifier.DatabaseMode> {
        @Override
        public MathVerifier.DatabaseMode convert(String value) {
            return MathVerifier.DatabaseMode.valueOf(value);
        }
    }

}
