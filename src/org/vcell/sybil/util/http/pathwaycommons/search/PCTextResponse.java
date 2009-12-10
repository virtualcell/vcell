package org.vcell.sybil.util.http.pathwaycommons.search;

/*   PCTextResponse  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Response from a web request from Pathway Commons consisting of text
 */

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;

public class PCTextResponse extends PathwayCommonsResponse {

	protected String text;
	
	public PCTextResponse(PathwayCommonsRequest requestNew, String textNew) {
		super(requestNew);
		text = textNew;
	}
	
	public String text() { return text; }

}
