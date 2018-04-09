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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ConfigurationException;

import cbit.vcell.math.Constant;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.VolVariable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.ode.ODEStateVariable;
import cbit.vcell.solver.ode.SensStateVariable;
import cbit.vcell.solver.ode.SensVariable;
import cbit.vcell.solver.ode.StateVariable;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solver.server.SolverStatus;
/**
 * Insert the type's description here.
 * Creation date: (6/26/2001 2:48:23 PM)
 * @author: Ion Moraru
 */
public abstract class AbstractSolver implements Solver, SimDataConstants {
	public static final Logger lg = LogManager.getLogger(AbstractSolver.class);

	private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
	private SolverStatus fieldSolverStatus = new SolverStatus(SolverStatus.SOLVER_READY, SimulationMessage.MESSAGE_SOLVER_READY);
	private File saveDirectory = null;
	private boolean saveEnabled = true;
	protected final SimulationTask simTask;
	public static boolean bMakeUserDirs = true;

/**
 * AbstractSolver constructor comment.
 */
public AbstractSolver(SimulationTask simTask, File directory) throws SolverException {

	this.simTask = simTask;
	if (!directory.exists() && !simTask.getSimulation().getSolverTaskDescription().isParallel()){
		if (bMakeUserDirs && !directory.exists()) {
			if (!directory.mkdirs()){
				String msg = "could not create directory "+directory;
				if (lg.isWarnEnabled()) lg.warn(msg);
				throw new ConfigurationException(msg);
			}
			//
			// directory create from container (possibly) as root, make this user directory accessible from user "vcell" 
			//
			directory.setWritable(true,false);
			directory.setExecutable(true,false);
			directory.setReadable(true,false);
		}
	}		 
	this.saveDirectory = directory;
//	ArrayList<Issue> issueList = new ArrayList<Issue>();
//	IssueContext issueContext = new IssueContext(ContextType.Simulation,simTask.getSimulation(),null);
//	simTask.getSimulation().getMathDescription().gatherIssues(issueContext,issueList);
//	simTask.getSimulation().gatherIssues(issueContext,issueList);
//	for (Issue issue : issueList) {
//		if (issue.getSeverity() == Issue.SEVERITY_ERROR){
//			throw new SolverException(issue.getMessage());
//		}
//	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:18:16 PM)
 * @param listener cbit.vcell.solver.SolverListener
 */
public synchronized void addSolverListener(cbit.vcell.solver.server.SolverListener listener) {
	listenerList.add(cbit.vcell.solver.server.SolverListener.class, listener);
}


/**
 * Method to support listener events.
 */
protected void fireSolverAborted(SimulationMessage message) {
	// Create event
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_ABORTED, message, getProgress(), getCurrentTime(), message.getHtcJobId());
	VCMongoMessage.sendSolverEvent(event);
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==SolverListener.class) {
		((SolverListener)listeners[i+1]).solverAborted(event);
	    }	       
	}
}


/**
 * Method to support listener events.
 */
protected void fireSolverFinished() {
	// Create event
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_FINISHED, SimulationMessage.MESSAGE_SOLVEREVENT_FINISHED, getProgress(), getCurrentTime(), null);
	VCMongoMessage.sendSolverEvent(event);
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==SolverListener.class) {
		((SolverListener)listeners[i+1]).solverFinished(event);
	    }	       
	}
}


/**
 * Method to support listener events.
 */
protected void fireSolverPrinted(double timepoint) {
	// Create event
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_PRINTED, SimulationMessage.solverPrinted(timepoint), getProgress(), timepoint, null);
	VCMongoMessage.sendSolverEvent(event);
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==SolverListener.class) {
		((SolverListener)listeners[i+1]).solverPrinted(event);
	    }	       
	}
}


/**
 * Method to support listener events.
 */
protected void fireSolverProgress(double progress) {
	// Create event
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_PROGRESS, SimulationMessage.solverProgress(progress), progress, getCurrentTime(), null);
	VCMongoMessage.sendSolverEvent(event);
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==SolverListener.class) {
		((SolverListener)listeners[i+1]).solverProgress(event);
	    }	       
	}
}


/**
 * Method to support listener events.
 */
protected void fireSolverStarting(SimulationMessage message) {
	// Create event
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_STARTING, message, 0, 0, message.getHtcJobId());
	VCMongoMessage.sendSolverEvent(event);
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==SolverListener.class) {
		((SolverListener)listeners[i+1]).solverStarting(event);
	    }	       
	}
}


/**
 * Method to support listener events.
 */
