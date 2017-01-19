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
import org.vcell.relationship.PathwayMapping;
import org.vcell.util.Displayable;

public class PhysicalEntity extends EntityImpl implements Displayable {
	private CellularLocationVocabulary cellularLocation;
	private ArrayList<EntityFeature> feature = new ArrayList<EntityFeature>();
	private ArrayList<PhysicalEntity> memberPhysicalEntity = new ArrayList<PhysicalEntity>();
	private ArrayList<EntityFeature> notFeature = new ArrayList<EntityFeature>();

	public CellularLocationVocabulary getCellularLocation() {
		return cellularLocation;
	}
	public ArrayList<EntityFeature> getFeature() {
		return feature;
	}
	public ArrayList<PhysicalEntity> getMemberPhysicalEntity() {
		return memberPhysicalEntity;
	}
	public ArrayList<EntityFeature> getNotFeature() {
		return notFeature;
	}
	
	public void setCellularLocation(CellularLocationVocabulary cellularLocation) {
		this.cellularLocation = cellularLocation;
	}
	public void setFeature(ArrayList<EntityFeature> feature) {
		this.feature = feature;
	}
	public void setMemberPhysicalEntity(
			ArrayList<PhysicalEntity> memberPhysicalEntity) {
		this.memberPhysicalEntity = memberPhysicalEntity;
	}

	public void setNotFeature(ArrayList<EntityFeature> notFeature) {
		this.notFeature = notFeature;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(cellularLocation == objectProxy) {
			cellularLocation = (CellularLocationVocabulary) concreteObject;
		}
		for (int i=0; i<feature.size(); i++) {
			EntityFeature thing = feature.get(i);
			if(thing == objectProxy) {
				feature.set(i, (EntityFeature)concreteObject);
			}
		}
		for (int i=0; i<memberPhysicalEntity.size(); i++) {
			PhysicalEntity thing = memberPhysicalEntity.get(i);
			if(thing == objectProxy) {
				memberPhysicalEntity.set(i, (PhysicalEntity)concreteObject);
			}
		}
		for (int i=0; i<notFeature.size(); i++) {
			EntityFeature thing = notFeature.get(i);
			if(thing == objectProxy) {
				notFeature.set(i, (EntityFeature)concreteObject);
			}
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
		
		if(cellularLocation instanceof RdfObjectProxy) {
			RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)cellularLocation;
			if (rdfObjectProxy.getID() != null){
				BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
				if (concreteObject != null){
					cellularLocation = (CellularLocationVocabulary) concreteObject;
				}
			}
		}
		for (int i=0; i<feature.size(); i++) {
			EntityFeature thing = feature.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						feature.set(i, (EntityFeature)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<memberPhysicalEntity.size(); i++) {
			PhysicalEntity thing = memberPhysicalEntity.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						memberPhysicalEntity.set(i, (PhysicalEntity)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<notFeature.size(); i++) {
			EntityFeature thing = notFeature.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						notFeature.set(i, (EntityFeature)concreteObject);
					}
				}
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "cellularLocation",cellularLocation,level);
		printObjects(sb, "feature",feature,level);
		printObjects(sb, "memberPhysicalEntity",memberPhysicalEntity,level);
		printObjects(sb, "notFeature",notFeature,level);
	}
	
	public static final String typeName = "PhysicalEntity";
	@Override
	public final String getDisplayName() {
		if(getName().size() == 0) {
			return PathwayMapping.getSafetyName(getID());
		} else {
			return PathwayMapping.getSafetyName(getName().get(0));
		}
	}
	@Override
	public final String getDisplayType() {
		return typeName;
	}
}
