package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

public class Control extends InteractionImpl {

	private Interaction controlledInteraction;

	private Pathway controlledPathway;

	private String controlType;

	private ArrayList<Pathway> pathwayControllers = new ArrayList<Pathway>();

	public Interaction getControlledInteraction() {
		return controlledInteraction;
	}

	public Pathway getControlledPathway() {
		return controlledPathway;
	}

	public String getControlType() {
		return controlType;
	}

	public ArrayList<Pathway> getPathwayControllers() {
		return pathwayControllers;
	}

	public void setControlledInteraction(Interaction controlledInteraction) {
		this.controlledInteraction = controlledInteraction;
	}
	public void setControlledPathway(Pathway controlledPathway) {
		this.controlledPathway = controlledPathway;
	}
	public void setControlType(String controlType) {
		this.controlType = controlType;
	}
	public void setPathwayControllers(ArrayList<Pathway> pathwayControllers) {
		this.pathwayControllers = pathwayControllers;
	}
	
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy,concreteObject);
		for (int i=0;i<pathwayControllers.size();i++){
			Pathway pathway = pathwayControllers.get(i);
			if (pathway == objectProxy){
				pathwayControllers.set(i, (Pathway)concreteObject);
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObject(sb,"controlledInteraction",controlledInteraction,level);
		printObject(sb,"controlledPathway",controlledPathway,level);
		printString(sb,"controlType",controlType,level);
		printObjects(sb,"pathwayControllers",pathwayControllers,level);
//		printObjects(sb,"physicalControllers",physicalControllers,level);
	}

}
