package org.vcell.sybil.util.http.pathwaycommons.search;

/*   PCTextModelResponse  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Response from a web request from Pathway Commons consisting of text converted to a Jena model
 */

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import com.hp.hpl.jena.rdf.model.Model;

public class PCTextModelResponse extends PCTextResponse {

	protected Model model;
	
	public PCTextModelResponse(PathwayCommonsRequest requestNew, String textNew, Model modelNew) {
		super(requestNew, textNew);
		model = modelNew;
	}
	
	public Model model() { return model; }
	
}
