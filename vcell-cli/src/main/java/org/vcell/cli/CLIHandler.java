package org.vcell.cli;

import org.apache.commons.cli.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.System.*;

public class CLIHandler {
    // timeout for compiled solver running long jobs; default 12 hours
    //public static long EXECUTABLE_MAX_WALLCLOK_MILLIS = 600000;
    public static long EXECUTABLE_MAX_WALLCLOK_MILLIS = 0;
    private static CLIHandler singleInstance;
    private CommandLine cmd = null; // TODO: make private and selectively expose components.
    private String fetchFailed = "Failed fetching VCell version";
    private boolean isInConversionMode, isInputPathDirectory;
    String osName = getProperty("os.name");
    String osVersion = getProperty("os.version");
    String javaVersion = getProperty("java.version");
    String javaVendor = getProperty("java.vendor");
    String machineArch = getProperty("os.arch");

    private static String syntax =   "VCell [-h | -v] [-q] -i <input> -o <outputDir> [-t <milliseconds>] [-c]  [-vcml | -sbml] " 
                                +   "[-hasDataOnly] [-makeLogsOnly] [-nonSpatialOnly] [-keepTempFiles] [-exactMatchOnly] [-forceLogFiles]";
    private static String usage = "usage: " + syntax;

    public static CLIHandler getCLIHandler(){
        if (CLIHandler.singleInstance == null){
            try {
                throw new InstantiationError("CLIHandler has not yet been intantiated. Instantiate the singleton first.");
            } catch(InstantiationError e){
                out.println(e);
                System.exit(3);
            }
        }
        return CLIHandler.singleInstance;
    }

    public static void instantiateCliHandler(String[] args){
        try {
            if (CLIHandler.singleInstance != null){
                throw new InstantiationError("CLIHandler is a singleton that is provided CLI args. Do not attempt to reinstantiate it.");
            } else {
                CLIHandler.singleInstance = new CLIHandler(args);
            }
        } catch (InstantiationError e){
            out.println(e);
            System.exit(3);
        }
    }

    private CLIHandler(String[] args) {
        CommandLineParser parser = new DefaultParser();

        // Try and parse the cmd args
        try {
            cmd = parser.parse(this.getCommandLineOptions(), args);
        } catch (ParseException e) {
            out.println(e + "\n\n" + usage);
            exit(2);
        }

        this.isInConversionMode = cmd.hasOption("c");

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

        if (cmd.hasOption("t")){
            try {
                EXECUTABLE_MAX_WALLCLOK_MILLIS = Integer.parseInt(cmd.getOptionValue("t"));
            } catch(NumberFormatException e) {
                out.println("Detected timeout duration: <" + cmd.getOptionValue("t") + "> could not be parsed.");
                exit(2);
            }   
        } else {
            EXECUTABLE_MAX_WALLCLOK_MILLIS = 0;
        }

        if (cmd.hasOption("vcml") && cmd.hasOption("sbml")){
            out.println("Flags -vcml and -sbml are mutually exclusive; please select one.");
            exit(2);
        }

        if (!cmd.hasOption("i") || !cmd.hasOption("o")){
            out.println("Please specify input/output with -i and -o flags.\n");
            out.println(usage);
            exit(2);
        }

        if (cmd.hasOption("i")){
            Path inPath = Paths.get(cmd.getOptionValue("i"));
            if (Files.notExists(inPath)){
                out.println("Input path '"+inPath+"'  can not be parsed. Please confirm path exists and that it is a directory or file.");
                exit(2);
            }

            if (Files.isDirectory(inPath)){
                this.isInputPathDirectory = true;
            } else if (Files.isRegularFile(inPath)){
                this.isInputPathDirectory = false;
            } else {
                // We have 0 clue what it is
                out.println("Input path can not be parsed. Please confirm path exists and that it is a directory or file.");
                exit(2);
            }
            // endsWith in Path is deceiving, it compares the whole string past the last separator, need to convert Paths to String before checking for extension
            if (!this.isInputPathDirectory && !inPath.getFileName().toString().endsWith("omex") && !inPath.getFileName().toString().endsWith("vcml")){
                out.println("Invalid input file detected. Check file extension and try again.");
                exit(2);
            }
            
        } else if (cmd.hasOption("o")){
            Path outPath = Paths.get(cmd.getOptionValue("o"));

            // Note the short circuits and side effect of mkdirs
            if ((Files.notExists(outPath) || !Files.isDirectory(outPath)) && (Files.isRegularFile(outPath) || !outPath.toFile().mkdirs())){ 
                out.println("Output directory can not be found as a directory nor created. Please double check names and permissions.");
                exit(2);
            }
        }
    }

    private Options getCommandLineOptions() {

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
            "timeout", 
            true, 
            "Specifices a timeout duration."
        );

        Option forceLogs = new Option(
            "forceLogFiles", 
            false,
            "Generates log files ordinarily run only for batch execution"
        
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
        options.addOption(forceLogs);
        return options;
    }

    public String getInputFilePath() {
        return this.cmd.getOptionValue("input");
    }

    public String getOutputDirPath() {
        return cmd.getOptionValue("outputDir");
    }

    public boolean inConversionMode(){
        return this.isInConversionMode;
    }

    public boolean isInputTypeDirectory(){
        return this.isInputPathDirectory;
    }

    public boolean isExactMatchOnly(){
        return this.cmd.hasOption("exactMatchOnly");
    }

    public boolean isKeepTempFiles(){
        return this.cmd.hasOption("keepTempFiles");
    }

    public boolean shouldForceVcml(){
        return this.cmd.hasOption("vcml");
    }

    public boolean shouldForceSbml(){
        return this.cmd.hasOption("sbml");
    }

    public boolean isHasDataOnly(){
        return this.cmd.hasOption("hasDataOnly");
    }

    public boolean isMakeLogsOnly(){
        return this.cmd.hasOption("makeLogsOnly");
    }

    public boolean isNonSpacialOnly(){
        return this.cmd.hasOption("nonSpatialOnly");
    }

    public boolean shouldKeepLogs(){
        return this.cmd.hasOption("forceLogFiles");
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