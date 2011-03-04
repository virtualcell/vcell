package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

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
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		for (int i=0; i<nextStep.size(); i++) {
			PathwayStep thing = nextStep.get(i);
			if(thing == objectProxy) {
				nextStep.set(i, (PathwayStep)concreteObject);
			}
		}
		for (int i=0;i<stepProcessInteraction.size();i++){
			Interaction thing = stepProcessInteraction.get(i);
			if (thing == objectProxy && concreteObject instanceof Interaction){
				stepProcessInteraction.set(i, (Interaction)concreteObject);
			} else if(thing == objectProxy && !(concreteObject instanceof Interaction)) {
				stepProcessInteraction.remove(i);
			}
		}
		for (int i=0;i<stepProcessPathway.size();i++){
			Pathway thing = stepProcessPathway.get(i);
			if (thing == objectProxy && concreteObject instanceof Pathway){
				stepProcessPathway.set(i, (Pathway)concreteObject);
			} else if (thing == objectProxy && !(concreteObject instanceof Pathway)){
				stepProcessPathway.remove(i);
			}
		}
		for (int i=0; i<evidence.size(); i++) {
			Evidence thing = evidence.get(i);
			if(thing == objectProxy) {
				evidence.set(i, (Evidence)concreteObject);
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "nextStep",nextStep,level);
		printObjects(sb, "stepProcessInteraction",stepProcessInteraction,level);
		printObjects(sb, "stepProcessPathway",stepProcessPathway,level);
		printObjects(sb, "evidence",evidence,level);
	}
}
