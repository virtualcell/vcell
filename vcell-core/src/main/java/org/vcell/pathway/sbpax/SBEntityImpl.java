/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.sbpax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.BioPaxObjectImpl;
import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class SBEntityImpl extends BioPaxObjectImpl implements SBEntity {

	private ArrayList<SBEntity> sbSubEntity = new ArrayList<SBEntity>();
	private ArrayList<SBVocabulary> sbTerm = new ArrayList<SBVocabulary>();

	public ArrayList<SBEntity> getSBSubEntity() {
		return sbSubEntity;
	}
	public void setSBSubEntity(ArrayList<SBEntity> sbSubEntity) {
		this.sbSubEntity = sbSubEntity;
	}
	
	public ArrayList<SBVocabulary> getSBTerm() {
		return sbTerm;
	}
	
	public void setSBTerm(ArrayList<SBVocabulary> sbTerm) {
		this.sbTerm = sbTerm;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		for (int i=0; i<sbSubEntity.size(); i++) {
			SBEntity thing = sbSubEntity.get(i);
			if(thing == objectProxy) {
				sbSubEntity.set(i, (SBEntity)concreteObject);
			}
		}
		for (int i=0; i<sbTerm.size(); i++) {
			SBVocabulary thing = sbTerm.get(i);
			if(thing == objectProxy) {
				sbTerm.set(i, (SBVocabulary)concreteObject);
			}
		}
	}

	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
		for (int i=0; i<sbSubEntity.size(); i++) {
			SBEntity thing = sbSubEntity.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						sbSubEntity.set(i, (SBEntity)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<sbTerm.size(); i++) {
			SBVocabulary thing = sbTerm.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						sbTerm.set(i, (SBVocabulary)concreteObject);
					}
				}
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb,"SBTerm", sbTerm, level);
		printObjects(sb, "sbMeasurable",sbSubEntity,level);
	}
	
}
