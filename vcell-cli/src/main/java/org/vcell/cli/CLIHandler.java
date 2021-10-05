package org.vcell.cli;

import org.apache.commons.cli.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import static java.lang.System.*;

public class CLIHandler {
    CommandLine cmd = null;
    String fetchFailed = "Failed fetching VCell version";
    String osName = getProperty("os.name");
    String osVersion = getProperty("os.version");
    String javaVersion = getProperty("java.version");
    String javaVendor = getProperty("java.vendor");
    String machineArch = getProperty("os.arch");

    public static String usage = "usage: VCell [-h] [-q] -i ARCHIVE [-o OUT_DIR] [-v] [-vcml]";

    public CLIHandler(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(this.getCommandLineOptions(), args);
        } catch (ParseException e) {
            out.println(usage);
            exit(1);
        }

        if (cmd.getOptions().length == 0) {
            out.println(usage);
            exit(1);
        }

        if (cmd.hasOption("h")) {
            this.printHelp();
            exit(1);
        }

        if (cmd.hasOption("v")) {
            if (getVersion().startsWith("Fail")){
                out.println(fetchFailed);
            } else
                out.println("VCell: " + getVersion() + "\nOS: " + osName + " " + osVersion + "\nJava Version: " + javaVersion + "\nJava Vendor: " + javaVendor + "\nMachine: " + machineArch);
            exit(1);
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
                "Path to OMEX/COMBINE Archive which contains one or more SED-ML encoded simulation experiments, or VCML file");

        Option output = new Option("o",
                "out-dir",
                true,
                "Directory to save outputs or the converted OMEX Archive from VCML file");

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
        String syntax = "VCell [-h] [-q] -i ARCHIVE [-o OUT_DIR] [-v]";
        String header = "\nBioSimulators-compliant command-line interface to the VCell simulation program <http://vcell.org>.\n\n" +
                "optional arguments:\n\n";
        formatter.printHelp(syntax, header, this.getCommandLineOptions(), "");
    }

    public String getVersion() {
        final String url = "http://vcell.org/webstart/Alpha/updates.xml";
        Document document;
        String version;
        try {
            document = Jsoup.connect(url).get();
            Elements entryElements = document.select("entry");
            version = entryElements.attr("newVersion");
            return version;
        } catch (IOException ignored) {
            return fetchFailed;
        }
    }
}