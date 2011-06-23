package org.vcell.sybil.util.http.pathwaycommons.search;

/*   PCTextModelResponse  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Response from a web request from Pathway Commons consisting of text converted to a Jena model
 */

import org.openrdf.model.Graph;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;

public class PCTextModelResponse extends PCTextResponse {

	protected Graph model;
	
	public PCTextModelResponse(PathwayCommonsRequest requestNew, String textNew, Graph modelNew) {
		super(requestNew, textNew);
		model = modelNew;
	}
	
	public Graph model() { return model; }
	
}
