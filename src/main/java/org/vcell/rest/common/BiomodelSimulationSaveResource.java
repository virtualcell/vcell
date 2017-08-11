package org.vcell.rest.common;

import org.json.JSONException;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;

public interface BiomodelSimulationSaveResource {

	/**
	 * saves the simulation (within a new BioModel instance) and redirects to that new simulation.
	 */
	@Post
	public void save(JsonRepresentation jsonOverrides) throws JSONException;
		
}
