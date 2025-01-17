package org.vcell.cli;

import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton class that manages VCell CLI's interaction with the `cli.py` external python calls
 * The general operation of this class is opening a single, dedicated, interactive python session, and running all commands through it.
 */
public class CLIPythonManager {
    /*
     * NOTES:
     *
     *      1) BufferedReader will block if asked to read the Python interpreter while the interpreter waiting for
     * input (assuming the buffer has already been iterated through).
     *      To prevent this, we catch the prefix to Python's prompt for a new command (">>> ")
     * to stop before we wait for eternity and break our program.
     */

    private static final Logger lg = LogManager.getLogger(CLIPythonManager.class);

    private static final Path CURRENT_WORKING_DIR = Paths.get("").toAbsolutePath();
    private static final String PYTHON_EXE_NAME = OperatingSystemInfo.getInstance().isWindows() ? "python" : "python3";

    private static CLIPythonManager instance = null;
    private static final int DEFAULT_TIMEOUT = 15000; // was 600 seconds (10 minutes), now 15 seconds

    private static final boolean USE_SHARED_SHELL = true;

    private Process pythonProcess; 			// hold python interpreter instance used in updateXxx...() methods
    private OutputStreamWriter pythonOSW; 	// input channel *to* python interpreter (see above)
    private BufferedReader pythonISB; 		// output channel ("Input Stream Buffer") *from* python interpreter (see above)

    /**
     * Retrieve the Python Manager, or create and return if it doesn't exist.
     * @return the manager
     * @deprecated CLIPythonManager is no longer used in CLI, and has no current use; should this be used again
     * you need to check whether there are still bugs with Arm Macs, and whether single-instance python shells are not a viable alternative
     */
    @Deprecated
    public static CLIPythonManager getInstance(){
        lg.trace("Getting Python instance");
        if (instance == null){
            instance = new CLIPythonManager();
        }
        return instance;
    }

    /**
     * Call one of the python functions in cli.py, and get it's return
     * 
     * @param functionName name of the pythonFunction to call
     * @param arguments the arguments to provide to the function call, each as their own string, in the correct order
     * @return the return response from Python
     * @throws PythonStreamException if there is any exception encountered in this exchange.
     * @deprecated See getInstance()
     */
    @Deprecated
    public String callPython(String functionName, String... arguments) throws PythonStreamException {
        String command = this.formatPythonFunctionCall(functionName, arguments);
        try {
            return CLIPythonManager.USE_SHARED_SHELL ?
                    this.callPython(command) : CLIPythonManager.callNonSharedPython(command);
        } catch (PythonStreamException e) {
            throw e;
        } catch (Exception e){
            lg.error("Error calling python: {}", e.getMessage());
            String errorPrefix = String.format("Command `%s` failed in non-shared mode.", command);
            throw new PythonStreamException(errorPrefix, e);
        }
    }

    /**
     * 
     * @param cliCommand the command to run
     * @throws InterruptedException if the python process was interrupted
     * @throws IOException if there was a system IO failure
     * @deprecated See getInstance()
     */
    @Deprecated
    private static String callNonSharedPython(String cliCommand)
            throws InterruptedException, IOException, PythonStreamException {
        Path cliWorkingDir = Paths.get(PropertyLoader.getRequiredProperty(PropertyLoader.cliWorkingDir));
        String commandString = "from vcell_cli_utils import wrapper; " + cliCommand;
        ProcessBuilder pb = new ProcessBuilder("poetry", "run", "python", "-c", commandString);
        pb.directory(cliWorkingDir.toFile());
        return CLIPythonManager.runAndPrintProcessStreams(pb, "","");
    }

    /**
     * Shuts down the python session and cleans up.
     * 
     * @throws IOException if there is a system IO issue.
     * @deprecated See getInstance()
     */
    @Deprecated
    public void closePythonProcess() throws IOException {
        // Exit the living Python Process
        lg.debug("Closing Python Instance");
        if (this.pythonOSW != null && this.pythonISB != null) this.sendNewCommand("exit()"); // Sends kill command ("exit()") to python.exe instance;
        try {
            for (int i = 1; i <= 5; i++) if (this.pythonProcess != null && this.pythonProcess.isAlive()) Thread.sleep(1000 * i);
        } catch (InterruptedException ignored) {} // Just want to try and give the process a chance to close gracefully
        if (this.pythonProcess != null && this.pythonProcess.isAlive()) this.pythonProcess.destroyForcibly(); // Making sure it's quite dead

        // Making sure we clean up
        if (this.pythonOSW != null) this.pythonOSW.close();
        if (this.pythonISB != null) this.pythonISB.close();

        // Unbind references to allow re-instantiation
        this.pythonISB = null;
        this.pythonOSW = null;
        this.pythonProcess = null;
    }

