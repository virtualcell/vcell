package cbit.vcell.solver;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Event used by solvers to indicate progress. These events
 * are sent at solver milestones, for example, whenever the
 * solver prints data to its result set, takes some kind of
 * step, or terminates nominally.  Each solver will throw all
 * or some of these events depending on how it solves problems.
 * Creation date: (8/16/2000 11:10:33 PM)
 * @author: John Wagner
 */
public class SolverEvent extends java.util.EventObject implements java.io.Serializable {
	//  Aborted indicates some kind of exception occurred, either
	//  something really serious (like thread death) or something
	//  more benign, like the time step...
	public final static int SOLVER_ABORTED  = 1;
	public final static int SOLVER_FINISHED = 2;
	public final static int SOLVER_PRINTED  = 3;
	public final static int SOLVER_PROGRESS  = 6;
	public final static int SOLVER_STARTING  = 4;
	public final static int SOLVER_STOPPED  = 5;
	private int fieldType;
	private SimulationMessage fieldSimulationMessage = null;
	private double fieldTimePoint = -1;
	private double fieldProgress = -1;
/**
 * IntegratorEvent constructor comment.
 * @param source java.lang.Object
 */
public SolverEvent(java.lang.Object source, int type, SimulationMessage simulationMessage, double progress, double timePoint) {
	super(source);
	this.fieldSimulationMessage = simulationMessage;
	this.fieldType = type;
	this.fieldProgress = progress;
	this.fieldTimePoint = timePoint;
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/2001 9:50:17 PM)
 * @return java.lang.String
 */
public SimulationMessage getSimulationMessage() {
	return fieldSimulationMessage;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/01 4:19:06 PM)
 * @return double
 */
public double getProgress() {
	return fieldProgress;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/01 4:18:45 PM)
 * @return double
 */
public double getTimePoint() {
	return fieldTimePoint;
}
/**
 * IntegratorEvent constructor comment.
 * @param source java.lang.Object
 */
public int getType() {
	return fieldType;
}
/**
 * Insert the method's description here.
 * Creation date: (8/17/01 11:00:16 AM)
 * @return java.lang.String
 */
public String toString() {
	return "SolverEvent: type="+getType()+", msg="+getSimulationMessage()+", progress="+getProgress()+", timepoint="+getTimePoint();
}
}
