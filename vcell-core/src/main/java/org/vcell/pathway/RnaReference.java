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

public class RnaReference extends EntityReference {
	private ArrayList<DnaRegionReference> dnaSubRegion = new ArrayList<DnaRegionReference>();
	private BioSource organism;
	private ArrayList<RnaRegionReference> rnaSubRegion = new ArrayList<RnaRegionReference>();
	private String sequence;
	
	public ArrayList<DnaRegionReference> getDnaSubRegion() {
		return dnaSubRegion;
	}
	public BioSource getOrganism() {
		return organism;
	}
	public ArrayList<RnaRegionReference> getRnaSubRegion() {
		return rnaSubRegion;
	}
	public String getSequence() {
		return sequence;
	}
	public void setDnaSubRegion(ArrayList<DnaRegionReference> dnaSubRegion) {
		this.dnaSubRegion = dnaSubRegion;
	}
	public void setOrganism(BioSource organism) {
		this.organism = organism;
	}
	public void setRnaSubRegion(ArrayList<RnaRegionReference> rnaSubRegion) {
		this.rnaSubRegion = rnaSubRegion;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	} 

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(organism == objectProxy) {
			organism = (BioSource) concreteObject;
		}
		for (int i=0;i<dnaSubRegion.size();i++){
			DnaRegionReference thing = dnaSubRegion.get(i);
			if (thing == objectProxy && concreteObject instanceof DnaRegionReference){
				dnaSubRegion.set(i, (DnaRegionReference)concreteObject);
			} else if(thing == objectProxy && !(concreteObject instanceof DnaRegionReference)) {
				dnaSubRegion.remove(i);
			}
		}
		for (int i=0;i<rnaSubRegion.size();i++){
			RnaRegionReference thing = rnaSubRegion.get(i);
			if (thing == objectProxy && concreteObject instanceof RnaRegionReference){
				rnaSubRegion.set(i, (RnaRegionReference)concreteObject);
			} else if (thing == objectProxy && !(concreteObject instanceof RnaRegionReference)){
				rnaSubRegion.remove(i);
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
		for (int i=0;i<dnaSubRegion.size();i++){
			DnaRegionReference thing = dnaSubRegion.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						if(concreteObject instanceof DnaRegionReference){
							dnaSubRegion.set(i, (DnaRegionReference)concreteObject);
						} else{
							dnaSubRegion.remove(i);
						}
					}
				}
			}
		}
		for (int i=0;i<rnaSubRegion.size();i++){
			RnaRegionReference thing = rnaSubRegion.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						if(concreteObject instanceof RnaRegionReference){
							rnaSubRegion.set(i, (RnaRegionReference)concreteObject);
						} else{
							rnaSubRegion.remove(i);
						}
					}
				}
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "dnaSubRegion",dnaSubRegion,level);
		printObject(sb, "organism",organism,level);
		printObjects(sb, "rnaSubRegion",rnaSubRegion,level);
		printString(sb, "sequence",sequence,level);
	}
}
