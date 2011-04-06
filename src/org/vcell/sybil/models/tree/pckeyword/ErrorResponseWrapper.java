package org.vcell.sybil.models.tree.pckeyword;

/*   ErrorResponseWrapper  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Wrapper for a tree node with a PCKeywordResponse
 */

import org.vcell.sybil.util.http.pathwaycommons.search.Error;
import org.vcell.sybil.util.http.pathwaycommons.search.PCErrorResponse;

public class ErrorResponseWrapper extends ResponseWrapper {

	public ErrorResponseWrapper(PCErrorResponse response) {
		super(response);
		Error error = response.error();
		append("Code: " + error.code());
		append("Message: " + error.msg());
		append("Details: " + error.details());
	}
	
	@Override
	public String toString() {
		Error error = data().error();
		return "Error " + error.code() + ": " + error.msg();
	}
	
	@Override
	public PCErrorResponse data() { return (PCErrorResponse) super.data(); }

}
