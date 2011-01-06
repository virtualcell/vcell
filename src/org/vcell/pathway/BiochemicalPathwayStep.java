package org.vcell.pathway;

public class BiochemicalPathwayStep extends PathwayStep {
	private Conversion stepConversion;
	private String stepDirection;
	public Conversion getStepConversion() {
		return stepConversion;
	}
	public String getStepDirection() {
		return stepDirection;
	}
	public void setStepConversion(Conversion stepConversion) {
		this.stepConversion = stepConversion;
	}
	public void setStepDirection(String stepDirection) {
		this.stepDirection = stepDirection;
	}
}
