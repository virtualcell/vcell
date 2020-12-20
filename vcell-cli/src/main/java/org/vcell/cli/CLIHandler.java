package org.vcell.cli;

import org.apache.commons.cli.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CLIHandler {
    private final String usage = "usage: VCell [-h] [-q] -i ARCHIVE [-o OUT_DIR] [-v]";
    private final String syntax = "VCell [-h] [-q] -i ARCHIVE [-o OUT_DIR] [-v]";
    private final String header = "\nBioSimulators-compliant command-line interface to the VCell simulation program <http://vcell.org>.\n\n" +
            "optional arguments:\n\n";
    CommandLine cmd = null;


    CLIHandler(String[] args) throws IOException {
        CommandLineParser parser = new DefaultParser();

        try {
            cmd = parser.parse(this.getCommandLineOptions(), args);
        } catch (ParseException e) {
            System.out.println(this.usage);
            System.exit(1);
        }

        if (cmd.getOptions().length == 0) {
            System.out.println(usage);
            System.exit(1);
        }

        if (cmd.hasOption("h")) {
            this.printHelp();
            System.exit(1);
        }

        if (cmd.hasOption("v")) {
            System.out.println("VCell version: " + getVersion());
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
                "Path to OMEX/COMBINE Archive file which contains one or more SED-ML encoded simulation experiments");

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

    public String getVersion() throws IOException {
        final String url = "http://vcell.org/webstart/Rel/updates.xml";
        final Document document = Jsoup.connect(url).get();

        Elements entryElements = document.select("entry");
        String version;
        version = entryElements.attr("newVersion");

        return version;
    }
}
