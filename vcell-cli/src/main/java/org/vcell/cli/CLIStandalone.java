package org.vcell.cli;

import org.vcell.cli.biosimulation.BiosimulationsCommand;
import org.vcell.cli.run.ExecuteCommand;
import org.vcell.cli.sbml.ModelCommand;
import org.vcell.cli.vcml.ExportOmexBatchCommand;

import cbit.vcell.mongodb.VCMongoMessage;
import org.vcell.cli.vcml.ExportOmexCommand;
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

    public static void main(String[] args) {
        VCMongoMessage.enabled = false;
        int exitCode = new CommandLine(new CLIStandalone()).execute(args);
        System.exit(exitCode);
    }

}




