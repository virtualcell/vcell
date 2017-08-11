package org.vcell.rest.common;

import org.restlet.resource.Get;

public interface SimulationTaskResource {

	@Get("json")
	public SimulationTaskRepresentation get_json();
	
}
