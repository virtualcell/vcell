package cbit.vcell.solvers;


/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.File;

import org.vcell.util.ConfigurationException;
import org.vcell.util.SessionLog;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.VolVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.Solver;
import cbit.vcell.solver.SolverEvent;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverListener;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.ode.ODEStateVariable;
import cbit.vcell.solver.ode.SensStateVariable;
import cbit.vcell.solver.ode.SensVariable;
import cbit.vcell.solver.ode.StateVariable;
/**
 * Insert the type's description here.
 * Creation date: (6/26/2001 2:48:23 PM)
 * @author: Ion Moraru
 */
public abstract class AbstractSolver implements Solver, SimDataConstants {
	private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
	private org.vcell.util.SessionLog fieldSessionLog = null;
	private SolverStatus fieldSolverStatus = new SolverStatus(SolverStatus.SOLVER_READY, SimulationMessage.MESSAGE_SOLVER_READY);
	private File saveDirectory = null;
	private boolean saveEnabled = true;
	protected final SimulationJob simulationJob;

/**
 * AbstractSolver constructor comment.
 */
public AbstractSolver(SimulationJob simulationJob, File directory, SessionLog sessionLog) throws SolverException {

	this.simulationJob = simulationJob;
	this.fieldSessionLog = sessionLog;
	if (!directory.exists()){
		if (!directory.mkdirs()){
			String msg = "could not create directory "+directory;
			sessionLog.alert(msg);
			throw new ConfigurationException(msg);
		}
	}		 
		this.saveDirectory = directory;
	if (!simulationJob.getSimulation().getIsValid()) {
		throw new SolverException("Simulation is not valid: "+simulationJob.getSimulation().getWarning());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:18:16 PM)
 * @param listener cbit.vcell.solver.SolverListener
 */
public synchronized void addSolverListener(cbit.vcell.solver.SolverListener listener) {
	listenerList.add(cbit.vcell.solver.SolverListener.class, listener);
}


/**
 * Method to support listener events.
 */
protected void fireSolverAborted(SimulationMessage message) {
	// Create event
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_ABORTED, message, getProgress(), getCurrentTime());
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
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_FINISHED, SimulationMessage.MESSAGE_SOLVEREVENT_FINISHED, getProgress(), getCurrentTime());
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
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_PRINTED, SimulationMessage.solverPrinted(timepoint), getProgress(), timepoint);
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
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_PROGRESS, SimulationMessage.solverProgress(progress), progress, getCurrentTime());
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
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_STARTING, message, 0, 0);
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
	SolverEvent event = new SolverEvent(this, SolverEvent.SOLVER_STOPPED, SimulationMessage.solverStopped("stopped by user"), getProgress(), getCurrentTime());
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
 * getCurrentTime method comment.
 */
protected final String getBaseName() {
	return (new File(getSaveDirectory(), simulationJob.getSimulationJobID()).getPath());
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/01 2:51:46 PM)
 * @return double
 */
public abstract double getCurrentTime();
protected abstract void initialize() throws SolverException;

public final FieldDataIdentifierSpec[] getFieldDataIdentifierSpecs() {
	return simulationJob.getFieldDataIdentifierSpecs();
}

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
	SimulationSymbolTable simSymbolTable = simulationJob.getSimulationSymbolTable();
	
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
	return simulationJob.getJobIndex();
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
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 */
protected final SessionLog getSessionLog() {
	return (fieldSessionLog);
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
 * Creation date: (6/6/2001 10:57:51 AM)
 * @return boolean
 * @param function cbit.vcell.math.Function
 */
public static boolean isFunctionSaved(cbit.vcell.math.Function function) {
	String name = function.getName();
	if (!name.startsWith("SurfToVol_") && 
		!name.startsWith("VolFract_") && 
		!name.startsWith("KFlux_") && 
		!name.startsWith("Kflux_") &&
		!name.endsWith("_init") && 
		!name.endsWith("_total") &&
		!name.equals(SimDataConstants.PSF_FUNCTION_NAME)){
		return true;
	}else{
		return false;
	}
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
	return simulationJob;
}

}