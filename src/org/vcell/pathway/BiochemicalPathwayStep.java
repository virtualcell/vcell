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

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObject(sb,"stepConversion",stepConversion,level);
		printString(sb,"stepDirection",stepDirection,level);
	}
}
