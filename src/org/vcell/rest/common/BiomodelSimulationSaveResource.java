package org.vcell.rest.common;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public interface BiomodelSimulationSaveResource {

	/**
	 * starts the simulation
	 */
	@Post
	public Representation save();
		
}
