/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.smoldyn;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.vcell.util.ExceptionInterpreter;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

import cbit.vcell.math.MathException;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.AbstractCompiledSolver;
import cbit.vcell.solvers.ApplicationMessage;
import cbit.vcell.solvers.MathExecutable;
import cbit.vcell.solvers.SubdomainInfo;

/**
 * Gibson solver 
 * Creation date: (7/13/2006 9:00:41 AM)
 * @author: Tracy LI
 */
public class SmoldynSolver extends AbstractCompiledSolver {
	
	static {
		ExceptionInterpreter.instance().add("libglut.so", ".*no such file.*",
				"Use Linux distribution's package manager to install libglut.so.3",
				"OpenSuse install 'libglut3'",
				"Fedora / Centos install 'freeglut'",
				"Ubuntu install 'freeglut3'"
				);
		
	}

public SmoldynSolver(SimulationTask simTask, java.io.File directory, SessionLog sessionLog, boolean bMsging) throws SolverException {
	super(simTask, directory, sessionLog, bMsging);
}


/**
 * Insert the method's description here.
 * Creation date: (7/13/2006 9:00:41 AM)
 */
public void cleanup() 
{
}


/**
 * show progress.
 * Creation date: (7/13/2006 9:00:41 AM)
 * @return cbit.vcell.solvers.ApplicationMessage
 * @param message java.lang.String
 */
protected ApplicationMessage getApplicationMessage(String message) {
	String SEPARATOR = ":";
	String DATA_PREFIX = "data:";
	String PROGRESS_PREFIX = "progress:";
	if (message.startsWith(DATA_PREFIX)){
		double timepoint = Double.parseDouble(message.substring(message.lastIndexOf(SEPARATOR)+1));
		setCurrentTime(timepoint);
		return new ApplicationMessage(ApplicationMessage.DATA_MESSAGE,getProgress(),timepoint,null,message);
	}else if (message.startsWith(PROGRESS_PREFIX)){
		String progressString = message.substring(message.lastIndexOf(SEPARATOR)+1,message.indexOf("%"));
		double progress = Double.parseDouble(progressString)/100.0;
		//double startTime = getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime();
		//double endTime = getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
		//setCurrentTime(startTime + (endTime-startTime)*progress);
		return new ApplicationMessage(ApplicationMessage.PROGRESS_MESSAGE,progress,-1,null,message);
	}else{
		throw new RuntimeException("unrecognized message");
	}
}

/**
 *  This method takes the place of the old runUnsteady()...
 */
protected void initialize() throws SolverException 
{
	SessionLog sessionLog = getSessionLog();
	sessionLog.print("SmoldynSolver.initialize()");
	fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INIT);
	writeFunctionsFile();
	
	// write subdomains file
	try {
		SubdomainInfo.write(new File(getBaseName() + SimDataConstants.SUBDOMAINS_FILE_SUFFIX), simTask.getSimulation().getMathDescription());
	} catch (IOException e1) {
		e1.printStackTrace();
		throw new SolverException(e1.getMessage());
	} catch (MathException e1) {
		e1.printStackTrace();
		throw new SolverException(e1.getMessage());
	}

	String inputFilename = getInputFilename();
	sessionLog.print("SmoldynSolver.initialize() baseName = " + getBaseName());

	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, SimulationMessage.MESSAGE_SOLVER_RUNNING_INPUT_FILE));
	fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INPUT_FILE);

	PrintWriter pw = null;
	try {
		pw = new PrintWriter(inputFilename);
		SmoldynSurfaceTessellator stFileWriter = new SmoldynFileWriter(pw, false, getBaseName(), simTask, bMessaging);
		stFileWriter.write();
	} catch (Exception e) {
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted("Could not generate input file: " + e.getMessage())));
		e.printStackTrace(System.out);
		throw new SolverException(e.getMessage());
	} finally {
		if (pw != null) {
			pw.close();	
		}
	}

	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING,SimulationMessage.MESSAGE_SOLVER_RUNNING_START));	
	//get executable path+name.
	setMathExecutable(new MathExecutable(getMathExecutableCommand(), getSaveDirectory()));	
	//setMathExecutable(new cbit.vcell.solvers.MathExecutable(executableName + " gibson " + getBaseName() + ".stochInput" + " " + getBaseName() + ".stoch"));
}

private String getInputFilename(){
	return getBaseName() + SMOLDYN_INPUT_FILE_EXTENSION;
}

@Override
public String[] getMathExecutableCommand() {
	String executableName = PropertyLoader.getRequiredProperty(PropertyLoader.smoldynExecutableProperty);
	String inputFilename = getInputFilename();
	return new String[] { executableName, inputFilename };
}

/**
 * Insert the method's description here.
 * Creation date: (10/11/2006 11:16:02 AM)
 */
public void propertyChange(java.beans.PropertyChangeEvent event)
{
	super.propertyChange(event);
	
	if (event.getSource() == getMathExecutable() && event.getPropertyName().equals("applicationMessage")) {
		String messageString = (String)event.getNewValue();
		if (messageString==null || messageString.length()==0){
			return;
		}
		ApplicationMessage appMessage = getApplicationMessage(messageString);
		if (appMessage!=null && appMessage.getMessageType() == ApplicationMessage.PROGRESS_MESSAGE) {
			fireSolverPrinted(getCurrentTime());
		}
	}
}

public Vector<AnnotatedFunction> createFunctionList() {
	//
	// add appropriate Function columns to result set
	//
	Vector<AnnotatedFunction> funcList = new Vector<AnnotatedFunction>();
	
//	SimulationSymbolTable simSymbolTable = simulationJob.getSimulationSymbolTable();
//	Function functions[] = simSymbolTable.getFunctions();
//	for (int i = 0; i < functions.length; i++){
//		if (SimulationSymbolTable.isFunctionSaved(functions[i])){
//			Expression exp1 = new Expression(functions[i].getExpression());
//			try {
//				exp1 = simSymbolTable.substituteFunctions(exp1).flatten();
//			} catch (MathException e) {
//				e.printStackTrace(System.out);
//				throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
//			} catch (ExpressionException e) {
//				e.printStackTrace(System.out);
//				throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
//			}
//			Domain domain = null;
//			AnnotatedFunction af = new AnnotatedFunction(functions[i].getName(), exp1, domain, "", VariableType.NONSPATIAL, FunctionCategory.PREDEFINED);
//			funcList.add(af);
//		}
//	}
	return funcList;
}

}
