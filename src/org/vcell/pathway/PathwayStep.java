package org.vcell.pathway;

import java.util.ArrayList;

public class PathwayStep implements UtilityClass {
	private ArrayList<PathwayStep> nextStep;
	private ArrayList<Interaction> stepProcessInteraction;
	private ArrayList<Pathway> stepProcessPathway;
	public ArrayList<PathwayStep> getNextStep() {
		return nextStep;
	}
	public ArrayList<Interaction> getStepProcessInteraction() {
		return stepProcessInteraction;
	}
	public ArrayList<Pathway> getStepProcessPathway() {
		return stepProcessPathway;
	}
	public void setNextStep(ArrayList<PathwayStep> nextStep) {
		this.nextStep = nextStep;
	}
	public void setStepProcessInteraction(
			ArrayList<Interaction> stepProcessInteraction) {
		this.stepProcessInteraction = stepProcessInteraction;
	}
	public void setStepProcessPathway(ArrayList<Pathway> stepProcessPathway) {
		this.stepProcessPathway = stepProcessPathway;
	}
}
