package org.vcell.pathway;

import java.util.ArrayList;

public class Control extends InteractionImpl {

	private InteractionImpl controlledInteraction;

	private Pathway controlledPathway;

	private String controlType;

	private ArrayList<Pathway> pathwayControllers;

	private ArrayList<PhysicalEntity> physicalControllers;

	public Control(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public InteractionImpl getControlledInteraction() {
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
	public void setControlledInteraction(InteractionImpl controlledInteraction) {
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

}
