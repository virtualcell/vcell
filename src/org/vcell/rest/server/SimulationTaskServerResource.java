package org.vcell.rest.server;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.vcell.rest.common.SimulationTaskRepresentation;
import org.vcell.rest.common.SimulationTaskResource;

public class SimulationTaskServerResource extends WadlServerResource implements SimulationTaskResource {

	private String simTaskId;
	
	
    @Override
    protected RepresentationInfo describe(MethodInfo methodInfo,
            Class<?> representationClass, Variant variant) {
        RepresentationInfo result = new RepresentationInfo(variant);
        result.setReference("simulationTask");
        return result;
    }

    /**
     * Retrieve the account identifier based on the URI path variable
     * "accountId" declared in the URI template attached to the application
     * router.
     */
    @Override
    protected void doInit() throws ResourceException {
        String simTaskIdAttribute = getAttribute("simTaskId");

        if (simTaskIdAttribute != null) {
            this.simTaskId = simTaskIdAttribute;
            setName("Resource for simulation task \"" + this.simTaskId + "\"");
            setDescription("The resource describing the simulation task id \"" + this.simTaskId + "\"");
        } else {
            setName("simulation task resource");
            setDescription("The resource describing a simulation task");
        }
    }
	
	
	@Override
	public SimulationTaskRepresentation retrieve() {
		SimulationTaskRepresentation sim = new SimulationTaskRepresentation();
		sim.simKey="123";
		return sim;
	}

	@Override
	public void store(SimulationTaskRepresentation simulation) {
		// TODO Auto-generated method stub

	}

}