    // Python Process Accessory Methods
    /**
     * Facilitates the construction of the python instance connection. This is done automatically with `callPython`, so use this 
     * as means of installation verification.
     * 
     * @throws IOException if there is a problem with System I/O
     * @deprecated this entire system is unstable on ARM Macs, and still a bit slow.
     */
    @Deprecated
    public void instantiatePythonProcess() throws IOException, PythonStreamException {
        if (this.pythonProcess != null) return; // prevent override
        lg.info("Initializing Python...");
        if (!USE_SHARED_SHELL) {
            lg.warn("Not using shared shell, canceling initialization; python will be slower!!");
            return;
        }
        // Confirm we have python properly installed or kill this exe where it stands.
        this.checkPythonInstallation();
        // install virtual environment
        // e.g. source ~/Library/Caches/pypoetry/virtualenvs/vcell-cli-utils-g4hrdDfL-py3.9/bin/activate

        // Start poetry based Python
        ProcessBuilder pb = new ProcessBuilder("poetry", "run", "python", "-i", "-W ignore");
        pb.redirectErrorStream(true);
        File cliWorkingDir = PropertyLoader.getRequiredDirectory(PropertyLoader.cliWorkingDir).getCanonicalFile();
        pb.directory(cliWorkingDir);
        lg.debug("Loading poetry in directory: {}", cliWorkingDir);
        this.pythonProcess = pb.start();
        this.pythonOSW = new OutputStreamWriter(this.pythonProcess.getOutputStream());
        this.pythonISB = new BufferedReader(new InputStreamReader(this.pythonProcess.getInputStream()));

        // "construction" commands
        try {
            this.getResultsOfLastCommand(); // Clear Buffer of Python Interpreter
            this.callPython("");
            this.callPython("from vcell_cli_utils import wrapper");
            lg.info("Python initialization success!");
        } catch (IOException | TimeoutException | InterruptedException e){
            lg.warn("Python instantiation Exception Thrown:\n", e);
            throw new PythonStreamException("Could not initialize Python. Problem is probably python-side.", e);
        }
    }


    // Needs properly formatted python command
    private String callPython(String command) throws PythonStreamException, TimeoutException {
        // Attempt 1
        try {
            this.instantiatePythonProcess(); // Make sure we have a python instance; will not override already done
            this.sendNewCommand(command);
            String lastCommandResult = this.getResultsOfLastCommand(CLIPythonManager.DEFAULT_TIMEOUT);
            return lastCommandResult == null ? "" : lastCommandResult;
        } catch (IOException | InterruptedException e){
            throw new PythonStreamException("Python process encountered an exception:\n" + e.getMessage(), e);
        } catch (TimeoutException e) {
            // Attempt 2
            lg.warn("Error calling python: {}", e.getMessage());
            lg.warn("Attempting to acknowledge warnings / prompts.");
            try {
                this.instantiatePythonProcess(); // Make sure we have a python instance; will not override already done
                // we may get the result of the original call, if we "accept" a warning / prompt the shell got
                this.sendNewCommand(""); // We just want to "hit enter" so to speak
                String result = this.getResultsOfLastCommand(CLIPythonManager.DEFAULT_TIMEOUT);
                lg.info("Second python attempt successful!");
                return result == null ? "" : result;
            } catch (TimeoutException e2){
                // Final Attempt; use the independent shell!
                lg.warn("Error on second attempt calling python: {}", e2.getMessage());
                lg.warn("Attempting last resort");
                try {
                    String lastChanceResult = CLIPythonManager.callNonSharedPython(command);
                    lg.info("Last python attempt successful!");
                    return lastChanceResult;
                } catch (InterruptedException | IOException e3){
                    lg.error("Error on last resort: {}", e3.getMessage());
                    String errorPrefix = String.format("Command `%s` failed in non-shared mode.", command);
                    throw new PythonStreamException(errorPrefix, e3);
                }
            } catch (IOException | InterruptedException e4){
                throw new PythonStreamException("Python process encountered an exception:\n" + e4.getMessage(), e4);
            } finally { // Let's make sure our buffer isn't clogged; for now, we'll log, but discard any extra output
                StringBuilder remainder = new StringBuilder("Leftovers in buffer: \n");
                try {
                    while (this.pythonISB.ready()){
                        remainder.append(this.getResultsOfLastCommand()).append("\n");
                    }
                } catch (IOException | InterruptedException cleaningException){
                    lg.warn("Received exception while trying to clear buffer:", cleaningException);
                }
                lg.debug(remainder.toString());
            }
        }
    }

