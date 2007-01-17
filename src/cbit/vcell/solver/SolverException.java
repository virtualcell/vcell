package cbit.vcell.solver;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The class <code>SolverException</code> are a form of <code>Exception</code>
 * that indicates a solver condition that a reasonable application might
 * want to catch.
 * Creation date: (4/18/00 12:33:33 AM)
 * @author: John Wagner
 */
public class SolverException extends Exception implements java.io.Serializable {
/**
 * Constructs a <code>SolverException</code> with no specified detail message. 
 */
public SolverException() {
	super();
}
/**
 * Constructs a <code>SolverException</code> with the specified detail message. 
 *
 * @param   s   the detail message.
 */
public SolverException(String s) {
	super(s);
}
}
