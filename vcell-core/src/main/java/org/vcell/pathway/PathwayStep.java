/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
		
		for (int i=0; i<nextStep.size(); i++) {
			PathwayStep thing = nextStep.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						nextStep.set(i, (PathwayStep)concreteObject);
					}
				}
			}
		}
		for (int i=0;i<stepProcessInteraction.size();i++){
			Interaction thing = stepProcessInteraction.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						if(concreteObject instanceof Interaction){
							stepProcessInteraction.set(i, (Interaction)concreteObject);
						}else{
							stepProcessInteraction.remove(i);
						}
					}
				}
			}
		}
		for (int i=0;i<stepProcessPathway.size();i++){
			Pathway thing = stepProcessPathway.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						if(concreteObject instanceof Pathway){
							stepProcessPathway.set(i, (Pathway)concreteObject);
						}else{
							stepProcessPathway.remove(i);
						}
					}
				}
			}
		}
		for (int i=0; i<evidence.size(); i++) {
			Evidence thing = evidence.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						evidence.set(i, (Evidence)concreteObject);
					}
				}
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
