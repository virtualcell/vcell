package org.vcell.admin.cli;

import cbit.vcell.modeldb.MathVerifier;
import cbit.vcell.mongodb.VCMongoMessage;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.admin.cli.db.DatabaseCompareSchemaCommand;
import org.vcell.admin.cli.db.DatabaseDestroyAndRecreateCommand;
import org.vcell.admin.cli.mathverifier.ModeldbLoadTestCommand;
import org.vcell.admin.cli.mathverifier.ModeldbMathGenTestCommand;
import org.vcell.admin.cli.sim.JobInfoCommand;
import org.vcell.admin.cli.sim.ResultSetCrawlerCommand;
import org.vcell.admin.cli.sim.SimDataVerifierCommand;
import org.vcell.admin.cli.tools.UsageCommand;
import org.vcell.dependency.server.VCellServerModule;
import org.vcell.util.document.KeyValue;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(name = "vcell-su", subcommands = {
        ModeldbLoadTestCommand.class,
        ModeldbMathGenTestCommand.class,
        DatabaseCompareSchemaCommand.class,
        DatabaseDestroyAndRecreateCommand.class,
        UsageCommand.class,
        ResultSetCrawlerCommand.class,
        SimDataVerifierCommand.class,
        JobInfoCommand.class,
        CommandLine.HelpCommand.class,
})
public class AdminCLI {

    private final static Logger logger = LogManager.getLogger(AdminCLI.class);

    public static void main(String[] args) {
        int exitCode = -1;
        try{
            if (logger.isDebugEnabled()) logger.debug("!!!DEBUG Mode Active!!!");
            VCMongoMessage.enabled = false;

            Injector injector = Guice.createInjector(new VCellServerModule());
            AdminCLI adminCLI = injector.getInstance(AdminCLI.class);
            CommandLine commandLine = new CommandLine(adminCLI);
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
