package org.vcell.sybil.util.http.pathwaycommons.search;

/*   Response  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Response from a web request using command search from Pathway Commons
 */

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;

public class PCEmptyResponse extends PathwayCommonsResponse {

	public PCEmptyResponse(PathwayCommonsRequest request) { 
		super(request);
	}
	
	
	@Override
	public String toString() {
		return "[Empty response]";
	}
}