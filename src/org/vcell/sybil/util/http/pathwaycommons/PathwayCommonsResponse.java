package org.vcell.sybil.util.http.pathwaycommons;

/*   PathwayCommonsResponse  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Response to a web request from Pathway Commons
 */

public class PathwayCommonsResponse {

	protected PathwayCommonsRequest request;
	
	public PathwayCommonsResponse(PathwayCommonsRequest request) {
		this.request = request;
	}
	
	public PathwayCommonsRequest request() { return request; }
	
}
