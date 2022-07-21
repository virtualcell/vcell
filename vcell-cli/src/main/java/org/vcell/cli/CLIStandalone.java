package org.vcell.cli;


import org.vcell.cli.run.ExecuteCommand;
import org.vcell.cli.vcml.ConvertCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Spec;

import java.util.concurrent.Callable;


@Command(name = "CLIStandalone", subcommands = { ConvertCommand.class, ExecuteCommand.class, VersionCommand.class, CommandLine.HelpCommand.class })
public class CLIStandalone implements Callable<Integer> {

    @Spec
    CommandLine.Model.CommandSpec spec;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new CLIStandalone()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.print("no command");
        return 0;
    }

}




