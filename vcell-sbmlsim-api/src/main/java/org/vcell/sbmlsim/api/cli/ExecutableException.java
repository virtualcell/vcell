package org.vcell.sbmlsim.api.cli;

public class ExecutableException extends Exception {
/**
 * ExecutableException constructor comment.
 */
public ExecutableException() {
	super();
}


/**
 * ExecutableException constructor comment.
 * @param s java.lang.String
 */
public ExecutableException(String s) {
	super(s);
}

public ExecutableException(String msg, Throwable t) {
	super(msg,t);
}
}
