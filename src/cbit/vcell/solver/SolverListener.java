package cbit.vcell.solver;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Listener interface for solver events.
 * Creation date: (8/16/2000 11:10:33 PM)
 * @author: John Wagner
 */
public interface SolverListener extends java.util.EventListener {
/**
 * Invoked when the solver aborts a calculation (abnormal termination).
 * @param event indicates the solver and the event type
 */
void solverAborted(SolverEvent event);
/**
 * Invoked when the solver finishes a calculation (normal termination).
 * @param event indicates the solver and the event type
 */
void solverFinished(SolverEvent event);
/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
void solverPrinted(SolverEvent event);
/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
void solverProgress(SolverEvent event);
/**
 * Invoked when the solver begins a calculation.
 * @param event indicates the solver and the event type
 */
void solverStarting(SolverEvent event);
/**
 * Invoked when the solver stops a calculation, usually because
 * of a user-initiated stop call.
 * @param event indicates the solver and the event type
 */
void solverStopped(SolverEvent event);
}
