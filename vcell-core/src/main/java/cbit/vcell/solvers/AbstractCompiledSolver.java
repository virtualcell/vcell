/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.*;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;
import org.vcell.util.document.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Insert the type's description here.
 * Creation date: (6/26/2001 3:18:18 PM)
 */
public abstract class AbstractCompiledSolver extends AbstractSolver implements java.beans.PropertyChangeListener {
    /**
     * thread used to run solver. All access to this field should be synchronized
     */
    private transient Thread fieldThread = null;

    private cbit.vcell.solvers.MathExecutable mathExecutable = null;
    private double currentTime = -1;
    protected final static String DATA_PREFIX = "data:";
    protected final static String PROGRESS_PREFIX = "progress:";
    protected final static String SEPARATOR = ":";
    protected boolean bMessaging;

    /**
     * AbstractPDESolver constructor comment.
     */
    public AbstractCompiledSolver(SimulationTask simTask, File directory, boolean bMessaging) throws SolverException {
        super(simTask, directory);
        this.bMessaging = bMessaging;
        setCurrentTime(simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime());
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/9/2002 4:50:53 PM)
     */
    public abstract void cleanup();

    /**
     * Insert the method's description here.
     * Creation date: (6/27/2001 2:48:22 PM)
     *
     * @param message java.lang.String
     * @return cbit.vcell.solvers.ApplicationMessage
     */
    protected abstract ApplicationMessage getApplicationMessage(String message);

    /**
     * Insert the method's description here.
     * Creation date: (6/28/01 2:44:43 PM)
     *
     * @return double
     */
    public double getCurrentTime() {
        return currentTime;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/26/2001 5:03:04 PM)
     *
     * @return cbit.vcell.solvers.MathExecutable
     */
    public MathExecutable getMathExecutable() {
        return mathExecutable;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/28/01 2:48:52 PM)
     *
     * @return double
     */
    public double getProgress() {
        Simulation simulation = simTask.getSimulationJob().getSimulation();
        TimeBounds timeBounds = simulation.getSolverTaskDescription().getTimeBounds();
        double startTime = timeBounds.getStartingTime();
        double endTime = timeBounds.getEndingTime();
        return (currentTime - startTime) / (endTime - startTime);
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/27/2001 2:33:03 PM)
     */
    public void propertyChange(java.beans.PropertyChangeEvent event) {
        if (event.getSource() == getMathExecutable() && event.getPropertyName().equals("applicationMessage")) {
            String messageString = (String) event.getNewValue();
            if (messageString == null || messageString.isEmpty()) {
                return;
            }
            ApplicationMessage appMessage = getApplicationMessage(messageString);
            if (appMessage == null) {
                if (lg.isWarnEnabled()) lg.warn("AbstractCompiledSolver: Unexpected Message '" + messageString + "'");
            } else {
                switch (appMessage.getMessageType()) {
                    case ApplicationMessage.PROGRESS_MESSAGE: {
                        fireSolverProgress(appMessage.getProgress());
                        break;
                    }
                    case ApplicationMessage.DATA_MESSAGE: {
                        fireSolverPrinted(appMessage.getTimepoint());
                        break;
                    }
                    case ApplicationMessage.ERROR_MESSAGE: {
                        if (lg.isWarnEnabled()) lg.warn(appMessage.getError());
                        break;
                    }
                    // ignore unknown message types
                }
            }
        }
    }

