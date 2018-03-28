package org.vcell.vcellij.api;

public class SimulationStatus{
	
	private SimulationState simState;
	public SimulationStatus(SimulationState simState) {
		super();
		this.simState = simState;
	}


	public SimulationState getSimState() {
		return simState;
	}

	public void setSimState(SimulationState simState) {
		this.simState = simState;
	}
	
}

