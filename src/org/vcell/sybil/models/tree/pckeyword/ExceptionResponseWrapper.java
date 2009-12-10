package org.vcell.sybil.models.tree.pckeyword;

/*   ErrorResponseWrapper  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Wrapper for a tree node with a PCKeywordResponse
 */

import org.vcell.sybil.util.http.pathwaycommons.PCExceptionResponse;

public class ExceptionResponseWrapper extends ResponseWrapper {

	public ExceptionResponseWrapper(PCExceptionResponse response) {
		super(response);
		Exception exception = response.exception();
		append("Class: " + exception.getClass());
		append("Message: " + exception.getMessage());
		Throwable cause = exception.getCause();
		if(cause != null) {
			append("Cause class: " + cause.getClass());			
			append("Cause message: " + cause.getMessage());			
		}
	}
	
	public String toString() {
		Exception exception = data().exception();
		return "Exception " + exception.getClass().getSimpleName() + ": " + exception.getMessage();
	}
	
	public PCExceptionResponse data() { return (PCExceptionResponse) super.data(); }

}
