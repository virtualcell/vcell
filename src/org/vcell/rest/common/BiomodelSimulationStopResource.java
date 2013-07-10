package org.vcell.rest.common;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public interface BiomodelSimulationStopResource {

	/**
	 * Returns the list of BioModels accessible to this user
	 */
	@Post
	public Representation stop();
	
}