protected void fireSolverStopped() {
	// Create event
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_STOPPED, SimulationMessage.solverStopped("stopped by user"), getProgress(), getCurrentTime(), null);
	VCMongoMessage.sendSolverEvent(event);
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==SolverListener.class) {
		((SolverListener)listeners[i+1]).solverStopped(event);
	    }	       
	}
}


/**
 * get name for solver files, without extension 
 */
protected final String getBaseName() {
	return new File(getSaveDirectory(), simTask.getSimulationJob().getSimulationJobID()).getPath();
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/01 2:51:46 PM)
 * @return double
 */
public abstract double getCurrentTime();
protected abstract void initialize() throws SolverException;

/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param identifier java.lang.String
 */
public Expression getFunctionSensitivity(Expression funcExpr, Constant constant, StateVariable stateVariables[]) throws ExpressionException {
	if (stateVariables==null || stateVariables.length == 0) {
		return null;
	}
	// this uses the chain rule
	//   d F(x)   del F(x)        del F(x)   del xi
	//   ------ = -------- + Sum (-------- * ------)
	//    d k      del k      i    del xi    del k
	//            explicit     via state variables
	//            dependence

	//
	// collect the explicit term
	//
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
	
	Expression sensFuncExp = funcExpr.differentiate(constant.getName());
	sensFuncExp.bindExpression(simSymbolTable);
	sensFuncExp = sensFuncExp.flatten();
	
	for (int i = 0; i < stateVariables.length; i++) {
		if (stateVariables[i] instanceof ODEStateVariable) {
			ODEStateVariable sv = (ODEStateVariable) stateVariables[i];
			VolVariable volVar = (VolVariable) sv.getVariable();
			//
			// get corresponding sensitivity state variable
			//
			SensStateVariable sensStateVariable = null;
			for (int j = 0; j < stateVariables.length; j++){
				if (stateVariables[j] instanceof SensStateVariable && 
					((SensVariable)((SensStateVariable)stateVariables[j]).getVariable()).getVolVariable().getName().equals(volVar.getName()) &&
					((SensStateVariable)stateVariables[j]).getParameter().getName().equals(constant.getName())){
					sensStateVariable = (SensStateVariable)stateVariables[j];
				}	
			}
			if (sensStateVariable == null){
				System.out.println("sens of "+volVar.getName()+" wrt "+constant.getName()+" is not found");
				return null;
			}

			//
			// get coefficient of proportionality   (e.g.  A = total + b*B + c*C ... gives dA/dK = b*dB/dK + c*dC/dK)
			//
			Expression coeffExpression = funcExpr.differentiate(volVar.getName());
			coeffExpression.bindExpression(simSymbolTable);
			coeffExpression = MathUtilities.substituteFunctions(coeffExpression,simSymbolTable);
			coeffExpression.bindExpression(simSymbolTable);
			coeffExpression = coeffExpression.flatten();
			sensFuncExp = Expression.add(sensFuncExp,Expression.mult(coeffExpression,new Expression(sensStateVariable.getVariable().getName())));
		}
	}

	return new Expression(sensFuncExp);
}


/**
 * Insert the method's description here.
 * Creation date: (6/26/2001 3:42:59 PM)
 * @return int
 */
public int getJobIndex() {
	return getSimulationJob().getJobIndex();
}

/**
 * Insert the method's description here.
 * Creation date: (6/26/2001 3:42:59 PM)
 * @return int
 */
public int getTaskID() {
	return simTask.getTaskID();
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/01 2:36:57 PM)
 * @return double
 */
public abstract double getProgress();


/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 10:52:44 AM)
 * @return java.io.File
 */
protected final java.io.File getSaveDirectory() {
	return saveDirectory;
}


/**
 * Gets the running property (boolean) value.
 * @return The running property value.
 */
public final SolverStatus getSolverStatus() {
	return (fieldSolverStatus);
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 10:53:53 AM)
 * @return boolean
 */
public final boolean isSaveEnabled() {
	return saveEnabled;
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:18:16 PM)
 * @param listener cbit.vcell.solver.SolverListener
 */
public synchronized void removeSolverListener(SolverListener listener) {
	listenerList.remove(SolverListener.class, listener);
}


/**
 * Gets the running property (boolean) value.
 * @return The running property value.
 */
protected final void setSolverStatus(SolverStatus solverStatus) {
	fieldSolverStatus = solverStatus;
}


public SimulationJob getSimulationJob() {
	return simTask.getSimulationJob();
}


/**
 * no-op
 */
@Override
public void setUnixMode() {
	
}

}
