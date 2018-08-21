package org.vcell.restopt.common;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;

public interface OptimizationResource {

	/**
	 * Returns the list of BioModels accessible to this user
	 */
	@Get("json")
	public JsonRepresentation get_json();
	
}
