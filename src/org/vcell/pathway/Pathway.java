package org.vcell.pathway;

import java.util.ArrayList;

public class Pathway extends EntityImpl {
	private BioSource organism;
	private ArrayList<Interaction> pathwayComponentInteraction;
	private ArrayList<Pathway> pathwayComponentPathway;
	private ArrayList<PathwayStep> pathwayOrder;

	public Pathway(String name) {
		super(name);
	}

	public BioSource getOrganism() {
		return organism;
	}

	public ArrayList<Interaction> getPathwayComponentInteraction() {
		return pathwayComponentInteraction;
	}

	public ArrayList<Pathway> getPathwayComponentPathway() {
		return pathwayComponentPathway;
	}

	public ArrayList<PathwayStep> getPathwayOrder() {
		return pathwayOrder;
	}

	public void setOrganism(BioSource organism) {
		this.organism = organism;
	}

	public void setPathwayComponentInteraction(
			ArrayList<Interaction> pathwayComponentInteraction) {
		this.pathwayComponentInteraction = pathwayComponentInteraction;
	}

	public void setPathwayComponentPathway(
			ArrayList<Pathway> pathwayComponentPathway) {
		this.pathwayComponentPathway = pathwayComponentPathway;
	}

	public void setPathwayOrder(ArrayList<PathwayStep> pathwayOrder) {
		this.pathwayOrder = pathwayOrder;
	}
}
