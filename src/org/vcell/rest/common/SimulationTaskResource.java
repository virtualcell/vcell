package org.vcell.rest.common;

import org.restlet.resource.Get;
import org.restlet.resource.Put;

public interface SimulationTaskResource {

	@Get("json")
	public SimulationTaskRepresentation get_json();
	
	@Put("json")
	public void put_json(SimulationTaskRepresentation simulation);
	
}
