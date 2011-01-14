package org.vcell.pathway;

import java.util.ArrayList;

public class PathwayStep extends BioPaxObjectImpl implements UtilityClass {
	private ArrayList<PathwayStep> nextStep = new ArrayList<PathwayStep>();
	private ArrayList<Interaction> stepProcessInteraction = new ArrayList<Interaction>();
	private ArrayList<Pathway> stepProcessPathway = new ArrayList<Pathway>();
	private ArrayList<Evidence> evidence = new ArrayList<Evidence>();
	
	public ArrayList<Evidence> getEvidence() {
		return evidence;
	}
	public void setEvidence(ArrayList<Evidence> evidence) {
		this.evidence = evidence;
	}
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
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "nextStep",nextStep,level);
		printObjects(sb, "stepProcessInteraction",stepProcessInteraction,level);
		printObjects(sb, "stepProcessPathway",stepProcessPathway,level);
		printObjects(sb, "evidence",evidence,level);
	}
}
