package org.vcell.sbmlsim.api.common;

public class SimulationSpec{

	private double outputTimeStep;
	private double totalTime;
	public double getOutputTimeStep() {
		return outputTimeStep;
	}
	public void setOutputTimeStep(double outputTimeStep) {
		this.outputTimeStep = outputTimeStep;
	}
	public double getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

}

