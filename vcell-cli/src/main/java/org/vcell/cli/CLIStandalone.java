package org.vcell.cli;

import org.vcell.cli.biosimulation.BiosimulationsCommand;
import org.vcell.cli.run.ExecuteCommand;
import org.vcell.cli.vcml.ConvertCommand;

import cbit.vcell.mongodb.VCMongoMessage;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(name = "CLIStandalone", subcommands = {
        BiosimulationsCommand.class,
        ConvertCommand.class,
        ExecuteCommand.class,
        VersionCommand.class,
        CommandLine.HelpCommand.class
})
public class CLIStandalone {

    public static void main(String[] args) {
        VCMongoMessage.enabled = false;
        int exitCode = new CommandLine(new CLIStandalone()).execute(args);
        System.exit(exitCode);
    }

}




