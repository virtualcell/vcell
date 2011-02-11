package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

public class Pathway extends EntityImpl {
	private BioSource organism;
	private ArrayList<Interaction> pathwayComponentInteraction = new ArrayList<Interaction>();
	private ArrayList<Pathway> pathwayComponentPathway = new ArrayList<Pathway>();
	private ArrayList<PathwayStep> pathwayOrder = new ArrayList<PathwayStep>();

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
	
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		if(organism == objectProxy) {
			organism = (BioSource) concreteObject;
		}
		for (int i=0;i<pathwayComponentInteraction.size();i++){
			Interaction interaction = pathwayComponentInteraction.get(i);
			if (interaction == objectProxy && concreteObject instanceof Interaction){
				pathwayComponentInteraction.set(i, (Interaction)concreteObject);
			}
		}
		for (int i=0;i<pathwayComponentPathway.size();i++){
			Pathway pathway = pathwayComponentPathway.get(i);
			if (pathway == objectProxy && concreteObject instanceof Pathway){
				pathwayComponentPathway.set(i, (Pathway)concreteObject);
			}
		}
		for (int i=0;i<pathwayOrder.size();i++){
			PathwayStep step = pathwayOrder.get(i);
			if (step == objectProxy){
				pathwayOrder.set(i, (PathwayStep) concreteObject);
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "organism",organism,level);
		printObjects(sb, "pathwayComponentInteraction",pathwayComponentInteraction,level);
		printObjects(sb, "pathwayComponentPathway",pathwayComponentPathway,level);
		printObjects(sb, "pathwayOrder",pathwayOrder,level);
	}


}
