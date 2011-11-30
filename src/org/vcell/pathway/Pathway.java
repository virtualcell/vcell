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
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(organism == objectProxy) {
			organism = (BioSource) concreteObject;
		}
		
		for (int i=0;i<pathwayComponentInteraction.size();i++){
			Interaction thing = pathwayComponentInteraction.get(i);
			if (thing == objectProxy && concreteObject instanceof Interaction){
				pathwayComponentInteraction.set(i, (Interaction)concreteObject);
			} else if(thing == objectProxy && !(concreteObject instanceof Interaction)) {
				pathwayComponentInteraction.remove(i);
			}
		}
/*		
 * We do NOT solve references to pathway and pathway steps because of infinite recursion
 * Ex: Pathway "E-cadherin signalling in the nascent..."  
 * http://www.pathwaycommons.org/pc/webservice.do?cmd=get_record_by_cpath_id&version=2.0&q=826249&output=biopax
 * The paths CPATH-826249 and CPATH-826243 cross reference each other
 * 
		// TODO: add another loop for pathway step
		for (int i=0;i<pathwayComponentPathway.size();i++){
			Pathway thing = pathwayComponentPathway.get(i);
			if (thing == objectProxy && concreteObject instanceof Pathway){
				pathwayComponentPathway.set(i, (Pathway)concreteObject);
			} else if (thing == objectProxy && !(concreteObject instanceof Pathway)){
				pathwayComponentPathway.remove(i);
			}
		}
		*/
		for (int i=0; i<pathwayOrder.size(); i++) {
			PathwayStep thing = pathwayOrder.get(i);
			if(thing == objectProxy && concreteObject instanceof PathwayStep) {
				pathwayOrder.set(i, (PathwayStep)concreteObject);
			}
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
		
		if(organism instanceof RdfObjectProxy) {
			RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)organism;
			if (rdfObjectProxy.getID() != null){
				BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
				if (concreteObject != null){
					organism = (BioSource) concreteObject;
				}
			}
		}
		
		
		for (int i=0;i<pathwayComponentInteraction.size();i++){
			Interaction thing = pathwayComponentInteraction.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						if(concreteObject instanceof Interaction){
							pathwayComponentInteraction.set(i, (Interaction)concreteObject);
						} else {
							pathwayComponentInteraction.remove(i);
						}
					}
				}
			}
		}
/*		
 * We do NOT solve references to pathway and pathway steps because of infinite recursion
 * Ex: Pathway "E-cadherin signalling in the nascent..."  
 * http://www.pathwaycommons.org/pc/webservice.do?cmd=get_record_by_cpath_id&version=2.0&q=826249&output=biopax
 * The paths CPATH-826249 and CPATH-826243 cross reference each other
 * 
		// TODO: add another loop for pathway step
		for (int i=0;i<pathwayComponentPathway.size();i++){
			Pathway thing = pathwayComponentPathway.get(i);
			if (thing == objectProxy && concreteObject instanceof Pathway){
				pathwayComponentPathway.set(i, (Pathway)concreteObject);
			} else if (thing == objectProxy && !(concreteObject instanceof Pathway)){
				pathwayComponentPathway.remove(i);
			}
		}
		*/
		for (int i=0; i<pathwayOrder.size(); i++) {
			PathwayStep thing = pathwayOrder.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						if(concreteObject instanceof PathwayStep){
							pathwayOrder.set(i, (PathwayStep)concreteObject);
						} else {
							
						}
					}
				}
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
