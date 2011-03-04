package org.vcell.pathway;

import java.util.ArrayList;
import java.util.List;

import org.vcell.pathway.persistence.BiopaxProxy;
import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;
import org.vcell.sybil.util.text.StringUtil;

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
	public List<PhysicalEntity> getPhysicalControllers() {
		return getParticipantPhysicalEntities(InteractionParticipant.Type.CONTROLLER);
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
	public void addPhysicalController(PhysicalEntity physicalController) {
		addPhysicalEntityAsParticipant(physicalController, InteractionParticipant.Type.CONTROLLER);
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy,concreteObject);
		
		// we can have a combination of controllers
		for (int i=0;i<pathwayControllers.size();i++){
			Pathway controller = pathwayControllers.get(i);
			if (controller == objectProxy && concreteObject instanceof Pathway){
				pathwayControllers.set(i, (Pathway)concreteObject);
			} else if (controller == objectProxy && !(concreteObject instanceof Pathway)){
				pathwayControllers.remove(i);
			}
		}
		
		// we assume we can only have one "controlled" entity
		if(controlledInteraction == objectProxy && concreteObject instanceof Interaction) {
			controlledInteraction = (Interaction)concreteObject;
			controlledPathway = null;
		} else if(controlledPathway == objectProxy && concreteObject instanceof Pathway) {
			controlledInteraction = null;
			controlledPathway = (Pathway)concreteObject;
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
		printObjects(sb,"physicalControllers",getPhysicalControllers(),level);
	}

}
