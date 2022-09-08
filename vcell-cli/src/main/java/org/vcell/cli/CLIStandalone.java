package org.vcell.cli;

import org.vcell.cli.biosimulation.BiosimulationsCommand;
import org.vcell.cli.run.ExecuteCommand;
import org.vcell.cli.sbml.ModelCommand;
import org.vcell.cli.vcml.ExportOmexBatchCommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.mongodb.VCMongoMessage;
import org.vcell.cli.vcml.ExportOmexCommand;
import org.vcell.util.VCellUtilityHub;

import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(name = "CLIStandalone", subcommands = {
        BiosimulationsCommand.class,
        ExportOmexCommand.class,
        ExportOmexBatchCommand.class,
        ExecuteCommand.class,
        VersionCommand.class,
        ModelCommand.class,
        CommandLine.HelpCommand.class
})
public class CLIStandalone {
    private final static Logger logger = LogManager.getLogger(CLIStandalone.class);
    public static void main(String[] args) {
        logger.info("Starting Vcell...");
        if (logger.isDebugEnabled()) logger.debug("!!!DEBUG Mode Active!!!");
        VCMongoMessage.enabled = false;
        VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);
        int exitCode = new CommandLine(new CLIStandalone()).execute(args);
        try {
            VCellUtilityHub.shutdown();
        } catch (Exception e){
            logger.error("VCellUtilityHub encountered a problem during shutdown: " + e.getMessage(), e);
        }
        System.exit(exitCode);
    }
}




