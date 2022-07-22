package org.vcell.cli;

import org.vcell.cli.run.ExecuteCommand;
import org.vcell.cli.vcml.ConvertCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(name = "CLIStandalone", subcommands = {ConvertCommand.class, ExecuteCommand.class, VersionCommand.class, CommandLine.HelpCommand.class})
public class CLIStandalone {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new CLIStandalone()).execute(args);
        System.exit(exitCode);
    }

}




