package cbit.vcell.client.desktop.biomodel;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Parameter;

public class ApplicationParameter {
	private SimulationContext simulationContext;
	private Parameter parameter;
	public ApplicationParameter(SimulationContext simulationContext, Parameter parameter) {
		super();
		this.simulationContext = simulationContext;
		this.parameter = parameter;
	}
	public final SimulationContext getSimulationContext() {
		return simulationContext;
	}
	public final Parameter getParameter() {
		return parameter;
	}	
}
