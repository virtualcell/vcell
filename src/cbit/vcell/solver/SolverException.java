/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;

/**
 * The class <code>SolverException</code> are a form of <code>Exception</code>
 * that indicates a solver condition that a reasonable application might
 * want to catch.
 * Creation date: (4/18/00 12:33:33 AM)
 * @author: John Wagner
 */
@SuppressWarnings("serial")
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
/**
 * see {@link Exception#Exception(String, Throwable)}
 * @param message
 * @param e
 */
public SolverException(String message, Exception e) {
	super(message,e);
}
}
