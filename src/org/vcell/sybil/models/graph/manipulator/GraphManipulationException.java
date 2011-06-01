/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.graph.manipulator;

/*   GraphManipulationException  --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   Generic interface for object executing graph manipulations.
 */

public class GraphManipulationException extends Exception {
	public GraphManipulationException(String message) { super(message); }
	public GraphManipulationException(Throwable throwable) { super(throwable); }
	
	public GraphManipulationException(String message, Throwable throwable) { 
		super(message, throwable); 
	}
	
	private static final long serialVersionUID = 6652963414971723066L;
}
