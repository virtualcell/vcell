package org.vcell.solver.comsol.model;

public class VCCTransientStudyFeature extends VCCStudyFeature {
	public final String startingTime;
	public final String timeStep;
	public final String endTime;
	public VCCTransientStudyFeature(String name, String startingTime, String timeStep, String endTime) {
		super(name, VCCStudyFeature.Type.Transient);
		this.startingTime = startingTime;
		this.timeStep = timeStep;
		this.endTime = endTime;
	}

}
