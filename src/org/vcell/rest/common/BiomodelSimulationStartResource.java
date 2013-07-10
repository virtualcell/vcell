package org.vcell.rest.common;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public interface BiomodelSimulationStartResource {

	/**
	 * starts the simulation
	 */
	@Post
	public Representation start();
		
}
