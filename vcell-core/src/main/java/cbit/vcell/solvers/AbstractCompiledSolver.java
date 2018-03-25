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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;
/**
 * Insert the type's description here.
 * Creation date: (6/26/2001 3:18:18 PM)
 * @author: Ion Moraru
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
	protected boolean bMessaging = true; 
/**
 * AbstractPDESolver constructor comment.
 */
public AbstractCompiledSolver(SimulationTask simTask, File directory, boolean bMsging) throws SolverException {
	super(simTask, directory);
	bMessaging = bMsging;
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
 * @return cbit.vcell.solvers.ApplicationMessage
 * @param message java.lang.String
 */
protected abstract ApplicationMessage getApplicationMessage(String message);
/**
 * Insert the method's description here.
 * Creation date: (6/28/01 2:44:43 PM)
 * @return double
 */
public double getCurrentTime() {
	return currentTime;
}
/**
 * Insert the method's description here.
 * Creation date: (6/26/2001 5:03:04 PM)
 * @return cbit.vcell.solvers.MathExecutable
 */
public MathExecutable getMathExecutable() {
	return mathExecutable;
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/01 2:48:52 PM)
 * @return double
 */
public double getProgress() {
	Simulation simulation = simTask.getSimulationJob().getSimulation();
	TimeBounds timeBounds = simulation.getSolverTaskDescription().getTimeBounds();
	double startTime = timeBounds.getStartingTime();
	double endTime = timeBounds.getEndingTime();
	return (currentTime-startTime)/(endTime-startTime);
}

/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 2:33:03 PM)
 */
public void propertyChange(java.beans.PropertyChangeEvent event) {
	if (event.getSource() == getMathExecutable() && event.getPropertyName().equals("applicationMessage")) {
		String messageString = (String)event.getNewValue();
		if (messageString==null || messageString.length()==0){
			return;
		}
		ApplicationMessage appMessage = getApplicationMessage(messageString);
		if (appMessage==null){
			if (lg.isWarnEnabled()) lg.warn("AbstractCompiledSolver: Unexpected Message '"+messageString+"'");
			return;
		}else{
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
		if(OperatingSystemInfo.getInstance().isLinux()){
			final String LD_LIB_PATH = "LD_LIBRARY_PATH";
			File solversDir = ResourceUtil.getLocalSolversDirectory();
			String existingLD_LIB_PATH = null;
			Map<String, String>envMap = System.getenv();
			Iterator<String> envIter = envMap.keySet().iterator();
			while(envIter.hasNext()){
				String key = envIter.next();
				String val = envMap.get(key).toString();
//				System.out.println(key+"\n     "+val);
				if(key.equals(LD_LIB_PATH)){
					existingLD_LIB_PATH = val;
					if(existingLD_LIB_PATH != null && existingLD_LIB_PATH.length() > 0 && !existingLD_LIB_PATH.endsWith(":")){
						existingLD_LIB_PATH+= ":";
					}
					break;
				}
			}
			String newLD_LIB_PATH = (existingLD_LIB_PATH==null?"":existingLD_LIB_PATH)+solversDir.getAbsolutePath();
			System.out.println("-----Setting executable "+LD_LIB_PATH+" to "+newLD_LIB_PATH);
			getMathExecutable().addEnvironmentVariable(LD_LIB_PATH, newLD_LIB_PATH);			
		}
		getMathExecutable().start();
		cleanup();
		//  getMathExecutable().start() may end prematurely (error or user stop), so check status before firing...
		if (getMathExecutable().getStatus().equals(org.vcell.util.exe.ExecutableStatus.COMPLETE)) {
			setSolverStatus(new SolverStatus(SolverStatus.SOLVER_FINISHED, SimulationMessage.MESSAGE_SOLVER_FINISHED));
			fireSolverFinished();
		}
	} catch (SolverException integratorException) {
		lg.error(integratorException.getMessage(),integratorException);
		cleanup();
		setSolverStatus(new SolverStatus (SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted(integratorException.getMessage())));
		fireSolverAborted(SimulationMessage.solverAborted(integratorException.getMessage()));
	} catch (org.vcell.util.exe.ExecutableException executableException) {
		lg.error(executableException.getMessage(),executableException);
		cleanup();
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted("Could not execute code: " + executableException.getMessage())));
		fireSolverAborted(SimulationMessage.solverAborted(executableException.getMessage()));
	} catch (Exception exception) {
		if (lg.isWarnEnabled()) lg.warn("AbstractODESolver.start() : Caught Throwable instead of SolverException -- THIS EXCEPTION SHOULD NOT HAPPEN!");
		lg.error(exception.getMessage(),exception);
		cleanup();
		setSolverStatus(new SolverStatus (SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted(exception.getMessage())));
		fireSolverAborted(SimulationMessage.solverAborted(exception.getMessage()));
	} finally {
		synchronized(this) {
			fieldThread = null;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/01 2:44:43 PM)
 * @param newCurrentTime double
 */
protected void setCurrentTime(double newCurrentTime) {
	currentTime = newCurrentTime;
}
/**
 * Insert the method's description here.
 * Creation date: (6/26/2001 5:03:04 PM)
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
	if (!(fieldThread != null && fieldThread.isAlive()) ) {
		setMathExecutable(null);
		fieldThread = new Thread() {
			public void run() {
				runSolver();
			}
		};
		fieldThread.setName("Compiled Solver (" + getClass().getName() + ")");
		fieldThread.start();
	}
}
/**
 */
public synchronized final void stopSolver() {
	if (fieldThread!= null && getMathExecutable()!=null){
		fieldThread.interrupt();
		getMathExecutable().stop();
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_STOPPED,SimulationMessage.MESSAGE_SOLVER_STOPPED_BY_USER));
		fireSolverStopped();
	}
}

public Vector<AnnotatedFunction> createFunctionList() {
	try{
		return simTask.getSimulationJob().getSimulationSymbolTable().createAnnotatedFunctionsList(simTask.getSimulation().getMathDescription());
	}catch(Exception e){
		e.printStackTrace();
		throw new RuntimeException("Simulation '"+simTask.getSimulationInfo().getName()+"', error createFunctionList(): "+e.getMessage(),e);
	}
}

public void writeFunctionsFile() {
	// ** Dumping the functions of a simulation into a '.functions' file.
	String functionFileName = getBaseName() + FUNCTIONFILE_EXTENSION;
	Vector<AnnotatedFunction> funcList = createFunctionList();
	
	//Try to save existing user defined functions
	FunctionFileGenerator functionFileGenerator = new FunctionFileGenerator(functionFileName, funcList);

	try {
		functionFileGenerator.generateFunctionFile();		
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new RuntimeException("Error creating .function file for "+functionFileGenerator.getBasefileName()+e.getMessage(),e);
	}		
}

/**
 * return set of {@link ExecutableCommand}s
 * @return new Collection
 */
public abstract Collection<ExecutableCommand> getCommands( );

}