    private String getResultsOfLastCommand() throws IOException, TimeoutException, InterruptedException {
        return this.getResultsOfLastCommand(CLIPythonManager.DEFAULT_TIMEOUT);
    }

    private String getResultsOfLastCommand(int msTimeout) throws IOException, TimeoutException, InterruptedException {
        String importantPrefix = ">>> ";
        StringBuilder results = new StringBuilder();

        int currentTime = 0;

        // Wait for python to finish what it's working on
        while(!this.pythonISB.ready()){
            if (currentTime++ >= msTimeout) throw new TimeoutException();
            Thread.sleep(1); // wait ~1ms at a time
            //TODO: Rewrite this class to work asynchronously, and get rid of this busy wait
        }

        // Python's ready (or we had a timeout?); lets get the buffer without going too far and getting blocked (see note 1 at bottom of file)
        while (this.pythonISB.ready() && (results.length() < importantPrefix.length()
                || !results.toString().endsWith(importantPrefix))){
            // we do this unless we've found the prefix we need at the end of the results
            results.append((char) this.pythonISB.read());
        }

        // Weird mac shell warning causing us bugs; try to clear and get the results.
        if (results.toString().contains("ecure coding is not enabled for restorable state")){
            this.sendNewCommand("");
        }


        if (results.toString().contains(importantPrefix)) {
            // Got the results we need. Now lets clean the results string up before returning it
            results = new StringBuilder(CLIUtils.stripString(results.substring(0, results.length() - importantPrefix.length())));
        } else {
            // We may have gotten a warning or something; continue to poll for
            return this.getResultsOfLastCommand();
        }


        return results.toString().isEmpty() ? null : results.toString();
    }

    private void sendNewCommand(String cmd) throws IOException {
        // we can easily send the command, but we need to format it first.

        String command = String.format("%s\n", CLIPythonManager.stripStringForPython(cmd));
        lg.trace("Sent cmd to Python: {}", command);
        this.pythonOSW.write(command);
        this.pythonOSW.flush();
    }

    // Helper method for easy, local calling.
    private void executeThroughPython() throws PythonStreamException {
        String results;
        try {
            results = this.callPython("from vcell_cli_utils import wrapper");
            this.parsePythonReturn(results);
        } catch (TimeoutException e) {
            throw new PythonStreamException("Python process encountered an exception:\n" + e.getMessage(), e);
        }
    }

