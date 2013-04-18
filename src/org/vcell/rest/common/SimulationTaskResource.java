package org.vcell.rest.common;

import org.restlet.resource.Get;
import org.restlet.resource.Put;

public interface SimulationTaskResource {

	@Get("json")
	public SimulationTaskRepresentation retrieve();
	
	@Put("json")
	public void store(SimulationTaskRepresentation simulation);
	
}
