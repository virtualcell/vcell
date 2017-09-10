package org.vcell.rest.common;

import org.json.JSONException;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public interface OptimizationRunResource {

	/**
	 * runs the optimization problem and redirects to the solution.
	 */
	@Post
	public Representation run(Representation OptRunJson) throws JSONException;
		
}