    private static Path createSymbolicLink(File mySolverLinkDir, String linkName, File localSolverPath) throws IOException {
    	if((new File(mySolverLinkDir,linkName)).exists()) {
    		return new File(mySolverLinkDir,linkName).toPath();
    	}
        Path path0 = new File(mySolverLinkDir, linkName).toPath();
        Path path1 = localSolverPath.toPath();
//	System.out.println("linking "+path0+" "+path1);
        return Files.createSymbolicLink(path0, path1);
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/26/2001 3:08:31 PM)
     */
    public void runSolver() {
        try {
            setCurrentTime(simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime());
            setSolverStatus(new SolverStatus(SolverStatus.SOLVER_STARTING, SimulationMessage.MESSAGE_SOLVER_STARTING_INIT));
            // fireSolverStarting("initializing");
            // depends on solver; the initialize() method in actual solver will fire detailed messages
            initialize();
            setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, SimulationMessage.MESSAGE_SOLVER_RUNNING_START));
            fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING);
            checkLinuxSharedLibs();
            getMathExecutable().start();
            cleanup();
            //  getMathExecutable().start() may end prematurely (error or user stop), so check status before firing...
            if (getMathExecutable().getStatus().equals(org.vcell.util.exe.ExecutableStatus.COMPLETE)) {
                setSolverStatus(new SolverStatus(SolverStatus.SOLVER_FINISHED, SimulationMessage.MESSAGE_SOLVER_FINISHED));
                fireSolverFinished();
            }
        } catch (SolverException integratorException) {
            lg.error(integratorException.getMessage(), integratorException);
            cleanup();
            setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted(integratorException.getMessage())));
            fireSolverAborted(SimulationMessage.solverAborted(integratorException.getMessage()));
        } catch (org.vcell.util.exe.ExecutableException executableException) {
            lg.error(executableException.getMessage(), executableException);
            cleanup();
            setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted("Could not execute code: " + executableException.getMessage())));
            fireSolverAborted(SimulationMessage.solverAborted(executableException.getMessage()));
        } catch (Exception exception) {
            if (lg.isWarnEnabled())
                lg.warn("AbstractODESolver.start() : Caught Throwable instead of SolverException -- THIS EXCEPTION SHOULD NOT HAPPEN!");
            lg.error(exception.getMessage(), exception);
            cleanup();
            setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted(exception.getMessage())));
            fireSolverAborted(SimulationMessage.solverAborted(exception.getMessage()));
        } finally {
            synchronized (this) {
                fieldThread = null;
            }
        }
    }

    private void checkLinuxSharedLibs() throws IOException, InterruptedException {
        if (OperatingSystemInfo.getInstance().isMac()) {
            lg.trace(String.format("Setting env var `%s` to: \"%s\"", "HDF5_DISABLE_VERSION_CHECK", "1"));
            getMathExecutable().addEnvironmentVariable("HDF5_DISABLE_VERSION_CHECK", "1");
            return;
        }
        if (!OperatingSystemInfo.getInstance().isLinux()) return;

        // Delete remnants of last run (or create a fresh link directory)
        File localSimDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());
        File mySolverLinkDir = new File(localSimDir, simTask.getSimKey().toString() + ResourceUtil.LOCAL_SOLVER_LIB_LINK_SUFFIX);
        if (!mySolverLinkDir.exists()) {
            boolean ignored = mySolverLinkDir.mkdir();
        }
        File[] temp = mySolverLinkDir.listFiles();
        if (temp == null) throw new NullPointerException();
        for (File file : temp) {
            boolean ignored = file.delete();
        }

        // Prepare for Linkage
        SolverDescription mySolverDescription = getSimulationJob().getSimulation().getSolverTaskDescription().getSolverDescription();
        File localSolverPath = SolverUtilities.getExes(mySolverDescription)[0];

        // Perform linkage
        this.linkAllDependenciesOf(localSolverPath, localSolverPath.getParentFile(), mySolverLinkDir);
        lg.trace(String.format("Setting env var `%s` to: \"%s\"", "LD_LIBRARY_PATH", mySolverLinkDir.getAbsolutePath()));
        getMathExecutable().addEnvironmentVariable("LD_LIBRARY_PATH", mySolverLinkDir.getAbsolutePath());

    }

    private void linkAllDependenciesOf(File dependency, File sourceDirectory, File targetLinkDirectory) throws IOException, InterruptedException {
        File[] directoryContents = sourceDirectory.listFiles();
        if (directoryContents == null) throw new NullPointerException();
        Set<File> allPotentialDependencies = Stream.of(directoryContents)
                .filter(AbstractCompiledSolver::isDependency).collect(Collectors.toSet());
        Map<String, File> dependencyToFileMapping = new HashMap<>();
        for (File dep : allPotentialDependencies){
            dependencyToFileMapping.put(dep.getName(), dep);
        }
        Map<String, String> setOfDepsToLink = this.getDependenciesNeeded(dependency, dependencyToFileMapping, new HashSet<>());

        for (String depName : setOfDepsToLink.keySet()){
            Path ignored = AbstractCompiledSolver.createSymbolicLink(targetLinkDirectory, depName, dependencyToFileMapping.get(setOfDepsToLink.get(depName)));
        }
    }

    private Map<String, String> getDependenciesNeeded(String startingDependencyName, Map<String, File> availableDependencies,
                                              Set<String> alreadyProcessedDependencies) throws IOException, InterruptedException {
        if (availableDependencies.containsKey(startingDependencyName)) {
            return this.getDependenciesNeeded(
                    availableDependencies.get(startingDependencyName), availableDependencies, alreadyProcessedDependencies);
        } else {
            lg.warn("Warning: exact dependency not found by name; trying to stitch together an alternative.");
            for (String alternate : availableDependencies.keySet()){
                if (!startingDependencyName.startsWith(alternate)) continue;
                Path stichedPath = Paths.get(availableDependencies.get(alternate).getParent(), startingDependencyName);
                lg.info("Alternative found; attempting to link stitched dependency.");
                return this.getDependenciesNeeded(stichedPath.toFile(), availableDependencies, alreadyProcessedDependencies);
            }
            throw new RuntimeException("No alternatives possible for missing dependency: `" + startingDependencyName + "`");
        }
    }

    private Map<String, String> getDependenciesNeeded(File startingDependency, Map<String, File> availableDependencies,
                                              Set<String> alreadyProcessedDependencies) throws IOException, InterruptedException {
        Map<String, String> dependenciesNeeded = new HashMap<>();
        File dependency;
        if (availableDependencies.containsKey(startingDependency.getName())){
            dependency = availableDependencies.get(startingDependency.getName());
        } else { // Determine alternative options
            int locationOfExtension = startingDependency.getName().indexOf(".so");
            if (locationOfExtension == -1) throw new RuntimeException("Dependency " + startingDependency.getName() + " not found in available dependencies");
            lg.warn("Warning: exact dependency not found by name; searching for inexact match...");
            String shortName = startingDependency.getName().substring(0, locationOfExtension + 3);
            String dependencyToAdd = this.getEntryStartingWith(shortName, availableDependencies.keySet());
            if (dependencyToAdd == null) throw new RuntimeException("No alternatives possible for missing dependency: `" + startingDependency.getName() + "`");
            lg.info("Alternative successfully found; preparing to link.");
            dependency = availableDependencies.get(dependencyToAdd);
            alreadyProcessedDependencies.add(startingDependency.getName());
        }
        if (alreadyProcessedDependencies.contains(dependency.getName())) return new HashMap<>(); // Already done
        dependenciesNeeded.put(startingDependency.getName(), dependency.getName());
        String lddRawResults = AbstractCompiledSolver.getLddResult(dependency);

        //Try to save output to file for cli without interfering with further processing
        File stdOutFile = new File(String.valueOf(Paths.get(System.getProperty("user.dir"))), "stdOut.txt");
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(stdOutFile))){
            bos.write(lddRawResults.getBytes());
        } catch(Exception e) {
            lg.error(e.getMessage(), e);
        }

        // Begin Parsing
        try (BufferedReader br = new BufferedReader(new StringReader(lddRawResults))) {
            for (String line = br.readLine(); line != null; line = br.readLine()){
                StringTokenizer libInfo = new StringTokenizer(line, " \t");
                if (libInfo.countTokens() != 4) continue; // We do not care about these
                // If we have 4 tokens, we have one of two cases: 
                //  >> Case 1: <lib_name>,"=>",<lib_path>,<offset>
                //  >> Case 2: <lib_name>,"=>","not","found" (<-- this is the case we want to identify!)
                String libName = libInfo.nextToken();
                String ignored = libInfo.nextToken(); // "=>"
                String libPath = libInfo.nextToken();
                String aux = libInfo.nextToken();

                if (!libPath.equals("not") || !aux.equals("found")) continue; // We only care about Case 2
                dependenciesNeeded.putAll(this.getDependenciesNeeded(
                        libName, availableDependencies, alreadyProcessedDependencies));

            }
        }
        alreadyProcessedDependencies.add(dependency.getName());
        return dependenciesNeeded;
    }

    private static String getLddResult(File solverToResolve) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("ldd", solverToResolve.toPath().toString());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        StringBuilder sb = new StringBuilder();
        // Can we pull inputStream outside the for-loop?
        for (int ioByte = p.getInputStream().read(); ioByte != -1; ioByte = p.getInputStream().read()){
            sb.append((char) ioByte);
        }
        p.waitFor();
        return sb.toString();
    }

    private static boolean isDependency(File potentialDependency){
        return potentialDependency.isFile()
                && potentialDependency.length() != 0
                && !Files.isSymbolicLink(potentialDependency.toPath())
                && !potentialDependency.getName().startsWith(".");
    }

    private String getEntryStartingWith(String shortName, Set<String> candidates){
        for (String candidate : candidates) if (candidate.startsWith(shortName)) return candidate;
        return null;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/28/01 2:44:43 PM)
     *
     * @param newCurrentTime double
     */
    protected void setCurrentTime(double newCurrentTime) {
        currentTime = newCurrentTime;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/26/2001 5:03:04 PM)
     *
     * @param newMathExecutable cbit.vcell.solvers.MathExecutable
     */
    protected void setMathExecutable(MathExecutable newMathExecutable) {
        if (mathExecutable != null) {
            mathExecutable.removePropertyChangeListener(this);
        }
        if (newMathExecutable != null) {
            newMathExecutable.addPropertyChangeListener(this);
        }
        mathExecutable = newMathExecutable;
    }

    public synchronized final void startSolver() {
        if (!(fieldThread != null && fieldThread.isAlive())) {
            setMathExecutable(null);
            fieldThread = new Thread(this::runSolver);
            fieldThread.setName("Compiled Solver (" + getClass().getName() + ")");
            fieldThread.start();
        }
    }

    /**
     *
     */
    public synchronized final void stopSolver() {
        if (fieldThread != null && getMathExecutable() != null) {
            fieldThread.interrupt();
            getMathExecutable().stop();
            setSolverStatus(new SolverStatus(SolverStatus.SOLVER_STOPPED, SimulationMessage.MESSAGE_SOLVER_STOPPED_BY_USER));
            fireSolverStopped();
        }
    }

    public Vector<AnnotatedFunction> createFunctionList() {
        try {
            return simTask.getSimulationJob().getSimulationSymbolTable().createAnnotatedFunctionsList(simTask.getSimulation().getMathDescription());
        } catch (Exception e) {
            throw new RuntimeException("Simulation '" + simTask.getSimulationInfo().getName() + "', error createFunctionList(): " + e.getMessage(), e);
        }
    }

    public void writeFunctionsFile() {
        // ** Dumping the functions of a simulation into a '.functions' file.
        String functionFileName = getBaseName() + FUNCTIONFILE_EXTENSION;
        Vector<AnnotatedFunction> funcList = null;

        // Create an empty function file if using langeVinSolver
        if (!simTask.getSimulation().getSolverTaskDescription().getSolverDescription().isLangevinSolver()) {
             funcList = createFunctionList();
         }

        //Try to save existing user defined functions
        FunctionFileGenerator functionFileGenerator = new FunctionFileGenerator(functionFileName, funcList);

        try {
            functionFileGenerator.generateFunctionFile();
        } catch (Exception e) {
            throw new RuntimeException("Error creating .function file for " + functionFileGenerator.getBasefileName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * return set of {@link ExecutableCommand}s
     *
     * @return new Collection
     */
    public abstract Collection<ExecutableCommand> getCommands();

}