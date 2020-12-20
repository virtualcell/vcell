package org.jlibsedml.execution;

import org.jlibsedml.SedMLError;


/**
 * Item containing information about a possible error execution of a  SED-ML task.<br>
 * This item is not to be confused with a {@link SedMLError} object, which holds information about the validity
 *  of a SED-ML document.
 * @author radams
 *
 */
public class ExecutionStatusElement {
    /**
     * Getter for any exception stored in this execution status report.
     * @return An {@link Exception}, or <code>null</code> if not found.
     */
	public Exception getExc() {
		return exc;
	}
	
	/**
	 * Getter for the message stored in this report object.
	 * @return A non-null <code>String</code>.
	 */
	public String getMessage() {
		return message;
	}
	
	/**
     * Getter for the error type stored in this report object.
     * @return A non-null <code>ExecutionStatusType</code>.
     */
	public ExecutionStatusType getType() {
		return type;
	}
	
	/**
	 * 
	 * @param exc An optional exception , can be <code>null</code>.
	 * @param message A non-null,non-empty human readable message.
	 * @param type The severity of the execution error.
	 * @throws IllegalArgumentException if <code>message == null</code> or <code>type == null</code>. 
	 */
	public ExecutionStatusElement(Exception exc, String message,
			ExecutionStatusType type) {
		super();
		if(message==null|| type==null){
			throw new IllegalArgumentException();
		}
		this.exc = exc;
		this.message = message;
		this.type = type;
	}
	
	/**
	 * An enumeration of possible execution error severities encountered during a simulation run.
	 * @author radams
	 *
	 */
	public enum ExecutionStatusType {
		INFO, ERROR;
	}
	private Exception exc;
	private String message;
	private ExecutionStatusType type;

}
