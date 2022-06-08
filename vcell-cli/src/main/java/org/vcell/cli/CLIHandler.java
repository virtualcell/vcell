package org.vcell.cli;

import org.apache.commons.cli.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import static java.lang.System.*;

public class CLIHandler {
    public CommandLine cmd = null; // TODO: make private and selectively expose components.
    String fetchFailed = "Failed fetching VCell version";
    String osName = getProperty("os.name");
    String osVersion = getProperty("os.version");
    String javaVersion = getProperty("java.version");
    String javaVendor = getProperty("java.vendor");
    String machineArch = getProperty("os.arch");

    public static String syntax =   "VCell [-h | -v] [-q] [-t <milliseconds>] [-c] [-i <input> -o <outputDir>] [-vcml | - sbml] " 
                                +   "[-hasDataOnly] [-makeLogsOnly] [-nonSpatialOnly] [-keepTempFiles] [-exactMatchOnly]";
    public static String usage = "usage: " + syntax;

    public CLIHandler(String[] args) {
        CommandLineParser parser = new DefaultParser();

        // Try and parse the cmd args
        try {
            cmd = parser.parse(this.getCommandLineOptions(), args);
        } catch (ParseException e) {
            out.println(e + "\n\n" + usage);
            exit(2);
        }

        if (cmd.getOptions().length == 0) { // One day maybe this boots the GUI?
            out.println("Welcome to VCell!\n\t" + usage);
            exit(0);
        }

        if (cmd.hasOption("h")) {
            this.printHelp();
            exit(2);
        } else if (cmd.hasOption("v")) {
            if (getVersion().startsWith("Fail")){
                out.println(fetchFailed);
                exit(2);
            } else {
                out.println("VCell: " + getVersion() + "\nOS: " + osName + " " + osVersion + "\nJava Version: " + javaVersion + "\nJava Vendor: " + javaVendor + "\nMachine: " + machineArch);
                exit(0);
            } 
        }

        if (cmd.hasOption("q")){
            // TODO: mute out and err streams
        }

        if (cmd.hasOption("c")){
            if (!cmd.hasOption("i") || !cmd.hasOption("o")){
                out.println("When converting omex, -i and -o flags must be specified.\n");
                out.println(usage);
                exit(2);
            }

            // TODO: finish with flag checks

        }
    }

    public Options getCommandLineOptions() {

        Option help = new Option(
            "h",
            "help",
            false,
            "show this help message and exit"
        );

        Option quiet = new Option(
            "q",
            "quiet",
            false,
            "Suppress all console output [NOT CURRENTLY IMPLIMENTED]"
        );

        Option input = new Option(
            "i",
            "input",
            true,
            "Path to OMEX/COMBINE Archive which contains one or more SED-ML encoded simulation experiments, or VCML file"
        );

        Option output = new Option(
            "o",
            "outputDir",
            true,
            "Directory to save outputs or the converted OMEX Archive from VCML file"
        );

        Option version = new Option(
            "v",
            "version",
            false,
            "Show program's version number and exit"
        );

        Option convert = new Option(
            "c",
            "convert",
            false,
            "Converts OMEX Archives to either sbml or vcml format, attempting sbml first then vcml unless otherwise specificed by flags. Requires -i and -o flags."
        );

        Option vcml = new Option (
            "vcml",
            false, 
            "Specifies conversion of OMEX Archive to vcml format."
        );

        Option sbml = new Option (
            "sbml",
            false, 
            "Specifies conversion of OMEX Archive to sbml format."
        );

        Option hasDataOnly = new Option (
            "hasDataOnly",
            false, 
            "Export the sims that have been previously run and have results on the VCell server."
        );

        Option makeLogsOnly = new Option (
            "makeLogsOnly",
            false, 
            "Rather than creating the omex files, just make the logs."
        );

        Option nonSpatialOnly = new Option (
            "nonSpatialOnly",
            false, 
            "Export only non-spatial simulations."
        );

        Option keepTempFiles = new Option (
            "keepTempFiles", 
            false, 
            "Keep simulation results for debugging." 
        );

        Option exactMatchOnly = new Option (
            "exactMatchOnly", 
            false, 
            "Run the model solver only if it's an exact kisao match."
        );

        Option timeOut = new Option (
            "t", 
            "timeOut", 
            true, 
            "Specifices a timeout duration."
        );

        Options options = new Options();

        options.addOption(help);
        options.addOption(quiet);
        options.addOption(input);
        options.addOption(output);
        options.addOption(version);
        options.addOption(convert);
        options.addOption(vcml);
        options.addOption(sbml);
        options.addOption(hasDataOnly);
        options.addOption(makeLogsOnly);
        options.addOption(nonSpatialOnly);
        options.addOption(keepTempFiles);
        options.addOption(exactMatchOnly);
        options.addOption(timeOut);
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