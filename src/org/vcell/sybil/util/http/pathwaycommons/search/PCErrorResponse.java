package org.vcell.sybil.util.http.pathwaycommons.search;

/*   Response  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Response from a web request using command search from Pathway Commons
 */

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;
import org.w3c.dom.Element;

public class PCErrorResponse extends PathwayCommonsResponse {

	protected Error error;
	
	public PCErrorResponse(PathwayCommonsRequest request, Element errorElement) { 
		super(request);
		if(errorElement != null) { error = new Error(errorElement); }
	}
	
	public Error error() { return error; }
	
	public String toString() {
		return "[Error response: \n" + (error != null ? "error=" + error.toString() + ";\n" : "") + "]\n";
	}
}