package org.vcell.cli;

import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class CLIPythonManager {
    /*
     * NOTES:
     *
     * 1) BufferedReader will block if asked to read the Python interpreter while the interpreter waiting for input (assuming the buffer has already been iterated through).
     *      To prevent this, we catch the prefix to Python's prompt for a new command (">>> ") to stop before we wait for etinity and break our program.
     */

    public static final Path currentWorkingDir = Paths.get("").toAbsolutePath();

    private static final String pythonExeName = OperatingSystemInfo.getInstance().isWindows() ? "python.exe" : "python3";

    private static CLIPythonManager instance = null;

    private Process pythonProcess; 			// hold python interpreter instance used in updateXxx...() methods
    private OutputStreamWriter pythonOSW; 	// input channel *to* python interpreter (see above)
    private BufferedReader pythonISB; 		// output channel ("Input Stream Buffer") *from* python interpreter (see above)

    public static CLIPythonManager getInstance(){
        if (instance == null){
            instance = new CLIPythonManager();
        }
        return instance;
    }

    // returns parsed interpreter return
    public String callPython(String functionName, String... arguments) throws PythonStreamException {
        return this.callPython(this.formatPythonFuctionCall(functionName, arguments));
    }

    private String callPython(String command) throws PythonStreamException {
        String returnString = "";
        try {
            this.instantiatePythonProcess(); // Make sure we have a python instance; calling will not override an existing intance
            this.sendNewCommand(command);
            returnString = this.getResultsOfLastCommand();
        } catch (IOException | InterruptedException | TimeoutException e){
            throw new PythonStreamException("Python process encounted an exception:\n" + e);
        }
        return returnString;
    }

    @Deprecated
    public static void callNonsharedPython(String cliCommand, String sedmlPath, String resultOutDir) throws InterruptedException, IOException {
        Path cliWorkingDir = Paths.get(PropertyLoader.getRequiredProperty(PropertyLoader.cliWorkingDir));
        Path cliPath = Paths.get(cliWorkingDir.toString(), "vcell_cli_utils", "cli.py");
        ProcessBuilder pb = new ProcessBuilder(new String[]{pythonExeName, cliPath.toString(), cliCommand, sedmlPath, resultOutDir});
        runAndPrintProcessStreams(pb, "","");
    }

    private void executeThroughPython(String command) throws PythonStreamException {
        String results = callPython(command);
        if (!this.printPythonErrors(results)) System.exit(1);
    }

    private int checkPythonInstallation() {
        String version = "--version", stdOutLog;
        ProcessBuilder processBuilder;
        Process process;
        int exitCode = -10;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            processBuilder = execShellCommand(new String[]{pythonExeName, version});
            process = processBuilder.start();
            exitCode = process.waitFor();
            if (exitCode == 0) {
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((stdOutLog = bufferedReader.readLine()) != null) {
                    stringBuilder.append(stdOutLog);
                    // search string can be one or more... (potential python 2.7.X bug here?) Maybe use regex?
                    if (!stringBuilder.toString().toLowerCase().startsWith("python 3")) throw new PythonStreamException();

                }
            }
        } catch (PythonStreamException e) {
            System.err.println("Please check your local Python and PIP Installation, install required packages and versions");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return exitCode;
    }

    public static int checkPythonInstallationError() {
        String version = "--version";
        ProcessBuilder processBuilder;
        Process process;
        int exitCode = -10;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder;
        String stdOutLog;

        try {
            processBuilder = execShellCommand(new String[]{pythonExeName, version});
            process = processBuilder.start();
            exitCode = process.waitFor();
            if (exitCode == 0) {
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                stringBuilder = new StringBuilder();
                while ((stdOutLog = bufferedReader.readLine()) != null) {
                    stringBuilder.append(stdOutLog);
                    // search string can be one or more... (potential python 2.7.X bug here?)
                    if (!stringBuilder.toString().toLowerCase().startsWith("python")) System.err.println("Please check your local Python and PIP Installation, install required packages");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return exitCode;
    }

    public void closePythonProcess() throws IOException {
        // Exit the living Python Process
        this.sendNewCommand("exit()"); // Sends kill command ("exit()") to python.exe instance;
        if (pythonProcess.isAlive()) pythonProcess.destroyForcibly(); // Making sure it's quite dead

        // Making sure we clean up
        pythonOSW.close();
        pythonISB.close();

        // Unbind references to allow reinstantiation
        pythonISB = null;
        pythonOSW = null;
        pythonProcess = null;
    }

    // Python Process Accessory Methods
    /**
     * Facilitates the construction of the python instance connection
     */
    public void instantiatePythonProcess() throws IOException {
        if (this.pythonProcess != null) return; // prevent override

        // Confirm we have python properly installed or kill this exe where it stands.
        this.checkPythonInstallation();
        // install virtual environment
        // e.g. source /Users/schaff/Library/Caches/pypoetry/virtualenvs/vcell-cli-utils-g4hrdDfL-py3.9/bin/activate

        // Start Python
        ProcessBuilder pb = new ProcessBuilder("poetry", "run", "python", "-i", "-W ignore");
        pb.redirectErrorStream(true);
        File cliWorkingDir = PropertyLoader.getRequiredDirectory(PropertyLoader.cliWorkingDir);
        pb.directory(cliWorkingDir);
        this.pythonProcess = pb.start();
        this.pythonOSW = new OutputStreamWriter(pythonProcess.getOutputStream());
        this.pythonISB = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));

        // "construction" commands
        try {
            this.getResultsOfLastCommand(); // Clear Buffer of Python Interpeter
            this.executeThroughPython("from vcell_cli_utils import wrapper");
            System.out.println("\nPython initalization success!\n");
        } catch (IOException | TimeoutException | InterruptedException e){
            System.out.println("Python instantiation Exception Thrown:\n" + e);
            System.exit(1);
        }
    }


    private String getResultsOfLastCommand() throws IOException, TimeoutException, InterruptedException {
        String importantPrefix = ">>> ";
        String results = "";

        int currentTime = 0, TIMEOUT_LIMIT = 600000; // 600 seconds (10 minutes)

        // Wait for python to finish what it's working on
        while(!this.pythonISB.ready()){
            if (currentTime++ >= TIMEOUT_LIMIT) throw new TimeoutException();
            Thread.sleep(1); // wait ~1ms at a time
        }

        // Python's ready (or we had a timeout?); lets get the buffer without going too far and getting blocked (see note 1 at bottom of file)
        while ( results.length() < importantPrefix.length()
                || !results.substring(results.length() - importantPrefix.length()).equals(importantPrefix)){ // unless we've found the prefix we need at the end of the results
            results += (char)this.pythonISB.read();
        }

        // Got the results we need. Now lets clean the results string up before returning it
        results = stripString(results.substring(0, results.length() - importantPrefix.length()));

        return results == "" ? null : results;
    }


    private void sendNewCommand(String cmd) throws IOException {
        // we can easily send the command, but we need to format it first.

        String command = String.format("%s\n", stripString(cmd));
        pythonOSW.write(command);
        pythonOSW.flush();
    }

    private static ProcessBuilder execShellCommand(String[] args) {
        // Setting the source and destination for subprocess standard I/O to be the same as those of the current Java process
        // return new ProcessBuilder(args).inheritIO();

        // the above is stupid because you can't capture and handle the process errors; need to use the default pipe, not inherit
        return new ProcessBuilder(args);
    }

    public static void runAndPrintProcessStreams(ProcessBuilder pb, String outString, String errString) throws InterruptedException, IOException {
        // Process printing code goes here
        File of = File.createTempFile("temp-", ".out", currentWorkingDir.toFile());
        File ef = File.createTempFile("temp-", ".err", currentWorkingDir.toFile());
        pb.redirectError(ef);
        pb.redirectOutput(of);
        Process process = pb.start();
        process.waitFor();
        StringBuilder sberr = new StringBuilder();
        StringBuilder sbout = new StringBuilder();
        List<String> lines = Files.readLines(ef, StandardCharsets.UTF_8);
        lines.forEach(line -> sberr.append(line).append("\n"));
        String es = sberr.toString();
        lines = Files.readLines(of, StandardCharsets.UTF_8);
        lines.forEach(line -> sbout.append(line).append("\n"));
        String os = sbout.toString();
        of.delete();
        ef.delete();
        if (process.exitValue() != 0) {
            System.err.println(errString);
            // don't print here, send the error down to caller who is responsible for dealing with it
            throw new RuntimeException(es);
        } else {
            System.out.println(outString);
            System.out.println(os);
        }
    }

    public boolean printPythonErrors(String returnedString){
        return this.printPythonErrors(returnedString, null, null);
    }

    public boolean printPythonErrors(String returnedString, String outString, String errString){
        boolean DEBUG_NORMAL_OUTPUT = false; // Manually override
        boolean hasPassed;
        String ERROR_PHRASE1 = "Traceback", ERROR_PHRASE2 = "File \"<stdin>\"";

        // Null or empty strings are considered passing results
        if(returnedString == null || (returnedString = stripString(returnedString)).equals(""))
            return true;

        if (
                (returnedString.length() >= ERROR_PHRASE1.length() && ERROR_PHRASE1.equals(returnedString.substring(0, ERROR_PHRASE1.length())))
                        ||  (returnedString.length() >= ERROR_PHRASE2.length() && ERROR_PHRASE2.equals(returnedString.substring(0, ERROR_PHRASE2.length())))
                        ||  (returnedString.contains("File \"<stdin>\""))

        ){ // Report an error:
            String resultString = (errString != null && errString.length() > 0) ? String.format("Result: %s\n", errString) : "";
            System.err.printf("Python error caught: <%s>\n%s", returnedString, resultString);
            hasPassed = false;
            System.exit(1);
        } else {
            String resultString = (outString != null && outString.length() > 0) ? String.format("Result: %s\n", outString) : "";
            System.out.printf("Python returned%s\nResult: %s\n", DEBUG_NORMAL_OUTPUT? String.format(": [%s]", returnedString) : " sucessfully.", resultString);
            hasPassed = true;
        }
        return hasPassed;
    }

    private String formatPythonFuctionCall(String functionName, String... arguments){
        return String.format("wrapper.%s(%s)", stripString(functionName), this.processPythonArguments(arguments));
    }


    private String processPythonArguments(String... arguments){
        String argList = "";
        int adjArgLength;
        for (String arg : arguments){
            argList += "r'" + stripString(arg) + "'" + ",";
        }
        adjArgLength = argList.length() == 0 ? 0 : argList.length() - 1;
        return argList.substring(0, adjArgLength);
    }

    public static String stripString(String str){
        return str.replaceAll("^[ \t]+|[ \t]+$", ""); // replace whitespace at the front and back with nothing
    }
}
