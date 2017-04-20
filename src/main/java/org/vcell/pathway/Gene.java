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

import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class Gene extends EntityImpl {
	private BioSource organism;

	public BioSource getOrganism() {
		return organism;
	}

	public void setOrganism(BioSource organism) {
		this.organism = organism;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(organism == objectProxy) {
			organism = (BioSource) concreteObject;
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
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "organism",organism,level);
	}
}
