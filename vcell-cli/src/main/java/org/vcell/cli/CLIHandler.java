package org.vcell.cli;

import org.apache.commons.cli.*;

public class CLIHandler {
    private final String usage = "usage: vcell [-h] [-q] -i ARCHIVE [-o OUT_DIR] [-v]";
    private final String syntax = "vcell [-h] [-q] -i ARCHIVE [-o OUT_DIR] [-v]";
    private final String header = "\nBioSimulations-compliant command-line interface to the vcell simulation program <http://vcell.org>.\n\n" +
            "optional arguments:\n\n";
    CommandLine cmd = null;


    CLIHandler(String[] args) {
        CommandLineParser parser = new DefaultParser();

        try {
            cmd = parser.parse(this.getCommandLineOptions(), args);
        } catch (ParseException e) {
            System.err.println("Unable to parse: "+e.getMessage());
            System.exit(1);
        }

        if (cmd.getOptions().length == 0) {
            System.out.println(usage);
            System.exit(1);
        }

        if (args[0].contains("-h") || args[0].contains("--help")) {
            this.printHelp();
            System.exit(1);
        }
    }


    public Options getCommandLineOptions() {

        Option help = new Option("h",
                "help",
                false,
                "show this help message and exit");

        Option quiet = new Option("q",
                "quiet",
                false,
                "suppress all console output");

        Option input = new Option("i",
                "archive",
                true,
                "Path to OMEX file which contains one or more SED-ML-encoded simulation experiments");

        Option output = new Option("o",
                "out-dir",
                true,
                "Directory to save outputs");

        Option version = new Option("v",
                "version",
                false,
                "show program's version number and exit");

        Options options = new Options();

        options.addOption(help);
        options.addOption(quiet);
        options.addOption(input);
        options.addOption(output);
        options.addOption(version);
        return options;
    }

    public String getInputFilePath() {
        return this.cmd.getOptionValue("archive");
    }

    public String getOutputDirPath() {
        return cmd.getOptionValue("out-dir");
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(this.syntax, this.header, this.getCommandLineOptions(), "");
    }
}
