package org.vcell.rest.common;

import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.resource.Get;

public interface BiomodelDiagramResource {

	/**
	 * Returns the list of BioModels accessible to this user
	 */

	@Get("image/png")
	public ByteArrayRepresentation get_png();
	
}