    private void checkPythonInstallation() {
        String version = "--version", stdOutLog;
        ProcessBuilder processBuilder;
        Process process;
        int exitCode;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            processBuilder = execShellCommand(new String[]{PYTHON_EXE_NAME, version});
            process = processBuilder.start();
            exitCode = process.waitFor();
            if (exitCode == 0) {
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((stdOutLog = bufferedReader.readLine()) != null) {
                    stringBuilder.append(stdOutLog);
                    // search string can be one or more... (potential python 2.7.X bug here?) Maybe use regex?
                    if (!stringBuilder.toString().toLowerCase().startsWith("python 3")) throw new PythonStreamException();
                    lg.debug(String.format("Python initialized as version %s", stringBuilder.toString().split(" ")[1]));
                }
            }
        } catch (PythonStreamException e) {
            lg.error("Please check your local Python and PIP Installation, install required packages and versions", e);
            throw new MissingResourceException("Error validating Python installation:\n" + e.getMessage(), "Python 3.9+", "");
        } catch (IOException | InterruptedException e) {
            String msg = "System produced " + e.getClass().getSimpleName() + " when trying to validate Python.";
            lg.error(msg, e);
        }
    }

    private static ProcessBuilder execShellCommand(String[] args) {
        // Setting the source and destination for subprocess standard I/O to be the same as those of the current Java process
        // return new ProcessBuilder(args).inheritIO();

        // the above is stupid because you can't capture and handle the process errors; need to use the default pipe, not inherit
        return new ProcessBuilder(args);
    }

    private static String runAndPrintProcessStreams(ProcessBuilder pb, String outString, String errString)
            throws InterruptedException, IOException, PythonStreamException {
        // Process printing code goes here
        File of = File.createTempFile("temp-", ".out", CURRENT_WORKING_DIR.toFile());
        File ef = File.createTempFile("temp-", ".err", CURRENT_WORKING_DIR.toFile());
        pb.redirectError(ef);
        pb.redirectOutput(of);
        Process process = pb.start();
        process.waitFor();
        StringBuilder sbError = new StringBuilder();
        StringBuilder sbOut = new StringBuilder();
        List<String> lines = Files.readLines(ef, StandardCharsets.UTF_8);
        lines.forEach(line -> sbError.append(line).append("\n"));
        String es = sbError.toString();
        lines = Files.readLines(of, StandardCharsets.UTF_8);
        lines.forEach(line -> sbOut.append(line).append("\n"));
        String os = sbOut.toString();
        if (of.delete()) lg.warn("Temp out file didn't delete properly");
        if (ef.delete()) lg.warn("Temp error file didn't delete properly");
        if (process.exitValue() != 0) {
            lg.error(errString);
            // don't print here, send the error down to caller who is responsible for dealing with it
            throw new PythonStreamException(es);
        } else {
            if (!outString.isEmpty()) lg.info(outString);
            if (!os.isEmpty()) lg.info(os);
        }
        return os;
    }

    /**
     * Checks whether python returned successfully or not
     * @param returnedString
     * @throws PythonStreamException
     * @deprecated See getInstance()
     */
    @Deprecated
    public void parsePythonReturn(String returnedString) throws PythonStreamException {
       this.parsePythonReturn(returnedString, null, null);
    }

    /**
     * Checks whether python returned successfully or not
     * @param returnedString
     * @param outString
     * @param errString
     * @throws PythonStreamException
     * @deprecated See getInstance()
     */
    @Deprecated
    public void parsePythonReturn(String returnedString, String outString, String errString) throws PythonStreamException {
        boolean DEBUG_NORMAL_OUTPUT = lg.isTraceEnabled(); // Consider getting rid of this, currently redundant
        String ERROR_PHRASE1 = "Traceback", ERROR_PHRASE2 = "File \"<stdin>\"";

        // If nulls, set empty strings
        if (outString == null) outString = "";
        if (errString == null) errString = "";

        // Null or empty strings are considered passing results
        if(returnedString == null || (returnedString = CLIUtils.stripString(returnedString)).isEmpty()){
            lg.trace("Python returned successfully.");
            return;
        }

        if (    // time for complex error checking
                (returnedString.length() >= ERROR_PHRASE1.length() && ERROR_PHRASE1.equals(returnedString.substring(0, ERROR_PHRASE1.length())))
            ||  (returnedString.length() >= ERROR_PHRASE2.length() && ERROR_PHRASE2.equals(returnedString.substring(0, ERROR_PHRASE2.length())))
            ||  (returnedString.contains("File \"<stdin>\""))
        ){ // Report an error:
            String resultString = errString.isEmpty() ? "" : String.format("Result: %s\n", errString);
            String errorMessage = String.format("Python error caught: <\n%s\n>\n%s\n", returnedString, resultString);
            throw new PythonStreamException(errorMessage);
        } else {
            String resultString = outString.isEmpty() ? "" : String.format("Result: %s\n", outString);
            String message = "Python returned%s\nResult: %s\n";
            String debugTypeString = DEBUG_NORMAL_OUTPUT? String.format(": [%s]", returnedString) : " successfully.";
            lg.trace(String.format(message, debugTypeString, resultString));
        }
    }

    private String formatPythonFunctionCall(String functionName, String... arguments){
        return String.format("wrapper.%s(%s)", CLIUtils.stripString(functionName), this.processPythonArguments(arguments));
    }


    private String processPythonArguments(String... arguments){
        StringBuilder argList = new StringBuilder();
        int adjArgLength;
        for (String arg : arguments){
            argList.append("r\"\"\"").append(CLIUtils.stripString(arg)).append("\"\"\","); // python r-string
        }
        adjArgLength = argList.isEmpty() ? 0 : argList.length() - 1;
        return argList.substring(0, adjArgLength);
    }

    private static String stripStringForPython(String str){
        return CLIUtils.stripString(str);
    }
}
