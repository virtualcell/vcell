package org.vcell.rest.common;

import org.json.JSONException;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface OptimizationRunResource {

	@Get("json")
	public JsonRepresentation get_json();

	/**
	 * runs the optimization problem and redirects to the solution.
	 */
	@Post
	public Representation run(Representation OptRunJson) throws JSONException;
		
}
