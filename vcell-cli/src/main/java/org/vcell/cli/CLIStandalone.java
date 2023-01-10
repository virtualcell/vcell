package org.vcell.cli;

import org.vcell.cli.biosimulation.BiosimulationsCommand;
import org.vcell.cli.run.ExecuteCommand;
import org.vcell.cli.sbml.ModelCommand;
import org.vcell.cli.vcml.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.mongodb.VCMongoMessage;
import org.vcell.util.VCellUtilityHub;
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
            exitCode = new CommandLine(new CLIStandalone()).execute(args);
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
}




