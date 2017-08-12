package org.vcell.rest.common;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface SimulationResource {

	/**
	 * Returns the list of BioModels accessible to this user
	 */
	@Get("json")
	public SimulationRepresentation get_json();
	
	@Get("html")
	public Representation get_html();
	
}
