package org.vcell.admin.cli.models;

import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.admin.cli.CLIDatabaseService;
import org.vcell.util.document.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Command(name = "model", description = "administrative operations on biomodels and mathmodels", subcommands = {
        ModelListCommand.class,
        ModelPermissionCommand.class,
})
public class ModelCommands {

    private final static Logger logger = LogManager.getLogger(ModelCommands.class);

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

}
