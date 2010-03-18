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