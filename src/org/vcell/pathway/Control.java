package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;
import org.vcell.sybil.util.text.StringUtil;

public class Control extends InteractionImpl {

	private Interaction controlledInteraction;
	private Pathway controlledPathway;

	private String controlType;

	private ArrayList<Pathway> pathwayControllers = new ArrayList<Pathway>();
	private ArrayList<PhysicalEntity> physicalControllers = new ArrayList<PhysicalEntity>();

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
	public ArrayList<PhysicalEntity> getPhysicalControllers() {
		return physicalControllers;
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
	public void setPhysicalControllers(ArrayList<PhysicalEntity> physicalControllers) {
		this.physicalControllers = physicalControllers;
	}
	
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy,concreteObject);
		for (int i=0;i<pathwayControllers.size();i++){
			Pathway pathway = pathwayControllers.get(i);
			if (pathway == objectProxy){
				pathwayControllers.set(i, (Pathway)concreteObject);
			}
		}
		if(controlledInteraction == objectProxy) {
			if(concreteObject instanceof Interaction) {
				controlledInteraction = (Interaction) concreteObject;
			} else if(concreteObject instanceof Pathway) {
				controlledInteraction = null;
				controlledPathway = (Pathway) concreteObject;
			} else {
				throwObjectNeitherInteractionNorPathwayException(concreteObject);
			}
		}
	}

	protected void throwObjectNeitherInteractionNorPathwayException(BioPaxObject concreteObject) {
		String thisName = toString();
		if(getName() != null && getName().size() > 0) {
			thisName = "[" + StringUtil.concat(getName(), ";");
		}
		String controlledObjectName = concreteObject.toString();
		if(concreteObject instanceof Entity) {
			Entity controlledEntity = (Entity) concreteObject;
			if(controlledEntity.getName() != null && controlledEntity.getName().size() > 0) {
				controlledObjectName = "[" + StringUtil.concat(controlledEntity.getName(), ";");
			}
		}
		throw new RuntimeException("For conversion " + thisName +
				", the controlled object " + controlledObjectName + 
				" fails to be either Interaction or Pathway");
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObject(sb,"controlledInteraction",controlledInteraction,level);
		printObject(sb,"controlledPathway",controlledPathway,level);
		printString(sb,"controlType",controlType,level);
		printObjects(sb,"pathwayControllers",pathwayControllers,level);
		printObjects(sb,"physicalControllers",physicalControllers,level);
	}

}
