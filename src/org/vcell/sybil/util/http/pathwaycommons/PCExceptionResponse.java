package org.vcell.sybil.util.http.pathwaycommons;

/*   Response  --- by Oliver Ruebenacker, UCHC --- March to December 2009
 *   Response from a web request from Pathway Commons consisting of an exception
 */

public class PCExceptionResponse extends PathwayCommonsResponse {

	protected Exception exception;
	
	public PCExceptionResponse(PathwayCommonsRequest requestNew, Exception exceptionNew) {
		super(requestNew);
		exception = exceptionNew;
	}
	
	public Exception exception() { return exception; }

}
