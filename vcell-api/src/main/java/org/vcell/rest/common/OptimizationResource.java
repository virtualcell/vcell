package org.vcell.rest.common;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface OptimizationResource {

	/**
	 * Returns the list of BioModels accessible to this user
	 */
	@Get("json")
	public JsonRepresentation get_json();
	
	@Get("html")
	public Representation get_html();
	
}
